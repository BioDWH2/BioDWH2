package de.unibi.agbi.biodwh2.card.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeMappingDescription;
import de.unibi.agbi.biodwh2.core.model.graph.PathMapping;
import de.unibi.agbi.biodwh2.core.model.graph.PathMappingDescription;

public final class CARDMappingDescriber extends MappingDescriber {
    public CARDMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        // TODO: Implement CARD node mapping
        return new NodeMappingDescription[0];
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        // TODO: Implement CARD path mapping
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[0];
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }

}

