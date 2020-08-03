package de.unibi.agbi.biodwh2.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.core.exceptions.*;
import de.unibi.agbi.biodwh2.core.model.DataSourceMetadata;
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
    private static final String PERSISTENT_GRAPH_FILE_NAME = "graph.db";

    private DataSourceMetadata metadata;

    public DataSourceMetadata getMetadata() {
        return metadata;
    }

    public abstract String getId();

    public abstract Updater getUpdater();

    protected abstract Parser getParser();

    protected abstract RDFExporter getRdfExporter();

    protected abstract GraphExporter getGraphExporter();

    public abstract MappingDescriber getMappingDescriber();

    void prepare(final Workspace workspace) throws DataSourceException {
        try {
            createDirectoryIfNotExists(workspace);
            createOrLoadMetadata(workspace);
        } catch (IOException e) {
            throw new DataSourceException("Failed to prepare data source '" + getId() + "'", e);
        }
    }

    private void createDirectoryIfNotExists(final Workspace workspace) throws IOException {
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

    final void trySaveMetadata(final Workspace workspace) {
        try {
            saveMetadata(workspace);
        } catch (IOException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Failed to save metadata for data source '" + getId() + "'", e);
        }
    }

    final Updater.UpdateState updateAutomatic(final Workspace workspace) {
        Updater.UpdateState state = Updater.UpdateState.FAILED;
        try {
            //noinspection unchecked
            state = getUpdater().update(workspace, this);
            metadata.updateSuccessful = state != Updater.UpdateState.FAILED;
        } catch (UpdaterOnlyManuallyException e) {
            logUpdaterOnlyManuallyException();
        } catch (UpdaterException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Failed to update data source '" + getId() + "'", e);
            metadata.updateSuccessful = false;
        }
        return state;
    }

    private void logUpdaterOnlyManuallyException() {
        if (LOGGER.isErrorEnabled())
            LOGGER.error("Data source '" + getId() + "' can only be updated manually. Download the new version of " +
                         getId() + " and use the command line parameter -u or --update with the parameters " +
                         "<workspacePath> \"" + getId() + "\" <version>.\n" +
                         "Help: https://github.com/AstrorEnales/BioDWH2/blob/develop/doc/usage.md");
    }

    final Updater.UpdateState updateManually(final Workspace workspace, final String version) {
        //noinspection unchecked
        final Updater.UpdateState state = getUpdater().updateManually(workspace, this, version);
        metadata.updateSuccessful = state != Updater.UpdateState.FAILED;
        return state;
    }

    final void parse(final Workspace workspace) {
        try {
            //noinspection unchecked
            metadata.parseSuccessful = getParser().parse(workspace, this);
        } catch (ParserException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Failed to parse data source '" + getId() + "'", e);
            metadata.parseSuccessful = false;
        }
    }

    final void export(final Workspace workspace, final boolean rdfEnabled, final boolean graphEnabled) {
        if (rdfEnabled)
            exportRdf(workspace);
        if (graphEnabled)
            exportGraphML(workspace);
        unloadData();
    }

    private void exportRdf(final Workspace workspace) {
        try {
            //noinspection unchecked
            metadata.exportRDFSuccessful = getRdfExporter().export(workspace, this);
        } catch (ExporterException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Failed to export data source '" + getId() + "' in RDF format", e);
            metadata.exportRDFSuccessful = false;
        }
    }

    private void exportGraphML(final Workspace workspace) {
        try {
            //noinspection unchecked
            metadata.exportGraphMLSuccessful = getGraphExporter().export(workspace, this);
        } catch (ExporterException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Failed to export data source '" + getId() + "' in GraphML format", e);
            metadata.exportGraphMLSuccessful = false;
        }
    }

    protected abstract void unloadData();

    public final String getGraphDatabaseFilePath(final Workspace workspace) {
        return Paths.get(workspace.getSourcesDirectory(), getId(), PERSISTENT_GRAPH_FILE_NAME).toString();
    }

    public final String getIntermediateGraphFilePath(final Workspace workspace, final GraphFileFormat format) {
        final String fileName = "intermediate." + format.extension;
        return Paths.get(workspace.getSourcesDirectory(), getId(), fileName).toString();
    }

    public final String getIntermediateGraphFilePath(final Workspace workspace, final GraphFileFormat format,
                                                     final int part) {
        final String fileName = "intermediate_part" + part + "." + format.extension;
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
}
