package de.unibi.agbi.biodwh2.hgnc.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class HGNCMappingDescriber extends MappingDescriber {
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
        description.addIdentifier(IdentifierType.HGNC_ID, node.<String>getProperty("hgnc_id").replace("HGNC:", ""));
        description.addIdentifier(IdentifierType.HGNC_SYMBOL, node.getProperty("symbol"));
        if (node.hasProperty("omim_id"))
            description.addIdentifier(IdentifierType.OMIM, node.getProperty("omim_id"));
        // TODO: more ids
        return description;
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
