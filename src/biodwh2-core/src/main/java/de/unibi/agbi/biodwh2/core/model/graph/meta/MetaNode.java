package de.unibi.agbi.biodwh2.core.model.graph.meta;

public final class MetaNode {
    public final String label;
    public final String dataSourceId;
    public final boolean isMappingLabel;
    public long count;

    public MetaNode(final String label, String dataSourceId, boolean isMappingLabel) {
        this.label = label;
        this.dataSourceId = dataSourceId;
        this.isMappingLabel = isMappingLabel;
    }
}
