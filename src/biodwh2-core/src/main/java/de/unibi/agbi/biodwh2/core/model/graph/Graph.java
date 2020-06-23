package de.unibi.agbi.biodwh2.core.model.graph;

import de.unibi.agbi.biodwh2.core.exceptions.GraphCacheException;
import de.unibi.agbi.biodwh2.core.io.SqliteDatabase;
import de.unibi.agbi.biodwh2.core.io.ValuePacker;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;

public final class Graph {
    private static final String AttachedDatabaseName = "db_to_merge";

    private static final String UnescapedQuotes = "'";
    private static final String EscapedQuotes = "''";

    private static final String FindNodeQueryStart = "SELECT * FROM nodes WHERE __labels='%s'";
    private static final String FindNodeIdQueryStart = "SELECT __id FROM nodes WHERE __labels='%s'";
    private static final String FindNodeWithoutLabelQueryStart = "SELECT * FROM nodes";
    private static final String FindNodeIdWithoutLabelQueryStart = "SELECT __id FROM nodes";
    private static final String FindQueryParam = "%s='%s'";
    private static final String FindNullQueryParam = "%s IS NULL";
    private static final String FindInStringArrayQueryParam = "%s REGEXP \"^.*\\|(.*,)?'%s'(,.*)?$\"";
    private static final String FindInNonStringArrayQueryParam = "%s REGEXP \"^.*\\|(.*,)?%s(,.*)?$\"";

    private final Map<Class<?>, ClassMapping> classMappingsCache = new HashMap<>();
    private long nextNodeId = 1;
    private long nextEdgeId = 1;
    private SqliteDatabase database;
    private PreparedStatement insertNodeStatement;
    private PreparedStatement insertEdgeStatement;
    private Map<Long, Node> nodeCache;
    private Map<String, Set<Long>> nodeLabelIdMap;
    private long maxDumpedId = 0;
    private String[] indexColumnNames = new String[0];

    public Graph(final String databaseFilePath) throws GraphCacheException {
        this(databaseFilePath, false);
    }

    public Graph(final String databaseFilePath, final boolean reopen) throws GraphCacheException {
        if (!reopen)
            deleteOldDatabaseFile(databaseFilePath);
        database = openDatabase(databaseFilePath);
        if (reopen)
            loadNextIds();
        else
            prepareDatabaseTables(database);
        prepareStatements();
        nodeCache = new HashMap<>();
        nodeLabelIdMap = new HashMap<>();
    }

    private void deleteOldDatabaseFile(final String filePath) throws GraphCacheException {
        try {
            Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            throw new GraphCacheException("Failed to remove old persisted database file '" + filePath + "'", e);
        }
    }

    private static SqliteDatabase openDatabase(final String filePath) throws GraphCacheException {
        try {
            return new SqliteDatabase(filePath);
        } catch (SQLException e) {
            throw new GraphCacheException("Failed to create persisted graph database file '" + filePath + "'", e);
        }
    }

    private static void prepareDatabaseTables(SqliteDatabase database) throws GraphCacheException {
        try {
            database.execute("CREATE TABLE nodes (__id integer PRIMARY KEY, __labels TEXT NOT NULL)");
            database.execute("CREATE TABLE edges (__id integer PRIMARY KEY, __label TEXT NOT NULL, " +
                             "__from_id integer NOT NULL, __to_id integer NOT NULL);");
            database.addIndexIfNotExists("nodes", "__labels");
            database.addIndexIfNotExists("edges", "__label");
            database.addIndexIfNotExists("edges", "__from_id");
            database.addIndexIfNotExists("edges", "__to_id");
        } catch (SQLException e) {
            throw new GraphCacheException("Failed to create persisted graph database tables", e);
        }
        database.updateCaches();
    }

    private void prepareStatements() throws GraphCacheException {
        try {
            insertNodeStatement = database.prepareStatement("INSERT INTO nodes (__id, __labels) VALUES (?, ?)");
            insertEdgeStatement = database.prepareStatement(
                    "INSERT INTO edges (__id, __label, __from_id, __to_id) VALUES (?, ?, ?, ?)");
        } catch (SQLException e) {
            throw new GraphCacheException("Failed to prepare statements", e);
        }
    }

    private void loadNextIds() {
        try {
            nextNodeId = database.getMaxColumnValue("nodes", "__id") + 1;
            nextEdgeId = database.getMaxColumnValue("edges", "__id") + 1;
            maxDumpedId = nextNodeId - 1;
        } catch (SQLException e) {
            throw new GraphCacheException("Failed to load next ids from persisted graph", e);
        }
    }

    public final <T> Node createNodeFromModel(final T obj) throws GraphCacheException {
        ClassMapping mapping = getClassMappingFromCache(obj.getClass());
        Node node = addNode(mapping.labels);
        setNodePropertiesFromClassMapping(node, mapping, obj);
        return node;
    }

    private ClassMapping getClassMappingFromCache(Class<?> type) {
        if (!classMappingsCache.containsKey(type))
            classMappingsCache.put(type, new ClassMapping(type));
        return classMappingsCache.get(type);
    }

    private void setNodePropertiesFromClassMapping(final Node node, final ClassMapping mapping,
                                                   final Object obj) throws GraphCacheException {
        try {
            for (ClassMapping.ClassMappingField field : mapping.fields)
                node.setProperty(field.propertyName, field.field.get(obj), true, true, false);
        } catch (IllegalAccessException e) {
            throw new GraphCacheException(e);
        }
    }

    public void setIndexColumnNames(String... names) {
        indexColumnNames = names;
    }

    void setNodeProperty(final Node node, final String key, final Object value) throws GraphCacheException {
        try {
            database.addColumnIfNotExists("nodes", key, "TEXT", ArrayUtils.contains(indexColumnNames, key));
        } catch (SQLException e) {
            throw new GraphCacheException("Failed to persist graph", e);
        }
        if (value == null || nodeCache.containsKey(node.getId()))
            return;
        String packedValue = StringUtils.replace(ValuePacker.packValue(value), UnescapedQuotes, EscapedQuotes);
        executeSql("UPDATE nodes SET \"" + key + "\"='" + packedValue + "' WHERE __id=" + node.getId());
    }

    private void executeSql(final String sql) throws GraphCacheException {
        try {
            database.execute(sql);
        } catch (SQLException e) {
            throw new GraphCacheException("Failed to persist graph at query '" + sql + "'", e);
        }
    }

    public void prefixAllLabels(String prefix) throws GraphCacheException {
        executeSql("UPDATE nodes SET __labels = '" + prefix + "_' || REPLACE(__labels, ';', ';" + prefix + "_')");
        executeSql("UPDATE edges SET __label = '" + prefix + "_' || __label");
    }

    void setEdgeProperty(final Edge edge, final String key, final Object value) throws GraphCacheException {
        try {
            database.addColumnIfNotExists("edges", key, "TEXT");
        } catch (SQLException e) {
            throw new GraphCacheException("Failed to persist graph", e);
        }
        if (value != null) {
            String packedValue = StringUtils.replace(ValuePacker.packValue(value), UnescapedQuotes, EscapedQuotes);
            executeSql("UPDATE edges SET " + key + "='" + packedValue + "' WHERE __id=" + edge.getId());
        }
    }

    public void synchronize(boolean force) throws GraphCacheException {
        if (!force && nodeCache.size() < 100000)
            return;
        long maxId = maxDumpedId;
        for (Long id : nodeCache.keySet()) {
            Node node = nodeCache.get(id);
            if (!node.isModified())
                continue;
            if (id > maxDumpedId) {
                try {
                    insertNodeStatement.setLong(1, node.getId());
                    insertNodeStatement.setString(2, String.join(";", node.getLabels()));
                    insertNodeStatement.execute();
                } catch (SQLException e) {
                    throw new GraphCacheException("Failed to persist graph", e);
                }
            }
            if (id > maxId)
                maxId = id;
            boolean atLeastOneValueNotNull = false;
            if (node.getPropertyKeys().size() > 0) {
                boolean first = true;
                StringBuilder sql = new StringBuilder("UPDATE nodes SET ");
                for (String key : node.getPropertyKeys()) {
                    Object value = node.getProperty(key);
                    if (value == null)
                        continue;
                    atLeastOneValueNotNull = true;
                    try {
                        database.addColumnIfNotExists("nodes", key, "TEXT", ArrayUtils.contains(indexColumnNames, key));
                    } catch (SQLException e) {
                        throw new GraphCacheException("Failed to persist graph", e);
                    }
                    if (!first)
                        sql.append(", ");
                    first = false;
                    String packedValue = StringUtils.replace(ValuePacker.packValue(value), UnescapedQuotes,
                                                             EscapedQuotes);
                    sql.append("\"").append(key).append("\"='").append(packedValue).append(UnescapedQuotes);
                }
                sql.append(" WHERE __id=").append(node.getId()).append(";");
                if (atLeastOneValueNotNull)
                    try {
                        database.execute(sql.toString());
                    } catch (SQLException e) {
                        throw new GraphCacheException("Failed to persist graph at query '" + sql + "'", e);
                    }
            }
        }
        maxDumpedId = maxId;
        nodeCache = new HashMap<>();
        nodeLabelIdMap.replaceAll((l, v) -> new HashSet<>());
        tryCommit();
    }

    public Node addNode(String... labels) throws GraphCacheException {
        synchronize(false);
        Node node = new Node(this, nextNodeId, true, labels);
        nextNodeId++;
        addNodeToMemoryCache(node);
        return node;
    }

    private void addNodeToMemoryCache(Node node) {
        nodeCache.put(node.getId(), node);
        for (String label : node.getLabels()) {
            if (!nodeLabelIdMap.containsKey(label))
                nodeLabelIdMap.put(label, new HashSet<>());
            nodeLabelIdMap.get(label).add(node.getId());
        }
    }

    public Edge addEdge(Node from, Node to, String label) throws GraphCacheException {
        return addEdge(from.getId(), to.getId(), label);
    }

    public Edge addEdge(Node from, long toId, String label) throws GraphCacheException {
        return addEdge(from.getId(), toId, label);
    }

    public Edge addEdge(long fromId, Node to, String label) throws GraphCacheException {
        return addEdge(fromId, to.getId(), label);
    }

    public Edge addEdge(long fromId, long toId, String label) throws GraphCacheException {
        Edge edge = new Edge(this, nextEdgeId, fromId, toId, label);
        nextEdgeId++;
        try {
            insertEdgeStatement.setLong(1, edge.getId());
            insertEdgeStatement.setString(2, edge.getLabel());
            insertEdgeStatement.setLong(3, edge.getFromId());
            insertEdgeStatement.setLong(4, edge.getToId());
            insertEdgeStatement.execute();
        } catch (SQLException e) {
            throw new GraphCacheException("Failed to persist graph", e);
        }
        return edge;
    }

    public void addEdgeFast(Node from, Node to, String label) throws GraphCacheException {
        addEdgeFast(from.getId(), to.getId(), label);
    }

    public void addEdgeFast(Node from, long toId, String label) throws GraphCacheException {
        addEdgeFast(from.getId(), toId, label);
    }

    public void addEdgeFast(long fromId, Node to, String label) throws GraphCacheException {
        addEdgeFast(fromId, to.getId(), label);
    }

    public void addEdgeFast(long fromId, long toId, String label) throws GraphCacheException {
        long edgeId = nextEdgeId;
        nextEdgeId++;
        try {
            insertEdgeStatement.setLong(1, edgeId);
            insertEdgeStatement.setString(2, label);
            insertEdgeStatement.setLong(3, fromId);
            insertEdgeStatement.setLong(4, toId);
            insertEdgeStatement.execute();
        } catch (SQLException e) {
            throw new GraphCacheException("Failed to persist graph", e);
        }
    }

    public Iterable<Node> getNodes() throws GraphCacheException {
        try {
            return database.iterateTable("nodes", this::createNodeFromResultSet);
        } catch (SQLException e) {
            throw new GraphCacheException("Failed to load nodes from persisted graph", e);
        }
    }

    public Iterable<Edge> getEdges() throws GraphCacheException {
        try {
            return database.iterateTable("edges", this::createEdgeFromResultSet);
        } catch (SQLException e) {
            throw new GraphCacheException("Failed to load edges from persisted graph", e);
        }
    }

    public Node findNode(String label, String propertyName, Object value) {
        return findNode(label, propertyName, value, false);
    }

    @SuppressWarnings("unused")
    public Node findNode(String label, String propertyName1, Object value1, String propertyName2, Object value2) {
        return findNode(label, new String[]{propertyName1, propertyName2}, new Object[]{value1, value2},
                        new boolean[]{false, false});
    }

    @SuppressWarnings("unused")
    public Node findNode(String label, String propertyName1, Object value1, String propertyName2, Object value2,
                         String propertyName3, Object value3) {
        return findNode(label, new String[]{propertyName1, propertyName2, propertyName3},
                        new Object[]{value1, value2, value3}, new boolean[]{false, false, false});
    }

    @SuppressWarnings("WeakerAccess")
    public Node findNode(String label, String propertyName, Object value, boolean inArray) {
        return findNode(label, new String[]{propertyName}, new Object[]{value}, new boolean[]{inArray});
    }

    private Node findNode(String label, String[] propertyNames, Object[] values, boolean[] inArrays) {
        List<Node> memoryNodes = findNodesInMemory(label, propertyNames, values, inArrays, 1);
        if (memoryNodes != null)
            return memoryNodes.get(0);
        String sql = getFindNodeQuery(false, label, propertyNames, values, inArrays);
        try (ResultSet result = database.executeQuery(sql)) {
            if (result.next()) {
                Node node = createNodeFromResultSet(result);
                addNodeToMemoryCache(node);
                if (result.next())
                    System.out.println("Warning, more nodes available for sql: " + sql);
                return node;
            }
        } catch (SQLException | GraphCacheException ignored) {
        }
        return null;
    }

    private List<Node> findNodesInMemory(final String label, final String[] propertyNames, final Object[] values,
                                         final boolean[] inArrays, final int limit) {
        List<Node> result = new ArrayList<>();
        if (label == null) {
            for (Node n : nodeCache.values())
                if (nodeMatchesSearchCriteria(n, propertyNames, values, inArrays)) {
                    result.add(n);
                    if (limit != -1 && result.size() == limit)
                        break;
                }
        } else if (nodeLabelIdMap.containsKey(label)) {
            Node n;
            for (Long nodeId : nodeLabelIdMap.get(label)) {
                n = nodeCache.get(nodeId);
                if (nodeMatchesSearchCriteria(n, propertyNames, values, inArrays)) {
                    result.add(n);
                    if (limit != -1 && result.size() == limit)
                        break;
                }
            }
        }
        return result.size() > 0 ? result : null;
    }

    private static boolean nodeMatchesSearchCriteria(final Node node, final String[] propertyNames,
                                                     final Object[] values, final boolean[] inArrays) {
        for (int i = 0; i < propertyNames.length; i++) {
            if (!node.hasProperty(propertyNames[i]))
                return false;
            if (!inArrays[i] && !node.propertyEquals(propertyNames[i], values[i]))
                return false;
            if (inArrays[i] && !node.propertyArrayContains(propertyNames[i], values[i]))
                return false;
        }
        return true;
    }

    private String getFindNodeQuery(boolean idOnly, String label, String[] propertyNames, Object[] values,
                                    boolean[] inArrays) {
        StringBuilder sql = new StringBuilder();
        if (label != null)
            sql.append(String.format(idOnly ? FindNodeIdQueryStart : FindNodeQueryStart, label));
        else
            sql.append(idOnly ? FindNodeIdWithoutLabelQueryStart : FindNodeWithoutLabelQueryStart);
        boolean addedCondition = label != null;
        for (int i = 0; i < values.length; i++) {
            sql.append(addedCondition ? " AND " : " WHERE ");
            addedCondition = true;
            if (values[i] != null) {
                String packedValue = StringUtils.replace(ValuePacker.packValue(values[i]), UnescapedQuotes,
                                                         EscapedQuotes);
                if (inArrays[i]) {
                    String packedArrayValue = StringUtils.replace(values[i].toString(), UnescapedQuotes, EscapedQuotes);
                    if (values[i].getClass() == String.class)
                        sql.append(String.format(FindInStringArrayQueryParam, propertyNames[i], packedArrayValue));
                    else
                        sql.append(String.format(FindInNonStringArrayQueryParam, propertyNames[i], packedArrayValue));
                } else
                    sql.append(String.format(FindQueryParam, propertyNames[i], packedValue));
            } else
                sql.append(String.format(FindNullQueryParam, propertyNames[i]));
        }
        return sql.toString();
    }

    private Node createNodeFromResultSet(ResultSet result) throws GraphCacheException {
        try {
            String[] labels = StringUtils.split(result.getString("__labels"), ';');
            Node node = new Node(this, result.getLong("__id"), false, labels);
            for (int i = 3; i <= result.getMetaData().getColumnCount(); i++) {
                String value = result.getString(i);
                if (value != null) {
                    Object unpackedValue = ValuePacker.unpackValue(
                            StringUtils.replace(value, EscapedQuotes, UnescapedQuotes));
                    node.setProperty(result.getMetaData().getColumnName(i), unpackedValue, false, false, false);
                }
            }
            return node;
        } catch (SQLException e) {
            throw new GraphCacheException("Failed to load node from persisted graph", e);
        }
    }

    @SuppressWarnings("unused")
    public List<Node> findNodes(String label, String propertyName, Object value) {
        return findNodes(label, propertyName, value, false);
    }

    @SuppressWarnings("unused")
    public List<Node> findNodes(String label, String propertyName1, Object value1, String propertyName2,
                                Object value2) {
        return findNodes(label, new String[]{propertyName1, propertyName2}, new Object[]{value1, value2},
                         new boolean[]{false, false});
    }

    @SuppressWarnings("unused")
    public List<Node> findNodes(String label, String propertyName1, Object value1, String propertyName2, Object value2,
                                String propertyName3, Object value3) {
        return findNodes(label, new String[]{propertyName1, propertyName2, propertyName3},
                         new Object[]{value1, value2, value3}, new boolean[]{false, false, false});
    }

    @SuppressWarnings("WeakerAccess")
    public List<Node> findNodes(String label, String propertyName, Object value, boolean inArray) {
        return findNodes(label, new String[]{propertyName}, new Object[]{value}, new boolean[]{inArray});
    }

    private List<Node> findNodes(String label, String[] propertyNames, Object[] values, boolean[] inArrays) {
        List<Node> nodes = findNodesInMemory(label, propertyNames, values, inArrays, -1);
        if (nodes == null)
            nodes = new ArrayList<>();
        String sql = getFindNodeQuery(false, label, propertyNames, values, inArrays);
        try (ResultSet result = database.executeQuery(sql)) {
            while (result.next()) {
                Node node = createNodeFromResultSet(result);
                boolean alreadyAdded = false;
                for (Node foundNode : nodes)
                    if (foundNode.getId() == node.getId()) {
                        alreadyAdded = true;
                        break;
                    }
                if (!alreadyAdded) {
                    addNodeToMemoryCache(node);
                    nodes.add(node);
                }
            }
        } catch (SQLException | GraphCacheException ignored) {
        }
        return nodes.size() > 0 ? nodes : null;
    }

    public Long findNodeId(String label, String propertyName, Object value) {
        return findNodeId(label, new String[]{propertyName}, new Object[]{value}, new boolean[]{false});
    }

    @SuppressWarnings("unused")
    public Long findNodeId(String label, String propertyName1, Object value1, String propertyName2, Object value2) {
        return findNodeId(label, new String[]{propertyName1, propertyName2}, new Object[]{value1, value2},
                          new boolean[]{false, false});
    }

    public Long findNodeId(String label, String propertyName1, Object value1, String propertyName2, Object value2,
                           String propertyName3, Object value3) {
        return findNodeId(label, new String[]{propertyName1, propertyName2, propertyName3},
                          new Object[]{value1, value2, value3}, new boolean[]{false, false, false});
    }

    @SuppressWarnings("unused")
    public Long findNodeId(String label, String propertyName, Object value, boolean inArray) {
        return findNodeId(label, new String[]{propertyName}, new Object[]{value}, new boolean[]{inArray});
    }

    private Long findNodeId(String label, String[] propertyNames, Object[] values, boolean[] inArrays) {
        List<Node> memoryNodes = findNodesInMemory(label, propertyNames, values, inArrays, 1);
        if (memoryNodes != null)
            return memoryNodes.get(0).getId();
        String sql = getFindNodeQuery(true, label, propertyNames, values, inArrays);
        Long id = null;
        try (ResultSet result = database.executeQuery(sql)) {
            if (result.next())
                id = result.getLong(1);
            if (result.next())
                System.out.println("Warning, more nodes available for sql: " + sql);
        } catch (SQLException ignored) {
        }
        return id;
    }

    private Edge createEdgeFromResultSet(ResultSet result) throws GraphCacheException {
        try {
            Edge edge = new Edge(this, result.getLong("__id"), result.getLong("__from_id"), result.getLong("__to_id"),
                                 result.getString("__label"));
            for (int i = 5; i <= result.getMetaData().getColumnCount(); i++) {
                String value = result.getString(i);
                if (value != null) {
                    Object unpackedValue = ValuePacker.unpackValue(
                            StringUtils.replace(value, EscapedQuotes, UnescapedQuotes));
                    edge.setProperty(result.getMetaData().getColumnName(i), unpackedValue, false);
                }
            }
            return edge;
        } catch (SQLException e) {
            throw new GraphCacheException("Failed to load edge from persisted graph", e);
        }
    }

    private void tryCommit() throws GraphCacheException {
        try {
            database.commit();
        } catch (SQLException e) {
            throw new GraphCacheException("Failed to persist graph", e);
        }
    }

    public long getNumberOfNodes() {
        return nextNodeId - 1;
    }

    public Node getNode(long id) {
        if (nodeCache.containsKey(id))
            return nodeCache.get(id);
        String sql = "SELECT * FROM nodes WHERE __id=" + id;
        try (ResultSet result = database.executeQuery(sql)) {
            if (result.next()) {
                Node node = createNodeFromResultSet(result);
                addNodeToMemoryCache(node);
                return node;
            }
        } catch (SQLException | GraphCacheException ignored) {
        }
        return null;
    }

    public Long[] getAdjacentNodeIdsForEdgeLabel(final long nodeId, final String edgeLabel) {
        final Set<Long> nodeIds = new HashSet<>();
        final String sql =
                "SELECT __from_id, __to_id FROM edges WHERE (__from_id = " + nodeId + " OR __to_id = " + nodeId +
                ") AND __label = '" + edgeLabel + "'";
        try (ResultSet result = database.executeQuery(sql)) {
            while (result.next()) {
                long id1 = result.getLong(1);
                if (id1 != nodeId)
                    nodeIds.add(id1);
                long id2 = result.getLong(2);
                if (id2 != nodeId)
                    nodeIds.add(id2);
            }
        } catch (SQLException | GraphCacheException ignored) {
        }
        return nodeIds.toArray(new Long[0]);
    }

    public void mergeNodes(final Node first, final Node second) {
        executeSql("UPDATE edges SET __from_id=" + first.getId() + " WHERE __from_id=" + second.getId());
        executeSql("UPDATE edges SET __to_id=" + first.getId() + " WHERE __to_id=" + second.getId());
        // TODO: properties
        nodeCache.remove(second.getId());
        executeSql("DELETE FROM nodes WHERE __id=" + second.getId());
    }

    public void mergeDatabase(final String filePath) throws GraphCacheException {
        try {
            database.setAutoCommit(true);
            database.execute("ATTACH '" + filePath + "' as " + AttachedDatabaseName);
            Set<String> mergeNodeColumnNames = new HashSet<>();
            Set<String> mergeEdgeColumnNames = new HashSet<>();
            database.getColumnNamesForTable(AttachedDatabaseName + ".nodes", mergeNodeColumnNames);
            database.getColumnNamesForTable(AttachedDatabaseName + ".edges", mergeEdgeColumnNames);
            for (String columnName : mergeNodeColumnNames)
                database.addColumnIfNotExists("nodes", columnName, "TEXT");
            for (String columnName : mergeEdgeColumnNames)
                database.addColumnIfNotExists("edges", columnName, "TEXT");
            String targetNodeColumnNamesJoined = "\"" + String.join("\", \"", mergeNodeColumnNames) + "\"";
            String targetEdgeColumnNamesJoined = "\"" + String.join("\", \"", mergeEdgeColumnNames) + "\"";
            String nodeIdOffsetSelect = "__id + " + (nextNodeId - 1);
            String edgeIdOffsetSelect = "__id + " + (nextEdgeId - 1);
            String edgeFromIdOffsetSelect = "__from_id + " + (nextNodeId - 1);
            String edgeToIdOffsetSelect = "__to_id + " + (nextNodeId - 1);
            String sourceNodeColumnNamesJoined = targetNodeColumnNamesJoined.replace("\"__id\"", nodeIdOffsetSelect);
            String sourceEdgeColumnNamesJoined = targetEdgeColumnNamesJoined.replace("\"__id\"", edgeIdOffsetSelect);
            sourceEdgeColumnNamesJoined = sourceEdgeColumnNamesJoined.replace("\"__from_id\"", edgeFromIdOffsetSelect);
            sourceEdgeColumnNamesJoined = sourceEdgeColumnNamesJoined.replace("\"__to_id\"", edgeToIdOffsetSelect);
            database.execute(
                    "INSERT INTO nodes (" + targetNodeColumnNamesJoined + ") SELECT " + sourceNodeColumnNamesJoined +
                    " FROM " + AttachedDatabaseName + ".nodes");
            database.execute(
                    "INSERT INTO edges (" + targetEdgeColumnNamesJoined + ") SELECT " + sourceEdgeColumnNamesJoined +
                    " FROM " + AttachedDatabaseName + ".edges");
            database.execute("DETACH " + AttachedDatabaseName);
            database.setAutoCommit(false);
        } catch (SQLException e) {
            throw new GraphCacheException("Failed to merge database '" + filePath + "'", e);
        }
        loadNextIds();
    }

    public void dispose() {
        try {
            insertNodeStatement.close();
            insertEdgeStatement.close();
        } catch (SQLException ignored) {
        }
        database.dispose();
        insertNodeStatement = null;
        insertEdgeStatement = null;
        database = null;
        nodeCache = null;
        nodeLabelIdMap = null;
    }

    public static Graph createTempGraph() throws IOException {
        Path tempFilePath = Files.createTempFile("graphdb_test", ".sqlite");
        return new Graph(tempFilePath.toString());
    }
}
