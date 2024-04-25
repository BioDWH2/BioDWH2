package de.unibi.agbi.biodwh2.bionda.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import de.unibi.agbi.biodwh2.core.model.graph.mapping.RNANodeMappingDescription;

public class BIONDAMappingDescriber extends MappingDescriber {
    public BIONDAMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (BIONDAGraphExporter.PUBLICATION_LABEL.equals(localMappingLabel))
            return describePublication(node);
        if (BIONDAGraphExporter.DISEASE_LABEL.equals(localMappingLabel))
            return describeDisease(node);
        if (BIONDAGraphExporter.BIOMARKER_LABEL.equals(localMappingLabel))
            return describeBiomarker(node);
        return null;
    }

    private NodeMappingDescription[] describePublication(final Node node) {
        final var pmid = node.<Integer>getProperty("pmid");
        if (pmid == null)
            return null;
        final var description = new NodeMappingDescription(NodeMappingDescription.NodeType.PUBLICATION);
        description.addIdentifier(IdentifierType.PUBMED_ID, pmid);
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeDisease(final Node node) {
        final var description = new NodeMappingDescription(NodeMappingDescription.NodeType.DISEASE);
        description.addIdentifier(IdentifierType.DOID, node.<Integer>getProperty(GraphExporter.ID_KEY));
        description.addName(node.getProperty("name"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeBiomarker(final Node node) {
        final String id = node.getProperty(GraphExporter.ID_KEY);
        if (id == null)
            return null;
        final NodeMappingDescription description;
        if (id.startsWith("MI")) {
            description = new RNANodeMappingDescription(RNANodeMappingDescription.RNAType.MI_RNA);
            description.addIdentifier(IdentifierType.MIRBASE, id);
        } else {
            description = new NodeMappingDescription(NodeMappingDescription.NodeType.PROTEIN);
            description.addIdentifier(IdentifierType.UNIPROT_KB, id);
        }
        description.addName(node.getProperty("name"));
        description.addName(node.getProperty("uniprot_protein_name"));
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{
                BIONDAGraphExporter.PUBLICATION_LABEL, BIONDAGraphExporter.DISEASE_LABEL,
                BIONDAGraphExporter.BIOMARKER_LABEL
        };
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
