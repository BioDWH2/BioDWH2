package de.unibi.agbi.biodwh2.core.model;

import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.GraphFileFormat;

public enum WorkspaceFileType {
    MERGED_PERSISTENT_GRAPH("merged." + Graph.EXTENSION),
    MERGED_GRAPHML("merged." + GraphFileFormat.GRAPH_ML.extension),
    MERGED_META_GRAPH_IMAGE("merged-meta-graph.png"),
    MERGED_META_GRAPH_STATISTICS("merged-meta-graph-statistics.txt"),
    MAPPED_PERSISTENT_GRAPH("mapped." + Graph.EXTENSION),
    MAPPED_GRAPHML("mapped." + GraphFileFormat.GRAPH_ML.extension),
    MAPPED_META_GRAPH_IMAGE("mapped-meta-graph.png"),
    MAPPED_META_GRAPH_STATISTICS("mapped-meta-graph-statistics.txt");

    private final String name;

    WorkspaceFileType(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
