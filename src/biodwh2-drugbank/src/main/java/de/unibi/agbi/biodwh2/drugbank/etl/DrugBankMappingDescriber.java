package de.unibi.agbi.biodwh2.drugbank.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class DrugBankMappingDescriber extends MappingDescriber {

    public DrugBankMappingDescriber(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if ("Drug".equalsIgnoreCase(localMappingLabel))
            return describeDrug(node);
        if ("Pathway".equalsIgnoreCase(localMappingLabel))
            return describePathway(node);
        if ("Drug".equalsIgnoreCase(localMappingLabel))
            return describeCompound(node);
        if ("Salt".equalsIgnoreCase(localMappingLabel))
            return describeCompound(node);
        if("Polypeptide".equalsIgnoreCase(localMappingLabel))
            return new NodeMappingDescription[]{describeGene(graph, node), describeProtein(graph, node)};
        if ("Article".equalsIgnoreCase(localMappingLabel))
            return describeArticle(node);
        if("Organism".equalsIgnoreCase(localMappingLabel))
            return describeOrganism(node);
        return null;
    }

    private NodeMappingDescription[] describeDrug(final Node node){
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.DRUG);
        description.addIdentifier(IdentifierType.DRUG_BANK, node.<String>getProperty("drugbank_id"));
        description.addName(node.getProperty("name"));
        return new NodeMappingDescription[]{description};
        }

    private NodeMappingDescription[] describePathway(final Node node){
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
        Long[] nodeIds = graph.getAdjacentNodeIdsForEdgeLabel(node.getId(),
                                                              "DrugBank_HAS_EXTERNAL_IDENTIFIER");
        description.addName(node.getProperty("name"));
            for (int i = 0; i < nodeIds.length; i++) {
                Node adjacentNode = graph.getNode(nodeIds[i]);
                if (adjacentNode.getProperty("resource").equals("UniProtKB")) {
                    description.addIdentifier(IdentifierType.UNIPROT_KB, adjacentNode.<String>getProperty("id"));
                } else if (adjacentNode.getProperty("resource").equals("UniProt Accession")) {
                    description.addIdentifier(IdentifierType.UNIPROT_KB, adjacentNode.<String>getProperty("id"));
                }

            }
        return description;
    }

    private NodeMappingDescription describeGene(final Graph graph, final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
        Long[] nodeIds = graph.getAdjacentNodeIdsForEdgeLabel(node.getId(),
                                                              "DrugBank_HAS_EXTERNAL_IDENTIFIER");
        description.addName(node.getProperty("gene_name"));
        for (int i = 0; i < nodeIds.length; i++) {
            Node adjacentNode = graph.getNode(nodeIds[i]);
            String hgncLabel = adjacentNode.getProperty("id");
            String correctedLabel = hgncLabel.replaceFirst("HGNC:", ""); 
            if (adjacentNode.getProperty("resource").equals("HUGO Gene Nomenclature Committee (HGNC)")) {
                description.addIdentifier(IdentifierType.HGNC_ID, correctedLabel);
            } else if (adjacentNode.getProperty("resource").equals("GenAtlas")) {
                description.addIdentifier(IdentifierType.GEN_ATLAS, adjacentNode.<String>getProperty("id"));
            }
        }
        return description;
    }

    private NodeMappingDescription[] describeArticle(final Node node){
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.PUBLICATION);
        description.addIdentifier(IdentifierType.PUBMED_ID, node.<String>getProperty("pubmed_id"));
        description.addName(node.getProperty("citation"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeOrganism(final Node node){
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.TAXON);
        description.addIdentifier(IdentifierType.NCBI_TAXON, node.<String>getProperty("id"));
        description.addName(node.getProperty("name"));
        return new NodeMappingDescription[]{description};
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{"Drug", "Pathway", "Salt", "Polypeptide",  "Article", "Organism"};
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