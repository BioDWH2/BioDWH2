package de.unibi.agbi.biodwh2.core.model.graph.meta;

import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class MetaGraph {
    private final Map<String, MetaNode> nodes;
    private final Map<String, MetaEdge> edges;

    public MetaGraph(final Graph graph) {
        nodes = new HashMap<>();
        edges = new HashMap<>();
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
        for (final Edge edge : graph.getEdges()) {
            final Node fromNode = graph.getNode(edge.getFromId());
            final Node toNode = graph.getNode(edge.getToId());
            if (fromNode == null || toNode == null)
                continue;
            final String fromLabel = fromNode.getLabel();
            final String toLabel = toNode.getLabel();
            final String key = edge.getLabel() + "|" + fromLabel + "|" + toLabel;
            if (!edges.containsKey(key))
                edges.put(key, new MetaEdge(fromLabel, toLabel, edge.getLabel()));
            edges.get(key).count++;
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
