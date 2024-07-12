package de.unibi.agbi.biodwh2.core;

import java.util.Map;
import java.util.regex.Pattern;

public abstract class OntologyDataSource extends DataSource {
    protected static final Pattern DASHED_YYYY_MM_DD_VERSION_PATTERN = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");

    @Override
    public Map<String, DataSourcePropertyType> getAvailableProperties() {
        final Map<String, DataSourcePropertyType> result = super.getAvailableProperties();
        result.put("ignoreObsolete", DataSourcePropertyType.BOOLEAN);
        return result;
    }

    public abstract String getIdPrefix();
}
