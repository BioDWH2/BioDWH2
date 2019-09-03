package de.unibi.agbi.biodwh2.hgnc.model;

import de.unibi.agbi.biodwh2.core.model.DataSourceMetadata;

public class HGNCDataSourceMetadata extends DataSourceMetadata {
    @Override
    public String getDataSourcePrefix() {
        return "HGNC";
    }
}
