package de.unibi.agbi.biodwh2.core.model.graph;

import de.unibi.agbi.biodwh2.core.exceptions.GraphCacheException;
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
    private static final FindOptions LimitOneOption = FindOptions.limit(0, 1);

    private Nitrite database;
    private ObjectRepository<Node> nodes;
    private ObjectRepository<Edge> edges;
    private final Map<Class<?>, ClassMapping> classMappingsCache = new HashMap<>();

    public Graph(final String databaseFilePath) throws GraphCacheException {
        this(databaseFilePath, false);
    }

    public Graph(final String databaseFilePath, final boolean reopen) throws GraphCacheException {
        if (!reopen)
            deleteOldDatabaseFile(databaseFilePath);
        database = openDatabase(databaseFilePath);
        nodes = database.getRepository(Node.class);
        edges = database.getRepository(Edge.class);
        createIndicesIfNotExist();
    }

    private void deleteOldDatabaseFile(final String filePath) throws GraphCacheException {
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
        if (!nodes.hasIndex(Node.LabelField))
            nodes.createIndex(Node.LabelField, IndexOptions.indexOptions(IndexType.NonUnique, false));
        if (!edges.hasIndex(Edge.LabelField))
            edges.createIndex(Edge.LabelField, IndexOptions.indexOptions(IndexType.NonUnique, false));
    }

    public void setNodeIndexPropertyKeys(String... keys) {
        for (String key : keys)
            nodes.createIndex(key, IndexOptions.indexOptions(IndexType.NonUnique, false));
    }

    public void prefixAllLabels(String prefix) {
        for (Node n : getNodes()) {
            n.prefixLabel(prefix);
            update(n);
        }
        for (Edge e : getEdges()) {
            e.prefixLabel(prefix);
            update(e);
        }
    }

    public Node addNode(final String label) {
        final Node n = new Node(label);
        nodes.insert(n);
        return n;
    }

    public final <T> Node addNodeFromModel(final T obj) throws GraphCacheException {
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

    private void setNodePropertiesFromClassMapping(final Node node, final ClassMapping mapping,
                                                   final Object obj) throws GraphCacheException {
        try {
            for (ClassMapping.ClassMappingField field : mapping.fields)
                node.setProperty(field.propertyName, field.field.get(obj));
        } catch (IllegalAccessException e) {
            throw new GraphCacheException(e);
        }
    }

    public void update(final Node node) {
        nodes.update(node.getEqFilter(), node, false);
    }

    public Edge addEdge(final Node from, final Node to, final String label) {
        final Edge e = new Edge(from.getId(), to.getId(), label);
        edges.insert(e);
        return e;
    }

    public Edge addEdge(final long fromId, final Node to, final String label) {
        final Edge e = new Edge(fromId, to.getId(), label);
        edges.insert(e);
        return e;
    }

    public Edge addEdge(final Node from, final long toId, final String label) {
        final Edge e = new Edge(from.getId(), toId, label);
        edges.insert(e);
        return e;
    }

    public Edge addEdge(final long fromId, final long toId, final String label) {
        final Edge e = new Edge(fromId, toId, label);
        edges.insert(e);
        return e;
    }

    public void update(final Edge edge) {
        edges.update(edge.getEqFilter(), edge, false);
    }

    public Iterable<Node> getNodes() {
        return () -> nodes.find().iterator();
    }

    public Iterable<Edge> getEdges() {
        return () -> edges.find().iterator();
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

    public Node findNode(final String label, final String propertyKey, final Object value) {
        return nodes.find(and(eq(Node.LabelField, label), eq(propertyKey, value)), LimitOneOption).firstOrDefault();
    }

    public Node findNode(final String label, final String propertyKey1, final Object value1, final String propertyKey2,
                         final Object value2) {
        return nodes.find(and(eq(Node.LabelField, label), eq(propertyKey1, value1), eq(propertyKey2, value2)),
                          LimitOneOption).firstOrDefault();
    }

    public Node findNode(final String label, final String propertyKey1, final Object value1, final String propertyKey2,
                         final Object value2, final String propertyKey3, final Object value3) {
        return nodes.find(and(eq(Node.LabelField, label), eq(propertyKey1, value1), eq(propertyKey2, value2),
                              eq(propertyKey3, value3)), LimitOneOption).firstOrDefault();
    }

    public Long[] getAdjacentNodeIdsForEdgeLabel(final long nodeId, final String edgeLabel) {
        Set<Long> nodeIds = new HashSet<>();
        ObjectFilter filter = and(eq(Edge.LabelField, edgeLabel),
                                  or(eq(Edge.FromIdField, nodeId), eq(Edge.ToIdField, nodeId)));
        for (Edge edge : edges.find(filter)) {
            nodeIds.add(edge.getFromId());
            nodeIds.add(edge.getToId());
        }
        nodeIds.remove(nodeId);
        return nodeIds.toArray(new Long[0]);
    }

    public void mergeNodes(final Node first, final Node second) {
        for (Edge edge : edges.find(eq(Edge.FromIdField, second.getId()))) {
            edge.setFromId(first.getId());
            update(edge);
        }
        for (Edge edge : edges.find(eq(Edge.ToIdField, second.getId()))) {
            edge.setToId(first.getId());
            update(edge);
        }
        // TODO: properties
        nodes.remove(second);
    }

    public void mergeDatabase(final String filePath) throws GraphCacheException {
        Graph databaseToMerge = new Graph(filePath, true);
        Map<Long, Long> mapping = new HashMap<>();
        for (Node n : databaseToMerge.nodes.find()) {
            Long oldId = n.getId();
            n.resetId();
            nodes.insert(n);
            mapping.put(oldId, n.getId());
        }
        for (Edge e : databaseToMerge.edges.find()) {
            e.resetId();
            e.setProperty(Edge.FromIdField, mapping.get(e.getFromId()));
            e.setProperty(Edge.ToIdField, mapping.get(e.getToId()));
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
        Path tempFilePath = Files.createTempFile("graphdb_test", ".db");
        return new Graph(tempFilePath.toString());
    }
}
