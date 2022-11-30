package de.unibi.agbi.biodwh2.trrust.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class TRRUSTMappingDescriber extends MappingDescriber {
    public TRRUSTMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (TRRUSTGraphExporter.GENE_LABEL.equals(localMappingLabel))
            return describeGene(node);
        if (TRRUSTGraphExporter.TRANSCRIPTION_FACTOR_LABEL.equals(localMappingLabel))
            return describeTF(node);
        return null;
    }

    private NodeMappingDescription[] describeGene(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
        description.addIdentifier(IdentifierType.NCBI_GENE, node.<Integer>getProperty(TRRUSTGraphExporter.GENE_ID_KEY));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeTF(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
        description.addIdentifier(IdentifierType.NCBI_GENE, node.<Integer>getProperty(TRRUSTGraphExporter.GENE_ID_KEY));
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{TRRUSTGraphExporter.GENE_LABEL, TRRUSTGraphExporter.TRANSCRIPTION_FACTOR_LABEL};
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
