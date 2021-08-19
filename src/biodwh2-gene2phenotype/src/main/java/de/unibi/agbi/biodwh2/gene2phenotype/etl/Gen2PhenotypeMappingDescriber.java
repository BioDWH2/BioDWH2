package de.unibi.agbi.biodwh2.gene2phenotype.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

import java.util.List;

public class Gen2PhenotypeMappingDescriber extends MappingDescriber {
    public Gen2PhenotypeMappingDescriber(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(Graph graph, Node node, String localMappingLabel) {
        if ("Gene".equalsIgnoreCase(localMappingLabel))
            return describeGene(node);
        if ("Disease".equalsIgnoreCase(localMappingLabel))
            return describeDisease(node);
        if ("Publication".equalsIgnoreCase(localMappingLabel))
            return describePublication(node);
        if ("Phenotype".equalsIgnoreCase(localMappingLabel))
            return describePhenotype(node);
        return null;
    }

    private NodeMappingDescription[] describePhenotype(Node node) {
        NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.UNKNOWN);
        if (node.hasProperty("hpo_id"))
            description.addIdentifier("HPO_ID", (String) node.getProperty("hpo_id"));

        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describePublication(Node node) {
        NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.PUBLICATION);
        if (node.hasProperty("pubmed_id"))
            description.addIdentifier(IdentifierType.PUBMED_ID, (String) node.getProperty("pubmed_id"));

        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeDisease(Node node) {
        NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.DISEASE);
        if (node.hasProperty("disease_name"))
            description.addName(node.getProperty("disease_name"));
        if (node.hasProperty("mim"))
            description.addIdentifier(IdentifierType.OMIM, (String) node.getProperty("mim"));

        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeGene(Node node) {
        NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
        if (node.hasProperty("hgnc_id"))
            description.addIdentifier(IdentifierType.HGNC_ID, (Integer) node.getProperty("hgnc_id"));
        if (node.hasProperty("hgnc_symbol"))
            description.addIdentifier(IdentifierType.HGNC_SYMBOL, (String) node.getProperty("hgnc_symbol"));
        if (node.hasProperty("mim"))
            description.addIdentifier(IdentifierType.OMIM, (String) node.getProperty("mim"));
        if (node.hasProperty("previous_symbols")){
            List<String> prev_symbols = node.getProperty("previous_symbols");
            if (prev_symbols != null)
                for (String prev_symbol : prev_symbols)
                    description.addIdentifier(IdentifierType.HGNC_SYMBOL, prev_symbol);
        }

        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(Graph graph, Node[] nodes, Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{"Gene", "Disease", "Publication", "Phenotype"};
    }

    @Override
    protected String[][] getEdgeMappingPaths() {
        return new String[0][];
    }
}
