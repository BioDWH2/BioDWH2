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
        description.addNames(node.getProperty("name"), node.getProperty("preferred_title"));
        description.addIdentifier(IdentifierType.HGNC_SYMBOL, node.<String>getProperty("approved_gene_symbol"));
        description.addIdentifier(IdentifierType.OMIM, node.<Integer>getProperty(OMIMGraphExporter.MIM_NUMBER_KEY));
        description.addIdentifier(IdentifierType.ENSEMBL_GENE_ID, node.<String>getProperty("ensembl_gene_id"));
        description.addIdentifier(IdentifierType.NCBI_GENE, node.<Integer>getProperty("entrez_gene_id"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describePhenotype(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(
                NodeMappingDescription.NodeType.PHENOTYPE);
        description.addNames(node.getProperty("name"), node.getProperty("preferred_title"));
        description.addIdentifier(IdentifierType.OMIM, node.<Integer>getProperty(OMIMGraphExporter.MIM_NUMBER_KEY));
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
