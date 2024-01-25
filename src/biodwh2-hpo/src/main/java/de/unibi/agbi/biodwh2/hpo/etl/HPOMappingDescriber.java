package de.unibi.agbi.biodwh2.hpo.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

public final class HPOMappingDescriber extends MappingDescriber {
    public HPOMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (HPOGraphExporter.GENE_LABEL.equals(localMappingLabel))
            return describeGene(node);
        if (HPOGraphExporter.DISEASE_LABEL.equals(localMappingLabel))
            return describeDisease(node);
        return null;
    }

    private NodeMappingDescription[] describeGene(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
        description.addIdentifier(IdentifierType.NCBI_GENE, node.<Integer>getProperty("id"));
        description.addName(node.getProperty("symbol"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeDisease(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.DISEASE);
        final String id = node.getProperty("id");
        final String[] idParts = StringUtils.split(id, ":", 2);
        if (idParts != null && idParts.length == 2) {
            if ("OMIM".equals(idParts[0]))
                description.addIdentifier(IdentifierType.OMIM, Integer.parseInt(idParts[1]));
            else if ("DECIPHER".equals(idParts[0]))
                description.addIdentifier("DECIPHER", idParts[1]);
            else if ("ORPHA".equals(idParts[0]))
                description.addIdentifier(IdentifierType.ORPHANET, idParts[1]);
        }
        final Set<String> names = node.getProperty("names");
        if (names != null)
            for (final String name : names)
                description.addName(name);
        return new NodeMappingDescription[]{description};
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{HPOGraphExporter.GENE_LABEL, HPOGraphExporter.DISEASE_LABEL};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
