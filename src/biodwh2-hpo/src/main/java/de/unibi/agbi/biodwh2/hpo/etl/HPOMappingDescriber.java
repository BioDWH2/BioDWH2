package de.unibi.agbi.biodwh2.hpo.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

import static de.unibi.agbi.biodwh2.hpo.etl.HPOGraphExporter.*;

public final class HPOMappingDescriber extends MappingDescriber {
    public HPOMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (GENE_LABEL.equals(localMappingLabel))
            return describeGene(node);
        if (DISEASE_LABEL.equals(localMappingLabel))
            return describeDisease(node);
        return null;
    }

    private NodeMappingDescription[] describeGene(final Node node) {
        final var description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
        description.addIdentifier(IdentifierType.NCBI_GENE, node.<Integer>getProperty(GraphExporter.ID_KEY));
        description.addName(node.getProperty("symbol"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeDisease(final Node node) {
        final var description = new NodeMappingDescription(NodeMappingDescription.NodeType.DISEASE);
        final String id = node.getProperty(GraphExporter.ID_KEY);
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
        return new String[]{GENE_LABEL, DISEASE_LABEL};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        if (edges.length == 2)
            return new PathMappingDescription(PathMappingDescription.EdgeType.ASSOCIATED_WITH);
        return null;
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        final var geneDiseaseAssociationPath = new PathMapping();
        geneDiseaseAssociationPath.add(GENE_LABEL, ASSOCIATED_WITH_LABEL, ASSOCIATION_LABEL, EdgeDirection.FORWARD);
        geneDiseaseAssociationPath.add(ASSOCIATION_LABEL, ASSOCIATED_WITH_LABEL, DISEASE_LABEL, EdgeDirection.BACKWARD);
        return new PathMapping[]{geneDiseaseAssociationPath};
    }
}
