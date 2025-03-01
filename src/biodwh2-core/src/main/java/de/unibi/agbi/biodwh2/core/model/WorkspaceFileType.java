package de.unibi.agbi.biodwh2.core.model;

import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.GraphFileFormat;

public enum WorkspaceFileType {
    MERGED_PERSISTENT_GRAPH("merged." + Graph.EXTENSION),
    MERGED_GRAPHML_GZ("merged." + GraphFileFormat.GRAPH_ML.extension + ".gz"),
    MERGED_META_GRAPH_IMAGE("merged-meta-graph.png"),
    MERGED_META_GRAPH_STATISTICS("merged-meta-graph-statistics.json"),
    MERGED_META_GRAPH_DYNAMIC_VIS("merged-meta-graph.html"),
    MAPPED_PERSISTENT_GRAPH("mapped." + Graph.EXTENSION),
    MAPPED_LOG_PERSISTENT_GRAPH("mapping-log." + Graph.EXTENSION),
    MAPPED_LOG_CLUSTERS("mapping-log-clusters.json"),
    MAPPED_GRAPHML_GZ("mapped." + GraphFileFormat.GRAPH_ML.extension + ".gz"),
    MAPPED_META_GRAPH_IMAGE("mapped-meta-graph.png"),
    MAPPED_META_GRAPH_STATISTICS("mapped-meta-graph-statistics.json"),
    MAPPED_META_GRAPH_DYNAMIC_VIS("mapped-meta-graph.html");

    private final String name;

    WorkspaceFileType(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
