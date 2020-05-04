package de.unibi.agbi.biodwh2.core.io;

import org.apache.commons.lang3.StringUtils;
import org.sqlite.SQLiteConfig;

import java.sql.*;
import java.util.*;
import java.util.function.Function;

public final class SqliteDatabase {
    private Connection connection;
    private final Map<String, Set<String>> tableColumnsMap = new HashMap<>();
    private final Set<String> indexNames = new HashSet<>();

    public SqliteDatabase(final String databaseFilePath) throws SQLException {
        connection = openDatabaseConnection(databaseFilePath);
        updateCaches();
    }

    private static Connection openDatabaseConnection(final String databaseFilePath) throws SQLException {
        String url = "jdbc:sqlite:" + databaseFilePath;
        SQLiteConfig config = new SQLiteConfig();
        config.setTransactionMode(SQLiteConfig.TransactionMode.EXCLUSIVE);
        config.setJournalMode(SQLiteConfig.JournalMode.OFF);
        Connection connection = DriverManager.getConnection(url, config.toProperties());
        connection.setAutoCommit(false);
        return connection;
    }

    public void updateCaches() {
        indexNames.clear();
        try (ResultSet result = executeQuery("SELECT name, type FROM sqlite_master WHERE type='table'")) {
            while (result.next()) {
                final String name = result.getString("name");
                final String type = result.getString("type");
                if (type.equals("table")) {
                    if (!tableColumnsMap.containsKey(name))
                        tableColumnsMap.put(name, new HashSet<>());
                    getColumnNamesForTable(name, tableColumnsMap.get(name));
                } else if (type.equals("index"))
                    indexNames.add(name);
            }
        } catch (SQLException ignored) {
        }
    }

    public void getColumnNamesForTable(final String tableName, final Set<String> columnNames) throws SQLException {
        String sql;
        if (tableName.contains(".")) {
            String[] parts = StringUtils.split(tableName, ".");
            sql = "PRAGMA " + parts[0] + ".table_info(" + parts[1] + ");";
        } else
            sql = "PRAGMA table_info(" + tableName + ");";
        ResultSet result = executeQuery(sql);
        while (result.next())
            columnNames.add(result.getString("name"));
        result.close();
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean execute(final String sql) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            return statement.execute(sql);
        }
    }

    public ResultSet executeQuery(final String sql) throws SQLException {
        return connection.createStatement().executeQuery(sql);
    }

    public PreparedStatement prepareStatement(final String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    public void addColumnIfNotExists(final String tableName, final String columnName,
                                     final String type) throws SQLException {
        addColumnIfNotExists(tableName, columnName, type, false);
    }

    public void addColumnIfNotExists(final String tableName, final String columnName, final String type,
                                     final boolean addIndex) throws SQLException {
        if (!tableColumnsMap.containsKey(tableName))
            tableColumnsMap.put(tableName, new HashSet<>());
        if (tableColumnsMap.get(tableName).contains(columnName))
            return;
        tableColumnsMap.get(tableName).add(columnName);
        execute("ALTER TABLE " + tableName + " ADD COLUMN \"" + columnName + "\" " + type);
        if (addIndex)
            addIndexIfNotExists(tableName, columnName);
    }

    public void addIndexIfNotExists(final String tableName, final String... columnNames) throws SQLException {
        String indexName = tableName + "_" + String.join("_", columnNames) + "_index";
        if (!indexNames.contains(indexName)) {
            indexNames.add(indexName);
            execute("CREATE INDEX " + indexName + " ON " + tableName + "(" + String.join(", ", columnNames) + ")");
        }
    }

    public long getMaxColumnValue(final String tableName, final String columnName) throws SQLException {
        try (ResultSet result = executeQuery("SELECT MAX(" + columnName + ") as result FROM " + tableName)) {
            long maxValue = result.getLong("result");
            result.close();
            return maxValue;
        }
    }

    public <T> Iterable<T> iterateTable(final String tableName,
                                        final Function<ResultSet, T> mapCallback) throws SQLException {
        ResultSet result;
        result = executeQuery("SELECT * FROM " + tableName);
        return () -> new Iterator<T>() {
            @Override
            public boolean hasNext() {
                try {
                    if (!result.isAfterLast())
                        return true;
                    result.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return false;
            }

            @Override
            public T next() {
                try {
                    result.next();
                    return mapCallback.apply(result);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public void setAutoCommit(final boolean autoCommit) throws SQLException {
        connection.setAutoCommit(autoCommit);
    }

    public void commit() throws SQLException {
        connection.commit();
    }

    public void dispose() {
        try {
            connection.commit();
        } catch (SQLException ignored) {
        }
        try {
            connection.close();
        } catch (SQLException ignored) {
        }
        connection = null;
    }
}
