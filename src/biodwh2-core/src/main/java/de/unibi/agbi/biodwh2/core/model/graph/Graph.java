package de.unibi.agbi.biodwh2.core.model.graph;

import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
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
    private static final String UnescapedDoubleQuotes = "'";
    private static final String EscapedDoubleQuotes = "''";
    private static final String UnescapedComma = ",";
    private static final String EscapedComma = "\\,";
    private long nextNodeId = 1;
    private long nextEdgeId = 1;
    private final Connection connection;
    private PreparedStatement insertNodeStatement;
    private PreparedStatement insertEdgeStatement;
    private final Set<String> nodeColumns = new HashSet<>();
    private final Set<String> edgeColumns = new HashSet<>();
    private Map<Long, Node> nodeCache;
    private Map<String, List<Long>> nodeLabelIdMap;
    private long maxDumpedId = 0;
    private String[] indexColumnNames = new String[0];

    public Graph(final String databaseFilePath) throws ExporterException {
        deleteOldDatabaseFile(databaseFilePath);
        connection = openDatabaseConnection(databaseFilePath);
        prepareDatabaseTables(connection);
        prepareStatements();
        nodeCache = new HashMap<>();
        nodeLabelIdMap = new HashMap<>();
    }

    private void deleteOldDatabaseFile(final String databaseFilePath) throws ExporterException {
        try {
            Files.deleteIfExists(Paths.get(databaseFilePath));
        } catch (IOException e) {
            throw new ExporterException("Failed to remove old persistent database file '" + databaseFilePath + "'", e);
        }
    }

    private static Connection openDatabaseConnection(final String databaseFilePath) throws ExporterException {
        String url = "jdbc:sqlite:" + databaseFilePath;
        try {
            SQLiteConfig config = new SQLiteConfig();
            config.setTransactionMode(SQLiteConfig.TransactionMode.EXCLUSIVE);
            config.setJournalMode(SQLiteConfig.JournalMode.OFF);
            Connection connection = DriverManager.getConnection(url, config.toProperties());
            if (connection == null)
                throw new ExporterException(
                        "Failed to create persistent graph database file '" + databaseFilePath + "'");
            connection.setAutoCommit(false);
            return connection;
        } catch (SQLException e) {
            throw new ExporterException("Failed to create persistent graph database file '" + databaseFilePath + "'",
                                        e);
        }
    }

    private static void prepareDatabaseTables(Connection connection) throws ExporterException {
        String sql = "CREATE TABLE nodes (__id integer PRIMARY KEY, __labels TEXT NOT NULL);";
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new ExporterException("Failed to create persistent graph database tables", e);
        }
        sql = "CREATE INDEX node_labels ON nodes (__labels);";
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new ExporterException("Failed to create persistent graph database tables", e);
        }
        sql = "CREATE TABLE edges (__id integer PRIMARY KEY, __label TEXT NOT NULL, " +
              "__from_id integer NOT NULL, __to_id integer NOT NULL);";
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new ExporterException("Failed to create persistent graph database tables", e);
        }
    }

    private void prepareStatements() throws ExporterException {
        try {
            insertNodeStatement = connection.prepareStatement("INSERT INTO nodes (__id, __labels) VALUES (?, ?);");
            insertEdgeStatement = connection.prepareStatement(
                    "INSERT INTO edges (__id, __label, __from_id, __to_id) VALUES (?, ?, ?, ?);");
        } catch (SQLException e) {
            throw new ExporterException("Failed to prepare statements", e);
        }
    }

    public void setIndexColumnNames(String... names) {
        indexColumnNames = names;
    }

    void setNodeProperty(final Node node, final String key, final Object value) throws ExporterException {
        if (value == null || nodeCache.containsKey(node.getId()))
            return;
        ensureNodeColumnExists(key);
        String packedValue = StringUtils.replace(packValue(value), UnescapedDoubleQuotes, EscapedDoubleQuotes);
        executeSql("UPDATE nodes SET " + key + "='" + packedValue + "' WHERE __id=" + node.getId() + ";");
    }

    private void ensureNodeColumnExists(String columnName) throws ExporterException {
        if (nodeColumns.contains(columnName))
            return;
        nodeColumns.add(columnName);
        executeSql("ALTER TABLE nodes ADD COLUMN " + columnName + " TEXT;");
        if (ArrayUtils.contains(indexColumnNames, columnName))
            executeSql("CREATE INDEX nodes_" + columnName + "_index ON nodes(" + columnName + ");");
    }

    private void executeSql(final String sql) throws ExporterException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new ExporterException("Failed to persist graph at query '" + sql + "'", e);
        }
    }

    void setEdgeProperty(final Edge edge, final String key, final Object value) throws ExporterException {
        if (value == null)
            return;
        ensureEdgeColumnExists(key);
        String packedValue = StringUtils.replace(packValue(value), UnescapedDoubleQuotes, EscapedDoubleQuotes);
        executeSql("UPDATE edges SET " + key + "='" + packedValue + "' WHERE __id=" + edge.getId() + ";");
    }

    private void ensureEdgeColumnExists(String columnName) throws ExporterException {
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
                    joinedArray.append(UnescapedDoubleQuotes).append(
                            elementValue.replace(UnescapedDoubleQuotes, EscapedDoubleQuotes)
                                        .replace(UnescapedComma, EscapedComma)).append(UnescapedDoubleQuotes);
                else
                    joinedArray.append(elementValue);
            }
            return valueType.getName() + "[]|" + joinedArray;
        }
        return value.getClass().getName() + "|" + value;
    }

    Object unpackValue(String packedValue) throws ExporterException {
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
                String[] parts = value.split("(?<!\\\\),");
                Object[] array = (Object[]) java.lang.reflect.Array.newInstance(valueType, parts.length);
                if (valueType == String.class)
                    for (int i = 0; i < parts.length; i++)
                        array[i] = parts[i].substring(1, parts[i].length() - 1).replace(EscapedComma, UnescapedComma)
                                           .replace(EscapedDoubleQuotes, UnescapedDoubleQuotes);
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
            throw new ExporterException("Failed to persist graph", e);
        }
        return null;
    }

    public void synchronize(boolean force) throws ExporterException {
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
                    throw new ExporterException("Failed to persist graph", e);
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
                    String packedValue = StringUtils.replace(packValue(value), UnescapedDoubleQuotes,
                                                             EscapedDoubleQuotes);
                    sql.append(key).append("='").append(packedValue).append(UnescapedDoubleQuotes);
                }
                sql.append(" WHERE __id=").append(node.getId()).append(";");
                if (atLeastOneValueNotNull)
                    try (Statement statement = connection.createStatement()) {
                        statement.execute(sql.toString());
                    } catch (SQLException e) {
                        throw new ExporterException("Failed to persist graph at query '" + sql + "'", e);
                    }
            }
        }
        maxDumpedId = maxId;
        nodeCache.clear();
        nodeLabelIdMap.replaceAll((l, v) -> new ArrayList<>());
        tryCommit();
    }

    public Node addNode(String... labels) throws ExporterException {
        synchronize(false);
        Node node = new Node(this, nextNodeId, true, labels);
        nextNodeId++;
        nodeCache.put(node.getId(), node);
        for (String label : labels) {
            if (!nodeLabelIdMap.containsKey(label))
                nodeLabelIdMap.put(label, new ArrayList<>());
            nodeLabelIdMap.get(label).add(node.getId());
        }
        return node;
    }

    public Edge addEdge(Node from, Node to, String label) throws ExporterException {
        Edge edge = new Edge(this, nextEdgeId, from.getId(), to.getId(), label);
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

    public Edge addEdge(Node from, long toId, String label) throws ExporterException {
        Edge edge = new Edge(this, nextEdgeId, from.getId(), toId, label);
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

    public Iterable<Node> getNodes() throws ExporterException {
        try {
            ResultSet result = connection.createStatement().executeQuery("SELECT * FROM nodes");
            return () -> new Iterator<Node>() {
                @Override
                public boolean hasNext() {
                    try {
                        return result.next();
                    } catch (SQLException e) {
                        e.printStackTrace(); // TODO: exception
                    }
                    return false;
                }

                @Override
                public Node next() {
                    try {
                        return createNodeFromResultSet(result);
                    } catch (SQLException | ExporterException e) {
                        e.printStackTrace(); // TODO: exception
                    }
                    return null;
                }
            };
        } catch (SQLException e) {
            throw new ExporterException("Failed to load nodes from persisted graph", e);
        }
    }

    public Iterable<Edge> getEdges() throws ExporterException {
        try {
            ResultSet result = connection.createStatement().executeQuery("SELECT * FROM edges");
            return () -> new Iterator<Edge>() {
                @Override
                public boolean hasNext() {
                    try {
                        return result.next();
                    } catch (SQLException e) {
                        e.printStackTrace(); // TODO: exception
                    }
                    return false;
                }

                @Override
                public Edge next() {
                    try {
                        return createEdgeFromResultSet(result);
                    } catch (SQLException | ExporterException e) {
                        e.printStackTrace(); // TODO: exception
                    }
                    return null;
                }
            };
        } catch (SQLException e) {
            throw new ExporterException("Failed to load edges from persisted graph", e);
        }
    }

    public Node findNode(String labels, String propertyName, String value) {
        if (value == null)
            return null;
        if (nodeLabelIdMap.containsKey(labels)) {
            for (Long nodeId : nodeLabelIdMap.get(labels)) {
                Node n = nodeCache.get(nodeId);
                if (n.getProperty(propertyName).equals(value))
                    return n;
            }
        }
        String packedValue = StringUtils.replace(packValue(value), UnescapedDoubleQuotes, EscapedDoubleQuotes);
        String sql =
                "SELECT * FROM nodes WHERE __labels='" + labels + "' AND " + propertyName + "='" + packedValue + "';";
        try (ResultSet result = connection.createStatement().executeQuery(sql)) {
            if (!result.next()) {
                System.out.println(
                        "Failed to find node with label " + labels + " and " + propertyName + "=" + value + "\n" + sql);
                return null;
            }
            Node node = createNodeFromResultSet(result);
            nodeCache.put(node.getId(), node);
            for (String label : node.getLabels()) {
                if (!nodeLabelIdMap.containsKey(label))
                    nodeLabelIdMap.put(label, new ArrayList<>());
                nodeLabelIdMap.get(label).add(node.getId());
            }
            return node;
        } catch (SQLException | ExporterException e) {
            e.printStackTrace(); // TODO: exception
        }
        return null;
    }

    private Node createNodeFromResultSet(ResultSet result) throws SQLException, ExporterException {
        Node node = new Node(this, result.getLong("__id"), false, result.getString("__labels").split(";"));
        for (int i = 3; i <= result.getMetaData().getColumnCount(); i++) {
            String value = result.getString(i);
            if (value != null) {
                Object unpackedValue = unpackValue(
                        StringUtils.replace(value, EscapedDoubleQuotes, UnescapedDoubleQuotes));
                node.setProperty(result.getMetaData().getColumnName(i), unpackedValue, false, false);
            }
        }
        return node;
    }

    public Long findNodeId(String labels, String propertyName, String value) {
        if (value == null)
            return null;
        if (nodeLabelIdMap.containsKey(labels)) {
            for (Long nodeId : nodeLabelIdMap.get(labels)) {
                Node n = nodeCache.get(nodeId);
                if (n.getProperty(propertyName).equals(value))
                    return n.getId();
            }
        }
        String packedValue = StringUtils.replace(packValue(value), UnescapedDoubleQuotes, EscapedDoubleQuotes);
        String sql = "SELECT __id FROM nodes WHERE __labels='" + labels + "' AND " + propertyName + "='" + packedValue +
                     "';";
        Long id = null;
        try (ResultSet result = connection.createStatement().executeQuery(sql)) {
            if (!result.next()) {
                System.out.println(
                        "Failed to find node id with label " + labels + " and " + propertyName + "=" + value + "\n" +
                        sql);
            } else
                id = result.getLong(1);
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: exception
        }
        return id;
    }

    private Edge createEdgeFromResultSet(ResultSet result) throws SQLException, ExporterException {
        Edge edge = new Edge(this, result.getLong("__id"), result.getLong("__from_id"), result.getLong("__to_id"),
                             result.getString("__label"));
        for (int i = 5; i <= result.getMetaData().getColumnCount(); i++) {
            String value = result.getString(i);
            if (value != null) {
                Object unpackedValue = unpackValue(
                        StringUtils.replace(value, EscapedDoubleQuotes, UnescapedDoubleQuotes));
                edge.setProperty(result.getMetaData().getColumnName(i), unpackedValue, false);
            }
        }
        return edge;
    }

    private void tryCommit() throws ExporterException {
        try {
            connection.commit();
        } catch (SQLException e) {
            throw new ExporterException("Failed to persist graph", e);
        }
    }
}
