package de.unibi.agbi.biodwh2.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.unibi.agbi.biodwh2.core.etl.RDFMerger;
import de.unibi.agbi.biodwh2.core.exceptions.*;
import de.unibi.agbi.biodwh2.core.model.Configuration;
import de.unibi.agbi.biodwh2.core.model.DataSourceMetadata;
import de.unibi.agbi.biodwh2.core.model.Version;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public final class Workspace {
    private static final Logger logger = LoggerFactory.getLogger(Workspace.class);

    public static final int Version = 1;
    private static final String SourcesDirectoryName = "sources";
    private static final String ConfigFileName = "config.json";
    private static final String MergedGraphRdfFileName = "merged.ttl";

    private final String workingDirectory;
    private final Configuration configuration;
    private final List<DataSource> dataSources;

    public Workspace(final String workingDirectory) throws IOException {
        this.workingDirectory = workingDirectory;
        createWorkingDirectoryIfNotExists();
        configuration = createOrLoadConfiguration();
        logger.info("Using data sources " + configuration.dataSourceIds);
        logger.info("Export and merge of RDF format is " + (configuration.rdfEnabled ? "enabled" : "disabled"));
        logger.info("Export and merge of Graph formats is " + (configuration.graphEnabled ? "enabled" : "disabled"));
        dataSources = resolveUsedDataSources();
    }

    private void createWorkingDirectoryIfNotExists() throws IOException {
        logger.info("Using workspace directory '" + workingDirectory + "'");
        Files.createDirectories(Paths.get(workingDirectory));
        Files.createDirectories(Paths.get(getSourcesDirectory()));
    }

    String getSourcesDirectory() {
        return Paths.get(workingDirectory, SourcesDirectoryName).toString();
    }

    private Configuration createOrLoadConfiguration() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Path path = Paths.get(workingDirectory, ConfigFileName);
        if (Files.exists(path))
            return objectMapper.readValue(path.toFile(), Configuration.class);
        Configuration configuration = new Configuration();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.writeValue(path.toFile(), configuration);
        return configuration;
    }

    private List<DataSource> resolveUsedDataSources() {
        List<DataSource> dataSources = new ArrayList<>();
        List<Class<DataSource>> availableDataSourceClasses = Factory.getInstance().getImplementations(DataSource.class);
        for (Class<DataSource> dataSourceClass : availableDataSourceClasses) {
            DataSource dataSource = tryInstantiateDataSource(dataSourceClass);
            if (dataSource != null && isDataSourceUsed(dataSource))
                dataSources.add(dataSource);
        }
        return dataSources;
    }

    private DataSource tryInstantiateDataSource(final Class<DataSource> dataSourceClass) {
        try {
            return dataSourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("Failed to instantiate data source '" + dataSourceClass.getName() + "'", e);
        }
        return null;
    }

    private boolean isDataSourceUsed(final DataSource dataSource) {
        return configuration.dataSourceIds.contains(dataSource.getId());
    }

    public void checkState(final boolean verbose) {
        if (prepareDataSources()) {
            Map<String, Boolean> sourcesUpToDate = createSourcesUpToDate();
            int countUpToDate = Collections.frequency(sourcesUpToDate.values(), true);
            ArrayList<String> notUpToDate = new ArrayList<>();
            for (String file : sourcesUpToDate.keySet())
                if (!sourcesUpToDate.get(file))
                    notUpToDate.add(file);
            String loggerInfo = (countUpToDate == dataSources.size()) ? "all source data are up-to-date." :
                                countUpToDate + "/" + dataSources.size() + " source data are up-to-date.";
            String state = createStateTable(sourcesUpToDate, verbose);
            logger.info(state);
            logger.info(loggerInfo);
            logger.info("Data sources to be updated: " + notUpToDate);
        }
    }

    private Map<String, Boolean> createSourcesUpToDate() {
        Map<String, Boolean> sourcesUpToDate = new HashMap<>();
        for (DataSource dataSource : dataSources) {
            Version workspaceVersion = dataSource.getMetadata().version;
            Version newestVersion = getLatestVersion(dataSource);
            boolean isUpToDate = workspaceVersion != null && newestVersion != null && newestVersion.compareTo(
                    workspaceVersion) == 0;
            sourcesUpToDate.put(dataSource.getId(), isUpToDate);
        }
        return sourcesUpToDate;
    }

    private Version getLatestVersion(final DataSource dataSource) {
        try {
            return dataSource.getUpdater().getNewestVersion();
        } catch (UpdaterException e) {
            logger.error("Failed to get newest version for data source '" + dataSource.getId() + "'");
            return null;
        }
    }

    private String createStateTable(final Map<String, Boolean> sourcesUpToDate, final boolean verbose) {
        String heading;
        final String separator = StringUtils.repeat("-", verbose ? 150 : 120);
        if (verbose)
            heading = String.format("\n%s\n%-15s%-33s%-23s%-25s%-37s%-28s\n%s\n", separator, "SourceID",
                                    "Version is up-to-date", "Version", "new Version", "Time of latest update", "Files",
                                    separator);
        else
            heading = String.format("\n%s\n%-15s%-33s%-23s%-25s%-37s\n%s\n", separator, "SourceID",
                                    "Version is up-to-date", "Version", "new Version", "Time of latest update",
                                    separator);
        String state = "";
        for (DataSource dataSource : dataSources) {
            final DataSourceMetadata meta = dataSource.getMetadata();
            state = String.format("%s%-23s%-21s%-25s%-25s%-35s", state, dataSource.getId(),
                                  sourcesUpToDate.get(dataSource.getId()), meta.version, getLatestVersion(dataSource),
                                  meta.getLocalUpdateDateTime());
            if (verbose) {
                final String spacer = StringUtils.repeat(" ", 129);
                final List<String> existingFiles = meta.sourceFileNames;
                state = String.format("%s%-30s\n", state, existingFiles.get(0));
                for (int i = 1; i < existingFiles.size(); i++)
                    state = String.format("%s%s%s\n", state, spacer, existingFiles.get(i));
            } else
                state += "\n";
        }
        return heading + state + separator;
    }

    private boolean prepareDataSources() {
        for (DataSource dataSource : dataSources) {
            try {
                dataSource.prepare(this);
            } catch (DataSourceException e) {
                logger.error("Failed to prepare data source '" + dataSource.getId() + "'", e);
                return false;
            }
        }
        return true;
    }

    public void updateDataSources(final String dataSourceId, final String version, final boolean skipUpdate) {
        if (prepareDataSources()) {
            for (DataSource dataSource : dataSources)
                if (dataSourceId == null || dataSource.getId().equals(dataSourceId))
                    updateDataSource(dataSource, version, skipUpdate);
            mergeDataSources();
        }
    }

    private void updateDataSource(final DataSource dataSource, final String version, final boolean skipUpdate) {
        logger.info("Processing of data source '" + dataSource.getId() + "' started");
        if (!skipUpdate) {
            logger.info("Running updater");
            if (version != null)
                dataSource.updateManually(this, version);
            else
                dataSource.updateAutomatic(this);
            dataSource.trySaveMetadata(this);
        } else if (dataSource.getMetadata().updateSuccessful == null) {
            logger.error("Update was skipped for data source '" + dataSource.getId() +
                         "' without successful previous update.");
            return;
        }
        if (dataSource.getMetadata().updateSuccessful) {
            logger.info("Running parser");
            dataSource.parse(this);
            logger.info("Running exporter");
            dataSource.export(this, configuration.rdfEnabled, configuration.graphEnabled);
        }
        dataSource.trySaveMetadata(this);
        logger.info("Processing of data source '" + dataSource.getId() + "' finished");
    }

    private void mergeDataSources() {
        logger.info("Merging of data sources started");
        if (configuration.rdfEnabled)
            mergeRDFDataSources();
        if (configuration.graphEnabled)
            mergeGraphDataSources();
        logger.info("Merging of data sources finished");
    }

    private void mergeRDFDataSources() {
        RDFMerger rdfMerger = new RDFMerger();
        String mergedFilePath = Paths.get(getSourcesDirectory(), MergedGraphRdfFileName).toString();
        try {
            PrintWriter writer = new PrintWriter(mergedFilePath);
            rdfMerger.merge(this, dataSources, writer);
        } catch (FileNotFoundException e) {
            logger.error("Failed to create merge file '" + mergedFilePath + "'", e);
        } catch (MergerException e) {
            logger.error("Failed to merge data sources", e);
        }
    }

    private void mergeGraphDataSources() {

    }
}
