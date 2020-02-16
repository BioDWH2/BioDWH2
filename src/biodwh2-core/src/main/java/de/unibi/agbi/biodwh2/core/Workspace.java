package de.unibi.agbi.biodwh2.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.unibi.agbi.biodwh2.core.etl.Merger;
import de.unibi.agbi.biodwh2.core.exceptions.*;
import de.unibi.agbi.biodwh2.core.model.Configuration;
import de.unibi.agbi.biodwh2.core.model.DataSourceMetadata;
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

public class Workspace {
    private static final Logger logger = LoggerFactory.getLogger(Workspace.class);

    public static final int Version = 1;
    private static final String SourcesDirectoryName = "sources";
    private static final String ConfigFileName = "config.json";

    private final String workingDirectory;
    private final Configuration configuration;
    private final List<DataSource> dataSources;

    public Workspace(String workingDirectory) throws IOException {
        this.workingDirectory = workingDirectory;
        createWorkingDirectoryIfNotExists();
        configuration = createOrLoadConfiguration();
        logger.info("Using data sources " + configuration.dataSourceIds);
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

    private DataSource tryInstantiateDataSource(Class<DataSource> dataSourceClass) {
        try {
            return dataSourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("Failed to instantiate data source '" + dataSourceClass.getName() + "'", e);
        }
        return null;
    }

    private boolean isDataSourceUsed(DataSource dataSource) {
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
            String currentVersion = dataSource.getMetadata().version.toString();
            String latestVersion = getLatestVersion(dataSource);
            sourcesUpToDate.put(dataSource.getId(), currentVersion.equals(latestVersion));
        }
        return sourcesUpToDate;
    }

    private String getLatestVersion(DataSource dataSource) {
        try {
            return dataSource.getUpdater().getNewestVersion().toString();
        } catch (UpdaterException e) {
            logger.error("Failed to get newest version for data source '" + dataSource.getId() + "'");
            return "-";
        }
    }

    private String createStateTable(Map<String, Boolean> sourcesUpToDate, boolean verbose) {
        String state = "";
        String heading;
        String separator;
        String spacer = StringUtils.repeat(" ", 129);
        if (verbose) {
            separator = StringUtils.repeat("-", 150);
            heading = String.format("\n%s\n%-15s%-33s%-23s%-25s%-37s%-28s\n%s\n", separator, "SourceID",
                                    "Version is up-to-date", "Version", "new Version", "Time of latest update", "Files",
                                    separator);
        } else {
            separator = StringUtils.repeat("-", 120);
            heading = String.format("\n%s\n%-15s%-33s%-23s%-25s%-37s\n%s\n", separator, "SourceID",
                                    "Version is up-to-date", "Version", "new Version", "Time of latest update",
                                    separator);
        }
        for (DataSource dataSource : dataSources) {
            Map<String, String> metaMap = createMetadataMap(dataSource, sourcesUpToDate);
            state = String.format("%s%-23s%-21s%-25s%-25s%-35s", state, metaMap.get("dataSourceId"),
                                  metaMap.get("isVersionUpToDate"), metaMap.get("workspaceVersion"),
                                  metaMap.get("latestVersion"), metaMap.get("latestUpdateTime"));
            if (verbose) {
                DataSourceMetadata meta = dataSource.getMetadata();
                List<String> existingFiles = meta.sourceFileNames;
                state = String.format("%s%-30s\n", state, existingFiles.get(0));
                for (int i = 1; i < existingFiles.size(); i++) {
                    state = String.format("%s%s%s\n", state, spacer, existingFiles.get(i));
                }
            } else {
                state += "\n";
            }
        }
        return heading + state + separator;
    }

    private Map<String, String> createMetadataMap(DataSource dataSource, Map<String, Boolean> sourcesUpToDate) {
        Map<String, String> metaMap = new HashMap<>();
        DataSourceMetadata meta = dataSource.getMetadata();
        metaMap.put("dataSourceId", dataSource.getId());
        metaMap.put("isVersionUpToDate", sourcesUpToDate.get(dataSource.getId()).toString());
        metaMap.put("workspaceVersion", meta.version.toString());
        metaMap.put("latestVersion", getLatestVersion(dataSource));
        metaMap.put("latestUpdateTime", meta.getLocalUpdateDateTime().toString());
        return metaMap;
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

    public void updateDataSources(final String sourceName, final String version, final boolean skipUpdate) {
        if (prepareDataSources()) {
            for (DataSource dataSource : dataSources)
                if (sourceName == null || dataSource.getId().equals(sourceName))
                    updateDataSource(dataSource, version, skipUpdate);
            mergeDataSources();
        }
    }

    private void updateDataSource(final DataSource dataSource, final String version, final boolean skipUpdate) {
        logger.info("Processing of data source '" + dataSource.getId() + "' started");
        if (!skipUpdate) {
            if (version != null)
                dataSource.updateManually(this, version);
            else
                dataSource.updateAutomatic(this);
            dataSource.trySaveMetadata(this);
        }
        if (dataSource.getMetadata().updateSuccessful) {
            dataSource.parse(this);
            dataSource.export(this);
        }
        dataSource.trySaveMetadata(this);
        logger.info("Processing of data source '" + dataSource.getId() + "' finished");
    }

    private void mergeDataSources() {
        logger.info("Merging of data sources started");
        Merger merger = new Merger();
        String mergedFilePath = Paths.get(getSourcesDirectory(), "merged.ttl").toString();
        try {
            PrintWriter writer = new PrintWriter(mergedFilePath);
            merger.merge(this, dataSources, writer);
        } catch (FileNotFoundException e) {
            logger.error("Failed to create merge file '" + mergedFilePath + "'");
        } catch (MergerException e) {
            logger.error("Failed to merge");
        }
        logger.info("Merging of data sources finished");
    }
}
