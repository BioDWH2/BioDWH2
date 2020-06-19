package de.unibi.agbi.biodwh2.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.unibi.agbi.biodwh2.core.etl.GraphMapper;
import de.unibi.agbi.biodwh2.core.etl.GraphMerger;
import de.unibi.agbi.biodwh2.core.etl.RDFMerger;
import de.unibi.agbi.biodwh2.core.exceptions.*;
import de.unibi.agbi.biodwh2.core.model.Configuration;
import de.unibi.agbi.biodwh2.core.model.DataSourceMetadata;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.model.graph.GraphFileFormat;
import de.unibi.agbi.biodwh2.core.text.TableFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public final class Workspace {
    private static final Logger logger = LoggerFactory.getLogger(Workspace.class);

    public static final int Version = 1;
    private static final String SourcesDirectoryName = "sources";
    private static final String ConfigFileName = "config.json";

    private final String workingDirectory;
    private final Configuration configuration;
    private final List<DataSource> dataSources;

    public Workspace(final String workingDirectory) throws IOException {
        this.workingDirectory = workingDirectory;
        createWorkingDirectoryIfNotExists();
        configuration = createOrLoadConfiguration();
        logger.info("Using data sources " + configuration.dataSourceIds);
        logger.info("Export and merge of RDF format is " + (configuration.rdfEnabled ? "enabled" : "disabled"));
        logger.info("Export and merge of GraphML format is " + (configuration.graphMLEnabled ? "enabled" : "disabled"));
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
        DataSourceLoader loader = new DataSourceLoader();
        for (DataSource dataSource : loader.getDataSources())
            if (isDataSourceUsed(dataSource))
                dataSources.add(dataSource);
        return dataSources;
    }

    private boolean isDataSourceUsed(final DataSource dataSource) {
        return configuration.dataSourceIds.contains(dataSource.getId());
    }

    public Configuration getConfiguration() {
        return configuration;
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
        final List<String> headers = new ArrayList<>();
        Collections.addAll(headers, "SourceID", "Version is up-to-date", "Version", "new Version",
                           "Time of latest update");
        if (verbose)
            headers.add("Files");
        final List<List<String>> rows = new ArrayList<>();
        for (DataSource dataSource : dataSources) {
            final List<String> row = new ArrayList<>();
            rows.add(row);
            final DataSourceMetadata metadata = dataSource.getMetadata();
            Version latestVersion = getLatestVersion(dataSource);
            Collections.addAll(row, dataSource.getId(), sourcesUpToDate.get(dataSource.getId()).toString(),
                               metadata.version.toString(), latestVersion != null ? latestVersion.toString() : null,
                               metadata.getLocalUpdateDateTime().toString());
            if (verbose)
                row.add(String.join(", ", metadata.sourceFileNames));
        }
        return new TableFormatter().format(headers, rows);
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

    public void processDataSources(final String dataSourceId, final String version, final boolean skipUpdate) {
        if (prepareDataSources()) {
            for (DataSource dataSource : dataSources)
                if (dataSourceId == null || dataSource.getId().equals(dataSourceId))
                    processDataSource(dataSource, version, skipUpdate);
            mergeDataSources();
        }
    }

    private void processDataSource(final DataSource dataSource, final String version, final boolean skipUpdate) {
        logger.info("Processing of data source '" + dataSource.getId() + "' started");
        if (skipUpdate) {
            if (dataSource.getMetadata().updateSuccessful == null) {
                logger.error("Update was skipped for data source '" + dataSource.getId() +
                             "' without successful previous update.");
                return;
            }
        } else {
            logger.info("Running updater");
            if (version != null)
                dataSource.updateManually(this, version);
            else
                dataSource.updateAutomatic(this);
            dataSource.trySaveMetadata(this);
        }
        if (dataSource.getMetadata().updateSuccessful) {
            logger.info("Running parser");
            dataSource.parse(this);
            logger.info("Running exporter");
            dataSource.export(this, configuration.rdfEnabled, configuration.graphMLEnabled);
        }
        dataSource.trySaveMetadata(this);
        logger.info("Processing of data source '" + dataSource.getId() + "' finished");
    }

    private void mergeDataSources() {
        logger.info("Merging of data sources started");
        if (configuration.rdfEnabled)
            mergeRDFDataSources();
        if (configuration.graphMLEnabled)
            mergeGraphMLDataSources();
        logger.info("Merging of data sources finished");
    }

    private void mergeRDFDataSources() {
        try {
            new RDFMerger().merge(this, dataSources, getMergedOutputFilePath(GraphFileFormat.RDFTurtle));
        } catch (MergerException e) {
            logger.error("Failed to merge RDF data sources", e);
        }
    }

    private String getMergedOutputFilePath(final GraphFileFormat format) {
        return Paths.get(getSourcesDirectory(), "merged." + format.extension).toString();
    }

    private void mergeGraphMLDataSources() {
        try {
            new GraphMerger().merge(this, dataSources, getMergedOutputFilePath(GraphFileFormat.GraphML));
            new GraphMapper().map(this, dataSources, getMergedOutputFilePath(GraphFileFormat.GraphML),
                                  getMappedOutputFilePath(GraphFileFormat.GraphML));
        } catch (MergerException e) {
            logger.error("Failed to merge GraphML data sources", e);
        }
    }

    private String getMappedOutputFilePath(final GraphFileFormat format) {
        return Paths.get(getSourcesDirectory(), "mapped." + format.extension).toString();
    }
}
