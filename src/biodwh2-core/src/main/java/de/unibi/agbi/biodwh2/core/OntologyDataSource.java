package de.unibi.agbi.biodwh2.core;

import java.util.Map;

public abstract class OntologyDataSource extends DataSource {
    @Override
    public Map<String, DataSourcePropertyType> getAvailableProperties() {
        final Map<String, DataSourcePropertyType> result = super.getAvailableProperties();
        result.put("ignoreObsolete", DataSourcePropertyType.BOOLEAN);
        return result;
    }

    public abstract String getIdPrefix();
}
