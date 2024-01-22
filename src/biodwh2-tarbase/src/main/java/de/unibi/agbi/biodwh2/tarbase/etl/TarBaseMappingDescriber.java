package de.unibi.agbi.biodwh2.tarbase.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import de.unibi.agbi.biodwh2.core.model.graph.mapping.RNANodeMappingDescription;

public class TarBaseMappingDescriber extends MappingDescriber {
    public TarBaseMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (TarBaseGraphExporter.GENE_LABEL.equals(localMappingLabel))
            return describeGene(node);
        if (TarBaseGraphExporter.TRANSCRIPT_LABEL.equals(localMappingLabel))
            return describeTranscript(node);
        if (TarBaseGraphExporter.MI_RNA_LABEL.equals(localMappingLabel))
            return describeMiRNA(node);
        return null;
    }

    private static NodeMappingDescription[] describeGene(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
        final String id = node.getProperty("id");
        description.addIdentifier(IdentifierType.ENSEMBL, id);
        description.addName(node.getProperty("name"));
        return new NodeMappingDescription[]{description};
    }

    private static NodeMappingDescription[] describeTranscript(final Node node) {
        final NodeMappingDescription description = new RNANodeMappingDescription(
                RNANodeMappingDescription.RNAType.M_RNA);
        final String id = node.getProperty("id");
        description.addIdentifier(IdentifierType.ENSEMBL, id);
        description.addName(node.getProperty("name"));
        return new NodeMappingDescription[]{description};
    }

    private static NodeMappingDescription[] describeMiRNA(final Node node) {
        final NodeMappingDescription description = new RNANodeMappingDescription(
                RNANodeMappingDescription.RNAType.MI_RNA);
        final String id = node.getProperty("id");
        description.addIdentifier(IdentifierType.MIRNA, id);
        description.addName(node.getProperty("name"));
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        if (edges.length == 1) {
            if (edges[0].getLabel().endsWith(TarBaseGraphExporter.TRANSCRIBES_TO_LABEL))
                return new PathMappingDescription(PathMappingDescription.EdgeType.TRANSCRIBES_TO);
            if (edges[0].getLabel().endsWith(TarBaseGraphExporter.TARGETS_LABEL))
                return new PathMappingDescription(PathMappingDescription.EdgeType.TARGETS);
        }
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{
                TarBaseGraphExporter.GENE_LABEL, TarBaseGraphExporter.TRANSCRIPT_LABEL,
                TarBaseGraphExporter.MI_RNA_LABEL
        };
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[]{
                new PathMapping().add(TarBaseGraphExporter.GENE_LABEL, TarBaseGraphExporter.TRANSCRIBES_TO_LABEL,
                                      TarBaseGraphExporter.MI_RNA_LABEL, EdgeDirection.FORWARD), new PathMapping().add(
                TarBaseGraphExporter.MI_RNA_LABEL, TarBaseGraphExporter.TARGETS_LABEL,
                TarBaseGraphExporter.TRANSCRIPT_LABEL, EdgeDirection.FORWARD)
        };
    }
}
