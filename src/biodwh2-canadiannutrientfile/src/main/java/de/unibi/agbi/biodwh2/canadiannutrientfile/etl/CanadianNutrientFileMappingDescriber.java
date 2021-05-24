package de.unibi.agbi.biodwh2.canadiannutrientfile.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class CanadianNutrientFileMappingDescriber extends MappingDescriber {
    public CanadianNutrientFileMappingDescriber(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(Graph graph, Node node, String localMappingLabel) {
        return new NodeMappingDescription[0];
    }

    @Override
    public PathMappingDescription describe(Graph graph, Node[] nodes, Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[0];
    }

    @Override
    protected String[][] getEdgeMappingPaths() {
        return new String[0][];
    }
}
