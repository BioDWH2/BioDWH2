package de.unibi.agbi.biodwh2.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.core.exceptions.*;
import de.unibi.agbi.biodwh2.core.model.DataSourceFileType;
import de.unibi.agbi.biodwh2.core.model.DataSourceMetadata;
import de.unibi.agbi.biodwh2.core.model.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public abstract class DataSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSource.class);
    private static final String SOURCE_DIRECTORY_NAME = "source";

    private DataSourceMetadata metadata;

    public final DataSourceMetadata getMetadata() {
        return metadata;
    }

    public abstract String getId();

    public String getFullName() {
        return "-";
    }

    public String getDescription() {
        return "-";
    }

    public abstract DevelopmentState getDevelopmentState();

    protected abstract Updater<? extends DataSource> getUpdater();

    protected abstract Parser<? extends DataSource> getParser();

    protected abstract GraphExporter<? extends DataSource> getGraphExporter();

    public abstract MappingDescriber getMappingDescriber();

    final void prepare(final Workspace workspace) throws DataSourceException {
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
        final Path path = getFilePath(workspace, DataSourceFileType.METADATA);
        if (Files.exists(path))
            metadata = loadMetadata(path);
        if (metadata == null) {
            metadata = new DataSourceMetadata();
            saveMetadata(workspace);
        }
    }

    public final Path getFilePath(final Workspace workspace, final DataSourceFileType type) {
        return Paths.get(workspace.getSourcesDirectory(), getId(), type.getName());
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
        final Path path = getFilePath(workspace, DataSourceFileType.METADATA);
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
            final GraphExporter<? extends DataSource> graphExporter = getGraphExporter();
            metadata.exportSuccessful = graphExporter.export(workspace);
            if (!metadata.exportSuccessful) {
                if (LOGGER.isErrorEnabled())
                    LOGGER.error("Failed to export data source '" + getId() + "'");
            } else {
                metadata.exportVersion = graphExporter.getExportVersion();
                if (LOGGER.isInfoEnabled())
                    LOGGER.info("Successfully exported data source '" + getId() + "'");
            }
        } catch (ExporterException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Failed to export data source '" + getId() + "' in GraphML format", e);
            metadata.exportSuccessful = false;
        }
    }

    protected abstract void unloadData();

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

    public final Map<String, String> getProperties(final Workspace workspace) {
        return workspace.getConfiguration().getDataSourceProperties(getId());
    }
}
