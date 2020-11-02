package de.unibi.agbi.biodwh2.hgnc.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class HGNCMappingDescriber extends MappingDescriber {
    private static final String HGNC_ID_KEY = "hgnc_id";
    private static final String OMIM_ID_KEY = "omim_id";

    public HGNCMappingDescriber(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription describe(final Graph graph, final Node node) {
        if (node.getLabel().endsWith("Gene"))
            return describeGene(node);
        return null;
    }

    private NodeMappingDescription describeGene(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription();
        description.type = NodeMappingDescription.NodeType.GENE;
        description.addIdentifier(IdentifierType.HGNC_ID, getHGNCIdFromNode(node));
        description.addIdentifier(IdentifierType.HGNC_SYMBOL, node.getProperty("prev_symbol"));
        description.addIdentifier(IdentifierType.HGNC_SYMBOL, node.getProperty("symbol"));
        if (node.hasProperty(OMIM_ID_KEY))
            description.addIdentifier(IdentifierType.OMIM, node.getProperty(OMIM_ID_KEY));
        // TODO: more ids
        return description;
    }

    private String getHGNCIdFromNode(final Node node) {
        return node.<String>getProperty(HGNC_ID_KEY).replace("HGNC:", "");
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{"Gene"};
    }

    @Override
    public PathMappingDescription describe(Graph graph, Node[] nodes, Edge[] edges) {
        return null;
    }

    @Override
    protected String[][] getEdgeMappingPaths() {
        return new String[0][];
    }
}
