package de.unibi.agbi.biodwh2.core;

import de.unibi.agbi.biodwh2.core.model.DataSourceMetadata;

public abstract class DataSource<T extends DataSourceMetadata> {
    protected T metadata;

    public T getMetadata() {
        return metadata;
    }
}
