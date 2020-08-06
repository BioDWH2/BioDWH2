package de.unibi.agbi.biodwh2.ndfrt.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class NDFRTMappingDescriber extends MappingDescriber {
    public NDFRTMappingDescriber(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription describe(Graph graph, Node node) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[0];
    }

    @Override
    public PathMappingDescription describe(Graph graph, Node[] nodes, Edge[] edges) {
        return null;
    }

    @Override
    protected String[][] getEdgeMappingPaths() {
        return new String[0][];
    }
}
