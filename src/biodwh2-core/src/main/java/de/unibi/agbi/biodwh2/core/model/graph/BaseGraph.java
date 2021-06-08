package de.unibi.agbi.biodwh2.core.model.graph;

import de.unibi.agbi.biodwh2.core.exceptions.GraphCacheException;
import de.unibi.agbi.biodwh2.core.io.mvstore.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

abstract class BaseGraph implements AutoCloseable {
    public static final int VERSION = 2;
    public static final String LABEL_PREFIX_SEPARATOR = "_";
    private static final char NODE_REPOSITORY_PREFIX = '$';
    private static final char EDGE_REPOSITORY_PREFIX = '!';
    private static final String VERSION_KEY = "version";
    public static final String EXTENSION = "db";

    private MVStoreDB database;
    private final MVMapWrapper<String, Object> metaMap;
    private final Map<String, MVStoreCollection<Node>> nodeRepositories;
    private final Map<String, MVStoreCollection<Edge>> edgeRepositories;

    protected BaseGraph(final Path databaseFilePath, final boolean reopen, final boolean readOnly) {
        if (!reopen)
            deleteOldDatabaseFile(databaseFilePath);
        nodeRepositories = new HashMap<>();
        edgeRepositories = new HashMap<>();
        database = openDatabase(databaseFilePath, readOnly);
        metaMap = database.openMap("metadata");
        if (!reopen)
            metaMap.put(VERSION_KEY, VERSION);
        for (final String repositoryKey : database.getCollectionNames()) {
            if (repositoryKey.charAt(0) == EDGE_REPOSITORY_PREFIX)
                edgeRepositories.put(repositoryKey.substring(1), database.getCollection(repositoryKey));
            else if (repositoryKey.charAt(0) == NODE_REPOSITORY_PREFIX)
                nodeRepositories.put(repositoryKey.substring(1), database.getCollection(repositoryKey));
        }
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
        for (final MVStoreCollection<Edge> edges : edgeRepositories.values())
            createEdgeRepositoryIndicesIfNotExist(edges);
    }

    private void createEdgeRepositoryIndicesIfNotExist(final MVStoreCollection<Edge> edges) {
        edges.getIndex(Edge.FROM_ID_FIELD, false, MVStoreIndexType.NON_UNIQUE);
        edges.getIndex(Edge.TO_ID_FIELD, false, MVStoreIndexType.NON_UNIQUE);
    }

    public final Integer getVersion() {
        return metaMap.containsKey(VERSION_KEY) ? (Integer) metaMap.get(VERSION_KEY) : null;
    }

    public void addIndex(final IndexDescription description) {
        if (description.getLabel() == null)
            throw new GraphCacheException("Indices with null label are not allowed");
        final MVStoreIndexType type = description.getType() == IndexDescription.Type.UNIQUE ? MVStoreIndexType.UNIQUE :
                                      MVStoreIndexType.NON_UNIQUE;
        if (description.getTarget() == IndexDescription.Target.NODE) {
            getOrCreateNodeRepository(description.getLabel()).getIndex(description.getProperty(),
                                                                       description.isArrayProperty(), type);
        } else if (description.getTarget() == IndexDescription.Target.EDGE) {
            getOrCreateEdgeRepository(description.getLabel()).getIndex(description.getProperty(),
                                                                       description.isArrayProperty(), type);
        }
    }

    public final void close() {
        if (database != null)
            database.close();
        nodeRepositories.clear();
        edgeRepositories.clear();
        database = null;
    }

    public final void update(final Node node) {
        if (node == null)
            throw new GraphCacheException("Failed to update node because it is null");
        final String label = node.getLabel();
        if (label == null || label.length() == 0)
            throw new GraphCacheException("Failed to add or update node because the label is null or empty");
        getOrCreateNodeRepository(label).put(node);
    }

    private MVStoreCollection<Node> getOrCreateNodeRepository(final String label) {
        MVStoreCollection<Node> nodes = nodeRepositories.get(label);
        if (nodes == null) {
            nodes = database.getCollection(NODE_REPOSITORY_PREFIX + label);
            nodeRepositories.put(label, nodes);
        }
        return nodes;
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
        return () -> new RepositoriesIterator<>(nodeRepositories.values());
    }

    public final Iterable<Edge> getEdges() {
        return () -> new RepositoriesIterator<>(edgeRepositories.values());
    }

    public final long getNumberOfNodes() {
        long result = 0;
        for (final MVStoreCollection<Node> nodes : nodeRepositories.values())
            result += nodes.size();
        return result;
    }

    public final long getNumberOfEdges() {
        long result = 0;
        for (final MVStoreCollection<Edge> edges : edgeRepositories.values())
            result += edges.size();
        return result;
    }

    public final Node getNode(final long nodeId) {
        for (final MVStoreCollection<Node> nodes : nodeRepositories.values()) {
            final Node node = nodes.get(nodeId);
            if (node != null)
                return node;
        }
        return null;
    }

    public final Edge getEdge(final long edgeId) {
        for (final MVStoreCollection<Edge> edges : edgeRepositories.values()) {
            final Edge edge = edges.get(edgeId);
            if (edge != null)
                return edge;
        }
        return null;
    }

    public Iterable<Node> findNodes(final String label) {
        return () -> getOrCreateNodeRepository(label).iterator();
    }

    public Iterable<Node> findNodes(final String label, final String propertyKey, final Comparable<?> value) {
        return getOrCreateNodeRepository(label).find(propertyKey, value);
    }

    public Iterable<Node> findNodes(final String label, final String propertyKey1, final Comparable<?> value1,
                                    final String propertyKey2, final Comparable<?> value2) {
        return getOrCreateNodeRepository(label).find(propertyKey1, value1, propertyKey2, value2);
    }

    public Iterable<Node> findNodes(final String label, final String propertyKey1, final Comparable<?> value1,
                                    final String propertyKey2, final Comparable<?> value2, final String propertyKey3,
                                    final Comparable<?> value3) {
        return getOrCreateNodeRepository(label).find(propertyKey1, value1, propertyKey2, value2, propertyKey3, value3);
    }

    public Iterable<Node> findNodes(final String label, final Map<String, Comparable<?>> properties) {
        if (properties == null || properties.size() == 0)
            return findNodes(label);
        final String[] keys = new String[properties.size()];
        final Comparable<?>[] values = new Comparable<?>[properties.size()];
        int index = 0;
        for (final String propertyKey : properties.keySet()) {
            keys[index] = propertyKey;
            values[index++] = properties.get(propertyKey);
        }
        return getOrCreateNodeRepository(label).find(keys, values);
    }

    public Iterable<Node> findNodes(final String propertyKey, final Comparable<?> value) {
        return () -> new RepositoriesIterator<Node>(nodeRepositories.values()) {
            @Override
            protected Iterator<Node> filterNextRepository(MVStoreCollection<Node> next) {
                return next.find(propertyKey, value).iterator();
            }
        };
    }

    public Iterable<Node> findNodes(final String propertyKey1, final Comparable<?> value1, final String propertyKey2,
                                    final Comparable<?> value2) {
        return () -> new RepositoriesIterator<Node>(nodeRepositories.values()) {
            @Override
            protected Iterator<Node> filterNextRepository(MVStoreCollection<Node> next) {
                return next.find(propertyKey1, value1, propertyKey2, value2).iterator();
            }
        };
    }

    public Iterable<Node> findNodes(final String propertyKey1, final Comparable<?> value1, final String propertyKey2,
                                    final Comparable<?> value2, final String propertyKey3, final Comparable<?> value3) {
        return () -> new RepositoriesIterator<Node>(nodeRepositories.values()) {
            @Override
            protected Iterator<Node> filterNextRepository(MVStoreCollection<Node> next) {
                return next.find(propertyKey1, value1, propertyKey2, value2, propertyKey3, value3).iterator();
            }
        };
    }

    public Iterable<Node> findNodes(final String propertyKey1, final Comparable<?> value1, final String propertyKey2,
                                    final Comparable<?> value2, final String propertyKey3, final Comparable<?> value3,
                                    final String propertyKey4, final Comparable<?> value4) {
        return () -> new RepositoriesIterator<Node>(nodeRepositories.values()) {
            @Override
            protected Iterator<Node> filterNextRepository(MVStoreCollection<Node> next) {
                return next.find(propertyKey1, value1, propertyKey2, value2, propertyKey3, value3, propertyKey4, value4)
                           .iterator();
            }
        };
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
        return () -> new RepositoriesIterator<Node>(nodeRepositories.values()) {
            @Override
            protected Iterator<Node> filterNextRepository(MVStoreCollection<Node> next) {
                return next.find(keys, values).iterator();
            }
        };
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

    public Iterable<Edge> findEdges(final String propertyKey, final Comparable<?> value) {
        return () -> new RepositoriesIterator<Edge>(edgeRepositories.values()) {
            @Override
            protected Iterator<Edge> filterNextRepository(MVStoreCollection<Edge> next) {
                return next.find(propertyKey, value).iterator();
            }
        };
    }

    public Iterable<Edge> findEdges(final String propertyKey1, final Comparable<?> value1, final String propertyKey2,
                                    final Comparable<?> value2) {
        return () -> new RepositoriesIterator<Edge>(edgeRepositories.values()) {
            @Override
            protected Iterator<Edge> filterNextRepository(MVStoreCollection<Edge> next) {
                return next.find(propertyKey1, value1, propertyKey2, value2).iterator();
            }
        };
    }

    public Iterable<Edge> findEdges(final String propertyKey1, final Comparable<?> value1, final String propertyKey2,
                                    final Comparable<?> value2, final String propertyKey3, final Comparable<?> value3) {
        return () -> new RepositoriesIterator<Edge>(edgeRepositories.values()) {
            @Override
            protected Iterator<Edge> filterNextRepository(MVStoreCollection<Edge> next) {
                return next.find(propertyKey1, value1, propertyKey2, value2, propertyKey3, value3).iterator();
            }
        };
    }

    public Iterable<Edge> findEdges(final String propertyKey1, final Comparable<?> value1, final String propertyKey2,
                                    final Comparable<?> value2, final String propertyKey3, final Comparable<?> value3,
                                    final String propertyKey4, final Comparable<?> value4) {
        return () -> new RepositoriesIterator<Edge>(edgeRepositories.values()) {
            @Override
            protected Iterator<Edge> filterNextRepository(MVStoreCollection<Edge> next) {
                return next.find(propertyKey1, value1, propertyKey2, value2, propertyKey3, value3, propertyKey4, value4)
                           .iterator();
            }
        };
    }

    public Iterable<Edge> findEdges(final Map<String, Comparable<?>> properties) {
        if (properties == null || properties.size() == 0)
            return getEdges();
        final String[] keys = new String[properties.size()];
        final Comparable<?>[] values = new Comparable<?>[properties.size()];
        int index = 0;
        for (final String propertyKey : properties.keySet()) {
            keys[index] = propertyKey;
            values[index++] = properties.get(propertyKey);
        }
        return () -> new RepositoriesIterator<Edge>(edgeRepositories.values()) {
            @Override
            protected Iterator<Edge> filterNextRepository(MVStoreCollection<Edge> next) {
                return next.find(keys, values).iterator();
            }
        };
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
        getOrCreateNodeRepository(second.getLabel()).remove(second);
    }

    public void mergeDatabase(final String dataSourceId, final BaseGraph databaseToMerge) {
        final String dataSourcePrefix = dataSourceId + LABEL_PREFIX_SEPARATOR;
        for (final String sourceLabel : databaseToMerge.nodeRepositories.keySet()) {
            final String targetLabel = dataSourcePrefix + sourceLabel;
            for (final MVStoreIndex index : databaseToMerge.nodeRepositories.get(sourceLabel).getIndices())
                getOrCreateNodeRepository(targetLabel).getIndex(index.getKey(), index.isArrayIndex(), index.getType());
        }
        for (final String sourceLabel : databaseToMerge.edgeRepositories.keySet()) {
            final String targetLabel = dataSourcePrefix + sourceLabel;
            for (final MVStoreIndex index : databaseToMerge.edgeRepositories.get(sourceLabel).getIndices())
                getOrCreateEdgeRepository(targetLabel).getIndex(index.getKey(), index.isArrayIndex(), index.getType());
        }
        final Map<Long, Long> mapping = new HashMap<>();
        for (final String sourceLabel : databaseToMerge.nodeRepositories.keySet()) {
            final String targetLabel = dataSourcePrefix + sourceLabel;
            for (final Node n : databaseToMerge.nodeRepositories.get(sourceLabel)) {
                final Long oldId = n.getId();
                n.resetId();
                n.setProperty(Node.LABEL_FIELD, targetLabel);
                getOrCreateNodeRepository(targetLabel).put(n);
                mapping.put(oldId, n.getId());
            }
        }
        for (final String sourceLabel : databaseToMerge.edgeRepositories.keySet()) {
            final String targetLabel = dataSourcePrefix + sourceLabel;
            for (final Edge e : databaseToMerge.edgeRepositories.get(sourceLabel)) {
                e.resetId();
                e.setProperty(Edge.LABEL_FIELD, targetLabel);
                e.setFromId(mapping.get(e.getFromId()));
                e.setToId(mapping.get(e.getToId()));
                getOrCreateEdgeRepository(targetLabel).put(e);
            }
        }
    }

    private static class RepositoriesIterator<T extends MVStoreModel> implements Iterator<T> {
        private Iterator<T> current = null;
        private final Iterator<MVStoreCollection<T>> repositories;

        RepositoriesIterator(Collection<MVStoreCollection<T>> repositories) {
            this.repositories = repositories.iterator();
        }

        @Override
        public boolean hasNext() {
            advanceRepositoryIteratorIfNeeded();
            return current != null && current.hasNext();
        }

        private void advanceRepositoryIteratorIfNeeded() {
            while ((current == null || !current.hasNext()) && repositories.hasNext())
                current = filterNextRepository(repositories.next());
        }

        protected Iterator<T> filterNextRepository(MVStoreCollection<T> next) {
            return next.iterator();
        }

        @Override
        public T next() {
            advanceRepositoryIteratorIfNeeded();
            return current != null ? current.next() : null;
        }
    }
}
