package de.unibi.agbi.biodwh2.medrt.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

import java.util.HashSet;
import java.util.Set;

public class MEDRTMappingDescriber extends MappingDescriber {
    private final String synonymOfLabel;

    public MEDRTMappingDescriber(final DataSource dataSource) {
        super(dataSource);
        synonymOfLabel = dataSource.getId() + "_SYNONYM_OF";
    }

    @Override
    public NodeMappingDescription describe(final Graph graph, final Node node, final String localMappingLabel) {
        if ("Drug".equals(localMappingLabel))
            return describeDrug(graph, node);
        return null;
    }

    private NodeMappingDescription describeDrug(final Graph graph, final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.DRUG);
        final String name = node.getProperty("name");
        if (name != null)
            description.addName(name);
        description.addNames(findSynonyms(graph, node));
        description.addIdentifier(IdentifierType.RX_NORM_CUI, node.getProperty("code"));
        return description;
    }

    private Set<String> findSynonyms(final Graph graph, final Node node) {
        final Iterable<Edge> edges = graph.findEdges(synonymOfLabel, Edge.FROM_ID_FIELD, node.getId());
        final Set<String> terms = new HashSet<>();
        for (final Edge edge : edges) {
            final Node termNode = graph.getNode(edge.getToId());
            final String name = termNode.getProperty("name");
            if (name != null)
                terms.add(name);
        }
        return terms;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{"Drug"};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[][] getEdgeMappingPaths() {
        return new String[0][];
    }
}
