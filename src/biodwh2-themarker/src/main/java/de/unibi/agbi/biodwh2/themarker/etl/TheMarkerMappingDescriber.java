package de.unibi.agbi.biodwh2.themarker.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class TheMarkerMappingDescriber extends MappingDescriber {
    public TheMarkerMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (TheMarkerGraphExporter.DISEASE_LABEL.equals(localMappingLabel))
            return describeDisease(node);
        if (TheMarkerGraphExporter.DRUG_LABEL.equals(localMappingLabel))
            return describeDrug(node);
        if (TheMarkerGraphExporter.MARKER_LABEL.equals(localMappingLabel))
            return describeMarker(node);
        return null;
    }

    private NodeMappingDescription[] describeDisease(final Node node) {
        // TODO
        return null;
    }

    private NodeMappingDescription[] describeDrug(final Node node) {
        // TODO
        return null;
    }

    private NodeMappingDescription[] describeMarker(final Node node) {
        // TODO
        return null;
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{
                TheMarkerGraphExporter.DISEASE_LABEL, TheMarkerGraphExporter.DRUG_LABEL,
                TheMarkerGraphExporter.MARKER_LABEL
        };
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
