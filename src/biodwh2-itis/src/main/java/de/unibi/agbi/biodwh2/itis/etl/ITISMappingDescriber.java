package de.unibi.agbi.biodwh2.itis.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class ITISMappingDescriber extends MappingDescriber {
    public ITISMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (ITISGraphExporter.TAXON_LABEL.equals(localMappingLabel))
            return describeTaxon(graph, node);
        return null;
    }

    private NodeMappingDescription describeTaxon(final Graph graph, final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.TAXON);
        description.addIdentifier(IdentifierType.ITIS_TAXON, node.<Integer>getProperty("id"));
        return description;
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{ITISGraphExporter.TAXON_LABEL};
    }

    @Override
    protected String[][] getEdgeMappingPaths() {
        return new String[0][];
    }
}
