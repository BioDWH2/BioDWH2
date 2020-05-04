package de.unibi.agbi.biodwh2.core.model.graph;

import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.GraphCacheException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.sqlite.SQLiteConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;

public final class Graph {
    private static final String AttachedDatabaseName = "db_to_merge";

    private static final String PackedStringArraySplitter = "','";
    private static final String UnescapedQuotes = "'";
    private static final String EscapedQuotes = "''";
    private static final String UnescapedComma = ",";

    private static final String FindNodeQueryStart = "SELECT * FROM nodes WHERE __labels='%s'";
    private static final String FindNodeIdQueryStart = "SELECT __id FROM nodes WHERE __labels='%s'";
    private static final String FindNodeWithoutLabelQueryStart = "SELECT * FROM nodes";
    private static final String FindNodeIdWithoutLabelQueryStart = "SELECT __id FROM nodes";
    private static final String FindQueryParam = "%s='%s'";
    private static final String FindNullQueryParam = "%s IS NULL";
    private static final String FindInStringArrayQueryParam = "%s LIKE '%%%s%%'";
    private static final String FindInNonStringArrayQueryParam = "%s REGEXP '^.*\\|(.*,)?%s(,.*)?$'";

    private long nextNodeId = 1;
    private long nextEdgeId = 1;
    private Connection connection;
    private PreparedStatement insertNodeStatement;
    private PreparedStatement insertEdgeStatement;
    private final Set<String> nodeColumns = new HashSet<>();
    private final Set<String> edgeColumns = new HashSet<>();
    private Map<Long, Node> nodeCache;
    private Map<String, List<Long>> nodeLabelIdMap;
    private long maxDumpedId = 0;
    private String[] indexColumnNames = new String[0];

    public Graph(final String databaseFilePath) throws GraphCacheException {
        this(databaseFilePath, false);
    }

    public Graph(final String databaseFilePath, final boolean reopen) throws GraphCacheException {
        if (!reopen)
            deleteOldDatabaseFile(databaseFilePath);
        connection = openDatabaseConnection(databaseFilePath);
        if (reopen)
            updateReopenedIndices();
        else {
            prepareDatabaseTables(connection);
            nodeColumns.addAll(Arrays.asList("__id", "__labels"));
            edgeColumns.addAll(Arrays.asList("__id", "__label", "__from_id", "__to_id"));
        }
        prepareStatements();
        nodeCache = new HashMap<>();
        nodeLabelIdMap = new HashMap<>();
    }

    private void deleteOldDatabaseFile(final String databaseFilePath) throws GraphCacheException {
        try {
            Files.deleteIfExists(Paths.get(databaseFilePath));
        } catch (IOException e) {
            throw new GraphCacheException("Failed to remove old persistent database file '" + databaseFilePath + "'",
                                          e);
        }
    }

    private static Connection openDatabaseConnection(final String databaseFilePath) throws GraphCacheException {
        String url = "jdbc:sqlite:" + databaseFilePath;
        try {
            SQLiteConfig config = new SQLiteConfig();
            config.setTransactionMode(SQLiteConfig.TransactionMode.EXCLUSIVE);
            config.setJournalMode(SQLiteConfig.JournalMode.OFF);
            Connection connection = DriverManager.getConnection(url, config.toProperties());
            if (connection == null)
                throw new GraphCacheException(
                        "Failed to create persistent graph database file '" + databaseFilePath + "'");
            connection.setAutoCommit(false);
            return connection;
        } catch (SQLException e) {
            throw new GraphCacheException("Failed to create persistent graph database file '" + databaseFilePath + "'",
                                          e);
        }
    }

    private static void prepareDatabaseTables(Connection connection) throws GraphCacheException {
        String sql = "CREATE TABLE nodes (__id integer PRIMARY KEY, __labels TEXT NOT NULL);";
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new GraphCacheException("Failed to create persistent graph database tables", e);
        }
        sql = "CREATE INDEX node_labels ON nodes (__labels);";
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new GraphCacheException("Failed to create persistent graph database tables", e);
        }
        sql = "CREATE TABLE edges (__id integer PRIMARY KEY, __label TEXT NOT NULL, " +
              "__from_id integer NOT NULL, __to_id integer NOT NULL);";
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new GraphCacheException("Failed to create persistent graph database tables", e);
        }
    }

    private void prepareStatements() throws GraphCacheException {
        try {
            insertNodeStatement = connection.prepareStatement("INSERT INTO nodes (__id, __labels) VALUES (?, ?);");
            insertEdgeStatement = connection.prepareStatement(
                    "INSERT INTO edges (__id, __label, __from_id, __to_id) VALUES (?, ?, ?, ?);");
        } catch (SQLException e) {
            throw new GraphCacheException("Failed to prepare statements", e);
        }
    }

    private void updateReopenedIndices() {
        getColumnNamesForTable("nodes", nodeColumns);
        getColumnNamesForTable("edges", edgeColumns);
        loadNextIds();
    }

    private void getColumnNamesForTable(final String tableName, final Set<String> columnNames) {
        getColumnNamesForTable(null, tableName, columnNames);
    }

    private void getColumnNamesForTable(final String databaseName, final String tableName,
                                        final Set<String> columnNames) {
        try {
            String tableInfo = databaseName != null ? databaseName + ".table_info" : "table_info";
            ResultSet result = connection.createStatement().executeQuery(
                    "PRAGMA " + tableInfo + "(" + tableName + ");");
            while (result.next())
                columnNames.add(result.getString("name"));
            result.close();
        } catch (SQLException e) {
            throw new GraphCacheException("Failed to load table column names from persisted graph", e);
        }
    }

    private void loadNextIds() {
        try {
            ResultSet result = connection.createStatement().executeQuery(
                    "SELECT * FROM (SELECT MAX(__id) + 1 as next_node_id FROM nodes as a), (SELECT MAX(__id) + 1 as next_edge_id FROM edges as b)");
            nextNodeId = result.getLong("next_node_id");
            nextEdgeId = result.getLong("next_edge_id");
            maxDumpedId = nextNodeId - 1;
            result.close();
        } catch (SQLException e) {
            throw new GraphCacheException("Failed to load next ids from persisted graph", e);
        }
    }

    public void setIndexColumnNames(String... names) {
        indexColumnNames = names;
    }

    void setNodeProperty(final Node node, final String key, final Object value) throws GraphCacheException {
        if (value == null || nodeCache.containsKey(node.getId()))
            return;
        ensureNodeColumnExists(key);
        String packedValue = StringUtils.replace(packValue(value), UnescapedQuotes, EscapedQuotes);
        executeSql("UPDATE nodes SET \"" + key + "\"='" + packedValue + "' WHERE __id=" + node.getId() + ";");
    }

    private void ensureNodeColumnExists(String columnName) throws GraphCacheException {
        if (nodeColumns.contains(columnName))
            return;
        nodeColumns.add(columnName);
        executeSql("ALTER TABLE nodes ADD COLUMN \"" + columnName + "\" TEXT;");
        if (ArrayUtils.contains(indexColumnNames, columnName))
            executeSql("CREATE INDEX nodes_" + columnName + "_index ON nodes(" + columnName + ");");
    }

    private void executeSql(final String sql) throws GraphCacheException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new GraphCacheException("Failed to persist graph at query '" + sql + "'", e);
        }
    }

    public void prefixAllLabels(String prefix) throws GraphCacheException {
        executeSql("UPDATE nodes SET __labels = '" + prefix + "_' || REPLACE(__labels, ';', ';" + prefix + "_');");
    }

    void setEdgeProperty(final Edge edge, final String key, final Object value) throws GraphCacheException {
        if (value == null)
            return;
        ensureEdgeColumnExists(key);
        String packedValue = StringUtils.replace(packValue(value), UnescapedQuotes, EscapedQuotes);
        executeSql("UPDATE edges SET " + key + "='" + packedValue + "' WHERE __id=" + edge.getId() + ";");
    }

    private void ensureEdgeColumnExists(String columnName) throws GraphCacheException {
        if (edgeColumns.contains(columnName))
            return;
        edgeColumns.add(columnName);
        executeSql("ALTER TABLE edges ADD COLUMN " + columnName + " TEXT;");
    }

    String packValue(Object value) {
        if (value.getClass() == int[].class)
            value = ArrayUtils.toObject((int[]) value);
        else if (value.getClass() == long[].class)
            value = ArrayUtils.toObject((long[]) value);
        else if (value.getClass() == boolean[].class)
            value = ArrayUtils.toObject((boolean[]) value);
        else if (value.getClass() == byte[].class)
            value = ArrayUtils.toObject((byte[]) value);
        if (value.getClass().isArray()) {
            Class<?> valueType = value.getClass().getComponentType();
            StringBuilder joinedArray = new StringBuilder();
            Object[] array = (Object[]) value;
            for (int i = 0; i < array.length; i++) {
                if (i > 0)
                    joinedArray.append(UnescapedComma);
                String elementValue = array[i].toString();
                if (array[i] instanceof CharSequence)
                    joinedArray.append(UnescapedQuotes).append(elementValue.replace(UnescapedQuotes, EscapedQuotes))
                               .append(UnescapedQuotes);
                else
                    joinedArray.append(elementValue);
            }
            return valueType.getName() + "[]|" + joinedArray;
        }
        return value.getClass().getName() + "|" + value;
    }

    Object unpackValue(String packedValue) throws GraphCacheException {
        int typeEndIndex = packedValue.indexOf("|");
        try {
            String typeName = packedValue.substring(0, typeEndIndex);
            String value = packedValue.substring(typeEndIndex + 1);
            Class<?> type = typeName.endsWith("[]") ? Class.forName(
                    "[L" + typeName.substring(0, typeName.length() - 2) + ";") : Class.forName(typeName);
            if (type == String.class)
                return value;
            if (ClassUtils.isPrimitiveOrWrapper(type)) {
                if (type == Integer.class)
                    return Integer.parseInt(value);
                if (type == Long.class)
                    return Long.parseLong(value);
                if (type == Boolean.class)
                    return Boolean.parseBoolean(value);
                if (type == Byte.class)
                    return Byte.parseByte(value);
            }
            if (type.isArray()) {
                Class<?> valueType = type.getComponentType();
                if (value.length() == 0)
                    return java.lang.reflect.Array.newInstance(valueType, 0);
                String[] parts;
                if (valueType == String.class)
                    parts = StringUtils.splitByWholeSeparator(value, PackedStringArraySplitter);
                else
                    parts = StringUtils.splitByWholeSeparator(value, UnescapedComma);
                Object[] array = (Object[]) java.lang.reflect.Array.newInstance(valueType, parts.length);
                if (valueType == String.class)
                    for (int i = 0; i < parts.length; i++)
                        array[i] = StringUtils.strip(parts[i], UnescapedQuotes).replace(EscapedQuotes, UnescapedQuotes);
                if (ClassUtils.isPrimitiveOrWrapper(valueType)) {
                    if (valueType == Integer.class)
                        for (int i = 0; i < parts.length; i++)
                            array[i] = Integer.parseInt(parts[i]);
                    if (valueType == Long.class)
                        for (int i = 0; i < parts.length; i++)
                            array[i] = Long.parseLong(parts[i]);
                    if (valueType == Boolean.class)
                        for (int i = 0; i < parts.length; i++)
                            array[i] = Boolean.parseBoolean(parts[i]);
                    if (valueType == Byte.class)
                        for (int i = 0; i < parts.length; i++)
                            array[i] = Byte.parseByte(parts[i]);
                }
                return array;
            }
        } catch (ClassNotFoundException e) {
            throw new GraphCacheException("Failed to persist graph", e);
        }
        return null;
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
                    ensureNodeColumnExists(key);
                    if (!first)
                        sql.append(", ");
                    first = false;
                    String packedValue = StringUtils.replace(packValue(value), UnescapedQuotes, EscapedQuotes);
                    sql.append("\"").append(key).append("\"='").append(packedValue).append(UnescapedQuotes);
                }
                sql.append(" WHERE __id=").append(node.getId()).append(";");
                if (atLeastOneValueNotNull)
                    try (Statement statement = connection.createStatement()) {
                        statement.execute(sql.toString());
                    } catch (SQLException e) {
                        throw new GraphCacheException("Failed to persist graph at query '" + sql + "'", e);
                    }
            }
        }
        maxDumpedId = maxId;
        nodeCache.clear();
        nodeLabelIdMap.replaceAll((l, v) -> new ArrayList<>());
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
                nodeLabelIdMap.put(label, new ArrayList<>());
            nodeLabelIdMap.get(label).add(node.getId());
        }
    }

    public Edge addEdge(Node from, Node to, String label) throws GraphCacheException {
        Edge edge = new Edge(this, nextEdgeId, from.getId(), to.getId(), label);
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

    public Edge addEdge(Node from, long toId, String label) throws GraphCacheException {
        Edge edge = new Edge(this, nextEdgeId, from.getId(), toId, label);
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

    public Edge addEdge(long fromId, Node to, String label) throws ExporterException {
        Edge edge = new Edge(this, nextEdgeId, fromId, to.getId(), label);
        nextEdgeId++;
        try {
            insertEdgeStatement.setLong(1, edge.getId());
            insertEdgeStatement.setString(2, edge.getLabel());
            insertEdgeStatement.setLong(3, edge.getFromId());
            insertEdgeStatement.setLong(4, edge.getToId());
            insertEdgeStatement.execute();
        } catch (SQLException e) {
            throw new ExporterException("Failed to persist graph", e);
        }
        return edge;
    }

    public Edge addEdge(long fromId, long toId, String label) throws ExporterException {
        Edge edge = new Edge(this, nextEdgeId, fromId, toId, label);
        nextEdgeId++;
        try {
            insertEdgeStatement.setLong(1, edge.getId());
            insertEdgeStatement.setString(2, edge.getLabel());
            insertEdgeStatement.setLong(3, edge.getFromId());
            insertEdgeStatement.setLong(4, edge.getToId());
            insertEdgeStatement.execute();
        } catch (SQLException e) {
            throw new ExporterException("Failed to persist graph", e);
        }
        return edge;
    }

    public Iterable<Node> getNodes() throws GraphCacheException {
        ResultSet result;
        try {
            result = connection.createStatement().executeQuery("SELECT * FROM nodes");
        } catch (SQLException e) {
            throw new GraphCacheException("Failed to load nodes from persisted graph", e);
        }
        return () -> new Iterator<Node>() {
            @Override
            public boolean hasNext() {
                try {
                    return result.next();
                } catch (SQLException e) {
                    throw new GraphCacheException("Failed to load nodes from persisted graph", e);
                }
            }

            @Override
            public Node next() {
                try {
                    return createNodeFromResultSet(result);
                } catch (SQLException | GraphCacheException e) {
                    throw new GraphCacheException("Failed to load nodes from persisted graph", e);
                }
            }
        };
    }

    public Iterable<Edge> getEdges() throws GraphCacheException {
        ResultSet result;
        try {
            result = connection.createStatement().executeQuery("SELECT * FROM edges");
        } catch (SQLException e) {
            throw new GraphCacheException("Failed to load edges from persisted graph", e);
        }
        return () -> new Iterator<Edge>() {
            @Override
            public boolean hasNext() {
                try {
                    return result.next();
                } catch (SQLException e) {
                    throw new GraphCacheException("Failed to load edges from persisted graph", e);
                }
            }

            @Override
            public Edge next() {
                try {
                    return createEdgeFromResultSet(result);
                } catch (SQLException | GraphCacheException e) {
                    throw new GraphCacheException("Failed to load edges from persisted graph", e);
                }
            }
        };
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
        Node memoryNode = findNodeInMemory(label, propertyNames, values);
        if (memoryNode != null)
            return memoryNode;
        StringBuilder sql = new StringBuilder(
                label != null ? String.format(FindNodeQueryStart, label) : FindNodeWithoutLabelQueryStart);
        boolean addedCondition = label != null;
        for (int i = 0; i < values.length; i++) {
            sql.append(addedCondition ? " AND " : " WHERE ");
            addedCondition = true;
            if (values[i] != null) {
                String packedValue = StringUtils.replace(packValue(values[i]), UnescapedQuotes, EscapedQuotes);
                if (inArrays[i]) {
                    if (values[i].getClass() == String.class)
                        sql.append(String.format(FindInStringArrayQueryParam, propertyNames[i], packedValue));
                    else
                        sql.append(String.format(FindInNonStringArrayQueryParam, propertyNames[i], packedValue));
                } else
                    sql.append(String.format(FindQueryParam, propertyNames[i], packedValue));
            } else
                sql.append(String.format(FindNullQueryParam, propertyNames[i]));
        }
        try (ResultSet result = connection.createStatement().executeQuery(sql.toString())) {
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

    private Node findNodeInMemory(String label, String[] propertyNames, Object[] values) {
        if (label == null) {
            for (Node n : nodeCache.values()) {
                boolean success = true;
                for (int i = 0; i < propertyNames.length; i++)
                    if (!n.hasProperty(propertyNames[i]) || !Objects.equals(values[i], n.getProperty(propertyNames[i])))
                        success = false;
                if (success)
                    return n;
            }
        } else if (nodeLabelIdMap.containsKey(label))
            for (Long nodeId : nodeLabelIdMap.get(label)) {
                Node n = nodeCache.get(nodeId);
                boolean success = true;
                for (int i = 0; i < propertyNames.length; i++)
                    if (!n.hasProperty(propertyNames[i]) || !Objects.equals(values[i], n.getProperty(propertyNames[i])))
                        success = false;
                if (success)
                    return n;
            }
        return null;
    }

    private Node createNodeFromResultSet(ResultSet result) throws SQLException, GraphCacheException {
        Node node = new Node(this, result.getLong("__id"), false, result.getString("__labels").split(";"));
        for (int i = 3; i <= result.getMetaData().getColumnCount(); i++) {
            String value = result.getString(i);
            if (value != null) {
                Object unpackedValue = unpackValue(StringUtils.replace(value, EscapedQuotes, UnescapedQuotes));
                node.setProperty(result.getMetaData().getColumnName(i), unpackedValue, false, false);
            }
        }
        return node;
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
        Node memoryNode = findNodeInMemory(label, propertyNames, values);
        if (memoryNode != null)
            return memoryNode.getId();
        StringBuilder sql = new StringBuilder(
                label != null ? String.format(FindNodeIdQueryStart, label) : FindNodeIdWithoutLabelQueryStart);
        boolean addedCondition = label != null;
        for (int i = 0; i < values.length; i++) {
            sql.append(addedCondition ? " AND " : " WHERE ");
            addedCondition = true;
            if (values[i] != null) {
                String packedValue = StringUtils.replace(packValue(values[i]), UnescapedQuotes, EscapedQuotes);
                if (inArrays[i]) {
                    if (values[i].getClass() == String.class)
                        sql.append(String.format(FindInStringArrayQueryParam, propertyNames[i], packedValue));
                    else
                        sql.append(String.format(FindInNonStringArrayQueryParam, propertyNames[i], packedValue));
                } else
                    sql.append(String.format(FindQueryParam, propertyNames[i], packedValue));
            } else
                sql.append(String.format(FindNullQueryParam, propertyNames[i]));
        }
        Long id = null;
        try (ResultSet result = connection.createStatement().executeQuery(sql.toString())) {
            if (result.next())
                id = result.getLong(1);
            if (result.next())
                System.out.println("Warning, more nodes available for sql: " + sql);
        } catch (SQLException ignored) {
        }
        return id;
    }

    private Edge createEdgeFromResultSet(ResultSet result) throws SQLException, GraphCacheException {
        Edge edge = new Edge(this, result.getLong("__id"), result.getLong("__from_id"), result.getLong("__to_id"),
                             result.getString("__label"));
        for (int i = 5; i <= result.getMetaData().getColumnCount(); i++) {
            String value = result.getString(i);
            if (value != null) {
                Object unpackedValue = unpackValue(StringUtils.replace(value, EscapedQuotes, UnescapedQuotes));
                edge.setProperty(result.getMetaData().getColumnName(i), unpackedValue, false);
            }
        }
        return edge;
    }

    private void tryCommit() throws GraphCacheException {
        try {
            connection.commit();
        } catch (SQLException e) {
            throw new GraphCacheException("Failed to persist graph", e);
        }
    }

    public long getNumberOfNodes() {
        return nextNodeId - 1;
    }

    public void mergeDatabase(final String filePath) throws GraphCacheException {
        try {
            connection.setAutoCommit(true);
            connection.prepareStatement("ATTACH '" + filePath + "' as " + AttachedDatabaseName).execute();
            Set<String> mergeNodeColumnNames = new HashSet<>();
            Set<String> mergeEdgeColumnNames = new HashSet<>();
            getColumnNamesForTable(AttachedDatabaseName, "nodes", mergeNodeColumnNames);
            getColumnNamesForTable(AttachedDatabaseName, "edges", mergeEdgeColumnNames);
            for (String columnName : mergeNodeColumnNames)
                ensureNodeColumnExists(columnName);
            for (String columnName : mergeEdgeColumnNames)
                ensureEdgeColumnExists(columnName);
            String targetNodeColumnNamesJoined = String.join(", ", mergeNodeColumnNames);
            String targetEdgeColumnNamesJoined = String.join(", ", mergeEdgeColumnNames);
            String nodeIdOffsetSelect = "__id + " + (nextNodeId - 1);
            String edgeIdOffsetSelect = "__id + " + (nextEdgeId - 1);
            String sourceNodeColumnNamesJoined = targetNodeColumnNamesJoined.replace("__id", nodeIdOffsetSelect);
            String sourceEdgeColumnNamesJoined = targetEdgeColumnNamesJoined.replace("__id", edgeIdOffsetSelect);
            connection.prepareStatement(
                    "INSERT INTO nodes (" + targetNodeColumnNamesJoined + ") SELECT " + sourceNodeColumnNamesJoined +
                    " FROM " + AttachedDatabaseName + ".nodes").execute();
            connection.prepareStatement(
                    "INSERT INTO edges (" + targetEdgeColumnNamesJoined + ") SELECT " + sourceEdgeColumnNamesJoined +
                    " FROM " + AttachedDatabaseName + ".edges").execute();
            connection.prepareStatement("DETACH " + AttachedDatabaseName).execute();
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new GraphCacheException("Failed to merge database '" + filePath + "'", e);
        }
        updateReopenedIndices();
    }

    public void dispose() {
        try {
            connection.commit();
        } catch (SQLException ignored) {
        }
        try {
            insertNodeStatement.close();
            insertEdgeStatement.close();
            connection.close();
        } catch (SQLException ignored) {
        }
        insertNodeStatement = null;
        insertEdgeStatement = null;
        connection = null;
        nodeCache = null;
        nodeLabelIdMap = null;
    }
}
