package de.unibi.agbi.biodwh2.efo.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import org.apache.commons.lang3.StringUtils;

public class EFOMappingDescriber extends MappingDescriber {
    private Long diseaseNodeId;

    public EFOMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (diseaseNodeId == null)
            diseaseNodeId = graph.findNode("EFO_Term", "id", "EFO:0000408").getId();
        if ("Term".equals(localMappingLabel)) {
            if (isNodeChildOfDisease(graph, node.getId())) {
                final NodeMappingDescription description = new NodeMappingDescription(
                        NodeMappingDescription.NodeType.DISEASE);
                final String id = node.getProperty("id");
                if (id != null) {
                    final String[] idParts = StringUtils.split(id, ':');
                    if ("EFO".equals(idParts[0]))
                        description.addIdentifier(IdentifierType.EFO, idParts[1]);
                    else if ("Orphanet".equals(idParts[0]))
                        description.addIdentifier(IdentifierType.ORPHANET, idParts[1]);
                    else if ("MONDO".equals(idParts[0]))
                        description.addIdentifier(IdentifierType.MONDO, idParts[1]);
                    else if ("DOID".equals(idParts[0]))
                        description.addIdentifier("DOID", idParts[1]);
                    else if ("HP".equals(idParts[0]))
                        description.addIdentifier("HP", idParts[1]);
                    else if ("NCIT".equals(idParts[0]))
                        description.addIdentifier("NCIT", idParts[1]);
                }
                // TODO: xrefs
                description.addName(node.getProperty("name"));
                return new NodeMappingDescription[]{description};
            }
        }
        return null;
    }

    private boolean isNodeChildOfDisease(final Graph graph, final long nodeId) {
        for (final long parentId : graph.getAdjacentNodeIdsForEdgeLabel(nodeId, "EFO_IS_A", EdgeDirection.FORWARD))
            if (diseaseNodeId == parentId || isNodeChildOfDisease(graph, parentId))
                return true;
        return false;
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{"Term"};
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
