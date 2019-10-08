package de.unibi.agbi.biodwh2.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.etl.RDFExporter;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.model.DataSourceMetadata;
import de.unibi.agbi.biodwh2.core.model.graph.GraphFileFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class DataSource {
    private static final Logger logger = LoggerFactory.getLogger(DataSource.class);

    private DataSourceMetadata metadata;

    public DataSourceMetadata getMetadata() {
        return metadata;
    }

    public abstract String getId();

    public abstract Updater getUpdater();

    public abstract Parser getParser();

    public abstract RDFExporter getRdfExporter();

    public abstract GraphExporter getGraphExporter();

    void createDirectoryIfNotExists(Workspace workspace) throws IOException {
        Files.createDirectories(Paths.get(workspace.getSourcesDirectory(), getId()));
        Files.createDirectories(Paths.get(workspace.getSourcesDirectory(), getId(), "source"));
    }

    void createOrLoadMetadata(Workspace workspace) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Path path = Paths.get(workspace.getSourcesDirectory(), getId(), "metadata.json");
        if (Files.exists(path))
            metadata = objectMapper.readValue(path.toFile(), DataSourceMetadata.class);
        else {
            metadata = new DataSourceMetadata();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.writeValue(path.toFile(), metadata);
        }
    }

    public final String getIntermediateGraphFilePath(Workspace workspace, GraphFileFormat format) {
        String fileName = "intermediate." + format.extension;
        return Paths.get(workspace.getSourcesDirectory(), getId(), fileName).toString();
    }

    public final String resolveSourceFilePath(Workspace workspace, String filePath) {
        return Paths.get(workspace.getSourcesDirectory(), getId(), "source", filePath).toString();
    }

    public final List<String> listSourceFiles(Workspace workspace) {
        Path sourcePath = Paths.get(workspace.getSourcesDirectory(), getId(), "source");
        try {
            return Files.walk(sourcePath).filter(Files::isRegularFile).map(sourcePath::relativize).map(Path::toString)
                        .collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("Failed to list files of data source '" + getId() + "'", e);
        }
        return new ArrayList<>();
    }

    public final void saveMetadata(Workspace workspace) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Path path = Paths.get(workspace.getSourcesDirectory(), getId(), "metadata.json");
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.writeValue(path.toFile(), metadata);
    }
}
