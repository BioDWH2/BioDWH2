package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import de.unibi.agbi.biodwh2.core.model.graph.mapping.RNANodeMappingDescription;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

public class MIMappingDescriber extends MappingDescriber {
    public MIMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (MIGraphExporter.INTERACTOR_LABEL.equals(localMappingLabel))
            return describeInteractor(node);
        return null;
    }

    private NodeMappingDescription[] describeInteractor(final Node node) {
        final String id = node.getProperty(GraphExporter.ID_KEY);
        if (id == null)
            return null;
        final String[] idParts = StringUtils.split(id, ":", 2);
        if (idParts.length != 2)
            return null;
        NodeMappingDescription description = null;
        switch (idParts[0].toLowerCase(Locale.ROOT)) {
            case "uniprotkb":
                description = new NodeMappingDescription(NodeMappingDescription.NodeType.PROTEIN);
                description.addIdentifier(IdentifierType.UNIPROT_KB, idParts[1]);
                break;
            case "chebi":
                // chebi:"CHEBI:15422"
                description = new NodeMappingDescription(NodeMappingDescription.NodeType.COMPOUND);
                if (idParts[1].contains(":"))
                    idParts[1] = StringUtils.split(idParts[1], ":", 2)[1];
                idParts[1] = StringUtils.strip(idParts[1], "\"'");
                description.addIdentifier(IdentifierType.CHEBI, Integer.parseInt(idParts[1]));
                break;
            case "ensembl":
                if (idParts[1].length() >= 12) {
                    final char type = idParts[1].charAt(idParts[1].length() - 12);
                    if (type == 'G')
                        description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
                    else if (type == 'P')
                        description = new NodeMappingDescription(NodeMappingDescription.NodeType.PROTEIN);
                    else if (type == 'T')
                        description = new NodeMappingDescription(NodeMappingDescription.NodeType.RNA);
                    if (description != null)
                        description.addIdentifier(IdentifierType.ENSEMBL, idParts[1]);
                }
                break;
            case "refseq":
                // refseq:73973953
                // refseq:NM_004500.2
                // refseq:NP_005821.1
                if (idParts[1].startsWith("NP") || idParts[1].startsWith("XP") || idParts[1].startsWith("WP"))
                    description = new NodeMappingDescription(NodeMappingDescription.NodeType.PROTEIN);
                else if (idParts[1].startsWith("NM") || idParts[1].startsWith("XM"))
                    description = new RNANodeMappingDescription(RNANodeMappingDescription.RNAType.M_RNA);
                if (description != null)
                    description.addIdentifier(IdentifierType.REFSEQ, idParts[1]);
                break;
        }
        // TODO:
        //  intact:EBI-10635749
        //  intenz:2.7.11.22
        //  rhea:"RHEA:57720"
        //  wwpdb:2w9f
        if (description != null)
            return new NodeMappingDescription[]{description};
        return null;
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        if (edges.length == 2) {
            final var negative = nodes[1].<Boolean>getProperty("negative");
            return new PathMappingDescription(
                    negative != null && negative ? PathMappingDescription.EdgeType.INTERACTS_NOT :
                    PathMappingDescription.EdgeType.INTERACTS);
        }
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{MIGraphExporter.INTERACTOR_LABEL};
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[]{
                new PathMapping().add(MIGraphExporter.INTERACTOR_LABEL, MIGraphExporter.INTERACTS_LABEL,
                                      MIGraphExporter.INTERACTION_LABEL, EdgeDirection.FORWARD).add(
                        MIGraphExporter.INTERACTION_LABEL, MIGraphExporter.INTERACTS_LABEL,
                        MIGraphExporter.INTERACTOR_LABEL, EdgeDirection.BACKWARD)
        };
    }
}
