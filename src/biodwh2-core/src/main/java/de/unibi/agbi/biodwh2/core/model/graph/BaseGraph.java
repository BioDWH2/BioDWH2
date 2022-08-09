package de.unibi.agbi.biodwh2.core.model.graph;

import java.util.Map;

public interface BaseGraph {
    Iterable<Node> getNodes();

    Iterable<Edge> getEdges();

    long getNumberOfNodes();

    long getNumberOfNodes(final String label);

    long getNumberOfEdges();

    long getNumberOfEdges(final String label);

    String[] getNodeLabels();

    String[] getEdgeLabels();

    Node getNode(final long nodeId);

    Edge getEdge(final long edgeId);

    Iterable<Node> findNodes(final String label);

    Iterable<Node> findNodes(final String label, final String propertyKey, final Comparable<?> value);

    Iterable<Node> findNodes(final String label, final String propertyKey1, final Comparable<?> value1,
                             final String propertyKey2, final Comparable<?> value2);

    Iterable<Node> findNodes(final String label, final String propertyKey1, final Comparable<?> value1,
                             final String propertyKey2, final Comparable<?> value2, final String propertyKey3,
                             final Comparable<?> value3);

    Iterable<Node> findNodes(final String label, final Map<String, Comparable<?>> properties);

    Iterable<Node> findNodes(final String propertyKey, final Comparable<?> value);

    Iterable<Node> findNodes(final String propertyKey1, final Comparable<?> value1, final String propertyKey2,
                             final Comparable<?> value2);

    Iterable<Node> findNodes(final String propertyKey1, final Comparable<?> value1, final String propertyKey2,
                             final Comparable<?> value2, final String propertyKey3, final Comparable<?> value3);

    Iterable<Node> findNodes(final String propertyKey1, final Comparable<?> value1, final String propertyKey2,
                             final Comparable<?> value2, final String propertyKey3, final Comparable<?> value3,
                             final String propertyKey4, final Comparable<?> value4);

    Iterable<Node> findNodes(final Map<String, Comparable<?>> properties);

    Iterable<Edge> findEdges(final String label);

    Iterable<Edge> findEdges(final String label, final String propertyKey, final Comparable<?> value);

    Iterable<Edge> findEdges(final String label, final String propertyKey1, final Comparable<?> value1,
                             final String propertyKey2, final Comparable<?> value2);

    Iterable<Edge> findEdges(final String label, final String propertyKey1, final Comparable<?> value1,
                             final String propertyKey2, final Comparable<?> value2, final String propertyKey3,
                             final Comparable<?> value3);

    Iterable<Edge> findEdges(final String label, final Map<String, Comparable<?>> properties);

    Iterable<Edge> findEdges(final String propertyKey, final Comparable<?> value);

    Iterable<Edge> findEdges(final String propertyKey1, final Comparable<?> value1, final String propertyKey2,
                             final Comparable<?> value2);

    Iterable<Edge> findEdges(final String propertyKey1, final Comparable<?> value1, final String propertyKey2,
                             final Comparable<?> value2, final String propertyKey3, final Comparable<?> value3);

    Iterable<Edge> findEdges(final String propertyKey1, final Comparable<?> value1, final String propertyKey2,
                             final Comparable<?> value2, final String propertyKey3, final Comparable<?> value3,
                             final String propertyKey4, final Comparable<?> value4);

    Iterable<Edge> findEdges(final Map<String, Comparable<?>> properties);
}
