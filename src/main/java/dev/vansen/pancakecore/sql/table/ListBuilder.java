package dev.vansen.pancakecore.sql.table;

import dev.vansen.pancakecore.sql.SQLiteManager;
import dev.vansen.pancakecore.sql.field.FieldType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.NotThreadSafe;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@SuppressWarnings("all")
@NotThreadSafe
public final class ListBuilder {
    private String tableName;
    private final ConcurrentMap<String, String> fields = new ConcurrentHashMap<>();
    private final SQLiteManager sqlite;

    /**
     * Constructs a new ListBuilder instance with the given SQLiteManager.
     *
     * @param connection the SQLiteManager instance
     */
    public ListBuilder(@NotNull SQLiteManager connection) {
        this.sqlite = connection;
    }

    /**
     * Sets the table to create.
     *
     * @param tableName the table to create
     * @return this ListBuilder instance
     */
    public @NotNull ListBuilder table(@NotNull String tableName) {
        this.tableName = tableName;
        return this;
    }

    /**
     * Adds a field to the table with the given name and type.
     *
     * @param name the name of the field
     * @param type the type of the field
     * @return this ListBuilder instance
     */
    public @NotNull ListBuilder field(@NotNull String name, @NotNull String type) {
        fields.put(name, type);
        return this;
    }

    /**
     * Adds a field to the table with the given name and type.
     *
     * @param name the name of the field
     * @param type the type of the field
     * @return this ListBuilder instance
     */
    public @NotNull ListBuilder field(@NotNull String name, @NotNull FieldType type) {
        fields.put(name, type.sqlType());
        return this;
    }

    /**
     * Creates the table in the database.
     *
     * @return the SQLiteManager instance
     * @throws RuntimeException if the table creation fails
     */
    public SQLiteManager create() {
        StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (");
        fields.forEach((columnName, columnType) -> query.append("\"").append(columnName).append("\" ").append(columnType).append(", "));
        query.delete(query.length() - 2, query.length()).append(");");
        try (Statement statement = sqlite.connection().createStatement()) {
            statement.executeUpdate(query.toString());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create table", e);
        }
        return sqlite;
    }
}