package de.unibi.agbi.biodwh2.core.model.graph.meta;

import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class MetaGraph {
    private final Map<String, MetaNode> nodes = new HashMap<>();
    private final Map<String, MetaEdge> edges = new HashMap<>();

    public MetaGraph(final Graph graph) {
        addMetaNodes(graph);
        addMetaEdges(graph);
    }

    private void addMetaNodes(final Graph graph) {
        for (final String label : graph.getNodeLabels()) {
            final MetaNode node = new MetaNode(label);
            node.count = graph.getNumberOfNodes(label);
            nodes.put(label, node);
        }
    }

    private void addMetaEdges(final Graph graph) {
        for (final String label : graph.getEdgeLabels()) {
            for (final Edge edge : graph.getEdges(label)) {
                final String fromLabel = graph.getNodeLabel(edge.getFromId());
                final String toLabel = graph.getNodeLabel(edge.getToId());
                final String key = label + '|' + fromLabel + '|' + toLabel;
                MetaEdge metaEdge = edges.get(key);
                if (metaEdge == null) {
                    metaEdge = new MetaEdge(fromLabel, toLabel, label);
                    edges.put(key, metaEdge);
                }
                metaEdge.count++;
            }
        }
    }

    public long getNodeLabelCount() {
        return nodes.size();
    }

    public long getTotalNodeCount() {
        return nodes.values().stream().map(n -> n.count).reduce(0L, Long::sum);
    }

    public long getEdgeLabelCount() {
        return edges.values().stream().map(e -> e.label).distinct().count();
    }

    public long getTotalEdgeCount() {
        return edges.values().stream().map(e -> e.count).reduce(0L, Long::sum);
    }

    public Collection<MetaNode> getNodes() {
        return nodes.values();
    }

    public Collection<MetaEdge> getEdges() {
        return edges.values();
    }
}
