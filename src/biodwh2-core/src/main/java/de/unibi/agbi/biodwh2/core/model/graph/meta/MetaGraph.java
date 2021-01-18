package de.unibi.agbi.biodwh2.core.model.graph.meta;

import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class MetaGraph {
    private final Map<String, MetaNode> nodes;
    private final Map<String, MetaEdge> edges;

    public MetaGraph(final Graph graph) {
        nodes = new HashMap<>();
        edges = new HashMap<>();
        for (final Node node : graph.getNodes()) {
            final String key = getKeyForLabels(node.getLabels());
            if (!nodes.containsKey(key)) {
                final MetaNode metaNode = new MetaNode();
                metaNode.id = key;
                metaNode.labels = node.getLabels();
                nodes.put(key, metaNode);
            }
        }
        for (final Edge edge : graph.getEdges()) {
            final Node fromNode = graph.getNode(edge.getFromId());
            final Node toNode = graph.getNode(edge.getToId());
            final String fromKey = getKeyForLabels(fromNode.getLabels());
            final String toKey = getKeyForLabels(toNode.getLabels());
            final String key = edge.getLabel() + "|" + fromKey + "|" + toKey;
            if (!edges.containsKey(key)) {
                final MetaEdge metaEdge = new MetaEdge();
                metaEdge.id = key;
                metaEdge.label = edge.getLabel();
                metaEdge.fromId = fromKey;
                metaEdge.toId = toKey;
                edges.put(key, metaEdge);
            }
        }
    }

    private String getKeyForLabels(String... labels) {
        return Arrays.stream(labels).sorted().collect(Collectors.joining("_"));
    }

    public int getNodeCount() {
        return nodes.size();
    }

    public Collection<MetaNode> getNodes() {
        return nodes.values();
    }

    public Collection<MetaEdge> getEdges() {
        return edges.values();
    }
}
