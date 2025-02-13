package de.unibi.agbi.biodwh2.core.model;

import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.GraphFileFormat;

public enum DataSourceFileType {
    METADATA("metadata.json"),
    PERSISTENT_GRAPH("intermediate." + Graph.EXTENSION),
    INTERMEDIATE_GRAPHML_GZ("intermediate." + GraphFileFormat.GRAPH_ML.extension + ".gz"),
    META_GRAPH_IMAGE("meta-graph.png"),
    META_GRAPH_STATISTICS("meta-graph-statistics.json"),
    META_GRAPH_DYNAMIC_VIS("meta-graph.html");

    private final String name;

    DataSourceFileType(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
