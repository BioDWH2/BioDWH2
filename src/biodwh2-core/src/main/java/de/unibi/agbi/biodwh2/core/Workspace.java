package de.unibi.agbi.biodwh2.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.unibi.agbi.biodwh2.core.etl.GraphMapper;
import de.unibi.agbi.biodwh2.core.etl.GraphMerger;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.*;
import de.unibi.agbi.biodwh2.core.model.Configuration;
import de.unibi.agbi.biodwh2.core.model.DataSourceMetadata;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.model.graph.GraphFileFormat;
import de.unibi.agbi.biodwh2.core.text.TableFormatter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public final class Workspace {
    private static final Logger LOGGER = LoggerFactory.getLogger(Workspace.class);

    public static final int VERSION = 1;
    private static final String SOURCES_DIRECTORY_NAME = "sources";
    private static final String CONFIG_FILE_NAME = "config.json";

    private final String workingDirectory;
    private final Configuration configuration;
    private final List<DataSource> dataSources;

    public Workspace(final String workingDirectory) throws IOException {
        this.workingDirectory = workingDirectory;
        createWorkingDirectoryIfNotExists();
        configuration = createOrLoadConfiguration();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Using data sources " + configuration.dataSourceIds);
        }
        dataSources = resolveUsedDataSources();
    }

    private void createWorkingDirectoryIfNotExists() throws IOException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Using workspace directory '" + workingDirectory + "'");
        Files.createDirectories(Paths.get(workingDirectory));
        Files.createDirectories(Paths.get(getSourcesDirectory()));
    }

    String getSourcesDirectory() {
        return Paths.get(workingDirectory, SOURCES_DIRECTORY_NAME).toString();
    }

    private Configuration createOrLoadConfiguration() throws IOException {
        Configuration configuration = loadConfiguration();
        return configuration == null ? createConfiguration() : configuration;
    }

    private Configuration loadConfiguration() throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Path path = getConfigurationFilePath();
        return Files.exists(path) ? objectMapper.readValue(path.toFile(), Configuration.class) : null;
    }

    private Path getConfigurationFilePath() {
        return Paths.get(workingDirectory, CONFIG_FILE_NAME);
    }

    private Configuration createConfiguration() throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Configuration configuration = new Configuration();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.writeValue(getConfigurationFilePath().toFile(), configuration);
        return configuration;
    }

    private List<DataSource> resolveUsedDataSources() {
        final List<DataSource> dataSources = new ArrayList<>();
        final DataSourceLoader loader = new DataSourceLoader();
        for (final DataSource dataSource : loader.getDataSources())
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
        if (prepareDataSources() && LOGGER.isInfoEnabled()) {
            LOGGER.info(createStateTable(verbose));
            final List<String> notUpToDate = dataSources.stream().filter(d -> !d.isUpToDate()).map(DataSource::getId)
                                                        .collect(Collectors.toList());
            final int countUpToDate = dataSources.size() - notUpToDate.size();
            LOGGER.info((countUpToDate == dataSources.size()) ? "All data sources are up-to-date." :
                        countUpToDate + "/" + dataSources.size() + " data sources are up-to-date.");
            LOGGER.info("Data sources to be updated: " + StringUtils.join(notUpToDate, ", "));
        }
    }

    private String createStateTable(final boolean verbose) {
        final List<String> headers = new ArrayList<>();
        Collections.addAll(headers, "SourceID", "Version is up-to-date", "Version", "new Version",
                           "Time of latest update");
        if (verbose)
            headers.add("Files");
        final List<List<String>> rows = new ArrayList<>();
        for (final DataSource dataSource : dataSources)
            rows.add(createDataSourceStateRow(dataSource, verbose));
        return new TableFormatter().format(headers, rows);
    }

    private List<String> createDataSourceStateRow(final DataSource dataSource, final boolean verbose) {
        final List<String> row = new ArrayList<>();
        final DataSourceMetadata metadata = dataSource.getMetadata();
        final Version latestVersion = dataSource.getNewestVersion();
        Collections.addAll(row, dataSource.getId(), String.valueOf(dataSource.isUpToDate()),
                           metadata.version.toString(), latestVersion == null ? null : latestVersion.toString(),
                           metadata.getLocalUpdateDateTime().toString());
        if (verbose)
            row.add(StringUtils.join(metadata.sourceFileNames, ", "));
        return row;
    }

    private boolean prepareDataSources() {
        boolean success = true;
        for (final DataSource dataSource : dataSources)
            success = success && prepareDataSource(dataSource);
        return true;
    }

    private boolean prepareDataSource(final DataSource dataSource) {
        try {
            dataSource.prepare(this);
            return true;
        } catch (DataSourceException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Failed to prepare data source '" + dataSource.getId() + "'", e);
            return false;
        }
    }

    public void processDataSources(final String dataSourceId, final String version, final boolean skipUpdate) {
        if (prepareDataSources()) {
            for (final DataSource dataSource : dataSources)
                if (dataSourceId == null || dataSource.getId().equals(dataSourceId))
                    processDataSource(dataSource, version, skipUpdate);
            mergeDataSources();
        }
    }

    private void processDataSource(final DataSource dataSource, final String version, final boolean skipUpdate) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Processing of data source '" + dataSource.getId() + "' started");
        Updater.UpdateState updateState;
        if (skipUpdate) {
            updateState = Updater.UpdateState.ALREADY_UP_TO_DATE;
            if (dataSource.getMetadata().updateSuccessful == null) {
                if (LOGGER.isErrorEnabled())
                    LOGGER.error("Update was skipped for data source '" + dataSource.getId() +
                                 "' without successful previous update.");
                return;
            }
        } else {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Running updater");
            updateState = version == null ? dataSource.updateAutomatic(this) : dataSource.updateManually(this, version);
            dataSource.trySaveMetadata(this);
        }
        if (isExportNeeded(updateState, dataSource)) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Running parser");
            dataSource.parse(this);
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Running exporter");
            dataSource.export(this);
        } else if (LOGGER.isInfoEnabled())
            LOGGER.info("Skipping export of data source '" + dataSource.getId() + "' because nothing changed");
        dataSource.trySaveMetadata(this);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Processing of data source '" + dataSource.getId() + "' finished");
    }

    private boolean isExportNeeded(final Updater.UpdateState updateState, final DataSource dataSource) {
        if (updateState == Updater.UpdateState.UPDATED || isDataSourceExportForced(dataSource))
            return true;
        final DataSourceMetadata metadata = dataSource.getMetadata();
        return metadata.exportSuccessful == null || !metadata.exportSuccessful || fileDoesNotExist(
                dataSource.getIntermediateGraphFilePath(this, GraphFileFormat.GRAPH_ML)) || fileDoesNotExist(
                dataSource.getGraphDatabaseFilePath(this));
    }

    private boolean isDataSourceExportForced(final DataSource dataSource) {
        final String id = dataSource.getId();
        return configuration.dataSourceProperties.containsKey(id) && "true".equalsIgnoreCase(
                configuration.dataSourceProperties.get(id).getOrDefault("forceExport", ""));
    }

    private boolean fileDoesNotExist(final String filePath) {
        return Files.notExists(Paths.get(filePath));
    }

    private void mergeDataSources() {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Merging of data sources started");
        try {
            new GraphMerger().merge(this, dataSources, getMergedOutputFilePath(GraphFileFormat.GRAPH_ML));
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Merging of data sources finished");
        } catch (MergerException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Failed to merge GraphML data sources", e);
        }
        new GraphMapper().map(this, dataSources, getMergedOutputFilePath(GraphFileFormat.GRAPH_ML),
                              getMappedOutputFilePath(GraphFileFormat.GRAPH_ML));
    }

    @SuppressWarnings("SameParameterValue")
    private String getMergedOutputFilePath(final GraphFileFormat format) {
        return Paths.get(getSourcesDirectory(), "merged." + format.extension).toString();
    }

    @SuppressWarnings("SameParameterValue")
    private String getMappedOutputFilePath(final GraphFileFormat format) {
        return Paths.get(getSourcesDirectory(), "mapped." + format.extension).toString();
    }
}
