package dev.vansen.pancakecore.sql.entry;

import dev.vansen.pancakecore.sql.SQLiteManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.NotThreadSafe;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
@NotThreadSafe
public final class EntryBuilder {
    private String tableName;
    private final Map<String, Object> values = new HashMap<>();
    private final SQLiteManager sqlite;

    /**
     * Constructs a new EntryBuilder instance with the given SQLiteManager.
     *
     * @param sqlite the SQLiteManager instance
     */
    public EntryBuilder(@NotNull SQLiteManager sqlite) {
        this.sqlite = sqlite;
    }

    /**
     * Sets the table to insert into.
     *
     * @param tableName the table to insert into
     * @return this EntryBuilder instance
     */
    public @NotNull EntryBuilder table(@NotNull String tableName) {
        this.tableName = tableName;
        return this;
    }

    /**
     * Sets a column value for the entry to be inserted.
     *
     * @param column the column to set
     * @param value  the value to set for the column
     * @return this EntryBuilder instance
     */
    public @NotNull EntryBuilder value(@NotNull String column, @Nullable Object value) {
        values.put(column, value);
        return this;
    }

    /**
     * Executes the INSERT query and inserts the entry into the database.
     *
     * @return the SQLiteManager instance
     * @throws RuntimeException if the query fails to execute
     */
    public SQLiteManager insert() {
        StringBuilder query = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
        values.keySet().forEach(column -> query.append(column).append(", "));
        query.delete(query.length() - 2, query.length()).append(") VALUES (");
        values.keySet().forEach(column -> query.append("?, "));
        query.delete(query.length() - 2, query.length()).append(");");
        try (PreparedStatement statement = sqlite.connection().prepareStatement(query.toString())) {
            int index = 1;
            for (Object value : values.values()) {
                statement.setObject(index++, value);
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert", e);
        }
        return sqlite;
    }

    /**
     * Executes the INSERT query and inserts the entry into the database, then runs the given Runnable.
     *
     * @param runnable the Runnable to run after inserting the entry
     * @return the SQLiteManager instance
     */
    public SQLiteManager insertThen(@NotNull Runnable runnable) {
        insert();
        runnable.run();
        return sqlite;
    }
}