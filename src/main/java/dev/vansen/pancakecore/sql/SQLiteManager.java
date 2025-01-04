package dev.vansen.pancakecore.sql;

import dev.vansen.pancakecore.sql.entry.EntryBuilder;
import dev.vansen.pancakecore.sql.entry.UpdateBuilder;
import dev.vansen.pancakecore.sql.reader.Reader;
import dev.vansen.pancakecore.sql.setup.SQLiteSetup;
import dev.vansen.pancakecore.sql.table.DeleteBuilder;
import dev.vansen.pancakecore.sql.table.ListBuilder;
import org.jetbrains.annotations.NotNull;

import java.sql.*;

@SuppressWarnings("unused")
public record SQLiteManager(@NotNull Connection connection) {

    /**
     * Creates a new SQLiteSetup instance.
     *
     * @return a new SQLiteSetup instance
     */
    public static @NotNull SQLiteSetup setup() {
        return new SQLiteSetup();
    }

    /**
     * Creates a new ListBuilder instance for creating tables.
     *
     * @return a new ListBuilder instance
     */
    public @NotNull ListBuilder list() {
        return new ListBuilder(this);
    }

    /**
     * Creates a new ListBuilder instance for creating a table with the given name.
     *
     * @param tableName the table name
     * @return a new ListBuilder instance
     */
    public @NotNull ListBuilder list(@NotNull String tableName) {
        return new ListBuilder(this)
                .table(tableName);
    }

    /**
     * Creates a new EntryBuilder instance for inserting entries.
     *
     * @return a new EntryBuilder instance
     */
    public @NotNull EntryBuilder add() {
        return new EntryBuilder(this);
    }

    /**
     * Creates a new EntryBuilder instance for inserting an entry into the table with the given name.
     *
     * @param tableName the table name
     * @return a new EntryBuilder instance
     */
    public @NotNull EntryBuilder add(@NotNull String tableName) {
        return new EntryBuilder(this)
                .table(tableName);
    }

    /**
     * Creates a new Reader instance for reading entries.
     *
     * @return a new Reader instance
     */
    public @NotNull Reader read() {
        return new Reader(this);
    }

    /**
     * Creates a new UpdateBuilder instance for updating entries.
     *
     * @return a new UpdateBuilder instance
     */
    public @NotNull UpdateBuilder update() {
        return new UpdateBuilder(this);
    }

    /**
     * Creates a new UpdateBuilder instance for updating entries in the table with the given name.
     *
     * @param tableName the table name
     * @return a new UpdateBuilder instance
     */
    public @NotNull UpdateBuilder update(@NotNull String tableName) {
        return new UpdateBuilder(this)
                .table(tableName);
    }

    /**
     * Creates a new DeleteBuilder instance for deleting entries.
     *
     * @return a new DeleteBuilder instance
     */
    public @NotNull DeleteBuilder delete() {
        return new DeleteBuilder(this);
    }

    /**
     * Creates a new DeleteBuilder instance for deleting entries from the table with the given name.
     *
     * @param tableName the table name
     * @return a new DeleteBuilder instance
     */
    public @NotNull DeleteBuilder delete(@NotNull String tableName) {
        return new DeleteBuilder(this)
                .table(tableName);
    }

    /**
     * Creates a new Reader instance for reading entries from the table with the given name.
     *
     * @param tableName the table name
     * @return a new Reader instance
     */
    public @NotNull Reader read(@NotNull String tableName) {
        return new Reader(this)
                .table(tableName);
    }

    /**
     * Clears the table with the given name.
     *
     * @param tableName the table name
     * @throws RuntimeException if the clear operation fails
     */
    public void clear(@NotNull String tableName) {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM " + tableName + ";");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clear", e);
        }
    }

    /**
     * Drops the table with the given name.
     *
     * @param tableName the table name
     * @throws RuntimeException if the drop operation fails
     */
    public void drop(@NotNull String tableName) {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DROP TABLE IF EXISTS " + tableName + ";");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to drop", e);
        }
    }

    /**
     * Checks if the table with the given name exists.
     *
     * @param tableName the table name
     * @return true if the table exists, false otherwise
     * @throws RuntimeException if the existence check fails
     */
    public boolean exists(@NotNull String tableName) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT name FROM sqlite_master WHERE type='table' AND name=?;")) {
            statement.setString(1, tableName);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check existence", e);
        }
    }

    /**
     * Closes the SQLite connection.
     *
     * @throws RuntimeException if the close operation fails
     */
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to close connection", e);
        }
    }
}