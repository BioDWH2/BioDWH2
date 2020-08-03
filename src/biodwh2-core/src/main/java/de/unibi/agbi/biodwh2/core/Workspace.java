package de.unibi.agbi.biodwh2.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.unibi.agbi.biodwh2.core.etl.GraphMapper;
import de.unibi.agbi.biodwh2.core.etl.GraphMerger;
import de.unibi.agbi.biodwh2.core.etl.RDFMerger;
import de.unibi.agbi.biodwh2.core.etl.Updater;
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
            LOGGER.info("Export and merge of RDF format is " + (configuration.rdfEnabled ? "enabled" : "disabled"));
            LOGGER.info(
                    "Export and merge of GraphML format is " + (configuration.graphMLEnabled ? "enabled" : "disabled"));
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
        final ObjectMapper objectMapper = new ObjectMapper();
        final Path path = Paths.get(workingDirectory, CONFIG_FILE_NAME);
        if (Files.exists(path))
            return objectMapper.readValue(path.toFile(), Configuration.class);
        final Configuration configuration = new Configuration();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.writeValue(path.toFile(), configuration);
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
            final Map<String, Boolean> sourcesUpToDate = createSourcesUpToDate();
            final int countUpToDate = Collections.frequency(sourcesUpToDate.values(), true);
            final ArrayList<String> notUpToDate = new ArrayList<>();
            for (final String file : sourcesUpToDate.keySet())
                if (!sourcesUpToDate.get(file))
                    notUpToDate.add(file);
            final String loggerInfo = (countUpToDate == dataSources.size()) ? "all source data are up-to-date." :
                                      countUpToDate + "/" + dataSources.size() + " source data are up-to-date.";
            final String state = createStateTable(sourcesUpToDate, verbose);
            LOGGER.info(state);
            LOGGER.info(loggerInfo);
            LOGGER.info("Data sources to be updated: " + notUpToDate);
        }
    }

    private Map<String, Boolean> createSourcesUpToDate() {
        final Map<String, Boolean> sourcesUpToDate = new HashMap<>();
        for (final DataSource dataSource : dataSources) {
            final Version workspaceVersion = dataSource.getMetadata().version;
            final Version newestVersion = getLatestVersion(dataSource);
            final boolean isUpToDate = workspaceVersion != null && newestVersion != null && newestVersion.compareTo(
                    workspaceVersion) == 0;
            sourcesUpToDate.put(dataSource.getId(), isUpToDate);
        }
        return sourcesUpToDate;
    }

    private Version getLatestVersion(final DataSource dataSource) {
        try {
            return dataSource.getUpdater().getNewestVersion();
        } catch (UpdaterException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Failed to get newest version for data source '" + dataSource.getId() + "'");
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
        for (final DataSource dataSource : dataSources) {
            final List<String> row = new ArrayList<>();
            rows.add(row);
            final DataSourceMetadata metadata = dataSource.getMetadata();
            final Version latestVersion = getLatestVersion(dataSource);
            Collections.addAll(row, dataSource.getId(), sourcesUpToDate.get(dataSource.getId()).toString(),
                               metadata.version.toString(), latestVersion == null ? null : latestVersion.toString(),
                               metadata.getLocalUpdateDateTime().toString());
            if (verbose)
                row.add(String.join(", ", metadata.sourceFileNames));
        }
        return new TableFormatter().format(headers, rows);
    }

    private boolean prepareDataSources() {
        for (final DataSource dataSource : dataSources) {
            try {
                dataSource.prepare(this);
            } catch (DataSourceException e) {
                if (LOGGER.isErrorEnabled())
                    LOGGER.error("Failed to prepare data source '" + dataSource.getId() + "'", e);
                return false;
            }
        }
        return true;
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
            dataSource.export(this, configuration.rdfEnabled, configuration.graphMLEnabled);
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
        final boolean exportGraphMLSuccessful =
                metadata.exportGraphMLSuccessful != null && metadata.exportGraphMLSuccessful && fileDoesExist(
                        dataSource.getIntermediateGraphFilePath(this, GraphFileFormat.GRAPH_ML)) && fileDoesExist(
                        dataSource.getGraphDatabaseFilePath(this));
        final boolean exportRDFSuccessful =
                metadata.exportRDFSuccessful != null && metadata.exportRDFSuccessful && fileDoesExist(
                        dataSource.getIntermediateGraphFilePath(this, GraphFileFormat.RDF_TURTLE));
        if (configuration.rdfEnabled && configuration.graphMLEnabled)
            return !exportRDFSuccessful || !exportGraphMLSuccessful;
        if (configuration.rdfEnabled)
            return !exportRDFSuccessful;
        if (configuration.graphMLEnabled)
            return !exportGraphMLSuccessful;
        return false;
    }

    private boolean isDataSourceExportForced(final DataSource dataSource) {
        final String id = dataSource.getId();
        return configuration.dataSourceProperties.containsKey(id) && "true".equalsIgnoreCase(
                configuration.dataSourceProperties.get(id).getOrDefault("forceExport", ""));
    }

    private boolean fileDoesExist(final String filePath) {
        return Files.exists(Paths.get(filePath));
    }

    private void mergeDataSources() {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Merging of data sources started");
        if (configuration.rdfEnabled)
            mergeRDFDataSources();
        if (configuration.graphMLEnabled)
            mergeGraphMLDataSources();
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Merging of data sources finished");
    }

    private void mergeRDFDataSources() {
        try {
            new RDFMerger().merge(this, dataSources, getMergedOutputFilePath(GraphFileFormat.RDF_TURTLE));
        } catch (MergerException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Failed to merge RDF data sources", e);
        }
    }

    private String getMergedOutputFilePath(final GraphFileFormat format) {
        return Paths.get(getSourcesDirectory(), "merged." + format.extension).toString();
    }

    private void mergeGraphMLDataSources() {
        try {
            new GraphMerger().merge(this, dataSources, getMergedOutputFilePath(GraphFileFormat.GRAPH_ML));
            new GraphMapper().map(this, dataSources, getMergedOutputFilePath(GraphFileFormat.GRAPH_ML),
                                  getMappedOutputFilePath(GraphFileFormat.GRAPH_ML));
        } catch (MergerException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Failed to merge GraphML data sources", e);
        }
    }

    private String getMappedOutputFilePath(final GraphFileFormat format) {
        return Paths.get(getSourcesDirectory(), "mapped." + format.extension).toString();
    }
}
