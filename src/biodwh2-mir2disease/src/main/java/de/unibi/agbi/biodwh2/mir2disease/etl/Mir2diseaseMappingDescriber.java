package de.unibi.agbi.biodwh2.mir2disease.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class Mir2diseaseMappingDescriber extends MappingDescriber {
    public Mir2diseaseMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(Graph graph, Node node, String localMappingLabel) {
        if (Mir2diseaseGraphExporter.MI_RNA_LABEL.equals(localMappingLabel))
            return describeMiRNA(node);
        else if (Mir2diseaseGraphExporter.DISEASE_LABEL.equals(localMappingLabel))
            return describeDisease(node);
        else if (Mir2diseaseGraphExporter.PUBLICATION_LABEL.equals(localMappingLabel))
            return describePublication(node);
        else if (Mir2diseaseGraphExporter.TARGET_LABEL.equals(localMappingLabel))
            return describeTarget(node);
        return new NodeMappingDescription[0];
    }

    private NodeMappingDescription[] describeTarget(Node node) {
        final NodeMappingDescription description = new NodeMappingDescription((NodeMappingDescription.NodeType.GENE));
        description.addIdentifier(IdentifierType.HGNC_SYMBOL, node.<String>getProperty("validated target"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describePublication(Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(
                NodeMappingDescription.NodeType.PUBLICATION);
        description.addName(node.getProperty("reference"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeDisease(Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.DISEASE);
        description.addIdentifier(IdentifierType.DOI, node.<String>getProperty(GraphExporter.ID_KEY));
        description.addName(node.getProperty("name"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeMiRNA(Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.RNA);
        description.addIdentifier(IdentifierType.MIRNA, node.<String>getProperty(GraphExporter.ID_KEY));
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(Graph graph, Node[] nodes, Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{
                Mir2diseaseGraphExporter.MI_RNA_LABEL, Mir2diseaseGraphExporter.TARGET_LABEL,
                Mir2diseaseGraphExporter.PUBLICATION_LABEL, Mir2diseaseGraphExporter.DISEASE_LABEL
        };
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
