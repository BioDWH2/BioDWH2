package de.unibi.agbi.biodwh2.core.model.graph;

import de.unibi.agbi.biodwh2.core.exceptions.GraphCacheException;
import de.unibi.agbi.biodwh2.core.io.mvstore.MVStoreCollection;
import de.unibi.agbi.biodwh2.core.io.mvstore.MVStoreDB;
import de.unibi.agbi.biodwh2.core.io.mvstore.MVStoreIndex;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public final class Graph implements AutoCloseable {
    public static final String LABEL_PREFIX_SEPARATOR = "_";
    //private static final char NODE_REPOSITORY_PREFIX = '$';
    private static final char EDGE_REPOSITORY_PREFIX = '!';
    public static final String EXTENSION = "db";

    private MVStoreDB database;
    private MVStoreCollection<Node> nodes;
    private final Map<String, MVStoreCollection<Edge>> edgeRepositories;
    private final Map<Class<?>, ClassMapping> classMappingsCache = new HashMap<>();
    private final Set<String> userDefinedNodeIndexPropertyKeys = new HashSet<>();

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
        if (!reopen)
            deleteOldDatabaseFile(databaseFilePath);
        edgeRepositories = new HashMap<>();
        database = openDatabase(databaseFilePath, readOnly);
        nodes = database.getCollection("nodes");
        for (final String repositoryKey : database.getCollectionNames())
            if (repositoryKey.charAt(0) == EDGE_REPOSITORY_PREFIX)
                edgeRepositories.put(repositoryKey.substring(1), database.getCollection(repositoryKey));
        if (!readOnly)
            createInternalIndicesIfNotExist();
    }

    private void deleteOldDatabaseFile(final Path filePath) {
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new GraphCacheException("Failed to remove old persisted database file '" + filePath + "'", e);
        }
    }

    private static MVStoreDB openDatabase(final Path filePath, final boolean readOnly) {
        return new MVStoreDB(filePath.toString(), readOnly);
    }

    private void createInternalIndicesIfNotExist() {
        addIndexIfNotExists(nodes, Node.LABELS_FIELD, true);
        for (final MVStoreCollection<Edge> edges : edgeRepositories.values())
            createEdgeRepositoryIndicesIfNotExist(edges);
    }

    private void addIndexIfNotExists(final MVStoreCollection<?> repository, final String key, final boolean array) {
        repository.getIndex(key, array);
    }

    private void createEdgeRepositoryIndicesIfNotExist(final MVStoreCollection<Edge> edges) {
        addIndexIfNotExists(edges, Edge.FROM_ID_FIELD, false);
        addIndexIfNotExists(edges, Edge.TO_ID_FIELD, false);
    }

    public void setNodeIndexPropertyKeys(final String... keys) {
        userDefinedNodeIndexPropertyKeys.addAll(Arrays.asList(keys));
        for (final String key : keys)
            addIndexIfNotExists(nodes, key, false);
    }

    public Node addNode(final String label) {
        final Node n = Node.newNode(label);
        nodes.put(n);
        return n;
    }

    public Node addNode(final String label, final String propertyKey, final Object propertyValue) {
        final Node n = Node.newNode(label);
        n.setProperty(propertyKey, propertyValue);
        nodes.put(n);
        return n;
    }

    public Node addNode(final String label, final String propertyKey1, final Object propertyValue1,
                        final String propertyKey2, final Object propertyValue2) {
        final Node n = Node.newNode(label);
        n.setProperty(propertyKey1, propertyValue1);
        n.setProperty(propertyKey2, propertyValue2);
        nodes.put(n);
        return n;
    }

    public Node addNode(final String label, final String propertyKey1, final Object propertyValue1,
                        final String propertyKey2, final Object propertyValue2, final String propertyKey3,
                        final Object propertyValue3) {
        final Node n = Node.newNode(label);
        n.setProperty(propertyKey1, propertyValue1);
        n.setProperty(propertyKey2, propertyValue2);
        n.setProperty(propertyKey3, propertyValue3);
        nodes.put(n);
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
        nodes.put(n);
        return n;
    }

    public Node addNode(final String label, final Map<String, Object> properties) {
        final Node n = Node.newNode(label);
        for (Map.Entry<String, Object> entry : properties.entrySet())
            n.setProperty(entry.getKey(), entry.getValue());
        nodes.put(n);
        return n;
    }

    public NodeBuilder buildNode() {
        return Node.newNodeBuilder(this);
    }

    public final <T> Node addNodeFromModel(final T obj) {
        final ClassMapping mapping = getClassMappingFromCache(obj.getClass());
        final Node n = Node.newNode(mapping.labels);
        mapping.setNodeProperties(n, obj);
        nodes.put(n);
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
        nodes.put(n);
        return n;
    }

    public final <T> Node addNodeFromModel(final T obj, final String propertyKey1, final Object propertyValue1,
                                           final String propertyKey2, final Object propertyValue2) {
        final ClassMapping mapping = getClassMappingFromCache(obj.getClass());
        final Node n = Node.newNode(mapping.labels);
        mapping.setNodeProperties(n, obj);
        n.setProperty(propertyKey1, propertyValue1);
        n.setProperty(propertyKey2, propertyValue2);
        nodes.put(n);
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
        nodes.put(n);
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
        nodes.put(n);
        return n;
    }

    public void update(final Node node) {
        if (node == null)
            throw new GraphCacheException("Failed to update node because it is null");
        nodes.put(node);
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
        validateEdgeLabel(label);
        final Edge e = Edge.newEdge(fromId, toId, label);
        getOrCreateEdgeRepository(label).put(e);
        return e;
    }

    private void validateEdgeLabel(final String label) {
        if (label == null || label.length() == 0)
            throw new GraphCacheException("Failed to add edge because the label is null or empty");
    }

    private MVStoreCollection<Edge> getOrCreateEdgeRepository(final String label) {
        MVStoreCollection<Edge> edges = edgeRepositories.get(label);
        if (edges == null) {
            edges = database.getCollection(EDGE_REPOSITORY_PREFIX + label);
            edgeRepositories.put(label, edges);
            createEdgeRepositoryIndicesIfNotExist(edges);
        }
        return edges;
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
        validateEdgeLabel(label);
        final Edge e = Edge.newEdge(fromId, toId, label);
        e.setProperty(propertyKey, propertyValue);
        getOrCreateEdgeRepository(label).put(e);
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
        validateEdgeLabel(label);
        final Edge e = Edge.newEdge(fromId, toId, label);
        e.setProperty(propertyKey1, propertyValue1);
        e.setProperty(propertyKey2, propertyValue2);
        getOrCreateEdgeRepository(label).put(e);
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
        validateEdgeLabel(label);
        final Edge e = Edge.newEdge(fromId, toId, label);
        for (Map.Entry<String, Object> entry : properties.entrySet())
            e.setProperty(entry.getKey(), entry.getValue());
        getOrCreateEdgeRepository(label).put(e);
        return e;
    }

    public EdgeBuilder buildEdge() {
        return new EdgeBuilder(this);
    }

    public void update(final Edge edge) {
        if (edge == null)
            throw new GraphCacheException("Failed to update edge because it is null");
        getOrCreateEdgeRepository(edge.getLabel()).put(edge);
    }

    public Iterable<Node> getNodes() {
        return nodes;
    }

    public Iterable<Node> getNodes(final String label) {
        if (label == null || label.length() == 0)
            return getNodes();
        return nodes.find(Node.LABELS_FIELD, label);
    }

    public Iterable<Edge> getEdges() {
        return () -> new Iterator<Edge>() {
            private Iterator<Edge> current = null;
            private final Iterator<MVStoreCollection<Edge>> repositories = edgeRepositories.values().iterator();

            @Override
            public boolean hasNext() {
                while ((current == null || !current.hasNext()) && repositories.hasNext())
                    current = repositories.next().iterator();
                return current != null && current.hasNext();
            }

            @Override
            public Edge next() {
                while ((current == null || !current.hasNext()) && repositories.hasNext())
                    current = repositories.next().iterator();
                return current != null ? current.next() : null;
            }
        };
    }

    public Iterable<Edge> getEdges(final String label) {
        if (label == null || label.length() == 0)
            return getEdges();
        return getOrCreateEdgeRepository(label);
    }

    public long getNumberOfNodes() {
        return nodes.size();
    }

    public long getNumberOfEdges() {
        long result = 0;
        for (final MVStoreCollection<Edge> edges : edgeRepositories.values())
            result += edges.size();
        return result;
    }

    public Node getNode(final long nodeId) {
        return nodes.get(nodeId);
    }

    public Edge getEdge(final long edgeId) {
        for (final MVStoreCollection<Edge> edges : edgeRepositories.values()) {
            final Edge edge = edges.get(edgeId);
            if (edge != null)
                return edge;
        }
        return null;
    }

    public Node findNode(final String label) {
        return firstOrDefault(nodes.find(Node.LABELS_FIELD, label));
    }

    private <T> T firstOrDefault(final Iterable<T> iterable) {
        return firstOrDefault(iterable.iterator());
    }

    private <T> T firstOrDefault(final Iterator<T> iterator) {
        return iterator.hasNext() ? iterator.next() : null;
    }

    public Node findNode(final String label, final String propertyKey, final Comparable<?> value) {
        return firstOrDefault(nodes.find(Node.LABELS_FIELD, label, propertyKey, value));
    }

    public Node findNode(final String label, final String propertyKey1, final Comparable<?> value1,
                         final String propertyKey2, final Comparable<?> value2) {
        return firstOrDefault(nodes.find(Node.LABELS_FIELD, label, propertyKey1, value1, propertyKey2, value2));
    }

    public Node findNode(final String label, final String propertyKey1, final Comparable<?> value1,
                         final String propertyKey2, final Comparable<?> value2, final String propertyKey3,
                         final Comparable<?> value3) {
        return firstOrDefault(
                nodes.find(Node.LABELS_FIELD, label, propertyKey1, value1, propertyKey2, value2, propertyKey3, value3));
    }

    public Node findNode(final String label, final Map<String, Comparable<?>> properties) {
        final String[] keys = new String[properties.size() + 1];
        final Comparable<?>[] values = new Comparable<?>[properties.size() + 1];
        keys[0] = Node.LABELS_FIELD;
        values[0] = label;
        int index = 1;
        for (final String propertyKey : properties.keySet()) {
            keys[index] = propertyKey;
            values[index++] = properties.get(propertyKey);
        }
        return firstOrDefault(nodes.find(keys, values));
    }

    public Node findNode(final String propertyKey, final Comparable<?> value) {
        return firstOrDefault(nodes.find(propertyKey, value));
    }

    public Node findNode(final String propertyKey1, final Comparable<?> value1, final String propertyKey2,
                         final Comparable<?> value2) {
        return firstOrDefault(nodes.find(propertyKey1, value1, propertyKey2, value2));
    }

    public Node findNode(final String propertyKey1, final Comparable<?> value1, final String propertyKey2,
                         final Comparable<?> value2, final String propertyKey3, final Comparable<?> value3) {
        return firstOrDefault(nodes.find(propertyKey1, value1, propertyKey2, value2, propertyKey3, value3));
    }

    public Node findNode(final Map<String, Comparable<?>> properties) {
        if (properties.size() == 0)
            return null;
        final String[] keys = new String[properties.size()];
        final Comparable<?>[] values = new Comparable<?>[properties.size()];
        int index = 0;
        for (final String propertyKey : properties.keySet()) {
            keys[index] = propertyKey;
            values[index++] = properties.get(propertyKey);
        }
        return firstOrDefault(nodes.find(keys, values));
    }

    public Iterable<Node> findNodes(final String label) {
        return nodes.find(Node.LABELS_FIELD, label);
    }

    public Iterable<Node> findNodes(final String label, final String propertyKey, final Comparable<?> value) {
        return nodes.find(Node.LABELS_FIELD, label, propertyKey, value);
    }

    public Iterable<Node> findNodes(final String label, final String propertyKey1, final Comparable<?> value1,
                                    final String propertyKey2, final Comparable<?> value2) {
        return nodes.find(Node.LABELS_FIELD, label, propertyKey1, value1, propertyKey2, value2);
    }

    public Iterable<Node> findNodes(final String label, final String propertyKey1, final Comparable<?> value1,
                                    final String propertyKey2, final Comparable<?> value2, final String propertyKey3,
                                    final Comparable<?> value3) {
        return nodes.find(Node.LABELS_FIELD, label, propertyKey1, value1, propertyKey2, value2, propertyKey3, value3);
    }

    public Iterable<Node> findNodes(final String label, final Map<String, Comparable<?>> properties) {
        final String[] keys = new String[properties.size() + 1];
        final Comparable<?>[] values = new Comparable<?>[properties.size() + 1];
        keys[0] = Node.LABELS_FIELD;
        values[0] = label;
        int index = 1;
        for (final String propertyKey : properties.keySet()) {
            keys[index] = propertyKey;
            values[index++] = properties.get(propertyKey);
        }
        return nodes.find(keys, values);
    }

    public Iterable<Node> findNodes(final String propertyKey, final Comparable<?> value) {
        return nodes.find(propertyKey, value);
    }

    public Iterable<Node> findNodes(final String propertyKey1, final Comparable<?> value1, final String propertyKey2,
                                    final Comparable<?> value2) {
        return nodes.find(propertyKey1, value1, propertyKey2, value2);
    }

    public Iterable<Node> findNodes(final String propertyKey1, final Comparable<?> value1, final String propertyKey2,
                                    final Comparable<?> value2, final String propertyKey3, final Comparable<?> value3) {
        return nodes.find(propertyKey1, value1, propertyKey2, value2, propertyKey3, value3);
    }

    public Iterable<Node> findNodes(final Map<String, Comparable<?>> properties) {
        if (properties.size() == 0)
            return getNodes();
        final String[] keys = new String[properties.size()];
        final Comparable<?>[] values = new Comparable<?>[properties.size()];
        int index = 0;
        for (final String propertyKey : properties.keySet()) {
            keys[index] = propertyKey;
            values[index++] = properties.get(propertyKey);
        }
        return nodes.find(keys, values);
    }

    public Edge findEdge(final String label) {
        return firstOrDefault(getOrCreateEdgeRepository(label));
    }

    public Edge findEdge(final String label, final String propertyKey, final Comparable<?> value) {
        return firstOrDefault(getOrCreateEdgeRepository(label).find(propertyKey, value));
    }

    public Edge findEdge(final String label, final String propertyKey1, final Comparable<?> value1,
                         final String propertyKey2, final Comparable<?> value2) {
        return firstOrDefault(getOrCreateEdgeRepository(label).find(propertyKey1, value1, propertyKey2, value2));
    }

    public Edge findEdge(final String label, final String propertyKey1, final Comparable<?> value1,
                         final String propertyKey2, final Comparable<?> value2, final String propertyKey3,
                         final Comparable<?> value3) {
        return firstOrDefault(getOrCreateEdgeRepository(label)
                                      .find(propertyKey1, value1, propertyKey2, value2, propertyKey3, value3));
    }

    public Edge findEdge(final String label, final Map<String, Comparable<?>> properties) {
        if (properties.size() == 0)
            return null;
        final String[] keys = new String[properties.size()];
        final Comparable<?>[] values = new Comparable<?>[properties.size()];
        int index = 0;
        for (final String propertyKey : properties.keySet()) {
            keys[index] = propertyKey;
            values[index++] = properties.get(propertyKey);
        }
        return firstOrDefault(getOrCreateEdgeRepository(label).find(keys, values));
    }

    public Iterable<Edge> findEdges(final String label) {
        return () -> getOrCreateEdgeRepository(label).iterator();
    }

    public Iterable<Edge> findEdges(final String label, final String propertyKey, final Comparable<?> value) {
        return getOrCreateEdgeRepository(label).find(propertyKey, value);
    }

    public Iterable<Edge> findEdges(final String label, final String propertyKey1, final Comparable<?> value1,
                                    final String propertyKey2, final Comparable<?> value2) {
        return getOrCreateEdgeRepository(label).find(propertyKey1, value1, propertyKey2, value2);
    }

    public Iterable<Edge> findEdges(final String label, final String propertyKey1, final Comparable<?> value1,
                                    final String propertyKey2, final Comparable<?> value2, final String propertyKey3,
                                    final Comparable<?> value3) {
        return getOrCreateEdgeRepository(label).find(propertyKey1, value1, propertyKey2, value2, propertyKey3, value3);
    }

    public Iterable<Edge> findEdges(final String label, final Map<String, Comparable<?>> properties) {
        if (properties.size() == 0)
            return getEdges();
        final String[] keys = new String[properties.size()];
        final Comparable<?>[] values = new Comparable<?>[properties.size()];
        int index = 0;
        for (final String propertyKey : properties.keySet()) {
            keys[index] = propertyKey;
            values[index++] = properties.get(propertyKey);
        }
        return getOrCreateEdgeRepository(label).find(keys, values);
    }

    public Long[] getAdjacentNodeIdsForEdgeLabel(final long nodeId, final String edgeLabel) {
        final Set<Long> nodeIds = new HashSet<>();
        for (final Edge edge : getOrCreateEdgeRepository(edgeLabel).find(Edge.FROM_ID_FIELD, nodeId))
            nodeIds.add(edge.getToId());
        for (final Edge edge : getOrCreateEdgeRepository(edgeLabel).find(Edge.TO_ID_FIELD, nodeId))
            nodeIds.add(edge.getFromId());
        return nodeIds.toArray(new Long[0]);
    }

    public void mergeNodes(final Node first, final Node second) {
        for (final MVStoreCollection<Edge> edges : edgeRepositories.values()) {
            for (final Edge edge : edges.find(Edge.FROM_ID_FIELD, second.getId())) {
                edge.setFromId(first.getId());
                update(edge);
            }
            for (final Edge edge : edges.find(Edge.TO_ID_FIELD, second.getId())) {
                edge.setToId(first.getId());
                update(edge);
            }
        }
        // TODO: properties
        nodes.remove(second);
    }

    public void mergeDatabase(final String dataSourceId, final Graph databaseToMerge) {
        final String dataSourcePrefix = dataSourceId + LABEL_PREFIX_SEPARATOR;
        for (final MVStoreIndex index : databaseToMerge.nodes.getIndices())
            nodes.getIndex(index.getKey(), index.isArrayIndex());
        final Map<Long, Long> mapping = new HashMap<>();
        for (final Node n : databaseToMerge.nodes) {
            final Long oldId = n.getId();
            n.resetId();
            final String[] labels = n.getLabels();
            for (int i = 0; i < labels.length; i++)
                labels[i] = dataSourcePrefix + labels[i];
            n.setProperty(Node.LABELS_FIELD, labels);
            nodes.put(n);
            mapping.put(oldId, n.getId());
        }
        for (final String sourceLabel : databaseToMerge.edgeRepositories.keySet()) {
            final String targetLabel = dataSourcePrefix + sourceLabel;
            for (final Edge e : databaseToMerge.edgeRepositories.get(sourceLabel)) {
                e.resetId();
                e.setProperty(Edge.LABEL_FIELD, targetLabel);
                e.setFromId(mapping.get(e.getFromId()));
                e.setToId(mapping.get(e.getToId()));
                getOrCreateEdgeRepository(e.getLabel()).put(e);
            }
        }
    }

    public void close() {
        if (database != null)
            database.close();
        nodes = null;
        edgeRepositories.clear();
        database = null;
    }

    public static Graph createTempGraph() throws IOException {
        final Path tempFilePath = Files.createTempFile("graphdb_test", ".db");
        return new Graph(tempFilePath.toString());
    }
}
