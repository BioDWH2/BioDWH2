package de.unibi.agbi.biodwh2.interpro.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class InterProMappingDescriber extends MappingDescriber {
    public InterProMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (InterProGraphExporter.PUBLICATION_LABEL.equals(localMappingLabel)) {
            final Integer pubmedId = node.getProperty("pmid");
            if (pubmedId != null) {
                final NodeMappingDescription description = new NodeMappingDescription(
                        NodeMappingDescription.NodeType.PUBLICATION);
                description.addIdentifier(IdentifierType.PUBMED_ID, pubmedId);
                return new NodeMappingDescription[]{description};
            }
        }
        return null;
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{InterProGraphExporter.PUBLICATION_LABEL};
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
