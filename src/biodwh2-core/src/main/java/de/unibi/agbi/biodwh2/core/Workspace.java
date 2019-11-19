package de.unibi.agbi.biodwh2.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.unibi.agbi.biodwh2.core.exceptions.*;
import de.unibi.agbi.biodwh2.core.model.Configuration;
import de.unibi.agbi.biodwh2.core.model.DataSourceMetadata;
import de.unibi.agbi.biodwh2.core.model.Version;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

public class Workspace {
    private static final Logger logger = LoggerFactory.getLogger(Workspace.class);

    public static final int Version = 1;
    private static final String SourcesDirectory = "sources";

    private final String workingDirectory;
    private final Configuration configuration;
    private final List<DataSource> dataSources;

    public Workspace(String workingDirectory) throws IOException {
        this.workingDirectory = workingDirectory;
        createWorkingDirectoryIfNotExists();
        configuration = createOrLoadConfiguration();
        dataSources = resolveUsedDataSources();
    }

    private void createWorkingDirectoryIfNotExists() throws IOException {
        Files.createDirectories(Paths.get(workingDirectory));
        Files.createDirectories(Paths.get(getSourcesDirectory()));
    }

    String getSourcesDirectory() {
        return Paths.get(workingDirectory, SourcesDirectory).toString();
    }

    private Configuration createOrLoadConfiguration() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Path path = Paths.get(workingDirectory, "config.json");
        if (Files.exists(path))
            return objectMapper.readValue(path.toFile(), Configuration.class);
        Configuration configuration = new Configuration();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.writeValue(path.toFile(), configuration);
        return configuration;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public Configuration getConfiguration() {
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

    public void checkState(boolean verbose) {
        ensureDataSourceDirectoriesExist();
        createOrLoadDataSourcesMetadata();
        Map<String, Boolean> sourcesUptodate = createSourcesUptodate(dataSources);
        int countUpToDate = countSourcesUptodate(sourcesUptodate);
        ArrayList<String> notUptodate = new ArrayList<>();
        String loggerInfo = (countUpToDate == dataSources.size()) ? "all source data are up-to-date." :
                            countUpToDate + "/" + dataSources.size() + " source data are up-to-date.";
            String state = createStateTable(dataSources, sourcesUptodate, verbose);
            logger.info(state);
            logger.info(loggerInfo);
        for (String file : sourcesUptodate.keySet()) {
            if (!sourcesUptodate.get(file)) {
                notUptodate.add(file);
            }
        }
        logger.info("To be updated: " + notUptodate);

    }

    private int countSourcesUptodate(Map<String, Boolean> sourcesUptodate) {
        return Collections.frequency(sourcesUptodate.values(), true);
    }

    private String getLatestVersion(DataSource dataSource) {
        try {
            return dataSource.getUpdater().getNewestVersion().toString();
        } catch (UpdaterException e) {
            logger.error("New version of " + dataSource.getId() + " is not accessible.");
            return "-";
        }
    }

    private Map<String, Boolean> createSourcesUptodate(List<DataSource> dataSources) {
        Map<String, Boolean> sourcesUptodate = new HashMap<>();
        for (DataSource dataSource : dataSources) {
            String currentVersion = dataSource.getMetadata().version.toString();
            String latestVersion = getLatestVersion(dataSource);
            sourcesUptodate.put(dataSource.getId(), currentVersion.equals(latestVersion));
        }
        return sourcesUptodate;
    }

    private String createVerboseTable(List<DataSource> dataSources, Map<String, Boolean> sourcesUptodate) {
        String state = "";
        String seperator = StringUtils.repeat("-", 150);
        String spacer = StringUtils.repeat(" ", 129);
        String heading = String.format("\n%s\n%-15s%-33s%-23s%-25s%-37s%-28s\n%s\n", seperator, "SourceID",
                                       "Version is up-to-date", "Version", "new Version", "Time of latest update",
                                       "Files", seperator);
        for (DataSource dataSource : dataSources) {
            String dataSourceId = dataSource.getId();
            DataSourceMetadata meta = dataSource.getMetadata();
            Boolean isVersionUptodate = sourcesUptodate.get(dataSourceId);
            Version workspaceVersion = meta.version;
            String latestVersion = getLatestVersion(dataSource);
            List<String> existingFiles = meta.sourceFileNames;
            LocalDateTime latestUpdateTime = meta.getLocalUpdateDateTime();
            state = String.format("%s%-23s%-21s%-25s%-25s%-35s%-30s\n", state, dataSourceId, isVersionUptodate,
                                  workspaceVersion, latestVersion, latestUpdateTime, existingFiles.get(0));
            for (int i = 1; i < existingFiles.size(); i++) {
                state = String.format("%s%s%s\n", state, spacer, existingFiles.get(i));
            }
        }
        return heading + state + "\n" + seperator;
    }

    private String createStateTable(List<DataSource> dataSources, Map<String, Boolean> sourcesUptodate, boolean verbose) {
        String state = "";
        String heading;
        String separator;
        String spacer = StringUtils.repeat(" ", 129);
        if (verbose) {
            separator = StringUtils.repeat("-", 150);
            heading = String.format("\n%s\n%-15s%-33s%-23s%-25s%-37s%-28s\n%s\n", separator,
                                    "SourceID", "Version is up-to-date", "Version", "new Version",
                                    "Time of latest update", "Files", separator);
        } else {
            separator = StringUtils.repeat("-", 120);
            heading = String.format("\n%s\n%-15s%-33s%-23s%-25s%-37s\n%s\n", separator, "SourceID",
                                    "Version is up-to-date", "Version", "new Version", "Time of latest update",
                                    separator);
        }
        for (DataSource dataSource : dataSources) {
            Map<String, String> metaMap = createMetadataMap(dataSource, sourcesUptodate);
            state = String.format("%s%-23s%-21s%-25s%-25s%-35s", state, metaMap.get("dataSourceId"), metaMap.get("isVersionUptodate"),
                                  metaMap.get("workspaceVersion"), metaMap.get("latestVersion"), metaMap.get("latestUpdateTime"));
            if (verbose) {
                DataSourceMetadata meta = dataSource.getMetadata();
                List<String> existingFiles =  meta.sourceFileNames;
                state = String.format("%s%-30s\n", state, existingFiles.get(0));
                for (int i = 1; i < existingFiles.size(); i++) {
                    state = String.format("%s%s%s\n", state, spacer, existingFiles.get(i));
                }
            } else {state += "\n";}
        }
        return heading + state + separator;
    }

    private Map<String, String> createMetadataMap(DataSource dataSource, Map<String, Boolean> sourcesUptodate) {
        Map<String, String> metaMap = new HashMap<>();
        DataSourceMetadata meta = dataSource.getMetadata();
        metaMap.put("dataSourceId", dataSource.getId());
        metaMap.put("isVersionUpToDate", sourcesUptodate.get(dataSource.getId()).toString());
        metaMap.put("workspaceVersion", meta.version.toString());
        metaMap.put("latestVersion", getLatestVersion(dataSource));
        metaMap.put("latestUpdateTime", meta.getLocalUpdateDateTime().toString());
        return metaMap;
    }

    private void ensureDataSourceDirectoriesExist() {
        for (DataSource dataSource : dataSources) {
            try {
                dataSource.createDirectoryIfNotExists(this);
            } catch (IOException e) {
                logger.error("Failed to create data source directory for '" + dataSource.getId() + "'", e);
            }
        }
    }

    private void createOrLoadDataSourcesMetadata() {
        for (DataSource dataSource : dataSources) {
            try {
                dataSource.createOrLoadMetadata(this);
            } catch (IOException e) {
                logger.error("Failed to load data source metadata for '" + dataSource.getId() + "'", e);
            }
        }
    }

    public void updateDataSources() {
        ensureDataSourceDirectoriesExist();
        createOrLoadDataSourcesMetadata();
        for (DataSource dataSource : dataSources) {
            logger.info("Processing of data source '" + dataSource.getId() + "' started");
            updateAuto(dataSource);
            parse(dataSource);
            export(dataSource);
        }
    }

    public void integrateDataSources(String sourceName, String version) {
        ensureDataSourceDirectoriesExist();
        createOrLoadDataSourcesMetadata();
        if (sourceName != null || version != null) {
            for (DataSource dataSource : dataSources) {
                if (dataSource.getId().equals(sourceName)) {
                    logger.info("Processing of data source '" + dataSource.getId() + "' started");
                    updateMan(dataSource, version);
                    parse(dataSource);
                    export(dataSource);
                }
            }
        } else {
            logger.error("Failed to read source name and version from the command line");
        }
    }

    private void export(DataSource dataSource) {
        try {
            boolean exported = dataSource.getRdfExporter().export(this, dataSource);
            logger.info("\texported: " + exported);
            dataSource.getMetadata().exportRDFSuccessfull = true;
        } catch (ExporterException e) {
            logger.error("Failed to export data source '" + dataSource.getId() + "' into RDF", e);
        }
        try {
            boolean exported = dataSource.getGraphExporter().export(this, dataSource);
            logger.info("\texported: " + exported);
            dataSource.getMetadata().exportGraphMLSuccessfull = true;
        } catch (ExporterException e) {
            logger.error("Failed to export data source '" + dataSource.getId() + "' into GraphML", e);
        }
        logger.info("Processing of data source '" + dataSource.getId() + "' finished");
    }

    private void parse(DataSource dataSource) {
        try {
            boolean parsed = dataSource.getParser().parse(this, dataSource);
            logger.info("\tparsed: " + parsed);
            dataSource.getMetadata().parseSuccessfull = true;
        } catch (ParserException e) {
            logger.error("Failed to parse data source '" + dataSource.getId() + "'", e);
        }
    }

    private void updateMan(DataSource dataSource, String version) {
        try {
            boolean updated = dataSource.getUpdater().integrate(this, dataSource, version);
            logger.info("\tupdated manually: " + updated);
            dataSource.getMetadata().updateSuccessful = true;
        } catch (UpdaterException e) {
            logger.error("Failed to update data source '" + dataSource.getId() + "'", e);
        }
    }

    private void updateAuto(DataSource dataSource) {
        try {
            boolean updated = dataSource.getUpdater().update(this, dataSource);
            logger.info("\tupdated: " + updated);
            dataSource.getMetadata().updateSuccessful = true;
        } catch (UpdaterOnlyManuallyException e) {
            logger.error("Data source '" + dataSource.getId() + "' can only be updated manually." +
                         "Download the new version of " + dataSource.getId() +
                         " and use the command line parameter -i or --integrate to add the data" +
                         " to the workspace. \n" +
                         "Help: https://github.com/AstrorEnales/BioDWH2/blob/develop/doc/usage.md");
        } catch (UpdaterException e) {
            logger.error("Failed to update data source '" + dataSource.getId() + "'", e);
        }
    }
}
