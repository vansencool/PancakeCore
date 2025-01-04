package dev.vansen.pancakecore.sql.reader;

import dev.vansen.pancakecore.sql.SQLiteManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.NotThreadSafe;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@NotThreadSafe
public final class Reader {
    private String tableName;
    private String columnName;
    private String conditionColumn;
    private Object conditionValue;
    private final SQLiteManager sqlite;

    /**
     * Constructs a new Reader instance with the given SQLiteManager.
     *
     * @param sqlite the SQLiteManager instance
     */
    public Reader(@NotNull SQLiteManager sqlite) {
        this.sqlite = sqlite;
    }

    /**
     * Sets the table to read from.
     *
     * @param tableName the table to read from
     * @return this Reader instance
     */
    public @NotNull Reader table(@NotNull String tableName) {
        this.tableName = tableName;
        return this;
    }

    /**
     * Sets the column to read.
     *
     * @param columnName the column to read
     * @return this Reader instance
     */
    public @NotNull Reader column(@NotNull String columnName) {
        this.columnName = columnName;
        return this;
    }

    /**
     * Sets the WHERE clause condition and value.
     *
     * @param conditionColumn the WHERE clause condition
     * @param conditionValue  the value to match in the WHERE clause
     * @return this Reader instance
     */
    public @NotNull Reader where(@NotNull String conditionColumn, @Nullable Object conditionValue) {
        this.conditionColumn = conditionColumn;
        this.conditionValue = conditionValue;
        return this;
    }

    /**
     * Executes the SELECT query and returns the result.
     *
     * @return the result of the query, or null if no rows were found
     * @throws RuntimeException if the query fails to execute
     */
    public @Nullable Object fetch() {
        String query = "SELECT " + columnName + " FROM " + tableName + " WHERE " + conditionColumn + " = ?;";
        try (PreparedStatement statement = sqlite.connection().prepareStatement(query)) {
            statement.setObject(1, conditionValue);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() ? resultSet.getObject(columnName) : null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to read", e);
        }
    }

    /**
     * Executes the SELECT query and returns the result as a List of objects.
     *
     * @return a List of objects representing the result of the query, or an empty List if no rows were found
     * @throws RuntimeException if the query fails to execute
     */
    public @NotNull List<Object> fetchList() {
        String query = "SELECT " + columnName + " FROM " + tableName + " WHERE " + conditionColumn + " = ?;";
        try (PreparedStatement statement = sqlite.connection().prepareStatement(query)) {
            statement.setObject(1, conditionValue);
            ResultSet resultSet = statement.executeQuery();
            List<Object> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(resultSet.getObject(columnName));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to read", e);
        }
    }

    /**
     * Executes the SELECT query and returns the result, cast to the specified type.
     *
     * @param type the type to cast the result to
     * @return the result of the query, cast to the specified type, or null if no rows were found
     */
    @SuppressWarnings("unchecked")
    public @Nullable <T> T fetch(@NotNull Class<T> type) {
        return (T) fetch();
    }

    /**
     * Executes the SELECT query and returns the result, cast to the specified type.
     *
     * @param type the type to cast the result to
     * @return a List of objects representing the result of the query, cast to the specified type, or an empty List if no rows were found
     */
    @SuppressWarnings("unchecked")
    public @NotNull <T> List<T> fetchList(@NotNull Class<T> type) {
        List<Object> objects = fetchList();
        List<T> result = new ArrayList<>(objects.size());
        for (Object object : objects) {
            result.add((T) object);
        }
        return result;
    }
}