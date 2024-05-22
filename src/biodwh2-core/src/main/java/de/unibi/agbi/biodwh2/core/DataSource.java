package de.unibi.agbi.biodwh2.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.core.exceptions.*;
import de.unibi.agbi.biodwh2.core.mapping.OpenArchivesInitiative;
import de.unibi.agbi.biodwh2.core.model.DataSourceFileType;
import de.unibi.agbi.biodwh2.core.model.DataSourceMetadata;
import de.unibi.agbi.biodwh2.core.model.Version;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class DataSource {
    private static final Logger LOGGER = LogManager.getLogger(DataSource.class);
    private static final String SOURCE_DIRECTORY_NAME = "source";

    private DataSourceMetadata metadata;

    public final DataSourceMetadata getMetadata() {
        return metadata;
    }

    public abstract String getId();

    public final String getOAIId() {
        return OpenArchivesInitiative.buildIdentifier(OpenArchivesInitiative.BIODWH2_NAMESPACE,
                                                      "datasource/" + getId());
    }

    public String getFullName() {
        return "";
    }

    public String getDescription() {
        return "";
    }

    public String getWebsite() {
        return null;
    }

    public String getLicense() {
        return "";
    }

    public String getLicenseUrl() {
        return null;
    }

    public String[] getDependencies() {
        return null;
    }

    public abstract DevelopmentState getDevelopmentState();

    protected abstract Updater<? extends DataSource> getUpdater();

    protected Parser<? extends DataSource> getParser() {
        return new PassThroughParser<>(this);
    }

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
        Files.createDirectories(workspace.getDataSourceDirectory(getId()));
        Files.createDirectories(getSourceFolderPath(workspace));
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

    public final Path getFilePath(final BaseWorkspace workspace, final DataSourceFileType type) {
        return workspace.getDataSourceDirectory(getId()).resolve(type.getName());
    }

    public DataSourceMetadata loadMetadata(final Path filePath) {
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
            LOGGER.error("Data source '" + getId() + "' can only be updated manually. For help visit " +
                         "https://github.com/BioDWH2/BioDWH2/blob/master/doc/usage.md");
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

    final boolean parse(final Workspace workspace) {
        try {
            metadata.parseSuccessful = getParser().parse(workspace);
        } catch (ParserException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Failed to parse data source '" + getId() + "'", e);
            metadata.parseSuccessful = false;
        }
        trySaveMetadata(workspace);
        return metadata.parseSuccessful;
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
                metadata.exportPropertiesHash = workspace.getConfiguration().getDataSourcePropertiesHash(getId());
                if (LOGGER.isInfoEnabled())
                    LOGGER.info("Successfully exported data source '" + getId() + "'");
            }
        } catch (ExporterException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Failed to export data source '" + getId() + "'", e);
            metadata.exportSuccessful = false;
        }
    }

    protected void unloadData() {
    }

    public final Path resolveSourceFilePath(final Workspace workspace, final String filePath) {
        return getSourceFolderPath(workspace).resolve(filePath);
    }

    public final String[] listSourceFiles(final Workspace workspace) {
        final Path sourcePath = getSourceFolderPath(workspace);
        try {
            return Files.walk(sourcePath).filter(Files::isRegularFile).map(sourcePath::relativize).map(Path::toString)
                        .toArray(String[]::new);
        } catch (IOException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Failed to list files of data source '" + getId() + "'", e);
        }
        return new String[0];
    }

    public final Path getSourceFolderPath(final Workspace workspace) {
        return workspace.getDataSourceDirectory(getId()).resolve(SOURCE_DIRECTORY_NAME);
    }

    public final boolean isUpToDate(final Workspace workspace) {
        return getUpdater().isDataSourceUpToDate(workspace);
    }

    public final Version getNewestVersion(final Workspace workspace) {
        return getUpdater().tryGetNewestVersion(workspace);
    }

    public Map<String, DataSourcePropertyType> getAvailableProperties() {
        final Map<String, DataSourcePropertyType> result = new HashMap<>();
        result.put("forceExport", DataSourcePropertyType.BOOLEAN);
        result.put("speciesFilter", DataSourcePropertyType.INTEGER_LIST);
        return result;
    }

    public final Map<String, Object> getProperties(final Workspace workspace) {
        return workspace.getConfiguration().getDataSourceProperties(getId());
    }

    public final <T> T getProperty(final Workspace workspace, final String key) {
        //noinspection unchecked
        return (T) workspace.getConfiguration().getDataSourceProperties(getId()).get(key);
    }

    public final String getStringProperty(final Workspace workspace, final String key) {
        final var value = getProperties(workspace).get(key);
        return value != null ? value.toString() : null;
    }

    public final Boolean getBooleanProperty(final Workspace workspace, final String key) {
        return getBooleanProperty(workspace, key, false);
    }

    public final Boolean getBooleanProperty(final Workspace workspace, final String key, final Boolean fallback) {
        final var value = getProperties(workspace).get(key);
        if (value == null)
            return fallback;
        if (value instanceof Boolean)
            return (Boolean) value;
        if (value instanceof Integer)
            return (Integer) value == 1;
        if (value instanceof Long)
            return (Long) value == 1;
        return "true".equalsIgnoreCase(value.toString());
    }

    public final Integer getIntegerProperty(final Workspace workspace, final String key) {
        return getIntegerProperty(workspace, key, null);
    }

    public final Integer getIntegerProperty(final Workspace workspace, final String key, final Integer fallback) {
        final var value = getProperties(workspace).get(key);
        if (value == null)
            return fallback;
        if (value instanceof Integer)
            return (Integer) value;
        try {
            return Integer.parseInt(value.toString());
        } catch (Exception ignored) {
        }
        return fallback;
    }

    public final Double getDoubleProperty(final Workspace workspace, final String key) {
        return getDoubleProperty(workspace, key, null);
    }

    public final Double getDoubleProperty(final Workspace workspace, final String key, Double fallback) {
        final var value = getProperties(workspace).get(key);
        if (value == null)
            return fallback;
        if (value instanceof Integer)
            return (double) (Integer) value;
        if (value instanceof Long)
            return (double) (Long) value;
        if (value instanceof Float)
            return (double) (Float) value;
        if (value instanceof Double)
            return (double) (Double) value;
        try {
            return Double.parseDouble(value.toString());
        } catch (Exception ignored) {
        }
        return fallback;
    }

    void setVersion(final Workspace workspace, final Version version) {
        metadata.version = version;
        metadata.setUpdateDateTimeNow();
        metadata.sourceFileNames = new ArrayList<>();
        Collections.addAll(metadata.sourceFileNames, listSourceFiles(workspace));
        metadata.updateSuccessful = true;
        trySaveMetadata(workspace);
    }
}
