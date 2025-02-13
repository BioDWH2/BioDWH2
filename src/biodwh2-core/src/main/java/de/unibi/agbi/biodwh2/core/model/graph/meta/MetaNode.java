package de.unibi.agbi.biodwh2.core.model.graph.meta;

import de.unibi.agbi.biodwh2.core.lang.Type;

import java.util.HashMap;
import java.util.Map;

public final class MetaNode {
    public final String label;
    public final String dataSourceId;
    public final boolean isMappingLabel;
    public final Map<String, Type> propertyKeyTypes = new HashMap<>();
    public long count;

    public MetaNode(final String label, final String dataSourceId, final boolean isMappingLabel) {
        this.label = label;
        this.dataSourceId = dataSourceId;
        this.isMappingLabel = isMappingLabel;
    }
}
