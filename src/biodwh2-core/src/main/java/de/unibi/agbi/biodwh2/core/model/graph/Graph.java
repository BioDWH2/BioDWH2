package de.unibi.agbi.biodwh2.core.model.graph;

import de.unibi.agbi.biodwh2.core.exceptions.GraphCacheException;
import org.apache.commons.lang3.StringUtils;
import org.dizitart.no2.*;
import org.dizitart.no2.objects.ObjectFilter;
import org.dizitart.no2.objects.ObjectRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.dizitart.no2.objects.filters.ObjectFilters.*;

public final class Graph {
    public static final String EXTENSION = "db";
    private static final FindOptions LIMIT_ONE_OPTION = FindOptions.limit(0, 1);

    private Nitrite database;
    private ObjectRepository<Node> nodes;
    private ObjectRepository<Edge> edges;
    private final Map<Class<?>, ClassMapping> classMappingsCache = new HashMap<>();

    public Graph(final String databaseFilePath) {
        this(databaseFilePath, false);
    }

    public Graph(final String databaseFilePath, final boolean reopen) {
        if (!reopen)
            deleteOldDatabaseFile(databaseFilePath);
        database = openDatabase(databaseFilePath);
        nodes = database.getRepository(Node.class);
        edges = database.getRepository(Edge.class);
        createIndicesIfNotExist();
    }

    private void deleteOldDatabaseFile(final String filePath) {
        try {
            Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            throw new GraphCacheException("Failed to remove old persisted database file '" + filePath + "'", e);
        }
    }

    private static Nitrite openDatabase(final String filePath) {
        return Nitrite.builder().compressed().filePath(filePath).openOrCreate();
    }

    private void createIndicesIfNotExist() {
        if (!nodes.hasIndex(Node.LABEL_FIELD))
            nodes.createIndex(Node.LABEL_FIELD, IndexOptions.indexOptions(IndexType.NonUnique, false));
        if (!edges.hasIndex(Edge.FROM_ID_FIELD))
            edges.createIndex(Edge.FROM_ID_FIELD, IndexOptions.indexOptions(IndexType.NonUnique, false));
        if (!edges.hasIndex(Edge.TO_ID_FIELD))
            edges.createIndex(Edge.TO_ID_FIELD, IndexOptions.indexOptions(IndexType.NonUnique, false));
        if (!edges.hasIndex(Edge.LABEL_FIELD))
            edges.createIndex(Edge.LABEL_FIELD, IndexOptions.indexOptions(IndexType.NonUnique, false));
    }

    public void setNodeIndexPropertyKeys(final String... keys) {
        for (final String key : keys)
            if (!nodes.hasIndex(key))
                nodes.createIndex(key, IndexOptions.indexOptions(IndexType.NonUnique, false));
    }

    public void prefixAllLabels(final String prefix) {
        nodes.getDocumentCollection().dropIndex(Node.LABEL_FIELD);
        edges.getDocumentCollection().dropIndex(Edge.LABEL_FIELD);
        for (final Document document : nodes.getDocumentCollection().find()) {
            document.put(Node.LABEL_FIELD, prefix + document.get(Node.LABEL_FIELD));
            nodes.getDocumentCollection().update(document);
        }
        for (final Document document : edges.getDocumentCollection().find()) {
            document.put(Edge.LABEL_FIELD, prefix + document.get(Edge.LABEL_FIELD));
            edges.getDocumentCollection().update(document);
        }
        createIndicesIfNotExist();
    }

    public Node addNode(final String label) {
        final Node n = new Node(label);
        nodes.insert(n);
        return n;
    }

    public Node addNode(final String label, final String propertyKey, final Object propertyValue) {
        final Node n = new Node(label);
        n.setProperty(propertyKey, propertyValue);
        nodes.insert(n);
        return n;
    }

    public Node addNode(final String label, final String propertyKey1, final Object propertyValue1,
                        final String propertyKey2, final Object propertyValue2) {
        final Node n = new Node(label);
        n.setProperty(propertyKey1, propertyValue1);
        n.setProperty(propertyKey2, propertyValue2);
        nodes.insert(n);
        return n;
    }

    public Node addNode(final String label, final Map<String, Object> properties) {
        final Node n = new Node(label);
        for (Map.Entry<String, Object> entry : properties.entrySet())
            n.setProperty(entry.getKey(), entry.getValue());
        nodes.insert(n);
        return n;
    }

    public NodeBuilder buildNode() {
        return new NodeBuilder(this);
    }

    public final <T> Node addNodeFromModel(final T obj) {
        final ClassMapping mapping = getClassMappingFromCache(obj.getClass());
        final Node node = new Node(mapping.label);
        setNodePropertiesFromClassMapping(node, mapping, obj);
        nodes.insert(node);
        return node;
    }

    private ClassMapping getClassMappingFromCache(final Class<?> type) {
        if (!classMappingsCache.containsKey(type))
            classMappingsCache.put(type, new ClassMapping(type));
        return classMappingsCache.get(type);
    }

    private void setNodePropertiesFromClassMapping(final Node node, final ClassMapping mapping, final Object obj) {
        try {
            for (final ClassMapping.ClassMappingField field : mapping.fields) {
                final Object value = field.field.get(obj);
                if (value != null)
                    node.setProperty(field.propertyName, value);
            }
            for (final ClassMapping.ClassMappingField field : mapping.arrayFields) {
                final Object value = field.field.get(obj);
                if (value != null)
                    node.setProperty(field.propertyName,
                                     StringUtils.splitByWholeSeparator(value.toString(), field.arrayDelimiter));
            }
        } catch (IllegalAccessException e) {
            throw new GraphCacheException(e);
        }
    }

    public void update(final Node node) {
        nodes.update(node.getEqFilter(), node, false);
    }

    public Edge addEdge(final Node from, final Node to, final String label) {
        return addEdge(from.getId(), to.getId(), label);
    }

    public Edge addEdge(final long fromId, final Node to, final String label) {
        return addEdge(fromId, to.getId(), label);
    }

    public Edge addEdge(final Node from, final long toId, final String label) {
        return addEdge(from.getId(), toId, label);
    }

    public Edge addEdge(final long fromId, final long toId, final String label) {
        final Edge e = new Edge(fromId, toId, label);
        edges.insert(e);
        return e;
    }

    public Edge addEdge(final Node from, final Node to, final String label, final String propertyKey,
                        final Object propertyValue) {
        return addEdge(from.getId(), to.getId(), label, propertyKey, propertyValue);
    }

    public Edge addEdge(final long fromId, final Node to, final String label, final String propertyKey,
                        final Object propertyValue) {
        return addEdge(fromId, to.getId(), label, propertyKey, propertyValue);
    }

    public Edge addEdge(final Node from, final long toId, final String label, final String propertyKey,
                        final Object propertyValue) {
        return addEdge(from.getId(), toId, label, propertyKey, propertyValue);
    }

    public Edge addEdge(final long fromId, final long toId, final String label, final String propertyKey,
                        final Object propertyValue) {
        final Edge e = new Edge(fromId, toId, label);
        e.setProperty(propertyKey, propertyValue);
        edges.insert(e);
        return e;
    }

    public void update(final Edge edge) {
        edges.update(edge.getEqFilter(), edge, false);
    }

    public Iterable<Node> getNodes() {
        return () -> nodes.find().iterator();
    }

    public Iterable<Node> getNodes(final String label) {
        return () -> nodes.find(eq(Node.LABEL_FIELD, label)).iterator();
    }

    public Iterable<Edge> getEdges() {
        return () -> edges.find().iterator();
    }

    public Iterable<Edge> getEdges(final String label) {
        return () -> edges.find(eq(Edge.LABEL_FIELD, label)).iterator();
    }

    public long getNumberOfNodes() {
        return nodes.size();
    }

    public long getNumberOfEdges() {
        return edges.size();
    }

    public Node getNode(final long nodeId) {
        return nodes.getById(NitriteId.createId(nodeId));
    }

    public Edge getEdge(final long edgeId) {
        return edges.getById(NitriteId.createId(edgeId));
    }

    public Node findNode(final String label) {
        return nodes.find(eq(Node.LABEL_FIELD, label), LIMIT_ONE_OPTION).firstOrDefault();
    }

    public Node findNode(final String label, final String propertyKey, final Object value) {
        return nodes.find(and(eq(Node.LABEL_FIELD, label), eq(propertyKey, value)), LIMIT_ONE_OPTION).firstOrDefault();
    }

    public Node findNode(final String label, final String propertyKey1, final Object value1, final String propertyKey2,
                         final Object value2) {
        return nodes.find(and(eq(Node.LABEL_FIELD, label), eq(propertyKey1, value1), eq(propertyKey2, value2)),
                          LIMIT_ONE_OPTION).firstOrDefault();
    }

    public Node findNode(final String label, final String propertyKey1, final Object value1, final String propertyKey2,
                         final Object value2, final String propertyKey3, final Object value3) {
        return nodes.find(and(eq(Node.LABEL_FIELD, label), eq(propertyKey1, value1), eq(propertyKey2, value2),
                              eq(propertyKey3, value3)), LIMIT_ONE_OPTION).firstOrDefault();
    }

    public Node findNode(final String label, final Map<String, Object> properties) {
        final ObjectFilter[] filter = new ObjectFilter[properties.size() + 1];
        filter[0] = eq(Node.LABEL_FIELD, label);
        int index = 1;
        for (final String propertyKey : properties.keySet())
            filter[index++] = eq(propertyKey, properties.get(propertyKey));
        return nodes.find(and(filter), LIMIT_ONE_OPTION).firstOrDefault();
    }

    public Iterable<Node> findNodes(final String label) {
        return () -> nodes.find(eq(Node.LABEL_FIELD, label)).iterator();
    }

    public Iterable<Node> findNodes(final String label, final String propertyKey, final Object value) {
        return () -> nodes.find(and(eq(Node.LABEL_FIELD, label), eq(propertyKey, value))).iterator();
    }

    public Iterable<Node> findNodes(final String label, final String propertyKey1, final Object value1,
                                    final String propertyKey2, final Object value2) {
        return () -> nodes.find(and(eq(Node.LABEL_FIELD, label), eq(propertyKey1, value1), eq(propertyKey2, value2)))
                          .iterator();
    }

    public Iterable<Node> findNodes(final String label, final String propertyKey1, final Object value1,
                                    final String propertyKey2, final Object value2, final String propertyKey3,
                                    final Object value3) {
        return () -> nodes.find(and(eq(Node.LABEL_FIELD, label), eq(propertyKey1, value1), eq(propertyKey2, value2),
                                    eq(propertyKey3, value3))).iterator();
    }

    public Iterable<Node> findNodes(final String label, final Map<String, Object> properties) {
        final ObjectFilter[] filter = new ObjectFilter[properties.size() + 1];
        filter[0] = eq(Node.LABEL_FIELD, label);
        int index = 1;
        for (final String propertyKey : properties.keySet())
            filter[index++] = eq(propertyKey, properties.get(propertyKey));
        return () -> nodes.find(and(filter)).iterator();
    }

    public Iterable<Edge> findEdges(final String label) {
        return () -> edges.find(eq(Edge.LABEL_FIELD, label)).iterator();
    }

    public Iterable<Edge> findEdges(final String label, final String propertyKey, final Object value) {
        return () -> edges.find(and(eq(Edge.LABEL_FIELD, label), eq(propertyKey, value))).iterator();
    }

    public Long[] getAdjacentNodeIdsForEdgeLabel(final long nodeId, final String edgeLabel) {
        final Set<Long> nodeIds = new HashSet<>();
        final ObjectFilter filter = and(eq(Edge.LABEL_FIELD, edgeLabel),
                                        or(eq(Edge.FROM_ID_FIELD, nodeId), eq(Edge.TO_ID_FIELD, nodeId)));
        for (final Edge edge : edges.find(filter)) {
            nodeIds.add(edge.getFromId());
            nodeIds.add(edge.getToId());
        }
        nodeIds.remove(nodeId);
        return nodeIds.toArray(new Long[0]);
    }

    public void mergeNodes(final Node first, final Node second) {
        for (final Edge edge : edges.find(eq(Edge.FROM_ID_FIELD, second.getId()))) {
            edge.setFromId(first.getId());
            update(edge);
        }
        for (final Edge edge : edges.find(eq(Edge.TO_ID_FIELD, second.getId()))) {
            edge.setToId(first.getId());
            update(edge);
        }
        // TODO: properties
        nodes.remove(second);
    }

    public void mergeDatabase(final String filePath) {
        final Graph databaseToMerge = new Graph(filePath, true);
        for (final Index index : databaseToMerge.nodes.listIndices())
            if (!nodes.hasIndex(index.getField()))
                nodes.createIndex(index.getField(), IndexOptions.indexOptions(index.getIndexType(), false));
        for (final Index index : databaseToMerge.edges.listIndices())
            if (!edges.hasIndex(index.getField()))
                edges.createIndex(index.getField(), IndexOptions.indexOptions(index.getIndexType(), false));
        final Map<Long, Long> mapping = new HashMap<>();
        for (final Node n : databaseToMerge.nodes.find()) {
            final Long oldId = n.getId();
            n.resetId();
            nodes.insert(n);
            mapping.put(oldId, n.getId());
        }
        for (final Edge e : databaseToMerge.edges.find()) {
            e.resetId();
            e.setProperty(Edge.FROM_ID_FIELD, mapping.get(e.getFromId()));
            e.setProperty(Edge.TO_ID_FIELD, mapping.get(e.getToId()));
            edges.insert(e);
        }
        databaseToMerge.dispose();
    }

    public void dispose() {
        if (database != null && !database.isClosed())
            database.close();
        nodes = null;
        edges = null;
        database = null;
    }

    public static Graph createTempGraph() throws IOException {
        final Path tempFilePath = Files.createTempFile("graphdb_test", ".db");
        return new Graph(tempFilePath.toString());
    }
}
