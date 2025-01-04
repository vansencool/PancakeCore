package dev.vansen.pancakecore.sql.entry;

import dev.vansen.pancakecore.sql.SQLiteManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.NotThreadSafe;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@SuppressWarnings("unused")
@NotThreadSafe
public class UpdateBuilder {
    private String tableName;
    private final ConcurrentMap<String, Object> updates = new ConcurrentHashMap<>();
    private String conditionColumn;
    private Object conditionValue;
    private final SQLiteManager sqlite;

    /**
     * Constructs a new UpdateBuilder instance with the given SQLiteManager.
     *
     * @param sqlite the SQLiteManager instance
     */
    public UpdateBuilder(@NotNull SQLiteManager sqlite) {
        this.sqlite = sqlite;
    }

    /**
     * Sets the table to update.
     *
     * @param tableName the table to update
     * @return this UpdateBuilder instance
     */
    public @NotNull UpdateBuilder table(@NotNull String tableName) {
        this.tableName = tableName;
        return this;
    }

    /**
     * Sets a column to update with the given value.
     *
     * @param column the column to update
     * @param value  the new value for the column
     * @return this UpdateBuilder instance
     */
    public @NotNull UpdateBuilder set(@NotNull String column, @Nullable Object value) {
        updates.put(column, value);
        return this;
    }

    /**
     * Sets the WHERE clause condition and value.
     *
     * @param conditionColumn the WHERE clause condition
     * @param conditionValue  the value to match in the WHERE clause
     * @return this UpdateBuilder instance
     */
    public @NotNull UpdateBuilder where(@NotNull String conditionColumn, @Nullable Object conditionValue) {
        this.conditionColumn = conditionColumn;
        this.conditionValue = conditionValue;
        return this;
    }

    /**
     * Executes the UPDATE query.
     *
     * @return the SQLiteManager instance
     * @throws RuntimeException if the query fails to execute
     */
    public SQLiteManager execute() {
        StringBuilder query = new StringBuilder("UPDATE ").append(tableName).append(" SET ");
        updates.forEach((column, value) -> query.append(column).append(" = ?, "));
        query.delete(query.length() - 2, query.length());
        query.append(" WHERE ").append(conditionColumn).append(" = ?;");
        try (PreparedStatement statement = sqlite.connection().prepareStatement(query.toString())) {
            int index = 1;
            for (Object value : updates.values()) {
                statement.setObject(index++, value);
            }
            statement.setObject(index, conditionValue);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update", e);
        }
        return sqlite;
    }
}
