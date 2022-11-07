package de.unibi.agbi.biodwh2.core.model.graph;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Readonly wrapper for a BaseGraph representing a view on specified node and edge labels.
 */
public class GraphView extends BaseGraph {
    private final MVStoreGraph graph;
    private final Set<String> nodeLabels;
    private final Set<String> edgeLabels;

    public GraphView(final MVStoreGraph graph, final String[] nodeLabels, final String[] edgeLabels) {
        this.graph = graph;
        this.nodeLabels = Arrays.stream(nodeLabels).collect(Collectors.toSet());
        this.edgeLabels = Arrays.stream(edgeLabels).collect(Collectors.toSet());
    }

    @Override
    public Iterable<Long> getNodeIds(final String label) {
        return graph.getNodeIds();
    }

    @Override
    public Iterable<Long> getEdgeIds(final String label) {
        return graph.getEdgeIds();
    }

    @Override
    public Iterable<Node> getNodes() {
        return filterNodesIterable(graph.getNodes());
    }

    private Iterable<Node> filterNodesIterable(final Iterable<Node> nodes) {
        return () -> new FilterNodesIterator(nodes.iterator());
    }

    @Override
    public Iterable<Edge> getEdges() {
        return filterEdgesIterable(graph.getEdges());
    }

    private Iterable<Edge> filterEdgesIterable(final Iterable<Edge> edges) {
        return () -> new FilterEdgesIterator(edges.iterator());
    }

    @Override
    public long getNumberOfNodes() {
        return nodeLabels.stream().map(graph::getNumberOfNodes).reduce(0L, Long::sum);
    }

    @Override
    public long getNumberOfNodes(final String label) {
        return nodeLabels.contains(label) ? graph.getNumberOfNodes(label) : 0;
    }

    @Override
    public long getNumberOfEdges() {
        return edgeLabels.stream().map(graph::getNumberOfEdges).reduce(0L, Long::sum);
    }

    @Override
    public long getNumberOfEdges(final String label) {
        return edgeLabels.contains(label) ? graph.getNumberOfEdges(label) : 0;
    }

    @Override
    public String[] getNodeLabels() {
        return nodeLabels.toArray(new String[0]);
    }

    @Override
    public String[] getEdgeLabels() {
        return edgeLabels.toArray(new String[0]);
    }

    @Override
    public Node getNode(final long nodeId) {
        final Node node = graph.getNode(nodeId);
        return node != null && nodeLabels.contains(node.getLabel()) ? node : null;
    }

    @Override
    public String getNodeLabel(long nodeId) {
        final Node node = graph.getNode(nodeId);
        return node != null && nodeLabels.contains(node.getLabel()) ? node.getLabel() : null;
    }

    @Override
    public Edge getEdge(final long edgeId) {
        final Edge edge = graph.getEdge(edgeId);
        return edge != null && edgeLabels.contains(edge.getLabel()) ? edge : null;
    }

    @Override
    public String getEdgeLabel(long edgeId) {
        final Edge edge = graph.getEdge(edgeId);
        return edge != null && edgeLabels.contains(edge.getLabel()) ? edge.getLabel() : null;
    }

    @Override
    public Iterable<Node> findNodes(final String label) {
        if (!nodeLabels.contains(label))
            return Collections.emptyList();
        return graph.findNodes(label);
    }

    @Override
    public Iterable<Node> findNodes(final String label, final String propertyKey, final Comparable<?> value) {
        if (!nodeLabels.contains(label))
            return Collections.emptyList();
        return graph.findNodes(label, propertyKey, value);
    }

    @Override
    public Iterable<Node> findNodes(final String label, final String propertyKey1, final Comparable<?> value1,
                                    final String propertyKey2, final Comparable<?> value2) {
        if (!nodeLabels.contains(label))
            return Collections.emptyList();
        return graph.findNodes(label, propertyKey1, value1, propertyKey2, value2);
    }

    @Override
    public Iterable<Node> findNodes(final String label, final String propertyKey1, final Comparable<?> value1,
                                    final String propertyKey2, final Comparable<?> value2, final String propertyKey3,
                                    final Comparable<?> value3) {
        if (!nodeLabels.contains(label))
            return Collections.emptyList();
        return graph.findNodes(label, propertyKey1, value1, propertyKey2, value2, propertyKey3, value3);
    }

    @Override
    public Iterable<Node> findNodes(final String label, final Map<String, Comparable<?>> properties) {
        if (!nodeLabels.contains(label))
            return Collections.emptyList();
        return graph.findNodes(label, properties);
    }

    @Override
    public Iterable<Node> findNodes(final String propertyKey, final Comparable<?> value) {
        return filterNodesIterable(graph.findNodes(propertyKey, value));
    }

    @Override
    public Iterable<Node> findNodes(final String propertyKey1, final Comparable<?> value1, final String propertyKey2,
                                    final Comparable<?> value2) {
        return filterNodesIterable(graph.findNodes(propertyKey1, value1, propertyKey2, value2));
    }

    @Override
    public Iterable<Node> findNodes(final String propertyKey1, final Comparable<?> value1, final String propertyKey2,
                                    final Comparable<?> value2, final String propertyKey3, final Comparable<?> value3) {
        return filterNodesIterable(graph.findNodes(propertyKey1, value1, propertyKey2, value2, propertyKey3, value3));
    }

    @Override
    public Iterable<Node> findNodes(final String propertyKey1, final Comparable<?> value1, final String propertyKey2,
                                    final Comparable<?> value2, final String propertyKey3, final Comparable<?> value3,
                                    final String propertyKey4, final Comparable<?> value4) {
        return filterNodesIterable(
                graph.findNodes(propertyKey1, value1, propertyKey2, value2, propertyKey3, value3, propertyKey4,
                                value4));
    }

    @Override
    public Iterable<Node> findNodes(final Map<String, Comparable<?>> properties) {
        return filterNodesIterable(graph.findNodes(properties));
    }

    @Override
    public Iterable<Edge> findEdges(final String label) {
        if (!nodeLabels.contains(label))
            return Collections.emptyList();
        return graph.findEdges(label);
    }

    @Override
    public Iterable<Edge> findEdges(final String label, final String propertyKey, final Comparable<?> value) {
        if (!nodeLabels.contains(label))
            return Collections.emptyList();
        return graph.findEdges(label, propertyKey, value);
    }

    @Override
    public Iterable<Edge> findEdges(final String label, final String propertyKey1, final Comparable<?> value1,
                                    final String propertyKey2, final Comparable<?> value2) {
        if (!nodeLabels.contains(label))
            return Collections.emptyList();
        return graph.findEdges(label, propertyKey1, value1, propertyKey2, value2);
    }

    @Override
    public Iterable<Edge> findEdges(final String label, final String propertyKey1, final Comparable<?> value1,
                                    final String propertyKey2, final Comparable<?> value2, final String propertyKey3,
                                    final Comparable<?> value3) {
        if (!nodeLabels.contains(label))
            return Collections.emptyList();
        return graph.findEdges(label, propertyKey1, value1, propertyKey2, value2, propertyKey3, value3);
    }

    @Override
    public Iterable<Edge> findEdges(final String label, final Map<String, Comparable<?>> properties) {
        if (!nodeLabels.contains(label))
            return Collections.emptyList();
        return graph.findEdges(label, properties);
    }

    @Override
    public Iterable<Edge> findEdges(final String propertyKey, final Comparable<?> value) {
        return filterEdgesIterable(graph.findEdges(propertyKey, value));
    }

    @Override
    public Iterable<Edge> findEdges(final String propertyKey1, final Comparable<?> value1, final String propertyKey2,
                                    final Comparable<?> value2) {
        return filterEdgesIterable(graph.findEdges(propertyKey1, value1, propertyKey2, value2));
    }

    @Override
    public Iterable<Edge> findEdges(final String propertyKey1, final Comparable<?> value1, final String propertyKey2,
                                    final Comparable<?> value2, final String propertyKey3, final Comparable<?> value3) {
        return filterEdgesIterable(graph.findEdges(propertyKey1, value1, propertyKey2, value2, propertyKey3, value3));
    }

    @Override
    public Iterable<Edge> findEdges(final String propertyKey1, final Comparable<?> value1, final String propertyKey2,
                                    final Comparable<?> value2, final String propertyKey3, final Comparable<?> value3,
                                    final String propertyKey4, final Comparable<?> value4) {
        return filterEdgesIterable(
                graph.findEdges(propertyKey1, value1, propertyKey2, value2, propertyKey3, value3, propertyKey4,
                                value4));
    }

    @Override
    public Iterable<Edge> findEdges(final Map<String, Comparable<?>> properties) {
        return filterEdgesIterable(graph.findEdges(properties));
    }

    private class FilterNodesIterator implements Iterator<Node> {
        private final Iterator<Node> parent;
        private Node nextNode = null;

        FilterNodesIterator(final Iterator<Node> parent) {
            this.parent = parent;
        }

        @Override
        public boolean hasNext() {
            nextNode = null;
            while (parent.hasNext()) {
                nextNode = parent.next();
                if (nodeLabels.contains(nextNode.getLabel())) {
                    break;
                } else {
                    nextNode = null;
                }
            }
            return nextNode != null;
        }

        @Override
        public Node next() {
            return nextNode;
        }
    }

    private class FilterEdgesIterator implements Iterator<Edge> {
        private final Iterator<Edge> parent;
        private Edge nextEdge = null;

        FilterEdgesIterator(final Iterator<Edge> parent) {
            this.parent = parent;
        }

        @Override
        public boolean hasNext() {
            nextEdge = null;
            while (parent.hasNext()) {
                nextEdge = parent.next();
                if (edgeLabels.contains(nextEdge.getLabel()) && nodeLabels.contains(
                        graph.getNodeLabel(nextEdge.getFromId())) && nodeLabels.contains(
                        graph.getNodeLabel(nextEdge.getToId()))) {
                    break;
                } else {
                    nextEdge = null;
                }
            }
            return nextEdge != null;
        }

        @Override
        public Edge next() {
            return nextEdge;
        }
    }
}
