package de.unibi.agbi.biodwh2.core.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DataSourceMetadata {
    protected Version version;
    protected LocalDateTime updateTime;
    protected List<String> managedFiles;

    public DataSourceMetadata() {
        managedFiles = new ArrayList<>();
    }

    public Version getVersion() {
        return version;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public String[] getManagedFiles() {
        return (String[]) managedFiles.toArray();
    }
}
