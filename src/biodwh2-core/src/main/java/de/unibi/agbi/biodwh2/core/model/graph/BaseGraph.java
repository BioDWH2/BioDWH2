package de.unibi.agbi.biodwh2.core.model.graph;

import de.unibi.agbi.biodwh2.core.exceptions.GraphCacheException;
import de.unibi.agbi.biodwh2.core.io.mvstore.MVStoreCollection;
import de.unibi.agbi.biodwh2.core.io.mvstore.MVStoreDB;
import de.unibi.agbi.biodwh2.core.io.mvstore.MVStoreIndex;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

abstract class BaseGraph implements AutoCloseable {
    public static final String LABEL_PREFIX_SEPARATOR = "_";
    //private static final char NODE_REPOSITORY_PREFIX = '$';
    private static final char EDGE_REPOSITORY_PREFIX = '!';
    public static final String EXTENSION = "db";

    private MVStoreDB database;
    private MVStoreCollection<Node> nodes;
    private final Map<String, MVStoreCollection<Edge>> edgeRepositories;

    protected BaseGraph(final Path databaseFilePath, final boolean reopen, final boolean readOnly) {
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
        for (final String key : keys)
            addIndexIfNotExists(nodes, key, false);
    }

    public final void close() {
        if (database != null)
            database.close();
        nodes = null;
        edgeRepositories.clear();
        database = null;
    }

    public final void update(final Node node) {
        if (node == null)
            throw new GraphCacheException("Failed to update node because it is null");
        nodes.put(node);
    }

    public final void update(final Edge edge) {
        if (edge == null)
            throw new GraphCacheException("Failed to update edge because it is null");
        final String label = edge.getLabel();
        if (label == null || label.length() == 0)
            throw new GraphCacheException("Failed to add or update edge because the label is null or empty");
        getOrCreateEdgeRepository(label).put(edge);
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

    public final Iterable<Node> getNodes() {
        return nodes;
    }

    public final Iterable<Edge> getEdges() {
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

    public final long getNumberOfNodes() {
        return nodes.size();
    }

    public final long getNumberOfEdges() {
        long result = 0;
        for (final MVStoreCollection<Edge> edges : edgeRepositories.values())
            result += edges.size();
        return result;
    }

    public final Node getNode(final long nodeId) {
        return nodes.get(nodeId);
    }

    public final Edge getEdge(final long edgeId) {
        for (final MVStoreCollection<Edge> edges : edgeRepositories.values()) {
            final Edge edge = edges.get(edgeId);
            if (edge != null)
                return edge;
        }
        return null;
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

    public Iterable<Node> findNodes(final String propertyKey1, final Comparable<?> value1, final String propertyKey2,
                                    final Comparable<?> value2, final String propertyKey3, final Comparable<?> value3,
                                    final String propertyKey4, final Comparable<?> value4) {
        return nodes.find(propertyKey1, value1, propertyKey2, value2, propertyKey3, value3, propertyKey4, value4);
    }

    public Iterable<Node> findNodes(final Map<String, Comparable<?>> properties) {
        if (properties == null || properties.size() == 0)
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

    public void mergeDatabase(final String dataSourceId, final BaseGraph databaseToMerge) {
        final String dataSourcePrefix = dataSourceId + LABEL_PREFIX_SEPARATOR;
        for (final MVStoreIndex index : databaseToMerge.nodes.getIndices()) {
            nodes.getIndex(index.getKey(), index.isArrayIndex());
        }
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
}
