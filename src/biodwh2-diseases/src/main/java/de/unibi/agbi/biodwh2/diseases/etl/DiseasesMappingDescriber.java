package de.unibi.agbi.biodwh2.diseases.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import de.unibi.agbi.biodwh2.core.model.graph.mapping.RNANodeMappingDescription;
import org.apache.commons.lang3.StringUtils;

public class DiseasesMappingDescriber extends MappingDescriber {
    public DiseasesMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (DiseasesGraphExporter.GENE_LABEL.equals(localMappingLabel))
            return describeGene(node);
        if (DiseasesGraphExporter.DISEASE_LABEL.equals(localMappingLabel))
            return describeDisease(node);
        return null;
    }

    private NodeMappingDescription[] describeGene(final Node node) {
        final String id = node.getProperty(GraphExporter.ID_KEY);
        if (id == null)
            return null;
        final String name = node.getProperty("name");
        if (id.startsWith("ENSP")) {
            final var description = new NodeMappingDescription(NodeMappingDescription.NodeType.PROTEIN);
            description.addName(name);
            description.addIdentifier(IdentifierType.ENSEMBL, id);
            return new NodeMappingDescription[]{description};
        }
        if (id.startsWith("hsa-miR") || id.startsWith("hsa-let")) {
            final var description = new RNANodeMappingDescription(RNANodeMappingDescription.RNAType.MI_RNA);
            description.addName(name);
            description.addIdentifier(IdentifierType.MIRNA, id);
            return new NodeMappingDescription[]{description};
        }
        // hsa_circ_000939, LINC00550
        return null;
    }

    private NodeMappingDescription[] describeDisease(final Node node) {
        final String id = node.getProperty(GraphExporter.ID_KEY);
        if (id == null)
            return null;
        final var description = new NodeMappingDescription(NodeMappingDescription.NodeType.DISEASE);
        description.addName(node.getProperty("name"));
        final String[] parts = StringUtils.split(id, ":", 2);
        if ("DOID".equals(parts[0])) {
            description.addIdentifier(IdentifierType.DOID, Integer.parseInt(parts[1]));
            return new NodeMappingDescription[]{description};
        }
        if ("ICD10".equals(parts[0])) {
            // TODO: quality check
            // description.addIdentifier(IdentifierType.ICD10, parts[1]);
            // return new NodeMappingDescription[]{description};
        }
        // AmyCo
        return null;
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        if (edges.length == 1) {
            final var description = new PathMappingDescription(PathMappingDescription.EdgeType.ASSOCIATED_WITH);
            description.setAdditionalProperties("evidence_type", edges[0].getProperty("evidence_type"));
            return description;
        }
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{DiseasesGraphExporter.GENE_LABEL, DiseasesGraphExporter.DISEASE_LABEL};
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[]{
                new PathMapping().add(DiseasesGraphExporter.GENE_LABEL, DiseasesGraphExporter.ASSOCIATED_WITH_LABEL,
                                      DiseasesGraphExporter.DISEASE_LABEL)
        };
    }
}
