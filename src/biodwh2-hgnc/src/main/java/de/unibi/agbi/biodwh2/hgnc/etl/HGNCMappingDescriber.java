package de.unibi.agbi.biodwh2.hgnc.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class HGNCMappingDescriber extends MappingDescriber {
    private static final String HGNC_ID_KEY = "hgnc_id";
    private static final String OMIM_ID_KEY = "omim_id";

    public HGNCMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription describe(final Graph graph, final Node node, final String localMappingLabel) {
        if ("Gene".equals(localMappingLabel))
            return describeGene(node);
        return null;
    }

    private NodeMappingDescription describeGene(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
        description.addName(node.getProperty("name"));
        final String[] aliasNames = node.getProperty("alias_names");
        if (aliasNames != null)
            description.addNames(aliasNames);
        description.addIdentifier(IdentifierType.HGNC_ID, getHGNCIdFromNode(node));
        final String[] prevSymbols = node.getProperty("prev_symbols");
        if (prevSymbols != null)
            for (final String symbol : prevSymbols)
                description.addIdentifier(IdentifierType.HGNC_SYMBOL, symbol);
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
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[][] getEdgeMappingPaths() {
        return new String[0][];
    }
}
