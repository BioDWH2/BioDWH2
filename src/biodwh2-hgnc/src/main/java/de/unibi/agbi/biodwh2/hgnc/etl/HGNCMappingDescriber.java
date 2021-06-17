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
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (HGNCGraphExporter.GENE_LABEL.equals(localMappingLabel))
            return describeGene(node);
        if (HGNCGraphExporter.PROTEIN_LABEL.equals(localMappingLabel))
            return describeProtein(node);
        return null;
    }

    private NodeMappingDescription[] describeGene(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
        description.addName(node.getProperty("name"));
        description.addNames(node.<String[]>getProperty("alias_names"));
        description.addIdentifier(IdentifierType.HGNC_ID, getHGNCIdFromNode(node));
        final String[] prevSymbols = node.getProperty("prev_symbols");
        if (prevSymbols != null)
            for (final String symbol : prevSymbols)
                description.addIdentifier(IdentifierType.HGNC_SYMBOL, symbol);
        description.addIdentifier(IdentifierType.HGNC_SYMBOL, node.<String>getProperty("symbol"));
        description.addIdentifier(IdentifierType.OMIM, node.<String>getProperty(OMIM_ID_KEY));
        // TODO: more ids
        return new NodeMappingDescription[]{description};
    }

    private String getHGNCIdFromNode(final Node node) {
        return node.<String>getProperty(HGNC_ID_KEY).replace("HGNC:", "");
    }

    private NodeMappingDescription[] describeProtein(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.PROTEIN);
        description.addIdentifier(IdentifierType.UNIPROT_KB, node.<String>getProperty("uniprot_id"));
        return new NodeMappingDescription[]{description};
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{HGNCGraphExporter.GENE_LABEL, HGNCGraphExporter.PROTEIN_LABEL};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        if (edges.length == 1 && HGNCGraphExporter.CODES_FOR_LABEL.equals(edges[0].getLabel()))
            return new PathMappingDescription(PathMappingDescription.EdgeType.CODES_FOR);
        return null;
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[]{
                new PathMapping().add(HGNCGraphExporter.GENE_LABEL, HGNCGraphExporter.CODES_FOR_LABEL,
                                      HGNCGraphExporter.PROTEIN_LABEL, PathMapping.EdgeDirection.FORWARD)
        };
    }
}
