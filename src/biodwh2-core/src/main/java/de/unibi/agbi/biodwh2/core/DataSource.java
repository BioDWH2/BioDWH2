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
    private static final Logger logger = LoggerFactory.getLogger(DataSource.class);
    private static final String SourceDirectoryName = "source";
    private static final String MetadataFileName = "metadata.json";
    private static final String PersistentGraphFileName = "graphdb.sqlite";

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
        Files.createDirectories(Paths.get(workspace.getSourcesDirectory(), getId(), SourceDirectoryName));
    }

    private void createOrLoadMetadata(final Workspace workspace) throws IOException {
        Path path = Paths.get(workspace.getSourcesDirectory(), getId(), MetadataFileName);
        if (Files.exists(path)) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                metadata = objectMapper.readValue(path.toFile(), DataSourceMetadata.class);
                if (metadata != null)
                    return;
            } catch (IOException e) {
                logger.warn("Failed to load data source '" + getId() + "' metadata. Creating a new one.", e);
            }
        }
        metadata = new DataSourceMetadata();
        saveMetadata(workspace);
    }

    private void saveMetadata(final Workspace workspace) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Path path = Paths.get(workspace.getSourcesDirectory(), getId(), MetadataFileName);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.writeValue(path.toFile(), metadata);
    }

    final void trySaveMetadata(final Workspace workspace) {
        try {
            saveMetadata(workspace);
        } catch (IOException e) {
            logger.error("Failed to save metadata for data source '" + getId() + "'", e);
        }
    }

    final void updateAutomatic(final Workspace workspace) {
        try {
            //noinspection unchecked
            metadata.updateSuccessful = getUpdater().update(workspace, this);
        } catch (UpdaterOnlyManuallyException e) {
            logger.error("Data source '" + getId() + "' can only be updated manually. Download the new version of " +
                         getId() + " and use the command line parameter -u or --update with the parameters " +
                         "<workspacePath> \"" + getId() + "\" <version>.\n" +
                         "Help: https://github.com/AstrorEnales/BioDWH2/blob/develop/doc/usage.md");
        } catch (UpdaterException e) {
            logger.error("Failed to update data source '" + getId() + "'", e);
            metadata.updateSuccessful = false;
        }
    }

    final void updateManually(final Workspace workspace, final String version) {
        //noinspection unchecked
        metadata.updateSuccessful = getUpdater().updateManually(workspace, this, version);
    }

    final void parse(final Workspace workspace) {
        try {
            //noinspection unchecked
            metadata.parseSuccessful = getParser().parse(workspace, this);
        } catch (ParserException e) {
            logger.error("Failed to parse data source '" + getId() + "'", e);
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
            logger.error("Failed to export data source '" + getId() + "' in RDF format", e);
            metadata.exportRDFSuccessful = false;
        }
    }

    private void exportGraphML(final Workspace workspace) {
        try {
            //noinspection unchecked
            metadata.exportGraphMLSuccessful = getGraphExporter().export(workspace, this);
        } catch (ExporterException e) {
            logger.error("Failed to export data source '" + getId() + "' in GraphML format", e);
            metadata.exportGraphMLSuccessful = false;
        }
    }

    protected abstract void unloadData();

    public final String getGraphDatabaseFilePath(final Workspace workspace) {
        return Paths.get(workspace.getSourcesDirectory(), getId(), PersistentGraphFileName).toString();
    }

    public final String getIntermediateGraphFilePath(final Workspace workspace, final GraphFileFormat format) {
        String fileName = "intermediate." + format.extension;
        return Paths.get(workspace.getSourcesDirectory(), getId(), fileName).toString();
    }

    public final String getIntermediateGraphFilePath(final Workspace workspace, final GraphFileFormat format,
                                                     final int part) {
        String fileName = "intermediate_part" + part + "." + format.extension;
        return Paths.get(workspace.getSourcesDirectory(), getId(), fileName).toString();
    }

    public final String resolveSourceFilePath(final Workspace workspace, final String filePath) {
        return Paths.get(workspace.getSourcesDirectory(), getId(), SourceDirectoryName, filePath).toString();
    }

    public final String[] listSourceFiles(final Workspace workspace) {
        Path sourcePath = Paths.get(workspace.getSourcesDirectory(), getId(), SourceDirectoryName);
        try {
            return Files.walk(sourcePath).filter(Files::isRegularFile).map(sourcePath::relativize).map(Path::toString)
                        .toArray(String[]::new);
        } catch (IOException e) {
            logger.error("Failed to list files of data source '" + getId() + "'", e);
        }
        return new String[0];
    }
}
