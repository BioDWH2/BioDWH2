package de.unibi.agbi.biodwh2.core.cache;

import de.unibi.agbi.biodwh2.core.model.Version;

import java.util.Map;

public final class DataSourceVersion {
    private final Version version;
    private final boolean latest;
    private final Map<String, String> files;

    DataSourceVersion(final Version version, final boolean latest, final Map<String, String> files) {
        this.version = version;
        this.latest = latest;
        this.files = files;
    }

    public Version getVersion() {
        return version;
    }

    public boolean isLatest() {
        return latest;
    }

    public Map<String, String> getFiles() {
        return files;
    }

    public String getFile(final String fileName) {
        return files.get(fileName);
    }
}
