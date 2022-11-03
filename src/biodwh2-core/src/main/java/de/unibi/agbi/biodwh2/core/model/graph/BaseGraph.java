package de.unibi.agbi.biodwh2.core.model.graph;

import de.unibi.agbi.biodwh2.core.collections.CombinedIterable;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public abstract class BaseGraph {
    public final Iterable<Long> getNodeIds() {
        return new CombinedIterable<>(Arrays.stream(getNodeLabels()).map(this::getNodeIds)
                                            .collect(Collectors.toList()));
    }

    public abstract Iterable<Long> getNodeIds(final String label);

    public final Iterable<Long> getEdgeIds() {
        return new CombinedIterable<>(Arrays.stream(getEdgeLabels()).map(this::getEdgeIds)
                                            .collect(Collectors.toList()));
    }

    public abstract Iterable<Long> getEdgeIds(final String label);

    public abstract Iterable<Node> getNodes();

    public abstract Iterable<Edge> getEdges();

    public abstract long getNumberOfNodes();

    public abstract long getNumberOfNodes(final String label);

    public abstract long getNumberOfEdges();

    public abstract long getNumberOfEdges(final String label);

    public abstract String[] getNodeLabels();

    public abstract String[] getEdgeLabels();

    public abstract Node getNode(final long nodeId);

    public abstract String getNodeLabel(final long nodeId);

    public abstract Edge getEdge(final long edgeId);

    public abstract String getEdgeLabel(final long edgeId);

    public abstract Iterable<Node> findNodes(final String label);

    public abstract Iterable<Node> findNodes(final String label, final String propertyKey, final Comparable<?> value);

    public abstract Iterable<Node> findNodes(final String label, final String propertyKey1, final Comparable<?> value1,
                                             final String propertyKey2, final Comparable<?> value2);

    public abstract Iterable<Node> findNodes(final String label, final String propertyKey1, final Comparable<?> value1,
                                             final String propertyKey2, final Comparable<?> value2,
                                             final String propertyKey3, final Comparable<?> value3);

    public abstract Iterable<Node> findNodes(final String label, final Map<String, Comparable<?>> properties);

    public abstract Iterable<Node> findNodes(final String propertyKey, final Comparable<?> value);

    public abstract Iterable<Node> findNodes(final String propertyKey1, final Comparable<?> value1,
                                             final String propertyKey2, final Comparable<?> value2);

    public abstract Iterable<Node> findNodes(final String propertyKey1, final Comparable<?> value1,
                                             final String propertyKey2, final Comparable<?> value2,
                                             final String propertyKey3, final Comparable<?> value3);

    public abstract Iterable<Node> findNodes(final String propertyKey1, final Comparable<?> value1,
                                             final String propertyKey2, final Comparable<?> value2,
                                             final String propertyKey3, final Comparable<?> value3,
                                             final String propertyKey4, final Comparable<?> value4);

    public abstract Iterable<Node> findNodes(final Map<String, Comparable<?>> properties);

    public abstract Iterable<Edge> findEdges(final String label);

    public abstract Iterable<Edge> findEdges(final String label, final String propertyKey, final Comparable<?> value);

    public abstract Iterable<Edge> findEdges(final String label, final String propertyKey1, final Comparable<?> value1,
                                             final String propertyKey2, final Comparable<?> value2);

    public abstract Iterable<Edge> findEdges(final String label, final String propertyKey1, final Comparable<?> value1,
                                             final String propertyKey2, final Comparable<?> value2,
                                             final String propertyKey3, final Comparable<?> value3);

    public abstract Iterable<Edge> findEdges(final String label, final Map<String, Comparable<?>> properties);

    public abstract Iterable<Edge> findEdges(final String propertyKey, final Comparable<?> value);

    public abstract Iterable<Edge> findEdges(final String propertyKey1, final Comparable<?> value1,
                                             final String propertyKey2, final Comparable<?> value2);

    public abstract Iterable<Edge> findEdges(final String propertyKey1, final Comparable<?> value1,
                                             final String propertyKey2, final Comparable<?> value2,
                                             final String propertyKey3, final Comparable<?> value3);

    public abstract Iterable<Edge> findEdges(final String propertyKey1, final Comparable<?> value1,
                                             final String propertyKey2, final Comparable<?> value2,
                                             final String propertyKey3, final Comparable<?> value3,
                                             final String propertyKey4, final Comparable<?> value4);

    public abstract Iterable<Edge> findEdges(final Map<String, Comparable<?>> properties);

    public Iterable<Node> getNodes(final String label) {
        if (label == null || label.length() == 0)
            return getNodes();
        return findNodes(label);
    }

    public Iterable<Edge> getEdges(final String label) {
        if (label == null || label.length() == 0)
            return getEdges();
        return findEdges(label);
    }

    public Node findNode(final String label) {
        return firstOrDefault(findNodes(label));
    }

    private <T> T firstOrDefault(final Iterable<T> iterable) {
        return firstOrDefault(iterable.iterator());
    }

    private <T> T firstOrDefault(final Iterator<T> iterator) {
        return iterator.hasNext() ? iterator.next() : null;
    }

    public Node findNode(final String label, final String propertyKey, final Comparable<?> value) {
        return firstOrDefault(findNodes(label, propertyKey, value));
    }

    public Node findNode(final String label, final String propertyKey1, final Comparable<?> value1,
                         final String propertyKey2, final Comparable<?> value2) {
        return firstOrDefault(findNodes(label, propertyKey1, value1, propertyKey2, value2));
    }

    public Node findNode(final String label, final String propertyKey1, final Comparable<?> value1,
                         final String propertyKey2, final Comparable<?> value2, final String propertyKey3,
                         final Comparable<?> value3) {
        return firstOrDefault(findNodes(label, propertyKey1, value1, propertyKey2, value2, propertyKey3, value3));
    }

    public Node findNode(final String label, final Map<String, Comparable<?>> properties) {
        return firstOrDefault(findNodes(label, properties));
    }

    public Node findNode(final String propertyKey, final Comparable<?> value) {
        return firstOrDefault(findNodes(propertyKey, value));
    }

    public Node findNode(final String propertyKey1, final Comparable<?> value1, final String propertyKey2,
                         final Comparable<?> value2) {
        return firstOrDefault(findNodes(propertyKey1, value1, propertyKey2, value2));
    }

    public Node findNode(final String propertyKey1, final Comparable<?> value1, final String propertyKey2,
                         final Comparable<?> value2, final String propertyKey3, final Comparable<?> value3) {
        return firstOrDefault(findNodes(propertyKey1, value1, propertyKey2, value2, propertyKey3, value3));
    }

    public Node findNode(final Map<String, Comparable<?>> properties) {
        return firstOrDefault(findNodes(properties));
    }

    public Edge findEdge(final String label) {
        return firstOrDefault(findEdges(label));
    }

    public Edge findEdge(final String label, final String propertyKey, final Comparable<?> value) {
        return firstOrDefault(findEdges(label, propertyKey, value));
    }

    public Edge findEdge(final String label, final String propertyKey1, final Comparable<?> value1,
                         final String propertyKey2, final Comparable<?> value2) {
        return firstOrDefault(findEdges(label, propertyKey1, value1, propertyKey2, value2));
    }

    public Edge findEdge(final String label, final String propertyKey1, final Comparable<?> value1,
                         final String propertyKey2, final Comparable<?> value2, final String propertyKey3,
                         final Comparable<?> value3) {
        return firstOrDefault(findEdges(label, propertyKey1, value1, propertyKey2, value2, propertyKey3, value3));
    }

    public Edge findEdge(final String label, final Map<String, Comparable<?>> properties) {
        return firstOrDefault(findEdges(label, properties));
    }

    public Edge findEdge(final String propertyKey, final Comparable<?> value) {
        return firstOrDefault(findEdges(propertyKey, value));
    }

    public Edge findEdge(final String propertyKey1, final Comparable<?> value1, final String propertyKey2,
                         final Comparable<?> value2) {
        return firstOrDefault(findEdges(propertyKey1, value1, propertyKey2, value2));
    }

    public Edge findEdge(final String propertyKey1, final Comparable<?> value1, final String propertyKey2,
                         final Comparable<?> value2, final String propertyKey3, final Comparable<?> value3) {
        return firstOrDefault(findEdges(propertyKey1, value1, propertyKey2, value2, propertyKey3, value3));
    }

    public Edge findEdge(final Map<String, Comparable<?>> properties) {
        return firstOrDefault(findEdges(properties));
    }

    public Long[] getAdjacentNodeIdsForEdgeLabel(final long nodeId) {
        return getAdjacentNodeIdsForEdgeLabel(nodeId, null, EdgeDirection.BIDIRECTIONAL);
    }

    public Long[] getAdjacentNodeIdsForEdgeLabel(final long nodeId, final String edgeLabel) {
        return getAdjacentNodeIdsForEdgeLabel(nodeId, edgeLabel, EdgeDirection.BIDIRECTIONAL);
    }

    /**
     * Find nodes directly connected to the provided node with a specified edge label and direction.
     *
     * @param nodeId    ID of the node to find adjacent nodes for
     * @param edgeLabel Label filter for connected edges (default: null)
     * @param direction Direction filter for connected edges (default: BIDIRECTIONAL)
     * @return Array of directly connected node IDs
     */
    public Long[] getAdjacentNodeIdsForEdgeLabel(final long nodeId, final String edgeLabel,
                                                 final EdgeDirection direction) {
        final Set<Long> nodeIds = new HashSet<>();
        if (direction != EdgeDirection.BACKWARD) {
            if (edgeLabel == null)
                for (final Edge edge : findEdges(Edge.FROM_ID_FIELD, nodeId))
                    nodeIds.add(edge.getToId());
            else
                for (final Edge edge : findEdges(edgeLabel, Edge.FROM_ID_FIELD, nodeId))
                    nodeIds.add(edge.getToId());
        }
        if (direction != EdgeDirection.FORWARD) {
            if (edgeLabel == null)
                for (final Edge edge : findEdges(Edge.TO_ID_FIELD, nodeId))
                    nodeIds.add(edge.getFromId());
            else
                for (final Edge edge : findEdges(edgeLabel, Edge.TO_ID_FIELD, nodeId))
                    nodeIds.add(edge.getFromId());
        }
        return nodeIds.toArray(new Long[0]);
    }
}
