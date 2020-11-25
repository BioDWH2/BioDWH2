package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.model.graph.*;

import java.util.Arrays;

public abstract class MappingDescriber {
    private final DataSource dataSource;

    public MappingDescriber(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public abstract NodeMappingDescription describe(final Graph graph, final Node node, final String localMappingLabel);

    public abstract PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges);

    protected abstract String[] getNodeMappingLabels();

    final String prefixLabel(final String label) {
        return dataSource.getId() + Graph.LABEL_PREFIX_SEPARATOR + label;
    }

    final String[][] getPrefixedEdgeMappingPaths() {
        return Arrays.stream(getEdgeMappingPaths()).map(path -> Arrays.stream(path).map(this::prefixLabel)
                                                                      .toArray(String[]::new)).toArray(String[][]::new);
    }

    protected abstract String[][] getEdgeMappingPaths();

    protected final String getLabelWithoutPrefix(final String label) {
        return label.startsWith(dataSource.getId()) ? label.substring(dataSource.getId().length() + 1) : label;
    }

    final String getDataSourceId() {
        return dataSource.getId();
    }
}
