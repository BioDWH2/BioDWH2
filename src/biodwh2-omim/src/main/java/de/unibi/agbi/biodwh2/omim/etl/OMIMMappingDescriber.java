package de.unibi.agbi.biodwh2.omim.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class OMIMMappingDescriber extends MappingDescriber {
    public OMIMMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (OMIMGraphExporter.GENE_LABEL.equals(localMappingLabel))
            return describeGene(node);
        if (OMIMGraphExporter.PHENOTYPE_LABEL.equals(localMappingLabel))
            return describePhenotype(node);
        return null;
    }

    private NodeMappingDescription[] describeGene(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
        description.addName(node.getProperty("gene_name"));
        // TODO: names
        description.addIdentifier(IdentifierType.HGNC_SYMBOL, node.<String>getProperty("approved_gene_symbol"));
        description.addIdentifier(IdentifierType.OMIM, node.<String>getProperty("mim_number"));
        description.addIdentifier(IdentifierType.ENSEMBL_GENE_ID, node.<String>getProperty("ensembl_gene_id"));
        description.addIdentifier(IdentifierType.ENTREZ_GENE_ID, node.<String>getProperty("entrez_gene_id"));
        // TODO: gene_symbols property?
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describePhenotype(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(
                NodeMappingDescription.NodeType.PHENOTYPE);
        // TODO: names
        description.addIdentifier(IdentifierType.OMIM, node.<String>getProperty("mim_number"));
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{
                OMIMGraphExporter.GENE_LABEL, OMIMGraphExporter.PHENOTYPE_LABEL
        };
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
