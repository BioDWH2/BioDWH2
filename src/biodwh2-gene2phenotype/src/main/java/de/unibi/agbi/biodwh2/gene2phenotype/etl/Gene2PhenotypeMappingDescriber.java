package de.unibi.agbi.biodwh2.gene2phenotype.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class Gene2PhenotypeMappingDescriber extends MappingDescriber {
    public Gene2PhenotypeMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if ("Gene".equalsIgnoreCase(localMappingLabel))
            return describeGene(node);
        if ("Disease".equalsIgnoreCase(localMappingLabel))
            return describeDisease(node);
        if ("Publication".equalsIgnoreCase(localMappingLabel))
            return describePublication(node);
        //if ("Phenotype".equalsIgnoreCase(localMappingLabel))
        //    return describePhenotype(node);
        return null;
    }

    private NodeMappingDescription[] describeGene(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
        description.addIdentifier(IdentifierType.HGNC_SYMBOL, node.<String>getProperty("hgnc_symbol"));
        if (node.hasProperty("hgnc_id"))
            description.addIdentifier(IdentifierType.HGNC_ID, node.<Integer>getProperty("hgnc_id"));
        if (node.hasProperty("mim"))
            description.addIdentifier(IdentifierType.OMIM, node.<String>getProperty("mim"));
        final String[] previousSymbols = node.getProperty("previous_symbols");
        if (previousSymbols != null)
            for (final String symbol : previousSymbols)
                description.addIdentifier(IdentifierType.HGNC_SYMBOL, symbol);
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeDisease(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.DISEASE);
        description.addName(node.getProperty("name"));
        if (node.hasProperty("mim"))
            description.addIdentifier(IdentifierType.OMIM, node.<String>getProperty("mim"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describePublication(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(
                NodeMappingDescription.NodeType.PUBLICATION);
        description.addIdentifier(IdentifierType.PUBMED_ID, node.<String>getProperty("pmid"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describePhenotype(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription("PHENOTYPE");
        description.addIdentifier("HPO", (String) node.getProperty("hpo_id"));
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{
                Gene2PhenotypeGraphExporter.GENE_LABEL, Gene2PhenotypeGraphExporter.DISEASE_LABEL,
                Gene2PhenotypeGraphExporter.PUBLICATION_LABEL, Gene2PhenotypeGraphExporter.PHENOTYPE_LABEL
        };
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
