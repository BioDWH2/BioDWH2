package de.unibi.agbi.biodwh2.interpro.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import org.apache.commons.lang3.StringUtils;

public class InterProMappingDescriber extends MappingDescriber {
    public InterProMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (InterProGraphExporter.PUBLICATION_LABEL.equals(localMappingLabel))
            return describePublication(node);
        if (InterProGraphExporter.DOMAIN_LABEL.equals(localMappingLabel))
            return describeDomain(node);
        return null;
    }

    private NodeMappingDescription[] describePublication(final Node node) {
        final Integer pubmedId = node.getProperty("pmid");
        if (pubmedId == null)
            return null;
        final NodeMappingDescription description = new NodeMappingDescription(
                NodeMappingDescription.NodeType.PUBLICATION);
        description.addIdentifier(IdentifierType.PUBMED_ID, pubmedId);
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeDomain(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(
                NodeMappingDescription.NodeType.PROTEIN_DOMAIN);
        final String[] memberIds = node.getProperty("members");
        if (memberIds != null) {
            for (final String memberId : memberIds) {
                final String[] memberIdParts = StringUtils.split(memberId, ":", 1);
                if ("PFAM".equals(memberIdParts[0]))
                    description.addIdentifier(IdentifierType.PFAM, memberIdParts[1]);
                // CDD:cd08342
                // PROFILE:PS51797
                // PRINTS:PR00026
                // HAMAP:MF_00137
                // SFLD:SFLDG01019
                // TIGRFAMs:TIGR00638
                // SMART:SM00475
                // PROSITE:PS01185
                // PIRSF:PIRSF000770
                // PANTHER:PTHR47097
            }
        }
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{
                InterProGraphExporter.PUBLICATION_LABEL, InterProGraphExporter.DOMAIN_LABEL
        };
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
