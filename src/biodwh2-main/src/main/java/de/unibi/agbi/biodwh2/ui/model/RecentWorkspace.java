package de.unibi.agbi.biodwh2.ui.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unibi.agbi.biodwh2.core.model.Configuration;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public final class RecentWorkspace {
    private final String path;
    private final boolean valid;

    public RecentWorkspace(final String path) {
        this.path = path;
        this.valid = checkIsValid(path);
    }

    private boolean checkIsValid(final String path) {
        final Path root = Paths.get(path);
        if (root.toFile().exists() && root.toFile().isDirectory()) {
            final Path configFilePath = Paths.get(path, "config.json");
            if (configFilePath.toFile().exists() && configFilePath.toFile().isFile()) {
                final ObjectMapper objectMapper = new ObjectMapper();
                try {
                    objectMapper.readValue(configFilePath.toFile(), Configuration.class);
                    return true;
                } catch (IOException ignored) {
                }
            }
        }
        return false;
    }

    public String getPath() {
        return path;
    }

    public boolean isValid() {
        return valid;
    }

    public boolean isValidForced() {
        return checkIsValid(path);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final RecentWorkspace that = (RecentWorkspace) o;
        return path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
