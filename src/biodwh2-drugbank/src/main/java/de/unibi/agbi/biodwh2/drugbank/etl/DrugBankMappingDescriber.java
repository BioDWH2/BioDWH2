package de.unibi.agbi.biodwh2.drugbank.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

import java.util.ArrayList;

public class DrugBankMappingDescriber extends MappingDescriber {

    public DrugBankMappingDescriber(DataSource dataSource) {
        super(dataSource);
    }


    @Override
    public NodeMappingDescription describe(Graph graph, Node node) {
        if (node.getLabel().endsWith("Drug")) {
            NodeMappingDescription description = new NodeMappingDescription();
            description.type = NodeMappingDescription.NodeType.DRUG;
            description.addIdentifier(IdentifierType.DRUG_BANK, node.getProperty("drugbank_id"));
            return description;
        }
        if(node.getLabel().endsWith("Pathway")){
            NodeMappingDescription description = new NodeMappingDescription();
            description.type = NodeMappingDescription.NodeType.PATHWAY;
            description.addIdentifier(IdentifierType.SMP_DB_ID, node.getProperty("smpdbId"));
            return description;
        }
        if(node.getLabel().endsWith("Chemical Property")){
            NodeMappingDescription description = new NodeMappingDescription();
            description.type = NodeMappingDescription.NodeType.COMPOUND;
            description.addIdentifier(IdentifierType.CAS, node.getProperty("cas-number"));
            return description;
        }
        if(node.getLabel().endsWith("Salt")){
            NodeMappingDescription description = new NodeMappingDescription();
            description.type = NodeMappingDescription.NodeType.COMPOUND;
            description.addIdentifier(IdentifierType.CAS, node.getProperty("cas-number"));
            description.addIdentifier(IdentifierType.UNII, node.getProperty("unii"));
            return description;
        }
        if(node.getLabel().endsWith("External Identifier")){
            return describeCompound(node);
        }
        if(node.getLabel().endsWith("Polypeptide")){
            return describeProtein(node, graph);
        }
        if(node.getLabel().endsWith("Article")){
            NodeMappingDescription description = new NodeMappingDescription();
            description.type = NodeMappingDescription.NodeType.PUBLICATION;
            description.addIdentifier(IdentifierType.PUBMED, node.getProperty("pubmedId"));
            return description;
        }
        return null;
    }

    private NodeMappingDescription describeCompound(final Node node) {
        NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.COMPOUND);
        if(node.getProperty("resource").equals("ChemSpider")){
            description.addIdentifier(IdentifierType.CHEMSPIDER, node.getProperty("identifier"));
            return description;
        }
        if(node.getProperty("resource").equals("PharmGKB")){
            description.addIdentifier(IdentifierType.PHARM_GKB, node.getProperty("identifier"));
            return description;
        }
        if(node.getProperty("resource").equals("PubChem Compound")){
            description.addIdentifier(IdentifierType.PUB_CHEM_COMPOUND, node.getProperty("identifier"));
            return description;
        }

    return null;
    }

    private NodeMappingDescription describeProtein(final Node node, Graph graph) {
        NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.PROTEIN);
        Long[] nodeIds = graph.getAdjacentNodeIdsForEdgeLabel(node.getProperty("__id"),
                                                              "DrugBank_HAS_POLYPEPTIDE_EXTERNAL_IDENTIFIER");
        for (int i = 0; i < nodeIds.length; i++) {
            Node adjacentNode = graph.getNode(nodeIds[i]);
            if (adjacentNode.getProperty("resource").equals("UniProtKB")) {
                description.addIdentifier(IdentifierType.UNIPROTKB, adjacentNode.getProperty("identifier"));
            } else if (adjacentNode.getProperty("resource").equals("UniProt Accession")) {
                description.addIdentifier(IdentifierType.UNIPROT_ACC, adjacentNode.getProperty("identifier"));
            }
        }
        return description;
    }

    private NodeMappingDescription describeGene(final Node node, Graph graph) {
        NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
        Long[] nodeIds = graph.getAdjacentNodeIdsForEdgeLabel(node.getProperty("__id"),
                                                              "DrugBank_HAS_POLYPEPTIDE_EXTERNAL_IDENTIFIER");
        for (int i = 0; i < nodeIds.length; i++) {
            Node adjacentNode = graph.getNode(nodeIds[i]);
            if (adjacentNode.getProperty("resource").equals("HUGO Gene Nomenclature Committee (HGNC)")) {
                description.addIdentifier(IdentifierType.HGNC_ID, adjacentNode.getProperty("identifier"));
            } else if (adjacentNode.getProperty("resource").equals("GenAtlas")) {
                description.addIdentifier(IdentifierType.GEN_ATLAS, adjacentNode.getProperty("identifier"));
            }
        }
        return description;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{"Drug", "Pathway", "Chemical Property", "Polypeptide", "External Identifier", "Salt", "Article"};
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
