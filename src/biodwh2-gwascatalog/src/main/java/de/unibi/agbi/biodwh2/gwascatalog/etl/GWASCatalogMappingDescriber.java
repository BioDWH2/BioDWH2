package de.unibi.agbi.biodwh2.gwascatalog.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import de.unibi.agbi.biodwh2.core.model.graph.mapping.PublicationNodeMappingDescription;

public final class GWASCatalogMappingDescriber extends MappingDescriber {
    public GWASCatalogMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (GWASCatalogGraphExporter.PUBLICATION_LABEL.equals(localMappingLabel))
            return describePublication(node);
        return null;
    }

    private NodeMappingDescription[] describePublication(final Node node) {
        final PublicationNodeMappingDescription description = new PublicationNodeMappingDescription();
        description.pubmedId = node.getProperty("pmid");
        description.addIdentifier(IdentifierType.PUBMED_ID, description.pubmedId);
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{GWASCatalogGraphExporter.PUBLICATION_LABEL};
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
