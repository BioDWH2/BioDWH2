package de.unibi.agbi.biodwh2.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.core.exceptions.*;
import de.unibi.agbi.biodwh2.core.model.DataSourceMetadata;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.model.graph.GraphFileFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class DataSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSource.class);
    private static final String SOURCE_DIRECTORY_NAME = "source";
    private static final String METADATA_FILE_NAME = "metadata.json";
    private static final String PERSISTENT_GRAPH_FILE_NAME = "intermediate.db";

    private DataSourceMetadata metadata;

    public DataSourceMetadata getMetadata() {
        return metadata;
    }

    public abstract String getId();

    protected abstract Updater<? extends DataSource> getUpdater();

    protected abstract Parser<? extends DataSource> getParser();

    protected abstract GraphExporter<? extends DataSource> getGraphExporter();

    public abstract MappingDescriber getMappingDescriber();

    void prepare(final Workspace workspace) throws DataSourceException {
        try {
            createFolderStructureIfNotExists(workspace);
            createOrLoadMetadata(workspace);
        } catch (IOException e) {
            throw new DataSourceException("Failed to prepare data source '" + getId() + "'", e);
        }
    }

    private void createFolderStructureIfNotExists(final Workspace workspace) throws IOException {
        Files.createDirectories(Paths.get(workspace.getSourcesDirectory(), getId()));
        Files.createDirectories(Paths.get(workspace.getSourcesDirectory(), getId(), SOURCE_DIRECTORY_NAME));
    }

    private void createOrLoadMetadata(final Workspace workspace) throws IOException {
        final Path path = Paths.get(workspace.getSourcesDirectory(), getId(), METADATA_FILE_NAME);
        if (Files.exists(path))
            metadata = loadMetadata(path);
        if (metadata == null) {
            metadata = new DataSourceMetadata();
            saveMetadata(workspace);
        }
    }

    private DataSourceMetadata loadMetadata(final Path filePath) {
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(filePath.toFile(), DataSourceMetadata.class);
        } catch (UnrecognizedPropertyException e) {
            if (LOGGER.isWarnEnabled())
                LOGGER.warn(e.getMessage());
        } catch (IOException e) {
            if (LOGGER.isWarnEnabled())
                LOGGER.warn("Failed to load data source '" + getId() + "' metadata. Creating a new one.", e);
        }
        return null;
    }

    private void saveMetadata(final Workspace workspace) throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Path path = Paths.get(workspace.getSourcesDirectory(), getId(), METADATA_FILE_NAME);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.writeValue(path.toFile(), metadata);
    }

    final Updater.UpdateState updateAutomatic(final Workspace workspace) {
        Updater.UpdateState state = Updater.UpdateState.FAILED;
        try {
            state = getUpdater().update(workspace);
            metadata.updateSuccessful = state != Updater.UpdateState.FAILED;
        } catch (UpdaterOnlyManuallyException e) {
            logUpdaterOnlyManuallyException();
        } catch (UpdaterException e) {
            handleUpdateAutomaticFailed(e);
        }
        trySaveMetadata(workspace);
        return state;
    }

    private void logUpdaterOnlyManuallyException() {
        if (LOGGER.isErrorEnabled())
            LOGGER.error("Data source '" + getId() + "' can only be updated manually. Download the new version of " +
                         getId() + " and use the command line parameter -u or --update with the parameters " +
                         "<workspacePath> \"" + getId() + "\" <version>.\n" +
                         "Help: https://github.com/AstrorEnales/BioDWH2/blob/develop/doc/usage.md");
    }

    private void handleUpdateAutomaticFailed(final UpdaterException e) {
        if (LOGGER.isErrorEnabled())
            LOGGER.error("Failed to update data source '" + getId() + "'", e);
        metadata.updateSuccessful = false;
    }

    private void trySaveMetadata(final Workspace workspace) {
        try {
            saveMetadata(workspace);
        } catch (IOException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Failed to save metadata for data source '" + getId() + "'", e);
        }
    }

    final Updater.UpdateState updateManually(final Workspace workspace, final String version) {
        final Updater.UpdateState state = getUpdater().updateManually(workspace, version);
        metadata.updateSuccessful = state != Updater.UpdateState.FAILED;
        trySaveMetadata(workspace);
        return state;
    }

    final void parse(final Workspace workspace) {
        try {
            metadata.parseSuccessful = getParser().parse(workspace);
        } catch (ParserException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Failed to parse data source '" + getId() + "'", e);
            metadata.parseSuccessful = false;
        }
        trySaveMetadata(workspace);
    }

    final void export(final Workspace workspace) {
        exportGraph(workspace);
        unloadData();
        trySaveMetadata(workspace);
    }

    private void exportGraph(final Workspace workspace) {
        try {
            metadata.exportSuccessful = getGraphExporter().export(workspace);
            if (!metadata.exportSuccessful)
                if (LOGGER.isErrorEnabled())
                    LOGGER.error("Failed to export data source '" + getId() + "'");
        } catch (ExporterException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Failed to export data source '" + getId() + "' in GraphML format", e);
            metadata.exportSuccessful = false;
        }
    }

    protected abstract void unloadData();

    public final String getGraphDatabaseFilePath(final Workspace workspace) {
        return Paths.get(workspace.getSourcesDirectory(), getId(), PERSISTENT_GRAPH_FILE_NAME).toString();
    }

    public final String getIntermediateGraphFilePath(final Workspace workspace) {
        final String fileName = "intermediate." + GraphFileFormat.GRAPH_ML.extension;
        return Paths.get(workspace.getSourcesDirectory(), getId(), fileName).toString();
    }

    public final String resolveSourceFilePath(final Workspace workspace, final String filePath) {
        return Paths.get(workspace.getSourcesDirectory(), getId(), SOURCE_DIRECTORY_NAME, filePath).toString();
    }

    public final String[] listSourceFiles(final Workspace workspace) {
        final Path sourcePath = Paths.get(workspace.getSourcesDirectory(), getId(), SOURCE_DIRECTORY_NAME);
        try {
            return Files.walk(sourcePath).filter(Files::isRegularFile).map(sourcePath::relativize).map(Path::toString)
                        .toArray(String[]::new);
        } catch (IOException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Failed to list files of data source '" + getId() + "'", e);
        }
        return new String[0];
    }

    public final boolean isUpToDate() {
        return getUpdater().isDataSourceUpToDate();
    }

    public final Version getNewestVersion() {
        return getUpdater().tryGetNewestVersion();
    }
}
