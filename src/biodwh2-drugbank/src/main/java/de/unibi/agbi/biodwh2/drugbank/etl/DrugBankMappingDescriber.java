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
    public NodeMappingDescription[] describe(Graph graph, Node node, String localMappingLabel) {
        if ("Drug".equals(localMappingLabel)) {
            NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.DRUG);
            description.addIdentifier(IdentifierType.DRUG_BANK, node.<String>getProperty("drugbank_id"));
            return new NodeMappingDescription[]{description};
        }
        if ("Pathway".equals(localMappingLabel)) {
            NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.PATHWAY);
            description.addIdentifier(IdentifierType.SMPDB, node.<String>getProperty("smpdbId"));
            return new NodeMappingDescription[]{description};
        }
        /*
        if("Chemical Property".equals(localMappingLabel)){
            NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.COMPOUND);
            description.addIdentifier(IdentifierType.CAS, node.getProperty("cas-number"));
            return new NodeMappingDescription[]{description};
        }*/
        if ("Salt".equals(localMappingLabel)) {
            NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.COMPOUND);
            description.addIdentifier(IdentifierType.CAS, node.<String>getProperty("cas-number"));
            description.addIdentifier(IdentifierType.UNII, node.<String>getProperty("unii"));
            return new NodeMappingDescription[]{description};
        }
        /*
        if("External Identifier")){
            return describeCompound(node);
        }*/
        if ("Polypeptide".equals(localMappingLabel)) {
            return describeProtein(node, graph);
        }
        if ("Article".equals(localMappingLabel)) {
            NodeMappingDescription description = new NodeMappingDescription(
                    NodeMappingDescription.NodeType.PUBLICATION);
            description.addIdentifier(IdentifierType.PUBMED_ID, node.<String>getProperty("pubmedId"));
            return new NodeMappingDescription[]{description};
        }
        return null;
    }

    private NodeMappingDescription[] describeCompound(final Node node) {
        NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.COMPOUND);
        if (node.getProperty("resource").equals("ChemSpider")) {
            description.addIdentifier(IdentifierType.CHEMSPIDER, node.<String>getProperty("identifier"));
            return new NodeMappingDescription[]{description};
        }
        if (node.getProperty("resource").equals("PharmGKB")) {
            description.addIdentifier(IdentifierType.PHARM_GKB, node.<String>getProperty("identifier"));
            return new NodeMappingDescription[]{description};
        }
        if (node.getProperty("resource").equals("PubChem Compound")) {
            description.addIdentifier(IdentifierType.PUB_CHEM_COMPOUND, node.<String>getProperty("identifier"));
            return new NodeMappingDescription[]{description};
        }

        return null;
    }

    private NodeMappingDescription[] describeProtein(final Node node, Graph graph) {
        NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.PROTEIN);
        Long[] nodeIds = graph.getAdjacentNodeIdsForEdgeLabel(node.getProperty("__id"),
                                                              "DrugBank_HAS_POLYPEPTIDE_EXTERNAL_IDENTIFIER");
        for (int i = 0; i < nodeIds.length; i++) {
            Node adjacentNode = graph.getNode(nodeIds[i]);
            if (adjacentNode.getProperty("resource").equals("UniProtKB")) {
                description.addIdentifier(IdentifierType.UNIPROT_KB, adjacentNode.<String>getProperty("identifier"));
            } else if (adjacentNode.getProperty("resource").equals("UniProt Accession")) {
                description.addIdentifier(IdentifierType.UNIPROT_KB, adjacentNode.<String>getProperty("identifier"));
            }
        }
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeGene(final Node node, Graph graph) {
        NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
        Long[] nodeIds = graph.getAdjacentNodeIdsForEdgeLabel(node.getProperty("__id"),
                                                              "DrugBank_HAS_POLYPEPTIDE_EXTERNAL_IDENTIFIER");
        for (int i = 0; i < nodeIds.length; i++) {
            Node adjacentNode = graph.getNode(nodeIds[i]);
            if (adjacentNode.getProperty("resource").equals("HUGO Gene Nomenclature Committee (HGNC)")) {
                description.addIdentifier(IdentifierType.HGNC_ID, adjacentNode.<String>getProperty("identifier"));
            } else if (adjacentNode.getProperty("resource").equals("GenAtlas")) {
                description.addIdentifier(IdentifierType.GEN_ATLAS, adjacentNode.<String>getProperty("identifier"));
            }
        }
        return new NodeMappingDescription[]{description};
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{"Drug", "Pathway", "Chemical Property", "Polypeptide", "Salt", "Article"};
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