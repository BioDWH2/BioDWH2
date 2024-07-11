package de.unibi.agbi.biodwh2.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.unibi.agbi.biodwh2.core.etl.GraphMapper;
import de.unibi.agbi.biodwh2.core.etl.GraphMerger;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.DataSourceException;
import de.unibi.agbi.biodwh2.core.exceptions.MergerException;
import de.unibi.agbi.biodwh2.core.exceptions.WorkspaceException;
import de.unibi.agbi.biodwh2.core.io.graph.OutputFormatWriter;
import de.unibi.agbi.biodwh2.core.model.Configuration;
import de.unibi.agbi.biodwh2.core.model.DataSourceFileType;
import de.unibi.agbi.biodwh2.core.model.DataSourceMetadata;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.migration.GraphMigrator;
import de.unibi.agbi.biodwh2.core.text.TableFormatter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Workspace extends BaseWorkspace {
    private static final Logger LOGGER = LogManager.getLogger(Workspace.class);

    private final Configuration configuration;
    private final DataSource[] dataSources;
    private final OutputFormatWriter[] outputFormatWriters;

    public Workspace(final String workingDirectory) {
        super(workingDirectory);
        createWorkingDirectoryIfNotExists();
        configuration = createOrLoadConfiguration();
        dataSources = getUsedDataSources();
        outputFormatWriters = getUsedOutputFormatWriters();
    }

    private void createWorkingDirectoryIfNotExists() {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Using workspace directory '{}'", workingDirectory);
        try {
            Files.createDirectories(workingDirectory);
            Files.createDirectories(getSourcesDirectory());
        } catch (IOException e) {
            throw new WorkspaceException("Failed to create workspace directories", e);
        }
    }

    private Configuration createOrLoadConfiguration() {
        try {
            final Configuration configuration = loadConfiguration();
            return configuration == null ? createConfiguration() : configuration;
        } catch (IOException e) {
            throw new WorkspaceException("Failed to load or create workspace configuration", e);
        }
    }

    private Configuration createConfiguration() throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Configuration configuration = new Configuration();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.writeValue(getConfigurationFilePath().toFile(), configuration);
        return configuration;
    }

    private DataSource[] getUsedDataSources() {
        final List<String> dataSourceIds = Arrays.asList(configuration.getDataSourceIds());
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Using data sources: {}", StringUtils.join(dataSourceIds, ", "));
        final DataSource[] result = DataSourceLoader.getInstance().getDataSources(configuration.getDataSourceIds());
        if (result.length < configuration.getNumberOfDataSources()) {
            throw new WorkspaceException("Failed to load all data sources. Please ensure the configured data source " +
                                         "IDs are valid and all data source modules are available in the classpath.");
        }
        return result;
    }

    private OutputFormatWriter[] getUsedOutputFormatWriters() {
        final List<String> outputFormatIds = Arrays.asList(configuration.getOutputFormatIds());
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Using output formats: {}", StringUtils.join(outputFormatIds, ", "));
        final OutputFormatWriter[] result = OutputFormatWriterLoader.getInstance().getOutputFormatWriters(
                configuration.getOutputFormatIds());
        if (result.length < configuration.getNumberOfOutputFormats()) {
            throw new WorkspaceException(
                    "Failed to load all output format writers. Please ensure the configured output format " +
                    "IDs are valid and all output format modules are available in the classpath.");
        }
        return result;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void checkState(final boolean verbose) {
        if (prepareDataSources() && LOGGER.isInfoEnabled()) {
            LOGGER.info(createStateTable(verbose));
            final List<String> notUpToDate = Arrays.stream(dataSources).filter(d -> !d.isUpToDate(this)).map(
                    DataSource::getId).collect(Collectors.toList());
            final int countUpToDate = dataSources.length - notUpToDate.size();
            LOGGER.info((countUpToDate == dataSources.length) ? "All data sources are up-to-date." :
                        countUpToDate + "/" + dataSources.length + " data sources are up-to-date.");
            if (!notUpToDate.isEmpty())
                LOGGER.info("Data sources to be updated: {}", StringUtils.join(notUpToDate, ", "));
        }
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
                LOGGER.error("Failed to prepare data source '{}'", dataSource.getId(), e);
            return false;
        }
    }

    private String createStateTable(final boolean verbose) {
        final List<String> headers = new ArrayList<>();
        Collections.addAll(headers, "SourceID", "Version is up-to-date", "Version", "new Version",
                           "Time of latest update", "Parse successful", "Export successful");
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
        final Version latestVersion = dataSource.getNewestVersion(this);
        final LocalDateTime updateDateTime = metadata.getLocalUpdateDateTime();
        Collections.addAll(row, dataSource.getId(), dataSource.isUpToDate(this) ? "true" : "-",
                           metadata.version == null ? "-" : metadata.version.toString(),
                           latestVersion == null ? "-" : latestVersion.toString(),
                           updateDateTime == null ? "-" : updateDateTime.toString(),
                           metadata.parseSuccessful == null ? "-" : "true",
                           metadata.exportSuccessful == null ? "-" : "true");
        if (verbose)
            row.add(StringUtils.join(metadata.sourceFileNames, ", "));
        return row;
    }

    public void processDataSources(final boolean skipUpdate) {
        if (configuration.getDataSourceIds().length == 0)
            throw new WorkspaceException("No data sources have been selected. Please ensure that data source IDs " +
                                         "have been added to the workspace config.json either directly or via " +
                                         "command line.");
        if (prepareDataSources()) {
            LOGGER.info("Processing data sources sequentially");
            final long start = System.currentTimeMillis();
            for (final DataSource dataSource : dataSources)
                processDataSource(dataSource, skipUpdate);
            final long stop = System.currentTimeMillis();
            LOGGER.info("Finished processing data sources within {}",
                        DurationFormatUtils.formatDuration(stop - start, "HH:mm:ss.S"));
            mergeDataSources();
            mapDataSources(false, 1);
        }
    }

    public void processDataSourcesInParallel(final boolean skipUpdate, final int numThreads) {
        if (configuration.getDataSourceIds().length == 0)
            throw new WorkspaceException("No data sources have been selected. Please ensure that data source IDs " +
                                         "have been added to the workspace config.json either directly or via " +
                                         "command line.");
        if (prepareDataSources()) {
            ForkJoinPool threadPool = null;
            try {
                // init new thread pool for processing
                LOGGER.info("Processing data sources in parallel with " + numThreads + " threads");
                threadPool = new ForkJoinPool(numThreads);
                final long start = System.currentTimeMillis();
                threadPool.submit(() -> Stream.of(dataSources).parallel().forEach(dataSource -> {
                    // submit task to pool
                    processDataSource(dataSource, skipUpdate);
                })).get();
                final long stop = System.currentTimeMillis();
                LOGGER.info("Finished processing data sources within {}",
                            DurationFormatUtils.formatDuration(stop - start, "HH:mm:ss.S"));
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("Failed to process data sources in parallel", e);
            } finally {
                // shutting down thread pool (execution continued in common pool)
                if (threadPool != null)
                    threadPool.shutdown();
            }
            mergeDataSources();
            mapDataSources(true, numThreads);
        }

    }

    private void processDataSource(final DataSource dataSource, final boolean skipUpdate) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Processing of data source '{}' started", dataSource.getId());
        Updater.UpdateState updateState;
        if (skipUpdate) {
            updateState = Updater.UpdateState.ALREADY_UP_TO_DATE;
            if (dataSource.getMetadata().updateSuccessful == null) {
                if (LOGGER.isErrorEnabled())
                    LOGGER.error("Update was skipped for data source '{}' without successful previous update.",
                                 dataSource.getId());
                return;
            }
        } else {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Running updater");
            updateState = dataSource.updateAutomatic(this);
        }
        if (isDataSourceExportNeeded(updateState, dataSource)) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Running parser");
            if (dataSource.parse(this)) {
                if (LOGGER.isInfoEnabled())
                    LOGGER.info("Running exporter");
                dataSource.export(this);
            }
        } else if (LOGGER.isInfoEnabled())
            LOGGER.info("Skipping export of data source '{}' because nothing changed", dataSource.getId());
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Processing of data source '{}' finished", dataSource.getId());
    }

    private boolean isDataSourceExportNeeded(final Updater.UpdateState updateState, final DataSource dataSource) {
        if (updateState == Updater.UpdateState.UPDATED || isDataSourceExportForced(dataSource))
            return true;
        final DataSourceMetadata metadata = dataSource.getMetadata();
        return metadata.exportSuccessful == null || !metadata.exportSuccessful || areDataSourceExportsMissing(
                dataSource) || isDataSourceExportOutdated(dataSource, metadata) || isExportedGraphVersionOutdated(
                dataSource);
    }

    public boolean isDataSourceExportForced(final DataSource dataSource) {
        return dataSource.getBooleanProperty(this, "forceExport", false);
    }

    private boolean areDataSourceExportsMissing(final DataSource dataSource) {
        return Files.notExists(dataSource.getFilePath(this, DataSourceFileType.PERSISTENT_GRAPH));
        // TODO: check output format writers
    }

    /**
     * An export is outdated whenever:
     * <ul>
     *   <li>no prior export version exists</li>
     *   <li>the exporter uses a higher export version</li>
     *   <li>no prior export properties hash exists</li>
     *   <li>the properties hash has changed.</li>
     * </ul>
     */
    private boolean isDataSourceExportOutdated(final DataSource dataSource, final DataSourceMetadata metadata) {
        return metadata.exportVersion == null ||
               metadata.exportVersion < dataSource.getGraphExporter().getExportVersion() ||
               metadata.exportPropertiesHash == null || !Objects.equals(
                configuration.getDataSourcePropertiesHash(dataSource.getId()), metadata.exportPropertiesHash);
    }

    private boolean isExportedGraphVersionOutdated(final DataSource dataSource) {
        final Path filePath = dataSource.getFilePath(this, DataSourceFileType.PERSISTENT_GRAPH);
        if (!filePath.toFile().exists())
            return true;
        final Integer exportedVersion = GraphMigrator.peekVersion(filePath);
        return exportedVersion == null || Graph.VERSION > exportedVersion;
    }

    private void mergeDataSources() {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Merging of data sources started");
        try {
            new GraphMerger().merge(this, dataSources);
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Merging of data sources finished");
        } catch (MergerException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Merging of data sources failed", e);
        }
    }

    private void mapDataSources(final boolean runsInParallel, final int numThreads) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Mapping of data sources started");

        new GraphMapper().map(this, dataSources, runsInParallel, numThreads);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Mapping of data sources finished");
    }

    public void addDataSource(final String dataSourceId) {
        configuration.addDataSource(dataSourceId);
    }

    public void removeDataSource(final String dataSourceId) {
        configuration.removeDataSource(dataSourceId);
    }

    public void setDataSourceVersion(final String dataSourceId, final String dataSourceVersion) {
        final Version version = Version.tryParse(dataSourceVersion);
        if (version == null) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error(
                        "Failed to set data source '{}' version to '{}', as the provided version did not match the format 'w.x[.y[.z]]'.",
                        dataSourceId, dataSourceVersion);
            return;
        }
        final Optional<DataSource> dataSourceMatch = Arrays.stream(dataSources).filter(
                d -> d.getId().equals(dataSourceId)).findFirst();
        if (dataSourceMatch.isPresent()) {
            final DataSource dataSource = dataSourceMatch.get();
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Setting data source '{}' version to '{}'.", dataSource.getId(), dataSourceVersion);
            prepareDataSource(dataSource);
            dataSource.setVersion(this, version);
        } else if (LOGGER.isErrorEnabled())
            LOGGER.error(
                    "Failed to set data source '{}' version to '{}', as the data source module could not be found. Is the data source ID correct?",
                    dataSourceId, dataSourceVersion);
    }

    public void saveConfiguration() throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Path path = getConfigurationFilePath();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), configuration);
    }

    public OutputFormatWriter[] getOutputFormatWriters() {
        return outputFormatWriters;
    }
}
