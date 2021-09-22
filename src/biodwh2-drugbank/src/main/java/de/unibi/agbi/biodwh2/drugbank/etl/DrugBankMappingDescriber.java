package de.unibi.agbi.biodwh2.drugbank.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.mapping.IdentifierUtils;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class DrugBankMappingDescriber extends MappingDescriber {
    public DrugBankMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if ("Drug".equalsIgnoreCase(localMappingLabel))
            return describeDrug(node);
        if ("Pathway".equalsIgnoreCase(localMappingLabel))
            return describePathway(node);
        if ("Salt".equalsIgnoreCase(localMappingLabel))
            return describeCompound(node);
        if ("Polypeptide".equalsIgnoreCase(localMappingLabel))
            return new NodeMappingDescription[]{describeGene(graph, node), describeProtein(graph, node)};
        if ("Article".equalsIgnoreCase(localMappingLabel))
            return describeArticle(node);
        if ("Organism".equalsIgnoreCase(localMappingLabel))
            return describeOrganism(node);
        return null;
    }

    private NodeMappingDescription[] describeDrug(final Node node) {
        final NodeMappingDescription drugDescription = new NodeMappingDescription(NodeMappingDescription.NodeType.DRUG);
        drugDescription.addIdentifier(IdentifierType.DRUG_BANK, node.<String>getProperty("drugbank_id"));
        drugDescription.addIdentifier(IdentifierType.CAS, node.<String>getProperty("cas_number"));
        drugDescription.addIdentifier(IdentifierType.UNII, node.<String>getProperty("unii"));
        drugDescription.addName(node.getProperty("name"));
        final NodeMappingDescription compoundDescription = new NodeMappingDescription(
                NodeMappingDescription.NodeType.COMPOUND);
        compoundDescription.addIdentifier(IdentifierType.DRUG_BANK, node.<String>getProperty("drugbank_id"));
        compoundDescription.addIdentifier(IdentifierType.CAS, node.<String>getProperty("cas_number"));
        compoundDescription.addIdentifier(IdentifierType.UNII, node.<String>getProperty("unii"));
        compoundDescription.addName(node.getProperty("name"));
        return new NodeMappingDescription[]{drugDescription, compoundDescription};
    }

    private NodeMappingDescription[] describePathway(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.PATHWAY);
        description.addIdentifier(IdentifierType.SMPDB, node.<String>getProperty("smpdb_id"));
        description.addName(node.getProperty("name"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeCompound(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.COMPOUND);
        description.addIdentifier(IdentifierType.CAS, node.<String>getProperty("cas_number"));
        description.addIdentifier(IdentifierType.UNII, node.<String>getProperty("unii"));
        description.addName(node.getProperty("name"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription describeProtein(final Graph graph, final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.PROTEIN);
        final Long[] nodeIds = graph.getAdjacentNodeIdsForEdgeLabel(node.getId(), "DrugBank_HAS_EXTERNAL_IDENTIFIER");
        description.addName(node.getProperty("name"));
        for (final Long nodeId : nodeIds) {
            final Node adjacentNode = graph.getNode(nodeId);
            if ("UniProtKB".equals(adjacentNode.getProperty("resource"))) {
                description.addIdentifier(IdentifierType.UNIPROT_KB, adjacentNode.<String>getProperty("id"));
            } else if ("UniProt Accession".equals(adjacentNode.getProperty("resource"))) {
                description.addIdentifier(IdentifierType.UNIPROT_KB, adjacentNode.<String>getProperty("id"));
            }
        }
        return description;
    }

    private NodeMappingDescription describeGene(final Graph graph, final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
        final Long[] nodeIds = graph.getAdjacentNodeIdsForEdgeLabel(node.getId(), "DrugBank_HAS_EXTERNAL_IDENTIFIER");
        description.addName(node.getProperty("gene_name"));
        for (final Long nodeId : nodeIds) {
            final Node adjacentNode = graph.getNode(nodeId);
            if ("HUGO Gene Nomenclature Committee (HGNC)".equals(adjacentNode.getProperty("resource"))) {
                final String hgncLabel = adjacentNode.getProperty("id");
                final String correctedLabel = hgncLabel.replaceFirst("HGNC:", "");
                description.addIdentifier(IdentifierType.HGNC_ID, correctedLabel);
            } else if ("GenAtlas".equals(adjacentNode.getProperty("resource"))) {
                description.addIdentifier(IdentifierType.GEN_ATLAS, adjacentNode.<String>getProperty("id"));
            }
        }
        return description;
    }

    private NodeMappingDescription[] describeArticle(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(
                NodeMappingDescription.NodeType.PUBLICATION);
        description.addIdentifier(IdentifierType.PUBMED_ID, node.<String>getProperty("pubmed_id"));
        final String citation = node.getProperty("citation");
        if (citation != null) {
            final String[] dois = IdentifierUtils.extractDois(citation);
            if (dois != null)
                for (final String doi : dois)
                    description.addIdentifier(IdentifierType.DOI, doi);
            description.addName(citation);
        }
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeOrganism(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.TAXON);
        description.addIdentifier(IdentifierType.NCBI_TAXON, node.<String>getProperty("id"));
        description.addName(node.getProperty("name"));
        return new NodeMappingDescription[]{description};
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{"Drug", "Pathway", "Salt", "Polypeptide", "Article", "Organism"};
    }

    @Override
    public PathMappingDescription describe(Graph graph, Node[] nodes, Edge[] edges) {
        if (edges.length > 0 && edges[0].getLabel().endsWith("INTERACTS_WITH_DRUG"))
            return new PathMappingDescription(PathMappingDescription.EdgeType.INTERACTS);
        return null;
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[]{
                new PathMapping().add("Drug", "INTERACTS_WITH_DRUG", "Drug", EdgeDirection.FORWARD)
        };
    }
}
