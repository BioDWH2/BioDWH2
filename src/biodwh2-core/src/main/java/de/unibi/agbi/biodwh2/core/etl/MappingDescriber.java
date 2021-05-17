package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public abstract class MappingDescriber {
    private final DataSource dataSource;

    public MappingDescriber(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public abstract NodeMappingDescription[] describe(final Graph graph, final Node node,
                                                      final String localMappingLabel);

    public abstract PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges);

    protected abstract String[] getNodeMappingLabels();

    final String prefixLabel(final String label) {
        return dataSource.getId() + Graph.LABEL_PREFIX_SEPARATOR + label;
    }

    protected abstract String[][] getEdgeMappingPaths();

    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }

    final String getDataSourceId() {
        return dataSource.getId();
    }
}
