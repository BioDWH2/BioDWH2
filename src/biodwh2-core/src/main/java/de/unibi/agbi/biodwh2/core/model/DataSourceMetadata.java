package de.unibi.agbi.biodwh2.core.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class DataSourceMetadata {
    protected Version version;
    protected LocalDateTime updateTime;
    protected List<String> sourceFileNames;

    public DataSourceMetadata() {
        sourceFileNames = new ArrayList<>();
    }

    public Version getVersion() {
        return version;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public String[] getSourceFileNames() {
        return (String[]) sourceFileNames.toArray();
    }

    public abstract String getDataSourcePrefix();
}
