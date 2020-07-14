package de.unibi.agbi.biodwh2.core.schema;

import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;

import java.util.*;

public class GraphSchema {
    public static class NodeType {
        String label;
        final Set<String> propertyKeys = new HashSet<>();

        public String getLabel() {
            return label;
        }

        public String[] getPropertyKeys() {
            return propertyKeys.toArray(new String[0]);
        }
    }

    public static class EdgeType {
        String label;
        final Set<String> propertyKeys = new HashSet<>();
        final Set<String> fromLabels = new HashSet<>();
        final Set<String> toLabels = new HashSet<>();
    }

    private final Map<String, NodeType> nodeTypes;
    private final Map<String, EdgeType> edgeTypes;

    public GraphSchema(final Graph graph) {
        nodeTypes = new HashMap<>();
        edgeTypes = new HashMap<>();
        loadNodeTypes(graph);
        loadEdgeTypes(graph);
    }

    private void loadNodeTypes(final Graph graph) {
        for (Node node : graph.getNodes())
            loadNodeType(node);
    }

    private void loadNodeType(final Node node) {
        NodeType type = nodeTypes.get(node.getLabel());
        if (type == null) {
            type = new NodeType();
            type.label = node.getLabel();
            nodeTypes.put(type.label, type);
        }
        type.propertyKeys.addAll(node.getPropertyKeys());
    }

    private void loadEdgeTypes(final Graph graph) {
        for (Edge edge : graph.getEdges())
            loadEdgeType(graph, edge);
    }

    private void loadEdgeType(final Graph graph, final Edge edge) {
        EdgeType type = edgeTypes.get(edge.getLabel());
        if (type == null) {
            type = new EdgeType();
            type.label = edge.getLabel();
            edgeTypes.put(type.label, type);
        }
        type.propertyKeys.addAll(edge.getPropertyKeys());
        Node fromNode = graph.getNode(edge.getFromId());
        Node toNode = graph.getNode(edge.getToId());
        type.fromLabels.add(fromNode.getLabel());
        type.toLabels.add(toNode.getLabel());
    }

    public NodeType[] getNodeTypes() {
        return nodeTypes.values().toArray(new NodeType[0]);
    }
}
