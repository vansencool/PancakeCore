package dev.vansen.pancakecore.sql.table;

import dev.vansen.pancakecore.sql.SQLiteManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.NotThreadSafe;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@SuppressWarnings("unused")
@NotThreadSafe
public class DeleteBuilder {
    private String table;
    private String where;
    private Object value;
    private final SQLiteManager sqlite;

    /**
     * Constructs a new DeleteBuilder with the given SQLiteManager instance.
     *
     * @param sqlite the SQLiteManager instance
     */
    public DeleteBuilder(@NotNull SQLiteManager sqlite) {
        this.sqlite = sqlite;
    }

    /**
     * Sets the table to delete from.
     *
     * @param table the table to delete from
     * @return this DeleteBuilder instance
     */
    public @NotNull DeleteBuilder table(@NotNull String table) {
        this.table = table;
        return this;
    }

    /**
     * Sets the WHERE clause condition and value.
     *
     * @param where the WHERE clause condition
     * @param value the value to match in the WHERE clause
     * @return this DeleteBuilder instance
     */
    public @NotNull DeleteBuilder where(@NotNull String where, @Nullable Object value) {
        this.where = where;
        this.value = value;
        return this;
    }

    /**
     * Deletes the specified row from the database.
     *
     * @throws RuntimeException if the delete fails to execute
     */
    public void execute() {
        String query = "DELETE FROM " + table + " WHERE " + where + " = ?;";
        try (PreparedStatement statement = sqlite.connection().prepareStatement(query)) {
            statement.setObject(1, value);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete", e);
        }
    }
}
