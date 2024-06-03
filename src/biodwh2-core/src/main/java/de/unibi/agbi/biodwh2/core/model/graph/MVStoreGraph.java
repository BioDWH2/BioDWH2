package de.unibi.agbi.biodwh2.core.model.graph;

import de.unibi.agbi.biodwh2.core.collections.LongTrie;
import de.unibi.agbi.biodwh2.core.exceptions.GraphCacheException;
import de.unibi.agbi.biodwh2.core.io.mvstore.*;
import de.unibi.agbi.biodwh2.core.lang.Type;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

abstract class MVStoreGraph extends BaseGraph implements AutoCloseable {
    public static final int VERSION = 3;
    public static final String LABEL_PREFIX_SEPARATOR = "_";
    private static final char NODE_REPOSITORY_PREFIX = '$';
    private static final char EDGE_REPOSITORY_PREFIX = '!';
    private static final String VERSION_KEY = "version";
    public static final String EXTENSION = "db";

    private final boolean readOnly;
    private final Path filePath;
    private MVStoreDB database;
    private final MVMapWrapper<String, Object> metaMap;
    private final ConcurrentMap<String, MVStoreCollection<Node>> nodeRepositories;
    private final ConcurrentMap<String, MVStoreCollection<Edge>> edgeRepositories;

    protected MVStoreGraph(final Path filePath, final boolean reopen, final boolean readOnly) {
        this.filePath = filePath;
        this.readOnly = readOnly;
        if (!reopen)
            deleteOldDatabaseFile(filePath);
        nodeRepositories = new ConcurrentHashMap<>();
        edgeRepositories = new ConcurrentHashMap<>();
        database = openDatabase(filePath, readOnly);
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

    public Path getFilePath() {
        return filePath;
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

    @Override
    public void close() {
        // End all index delays that may still be enabled
        for (final MVStoreCollection<Edge> edges : edgeRepositories.values())
            edges.endIndicesDelay();
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
        if (label == null || label.isEmpty())
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
        if (label == null || label.isEmpty())
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

    public final void beginEdgeIndicesDelay(final String label) {
        getOrCreateEdgeRepository(label).beginIndicesDelay();
    }

    public final void endEdgeIndicesDelay(final String label) {
        getOrCreateEdgeRepository(label).endIndicesDelay();
    }

    public final IndexDescription[] indexDescriptions() {
        final List<IndexDescription> result = new ArrayList<>();
        for (final String label : nodeRepositories.keySet())
            for (final MVIndexDescription indexDescription : nodeRepositories.get(label).getIndexDescriptions())
                result.add(convertIndexDescription(IndexDescription.Target.NODE, label, indexDescription));
        for (final String label : edgeRepositories.keySet())
            for (final MVIndexDescription indexDescription : edgeRepositories.get(label).getIndexDescriptions())
                result.add(convertIndexDescription(IndexDescription.Target.EDGE, label, indexDescription));
        return result.toArray(new IndexDescription[0]);
    }

    private IndexDescription convertIndexDescription(final IndexDescription.Target target, final String label,
                                                     final MVIndexDescription indexDescription) {
        return new IndexDescription(target, label, indexDescription.getProperty(), indexDescription.isArrayProperty(),
                                    indexDescription.getType() == MVStoreIndexType.UNIQUE ?
                                    IndexDescription.Type.UNIQUE : IndexDescription.Type.NON_UNIQUE);
    }

    public final Map<String, Type> getPropertyKeyTypesForNodeLabel(final String label) {
        final MVStoreCollection<Node> nodes = nodeRepositories.get(label);
        if (nodes != null)
            return nodes.getPropertyKeyTypes();
        return new HashMap<>();
    }

    public final Map<String, Type> getPropertyKeyTypesForEdgeLabel(final String label) {
        final MVStoreCollection<Edge> edges = edgeRepositories.get(label);
        if (edges != null)
            return edges.getPropertyKeyTypes();
        return new HashMap<>();
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

    public final long getNumberOfNodes(final String label) {
        final MVStoreCollection<Node> nodes = nodeRepositories.get(label);
        return nodes != null ? nodes.size() : 0;
    }

    public final long getNumberOfEdges() {
        long result = 0;
        for (final MVStoreCollection<Edge> edges : edgeRepositories.values())
            result += edges.size();
        return result;
    }

    public final long getNumberOfEdges(final String label) {
        final MVStoreCollection<Edge> edges = edgeRepositories.get(label);
        return edges != null ? edges.size() : 0;
    }

    @Override
    public Iterable<Long> getNodeIds(final String label) {
        final MVStoreCollection<Node> nodes = nodeRepositories.get(label);
        return nodes != null ? nodes.keySet() : Collections.emptyList();
    }

    @Override
    public Iterable<Long> getEdgeIds(final String label) {
        final MVStoreCollection<Edge> edges = edgeRepositories.get(label);
        return edges != null ? edges.keySet() : Collections.emptyList();
    }

    @Override
    public final String[] getNodeLabels() {
        return nodeRepositories.keySet().toArray(new String[0]);
    }

    @Override
    public final String[] getEdgeLabels() {
        return edgeRepositories.keySet().toArray(new String[0]);
    }

    @Override
    public final Node getNode(final long nodeId) {
        for (final MVStoreCollection<Node> nodes : nodeRepositories.values()) {
            final Node node = nodes.get(nodeId);
            if (node != null)
                return node;
        }
        return null;
    }

    public final boolean nodeExists(final long nodeId) {
        for (final MVStoreCollection<Node> nodes : nodeRepositories.values())
            if (nodes.keySet().contains(nodeId))
                return true;
        return false;
    }

    @Override
    public String getNodeLabel(final long nodeId) {
        for (final String label : nodeRepositories.keySet())
            if (nodeRepositories.get(label).contains(nodeId))
                return label;
        return null;
    }

    @Override
    public final Edge getEdge(final long edgeId) {
        for (final MVStoreCollection<Edge> edges : edgeRepositories.values()) {
            final Edge edge = edges.get(edgeId);
            if (edge != null)
                return edge;
        }
        return null;
    }

    public final boolean edgeExists(final long edgeId) {
        for (final MVStoreCollection<Edge> edges : edgeRepositories.values())
            if (edges.keySet().contains(edgeId))
                return true;
        return false;
    }

    @Override
    public String getEdgeLabel(final long edgeId) {
        for (final String label : edgeRepositories.keySet())
            if (edgeRepositories.get(label).contains(edgeId))
                return label;
        return null;
    }

    public Map<String, Map<String, Long>> getEdgeFromToLabels(final String edgeLabel) {
        final MVStoreCollection<Edge> edges = edgeRepositories.get(edgeLabel);
        if (edges == null)
            return null;
        final Map<String, LongTrie> fromLabelEdgeIdsMap = new HashMap<>();
        final MVStoreIndex fromIndex = edges.getIndex(Edge.FROM_ID_FIELD);
        for (final Comparable<?> fromId : fromIndex.getIndexedValues()) {
            final String fromLabel = getNodeLabel((Long) fromId);
            final LongTrie edgeIds = fromLabelEdgeIdsMap.computeIfAbsent(fromLabel, k -> new LongTrie());
            edgeIds.addAll(fromIndex.find(fromId));
        }
        final Map<String, Map<String, Long>> result = new HashMap<>();
        for (final String key : fromLabelEdgeIdsMap.keySet())
            result.put(key, new HashMap<>());
        final MVStoreIndex toIndex = edges.getIndex(Edge.TO_ID_FIELD);
        for (final Comparable<?> toId : toIndex.getIndexedValues()) {
            final String toLabel = getNodeLabel((Long) toId);
            for (final Long edgeId : toIndex.find(toId)) {
                for (final Map.Entry<String, LongTrie> fromEntries : fromLabelEdgeIdsMap.entrySet()) {
                    if (fromEntries.getValue().contains(edgeId)) {
                        final Map<String, Long> toLabelCountMap = result.get(fromEntries.getKey());
                        Long edgeCount = toLabelCountMap.get(toLabel);
                        toLabelCountMap.put(toLabel, edgeCount != null ? edgeCount + 1L : 1L);
                        fromEntries.getValue().remove(edgeId);
                        break;
                    }
                }
            }
        }
        return result;
    }

    @Override
    public Iterable<Node> findNodes(final String label) {
        return () -> getOrCreateNodeRepository(label).iterator();
    }

    @Override
    public Iterable<Node> findNodes(final String label, final String propertyKey, final Comparable<?> value) {
        return getOrCreateNodeRepository(label).find(propertyKey, value);
    }

    @Override
    public Iterable<Node> findNodes(final String label, final String propertyKey1, final Comparable<?> value1,
                                    final String propertyKey2, final Comparable<?> value2) {
        return getOrCreateNodeRepository(label).find(propertyKey1, value1, propertyKey2, value2);
    }

    @Override
    public Iterable<Node> findNodes(final String label, final String propertyKey1, final Comparable<?> value1,
                                    final String propertyKey2, final Comparable<?> value2, final String propertyKey3,
                                    final Comparable<?> value3) {
        return getOrCreateNodeRepository(label).find(propertyKey1, value1, propertyKey2, value2, propertyKey3, value3);
    }

    @Override
    public Iterable<Node> findNodes(final String label, final Map<String, Comparable<?>> properties) {
        if (properties == null || properties.isEmpty())
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

    @Override
    public Iterable<Node> findNodes(final String propertyKey, final Comparable<?> value) {
        return () -> new RepositoriesIterator<>(nodeRepositories.values()) {
            @Override
            protected Iterator<Node> filterNextRepository(MVStoreCollection<Node> next) {
                return next.find(propertyKey, value).iterator();
            }
        };
    }

    @Override
    public Iterable<Node> findNodes(final String propertyKey1, final Comparable<?> value1, final String propertyKey2,
                                    final Comparable<?> value2) {
        return () -> new RepositoriesIterator<>(nodeRepositories.values()) {
            @Override
            protected Iterator<Node> filterNextRepository(MVStoreCollection<Node> next) {
                return next.find(propertyKey1, value1, propertyKey2, value2).iterator();
            }
        };
    }

    @Override
    public Iterable<Node> findNodes(final String propertyKey1, final Comparable<?> value1, final String propertyKey2,
                                    final Comparable<?> value2, final String propertyKey3, final Comparable<?> value3) {
        return () -> new RepositoriesIterator<>(nodeRepositories.values()) {
            @Override
            protected Iterator<Node> filterNextRepository(MVStoreCollection<Node> next) {
                return next.find(propertyKey1, value1, propertyKey2, value2, propertyKey3, value3).iterator();
            }
        };
    }

    @Override
    public Iterable<Node> findNodes(final String propertyKey1, final Comparable<?> value1, final String propertyKey2,
                                    final Comparable<?> value2, final String propertyKey3, final Comparable<?> value3,
                                    final String propertyKey4, final Comparable<?> value4) {
        return () -> new RepositoriesIterator<>(nodeRepositories.values()) {
            @Override
            protected Iterator<Node> filterNextRepository(MVStoreCollection<Node> next) {
                return next.find(propertyKey1, value1, propertyKey2, value2, propertyKey3, value3, propertyKey4, value4)
                           .iterator();
            }
        };
    }

    @Override
    public Iterable<Node> findNodes(final Map<String, Comparable<?>> properties) {
        if (properties == null || properties.isEmpty())
            return getNodes();
        final String[] keys = new String[properties.size()];
        final Comparable<?>[] values = new Comparable<?>[properties.size()];
        int index = 0;
        for (final String propertyKey : properties.keySet()) {
            keys[index] = propertyKey;
            values[index++] = properties.get(propertyKey);
        }
        return () -> new RepositoriesIterator<>(nodeRepositories.values()) {
            @Override
            protected Iterator<Node> filterNextRepository(MVStoreCollection<Node> next) {
                return next.find(keys, values).iterator();
            }
        };
    }

    public boolean containsEdge(final String label, final Node from, final Node to) {
        final MVStoreCollection<Edge> edges = edgeRepositories.get(label);
        if (edges == null)
            return false;
        return edges.getIndex(Edge.FROM_ID_FIELD).contains(from.getId()) && edges.getIndex(Edge.TO_ID_FIELD).contains(
                to.getId());
    }

    @Override
    public boolean containsEdge(final String label, final Long fromId, final Long toId) {
        final MVStoreCollection<Edge> edges = edgeRepositories.get(label);
        if (edges == null)
            return false;
        return edges.getIndex(Edge.FROM_ID_FIELD).contains(fromId) && edges.getIndex(Edge.TO_ID_FIELD).contains(toId);
    }

    @Override
    public Iterable<Edge> findEdges(final String label) {
        return () -> getOrCreateEdgeRepository(label).iterator();
    }

    @Override
    public Iterable<Edge> findEdges(final String label, final String propertyKey, final Comparable<?> value) {
        return getOrCreateEdgeRepository(label).find(propertyKey, value);
    }

    @Override
    public Iterable<Edge> findEdges(final String label, final String propertyKey1, final Comparable<?> value1,
                                    final String propertyKey2, final Comparable<?> value2) {
        return getOrCreateEdgeRepository(label).find(propertyKey1, value1, propertyKey2, value2);
    }

    @Override
    public Iterable<Edge> findEdges(final String label, final String propertyKey1, final Comparable<?> value1,
                                    final String propertyKey2, final Comparable<?> value2, final String propertyKey3,
                                    final Comparable<?> value3) {
        return getOrCreateEdgeRepository(label).find(propertyKey1, value1, propertyKey2, value2, propertyKey3, value3);
    }

    @Override
    public Iterable<Edge> findEdges(final String label, final Map<String, Comparable<?>> properties) {
        if (properties.isEmpty())
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

    @Override
    public Iterable<Edge> findEdges(final String propertyKey, final Comparable<?> value) {
        return () -> new RepositoriesIterator<>(edgeRepositories.values()) {
            @Override
            protected Iterator<Edge> filterNextRepository(MVStoreCollection<Edge> next) {
                return next.find(propertyKey, value).iterator();
            }
        };
    }

    @Override
    public Iterable<Edge> findEdges(final String propertyKey1, final Comparable<?> value1, final String propertyKey2,
                                    final Comparable<?> value2) {
        return () -> new RepositoriesIterator<>(edgeRepositories.values()) {
            @Override
            protected Iterator<Edge> filterNextRepository(MVStoreCollection<Edge> next) {
                return next.find(propertyKey1, value1, propertyKey2, value2).iterator();
            }
        };
    }

    @Override
    public Iterable<Edge> findEdges(final String propertyKey1, final Comparable<?> value1, final String propertyKey2,
                                    final Comparable<?> value2, final String propertyKey3, final Comparable<?> value3) {
        return () -> new RepositoriesIterator<>(edgeRepositories.values()) {
            @Override
            protected Iterator<Edge> filterNextRepository(MVStoreCollection<Edge> next) {
                return next.find(propertyKey1, value1, propertyKey2, value2, propertyKey3, value3).iterator();
            }
        };
    }

    @Override
    public Iterable<Edge> findEdges(final String propertyKey1, final Comparable<?> value1, final String propertyKey2,
                                    final Comparable<?> value2, final String propertyKey3, final Comparable<?> value3,
                                    final String propertyKey4, final Comparable<?> value4) {
        return () -> new RepositoriesIterator<>(edgeRepositories.values()) {
            @Override
            protected Iterator<Edge> filterNextRepository(MVStoreCollection<Edge> next) {
                return next.find(propertyKey1, value1, propertyKey2, value2, propertyKey3, value3, propertyKey4, value4)
                           .iterator();
            }
        };
    }

    @Override
    public Iterable<Edge> findEdges(final Map<String, Comparable<?>> properties) {
        if (properties == null || properties.isEmpty())
            return getEdges();
        final String[] keys = new String[properties.size()];
        final Comparable<?>[] values = new Comparable<?>[properties.size()];
        int index = 0;
        for (final String propertyKey : properties.keySet()) {
            keys[index] = propertyKey;
            values[index++] = properties.get(propertyKey);
        }
        return () -> new RepositoriesIterator<>(edgeRepositories.values()) {
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
        removeNode(second);
    }

    public final void removeNode(final Node node) {
        final MVStoreCollection<Node> nodes = nodeRepositories.get(node.getLabel());
        if (nodes != null)
            nodes.remove(node);
    }

    public final void removeEdge(final Edge edge) {
        final MVStoreCollection<Edge> edges = edgeRepositories.get(edge.getLabel());
        if (edges != null)
            edges.remove(edge);
    }

    public Map<Long, Long> mergeDatabase(final String labelPrefix, final MVStoreGraph databaseToMerge,
                                         final Consumer<Long> nodeProgressCallback,
                                         final Consumer<Long> edgeProgressCallback) {
        return mergeDatabase(labelPrefix, databaseToMerge, nodeProgressCallback, edgeProgressCallback, true, null);
    }

    public Map<Long, Long> mergeDatabase(final String labelPrefix, final MVStoreGraph databaseToMerge,
                                         final Consumer<Long> nodeProgressCallback,
                                         final Consumer<Long> edgeProgressCallback, final boolean resetIds,
                                         final Map<String, Object> injectedProperties) {
        final String dataSourcePrefix = StringUtils.isNotEmpty(labelPrefix) ? labelPrefix + LABEL_PREFIX_SEPARATOR : "";
        final var mapping = mergeDatabaseNodes(dataSourcePrefix, databaseToMerge, nodeProgressCallback, resetIds,
                                               injectedProperties, null);
        mergeDatabaseEdges(dataSourcePrefix, databaseToMerge, edgeProgressCallback, resetIds, injectedProperties,
                           mapping);
        return mapping;
    }

    private Map<Long, Long> mergeDatabaseNodes(final String dataSourcePrefix, final MVStoreGraph databaseToMerge,
                                               final Consumer<Long> nodeProgressCallback, final boolean resetIds,
                                               final Map<String, Object> injectedProperties,
                                               final String discardPropertyKey) {
        for (final String sourceLabel : databaseToMerge.nodeRepositories.keySet()) {
            final String targetLabel = dataSourcePrefix + sourceLabel;
            for (final MVStoreIndex index : databaseToMerge.nodeRepositories.get(sourceLabel).getIndices())
                getOrCreateNodeRepository(targetLabel).getIndex(index.getKey(), index.isArrayIndex(), index.getType());
        }
        final long[] counter = new long[]{0};
        final Map<Long, Long> mapping = new HashMap<>();
        for (final String sourceLabel : databaseToMerge.nodeRepositories.keySet()) {
            final String targetLabel = dataSourcePrefix + sourceLabel;
            final MVStoreCollection<Node> targetNodes = getOrCreateNodeRepository(targetLabel);
            databaseToMerge.nodeRepositories.get(sourceLabel).fastUnsafeIteration((n) -> {
                counter[0]++;
                if (nodeProgressCallback != null && counter[0] % 100_000 == 0)
                    nodeProgressCallback.accept(counter[0]);
                if (discardPropertyKey != null && n.hasProperty(discardPropertyKey))
                    return;
                final Long oldId = n.getId();
                if (resetIds)
                    n.resetId();
                n.setLabel(targetLabel);
                if (injectedProperties != null)
                    for (final var entry : injectedProperties.entrySet())
                        n.setProperty(entry.getKey(), entry.getValue());
                targetNodes.put(n);
                if (resetIds)
                    mapping.put(oldId, n.getId());
            });
        }
        // Cleanup node repositories without nodes such as remnants of merged dependencies
        for (final String label : getNodeLabels())
            if (getNumberOfNodes(label) == 0)
                removeNodeLabel(label);
        return mapping;
    }

    private void mergeDatabaseEdges(final String dataSourcePrefix, final MVStoreGraph databaseToMerge,
                                    final Consumer<Long> edgeProgressCallback, final boolean resetIds,
                                    final Map<String, Object> injectedProperties, final Map<Long, Long> mapping) {
        for (final String sourceLabel : databaseToMerge.edgeRepositories.keySet()) {
            final String targetLabel = dataSourcePrefix + sourceLabel;
            for (final MVStoreIndex index : databaseToMerge.edgeRepositories.get(sourceLabel).getIndices())
                getOrCreateEdgeRepository(targetLabel).getIndex(index.getKey(), index.isArrayIndex(), index.getType());
        }
        final long[] counter = new long[]{0};
        for (final String sourceLabel : databaseToMerge.edgeRepositories.keySet()) {
            final String targetLabel = dataSourcePrefix + sourceLabel;
            beginEdgeIndicesDelay(targetLabel);
            final MVStoreCollection<Edge> targetEdges = getOrCreateEdgeRepository(targetLabel);
            databaseToMerge.edgeRepositories.get(sourceLabel).fastUnsafeIteration((e) -> {
                counter[0]++;
                if (edgeProgressCallback != null && counter[0] % 100_000 == 0)
                    edgeProgressCallback.accept(counter[0]);
                if (resetIds) {
                    e.resetId();
                    e.setFromId(mapping.get(e.getFromId()));
                    e.setToId(mapping.get(e.getToId()));
                }
                e.setLabel(targetLabel);
                if (injectedProperties != null)
                    for (final var entry : injectedProperties.entrySet())
                        e.setProperty(entry.getKey(), entry.getValue());
                targetEdges.put(e);
            });
            endEdgeIndicesDelay(targetLabel);
        }
        // Cleanup edge repositories without edges such as remnants of merged dependencies
        for (final String label : getEdgeLabels())
            if (getNumberOfEdges(label) == 0)
                removeEdgeLabel(label);
    }

    public void mergeDatabaseComplex(final String labelPrefix, final MVStoreGraph databaseToMerge,
                                     final Consumer<Long> nodeProgressCallback,
                                     final Consumer<Long> edgeProgressCallback, final String discardPropertyKey,
                                     final Map<Long, Long> discardedNodeIdMap) {
        final String dataSourcePrefix = StringUtils.isNotEmpty(labelPrefix) ? labelPrefix + LABEL_PREFIX_SEPARATOR : "";
        final var mapping = mergeDatabaseNodes(dataSourcePrefix, databaseToMerge, nodeProgressCallback, true, null,
                                               discardPropertyKey);
        if (discardedNodeIdMap != null) {
            final var intersection = new HashSet<>(mapping.entrySet());
            intersection.retainAll(discardedNodeIdMap.entrySet());
            if (!intersection.isEmpty()) {
                System.out.println("ERROR: overlapping node id mappings");
            }
        }
        for (final String sourceLabel : databaseToMerge.edgeRepositories.keySet()) {
            final String targetLabel = dataSourcePrefix + sourceLabel;
            for (final MVStoreIndex index : databaseToMerge.edgeRepositories.get(sourceLabel).getIndices())
                getOrCreateEdgeRepository(targetLabel).getIndex(index.getKey(), index.isArrayIndex(), index.getType());
        }
        final long[] counter = new long[]{0};
        for (final String sourceLabel : databaseToMerge.edgeRepositories.keySet()) {
            final String targetLabel = dataSourcePrefix + sourceLabel;
            beginEdgeIndicesDelay(targetLabel);
            final MVStoreCollection<Edge> targetEdges = getOrCreateEdgeRepository(targetLabel);
            databaseToMerge.edgeRepositories.get(sourceLabel).fastUnsafeIteration((e) -> {
                counter[0]++;
                if (edgeProgressCallback != null && counter[0] % 100_000 == 0)
                    edgeProgressCallback.accept(counter[0]);
                if (discardPropertyKey != null && e.hasProperty(discardPropertyKey))
                    return;
                e.resetId();
                Long fromId = mapping.get(e.getFromId());
                if (fromId == null && discardedNodeIdMap != null)
                    fromId = discardedNodeIdMap.get(e.getFromId());
                if (fromId == null) {
                    // TODO: error
                    System.out.println("TODO: error edge fromId");
                    return;
                }
                e.setFromId(fromId);
                Long toId = mapping.get(e.getToId());
                if (toId == null && discardedNodeIdMap != null)
                    toId = discardedNodeIdMap.get(e.getToId());
                if (toId == null) {
                    // TODO: error
                    System.out.println("TODO: error edge toId");
                    return;
                }
                e.setToId(toId);
                e.setLabel(targetLabel);
                targetEdges.put(e);
            });
            endEdgeIndicesDelay(targetLabel);
        }
        // Cleanup edge repositories without edges such as remnants of merged dependencies
        for (final String label : getEdgeLabels())
            if (getNumberOfEdges(label) == 0)
                removeEdgeLabel(label);
    }

    public void removeNodeLabel(final String label) {
        if (!readOnly) {
            nodeRepositories.remove(label);
            database.removeCollection(NODE_REPOSITORY_PREFIX + label);
        }
    }

    public void removeEdgeLabel(final String label) {
        if (!readOnly) {
            edgeRepositories.remove(label);
            database.removeCollection(EDGE_REPOSITORY_PREFIX + label);
        }
    }

    private static class RepositoriesIterator<T extends MVStoreModel> implements Iterator<T> {
        private Iterator<T> current;
        private final Iterator<MVStoreCollection<T>> repositories;

        RepositoriesIterator(final Collection<MVStoreCollection<T>> repositories) {
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

        protected Iterator<T> filterNextRepository(final MVStoreCollection<T> next) {
            return next.iterator();
        }

        @Override
        public T next() {
            advanceRepositoryIteratorIfNeeded();
            return current != null ? current.next() : null;
        }
    }
}
