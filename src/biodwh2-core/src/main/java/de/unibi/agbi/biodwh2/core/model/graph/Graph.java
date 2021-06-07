package de.unibi.agbi.biodwh2.core.model.graph;

import de.unibi.agbi.biodwh2.core.exceptions.GraphCacheException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@SuppressWarnings("unused")
public final class Graph extends BaseGraph {
    private final Map<Class<?>, ClassMapping> classMappingsCache = new HashMap<>();

    public Graph(final String databaseFilePath) {
        this(Paths.get(databaseFilePath), false, false);
    }

    public Graph(final Path databaseFilePath) {
        this(databaseFilePath, false, false);
    }

    public Graph(final Path databaseFilePath, final boolean reopen) {
        this(databaseFilePath, reopen, false);
    }

    public Graph(final Path databaseFilePath, final boolean reopen, final boolean readOnly) {
        super(databaseFilePath, reopen, readOnly);
    }

    public Node addNode(final String label) {
        final Node n = Node.newNode(label);
        update(n);
        return n;
    }

    public Node addNode(final String label, final String propertyKey, final Object propertyValue) {
        final Node n = Node.newNode(label);
        n.setProperty(propertyKey, propertyValue);
        update(n);
        return n;
    }

    public Node addNode(final String label, final String propertyKey1, final Object propertyValue1,
                        final String propertyKey2, final Object propertyValue2) {
        final Node n = Node.newNode(label);
        n.setProperty(propertyKey1, propertyValue1);
        n.setProperty(propertyKey2, propertyValue2);
        update(n);
        return n;
    }

    public Node addNode(final String label, final String propertyKey1, final Object propertyValue1,
                        final String propertyKey2, final Object propertyValue2, final String propertyKey3,
                        final Object propertyValue3) {
        final Node n = Node.newNode(label);
        n.setProperty(propertyKey1, propertyValue1);
        n.setProperty(propertyKey2, propertyValue2);
        n.setProperty(propertyKey3, propertyValue3);
        update(n);
        return n;
    }

    public Node addNode(final String label, final String propertyKey1, final Object propertyValue1,
                        final String propertyKey2, final Object propertyValue2, final String propertyKey3,
                        final Object propertyValue3, final String propertyKey4, final Object propertyValue4) {
        final Node n = Node.newNode(label);
        n.setProperty(propertyKey1, propertyValue1);
        n.setProperty(propertyKey2, propertyValue2);
        n.setProperty(propertyKey3, propertyValue3);
        n.setProperty(propertyKey4, propertyValue4);
        update(n);
        return n;
    }

    public Node addNode(final String label, final Map<String, Object> properties) {
        final Node n = Node.newNode(label);
        for (Map.Entry<String, Object> entry : properties.entrySet())
            n.setProperty(entry.getKey(), entry.getValue());
        update(n);
        return n;
    }

    Node addNode(final String[] labels, final Map<String, Object> properties) {
        final Node n = Node.newNode(labels);
        for (Map.Entry<String, Object> entry : properties.entrySet())
            n.setProperty(entry.getKey(), entry.getValue());
        update(n);
        return n;
    }

    public NodeBuilder buildNode() {
        return Node.newNodeBuilder(this);
    }

    public final <T> Node addNodeFromModel(final T obj) {
        final ClassMapping mapping = getClassMappingFromCache(obj.getClass());
        final Node n = Node.newNode(mapping.labels);
        mapping.setNodeProperties(n, obj);
        update(n);
        return n;
    }

    private ClassMapping getClassMappingFromCache(final Class<?> type) {
        if (!classMappingsCache.containsKey(type))
            classMappingsCache.put(type, new ClassMapping(type));
        return classMappingsCache.get(type);
    }

    public final <T> Node addNodeFromModel(final T obj, final String propertyKey, final Object propertyValue) {
        final ClassMapping mapping = getClassMappingFromCache(obj.getClass());
        final Node n = Node.newNode(mapping.labels);
        mapping.setNodeProperties(n, obj);
        n.setProperty(propertyKey, propertyValue);
        update(n);
        return n;
    }

    public final <T> Node addNodeFromModel(final T obj, final String propertyKey1, final Object propertyValue1,
                                           final String propertyKey2, final Object propertyValue2) {
        final ClassMapping mapping = getClassMappingFromCache(obj.getClass());
        final Node n = Node.newNode(mapping.labels);
        mapping.setNodeProperties(n, obj);
        n.setProperty(propertyKey1, propertyValue1);
        n.setProperty(propertyKey2, propertyValue2);
        update(n);
        return n;
    }

    public final <T> Node addNodeFromModel(final T obj, final String propertyKey1, final Object propertyValue1,
                                           final String propertyKey2, final Object propertyValue2,
                                           final String propertyKey3, final Object propertyValue3) {
        final ClassMapping mapping = getClassMappingFromCache(obj.getClass());
        final Node n = Node.newNode(mapping.labels);
        mapping.setNodeProperties(n, obj);
        n.setProperty(propertyKey1, propertyValue1);
        n.setProperty(propertyKey2, propertyValue2);
        n.setProperty(propertyKey3, propertyValue3);
        update(n);
        return n;
    }

    public final <T> Node addNodeFromModel(final T obj, final String propertyKey1, final Object propertyValue1,
                                           final String propertyKey2, final Object propertyValue2,
                                           final String propertyKey3, final Object propertyValue3,
                                           final String propertyKey4, final Object propertyValue4) {
        final ClassMapping mapping = getClassMappingFromCache(obj.getClass());
        final Node n = Node.newNode(mapping.labels);
        mapping.setNodeProperties(n, obj);
        n.setProperty(propertyKey1, propertyValue1);
        n.setProperty(propertyKey2, propertyValue2);
        n.setProperty(propertyKey3, propertyValue3);
        n.setProperty(propertyKey4, propertyValue4);
        update(n);
        return n;
    }

    public Edge addEdge(final Node from, final Node to, final String label) {
        validateSourceNode(from);
        validateTargetNode(to);
        return addEdge(from.getId(), to.getId(), label);
    }

    private void validateSourceNode(final Node node) {
        if (node == null)
            throw new GraphCacheException("Failed to add edge because the source node is null");
    }

    private void validateTargetNode(final Node node) {
        if (node == null)
            throw new GraphCacheException("Failed to add edge because the target node is null");
    }

    public Edge addEdge(final long fromId, final Node to, final String label) {
        validateTargetNode(to);
        return addEdge(fromId, to.getId(), label);
    }

    public Edge addEdge(final Node from, final long toId, final String label) {
        validateSourceNode(from);
        return addEdge(from.getId(), toId, label);
    }

    public Edge addEdge(final long fromId, final long toId, final String label) {
        final Edge e = Edge.newEdge(fromId, toId, label);
        update(e);
        return e;
    }

    public Edge addEdge(final Node from, final Node to, final String label, final String propertyKey,
                        final Object propertyValue) {
        validateSourceNode(from);
        validateTargetNode(to);
        return addEdge(from.getId(), to.getId(), label, propertyKey, propertyValue);
    }

    public Edge addEdge(final long fromId, final Node to, final String label, final String propertyKey,
                        final Object propertyValue) {
        validateTargetNode(to);
        return addEdge(fromId, to.getId(), label, propertyKey, propertyValue);
    }

    public Edge addEdge(final Node from, final long toId, final String label, final String propertyKey,
                        final Object propertyValue) {
        validateSourceNode(from);
        return addEdge(from.getId(), toId, label, propertyKey, propertyValue);
    }

    public Edge addEdge(final long fromId, final long toId, final String label, final String propertyKey,
                        final Object propertyValue) {
        final Edge e = Edge.newEdge(fromId, toId, label);
        e.setProperty(propertyKey, propertyValue);
        update(e);
        return e;
    }

    public Edge addEdge(final Node from, final Node to, final String label, final String propertyKey1,
                        final Object propertyValue1, final String propertyKey2, final Object propertyValue2) {
        validateSourceNode(from);
        validateTargetNode(to);
        return addEdge(from.getId(), to.getId(), label, propertyKey1, propertyValue1, propertyKey2, propertyValue2);
    }

    public Edge addEdge(final long fromId, final Node to, final String label, final String propertyKey1,
                        final Object propertyValue1, final String propertyKey2, final Object propertyValue2) {
        validateTargetNode(to);
        return addEdge(fromId, to.getId(), label, propertyKey1, propertyValue1, propertyKey2, propertyValue2);
    }

    public Edge addEdge(final Node from, final long toId, final String label, final String propertyKey1,
                        final Object propertyValue1, final String propertyKey2, final Object propertyValue2) {
        validateSourceNode(from);
        return addEdge(from.getId(), toId, label, propertyKey1, propertyValue1, propertyKey2, propertyValue2);
    }

    public Edge addEdge(final long fromId, final long toId, final String label, final String propertyKey1,
                        final Object propertyValue1, final String propertyKey2, final Object propertyValue2) {
        final Edge e = Edge.newEdge(fromId, toId, label);
        e.setProperty(propertyKey1, propertyValue1);
        e.setProperty(propertyKey2, propertyValue2);
        update(e);
        return e;
    }

    public Edge addEdge(final Node from, final Node to, final String label, final Map<String, Object> properties) {
        validateSourceNode(from);
        validateTargetNode(to);
        return addEdge(from.getId(), to.getId(), label, properties);
    }

    public Edge addEdge(final long fromId, final Node to, final String label, final Map<String, Object> properties) {
        validateTargetNode(to);
        return addEdge(fromId, to.getId(), label, properties);
    }

    public Edge addEdge(final Node from, final long toId, final String label, final Map<String, Object> properties) {
        validateSourceNode(from);
        return addEdge(from.getId(), toId, label, properties);
    }

    public Edge addEdge(final long fromId, final long toId, final String label, final Map<String, Object> properties) {
        final Edge e = Edge.newEdge(fromId, toId, label);
        for (Map.Entry<String, Object> entry : properties.entrySet())
            e.setProperty(entry.getKey(), entry.getValue());
        update(e);
        return e;
    }

    public EdgeBuilder buildEdge() {
        return new EdgeBuilder(this);
    }

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
        return firstOrDefault(findNodes(Node.LABELS_FIELD, label));
    }

    private <T> T firstOrDefault(final Iterable<T> iterable) {
        return firstOrDefault(iterable.iterator());
    }

    private <T> T firstOrDefault(final Iterator<T> iterator) {
        return iterator.hasNext() ? iterator.next() : null;
    }

    public Node findNode(final String label, final String propertyKey, final Comparable<?> value) {
        return firstOrDefault(findNodes(Node.LABELS_FIELD, label, propertyKey, value));
    }

    public Node findNode(final String label, final String propertyKey1, final Comparable<?> value1,
                         final String propertyKey2, final Comparable<?> value2) {
        return firstOrDefault(findNodes(Node.LABELS_FIELD, label, propertyKey1, value1, propertyKey2, value2));
    }

    public Node findNode(final String label, final String propertyKey1, final Comparable<?> value1,
                         final String propertyKey2, final Comparable<?> value2, final String propertyKey3,
                         final Comparable<?> value3) {
        return firstOrDefault(
                findNodes(Node.LABELS_FIELD, label, propertyKey1, value1, propertyKey2, value2, propertyKey3, value3));
    }

    public Node findNode(final String label, final Map<String, Comparable<?>> properties) {
        properties.put(Node.LABELS_FIELD, label);
        return firstOrDefault(findNodes(properties));
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

    public Iterable<Node> findNodes(final String label) {
        return findNodes(Node.LABELS_FIELD, label);
    }

    public Iterable<Node> findNodes(final String label, final String propertyKey, final Comparable<?> value) {
        return findNodes(Node.LABELS_FIELD, label, propertyKey, value);
    }

    public Iterable<Node> findNodes(final String label, final String propertyKey1, final Comparable<?> value1,
                                    final String propertyKey2, final Comparable<?> value2) {
        return findNodes(Node.LABELS_FIELD, label, propertyKey1, value1, propertyKey2, value2);
    }

    public Iterable<Node> findNodes(final String label, final String propertyKey1, final Comparable<?> value1,
                                    final String propertyKey2, final Comparable<?> value2, final String propertyKey3,
                                    final Comparable<?> value3) {
        return findNodes(Node.LABELS_FIELD, label, propertyKey1, value1, propertyKey2, value2, propertyKey3, value3);
    }

    public Iterable<Node> findNodes(final String label, final Map<String, Comparable<?>> properties) {
        properties.put(Node.LABELS_FIELD, label);
        return findNodes(properties);
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

    public Long[] getAdjacentNodeIdsForEdgeLabel(final long nodeId, final String edgeLabel) {
        final Set<Long> nodeIds = new HashSet<>();
        for (final Edge edge : findEdges(edgeLabel, Edge.FROM_ID_FIELD, nodeId))
            nodeIds.add(edge.getToId());
        for (final Edge edge : findEdges(edgeLabel, Edge.TO_ID_FIELD, nodeId))
            nodeIds.add(edge.getFromId());
        return nodeIds.toArray(new Long[0]);
    }

    public static Graph createTempGraph() throws IOException {
        final Path tempFilePath = Files.createTempFile("graphdb_test", ".db");
        return new Graph(tempFilePath.toString());
    }
}
