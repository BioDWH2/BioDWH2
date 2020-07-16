package de.unibi.agbi.biodwh2.core.schema;

import java.io.IOException;

public abstract class SchemaWriter {
    protected final GraphSchema schema;

    protected SchemaWriter(final GraphSchema schema) {
        this.schema = schema;
    }

    public abstract void save(final String filePath) throws IOException;
}
