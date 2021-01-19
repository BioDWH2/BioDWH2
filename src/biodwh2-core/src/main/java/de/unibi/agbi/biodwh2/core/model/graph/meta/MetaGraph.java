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
        for (final Node node : graph.getNodes())
            for (final String label : node.getLabels())
                if (!nodes.containsKey(label)) {
                    final MetaNode metaNode = new MetaNode();
                    metaNode.label = label;
                    nodes.put(label, metaNode);
                }
        for (final Edge edge : graph.getEdges()) {
            final Node fromNode = graph.getNode(edge.getFromId());
            final Node toNode = graph.getNode(edge.getToId());
            for (final String fromLabel : fromNode.getLabels())
                for (final String toLabel : toNode.getLabels()) {
                    final String key = edge.getLabel() + "|" + fromLabel + "|" + toLabel;
                    if (!edges.containsKey(key)) {
                        final MetaEdge metaEdge = new MetaEdge();
                        metaEdge.id = key;
                        metaEdge.label = edge.getLabel();
                        metaEdge.fromLabel = fromLabel;
                        metaEdge.toLabel = toLabel;
                        edges.put(key, metaEdge);
                    }
                }
        }
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
