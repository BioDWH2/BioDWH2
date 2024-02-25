package de.unibi.agbi.biodwh2.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unibi.agbi.biodwh2.core.model.Configuration;
import de.unibi.agbi.biodwh2.core.model.WorkspaceFileType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BaseWorkspace {
    public static final int VERSION = 1;
    public static final String SOURCES_DIRECTORY_NAME = "sources";
    public static final String CONFIG_FILE_NAME = "config.json";

    protected final Path workingDirectory;

    public BaseWorkspace(String workingDirectory) {
        this.workingDirectory = Paths.get(workingDirectory);
    }

    public boolean exists() {
        return Files.exists(workingDirectory) && Files.exists(getConfigurationFilePath());
    }

    public Path getConfigurationFilePath() {
        return workingDirectory.resolve(CONFIG_FILE_NAME);
    }

    public Path getSourcesDirectory() {
        return workingDirectory.resolve(SOURCES_DIRECTORY_NAME);
    }

    public Path getDataSourceDirectory(final String id) {
        return getSourcesDirectory().resolve(id);
    }

    public Path getFilePath(final WorkspaceFileType type) {
        return getSourcesDirectory().resolve(type.getName());
    }

    public Configuration loadConfiguration() throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Path path = getConfigurationFilePath();
        return Files.exists(path) ? objectMapper.readValue(path.toFile(), Configuration.class) : null;
    }
}
