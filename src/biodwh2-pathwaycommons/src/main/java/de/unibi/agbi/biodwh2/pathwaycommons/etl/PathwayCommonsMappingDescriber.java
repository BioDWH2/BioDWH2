package de.unibi.agbi.biodwh2.pathwaycommons.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import org.apache.commons.lang3.StringUtils;

public class PathwayCommonsMappingDescriber extends MappingDescriber {
    public PathwayCommonsMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (PathwayCommonsGraphExporter.PATHWAY_LABEL.equals(localMappingLabel))
            return describePathway(node);
        if (PathwayCommonsGraphExporter.GENE_LABEL.equals(localMappingLabel))
            return describeGene(node);
        if (PathwayCommonsGraphExporter.PROTEIN_LABEL.equals(localMappingLabel))
            return describeProtein(node);
        return null;
    }

    private NodeMappingDescription[] describePathway(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.PATHWAY);
        final String source = node.getProperty("source");
        final String id = node.getProperty("id");
        if (source != null && id != null) {
            if ("kegg".equalsIgnoreCase(source)) {
                final String[] parts = StringUtils.split(id, '/');
                description.addIdentifier(IdentifierType.KEGG, parts[parts.length - 1]);
            } else if ("pathbank".equalsIgnoreCase(source)) {
                if (id.contains("smpdb")) {
                    final String[] parts = StringUtils.split(id, '/');
                    description.addIdentifier(IdentifierType.SMPDB, parts[parts.length - 1]);
                }
            } else if ("reactome".equalsIgnoreCase(source)) {
                final String[] parts = StringUtils.split(id, '/');
                description.addIdentifier(IdentifierType.REACTOME, parts[parts.length - 1]);
            } else if ("panther".equalsIgnoreCase(source)) {
                final String[] parts = StringUtils.split(id, '/');
                description.addIdentifier(IdentifierType.PANTHER, parts[parts.length - 1]);
            }
            // TODO: netpath, pid, inoh, humancyc
        }
        description.addName(node.getProperty("name"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeGene(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
        description.addIdentifier(IdentifierType.HGNC_SYMBOL, node.<String>getProperty("symbol"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeProtein(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.PROTEIN);
        description.addIdentifier(IdentifierType.UNIPROT_KB, node.<String>getProperty("id"));
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{
                PathwayCommonsGraphExporter.PATHWAY_LABEL, PathwayCommonsGraphExporter.GENE_LABEL,
                PathwayCommonsGraphExporter.PROTEIN_LABEL
        };
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
