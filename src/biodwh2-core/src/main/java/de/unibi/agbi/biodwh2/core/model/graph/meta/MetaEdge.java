package de.unibi.agbi.biodwh2.core.model.graph.meta;

import de.unibi.agbi.biodwh2.core.lang.Type;

import java.util.HashMap;
import java.util.Map;

public final class MetaEdge {
    public final String fromLabel;
    public final String toLabel;
    public final String label;
    public final String dataSourceId;
    public final boolean isMappingLabel;
    public final Map<String, Type> propertyKeyTypes = new HashMap<>();
    public long count;

    public MetaEdge(final String fromLabel, final String toLabel, final String label, final String dataSourceId,
                    final boolean isMappingLabel) {
        this.fromLabel = fromLabel;
        this.toLabel = toLabel;
        this.label = label;
        this.dataSourceId = dataSourceId;
        this.isMappingLabel = isMappingLabel;
    }
}
