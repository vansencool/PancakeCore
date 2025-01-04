package dev.vansen.pancakecore.sql.table;

import dev.vansen.pancakecore.sql.SQLiteManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.NotThreadSafe;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@NotThreadSafe
public class DeleteBuilder {
    private String table;
    private List<WhereCondition> whereConditions;
    private final SQLiteManager sqlite;

    /**
     * Constructs a new DeleteBuilder with the given SQLiteManager instance.
     *
     * @param sqlite the SQLiteManager instance
     */
    public DeleteBuilder(@NotNull SQLiteManager sqlite) {
        this.sqlite = sqlite;
        this.whereConditions = new ArrayList<>();
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
     * Adds a WHERE condition to the query.
     *
     * @param column the column to filter on
     * @param value  the value to match
     * @return this DeleteBuilder instance
     */
    public @NotNull DeleteBuilder where(@NotNull String column, @Nullable Object value) {
        whereConditions.add(new WhereCondition(column, value));
        return this;
    }

    /**
     * Executes the DELETE query.
     *
     * @throws RuntimeException if the delete fails to execute
     */
    public void execute() {
        StringBuilder query = new StringBuilder("DELETE FROM " + table + " WHERE ");
        List<Object> values = new ArrayList<>();

        for (int i = 0; i < whereConditions.size(); i++) {
            WhereCondition condition = whereConditions.get(i);
            query.append(condition.column()).append(" = ?");
            values.add(condition.value());

            if (i < whereConditions.size() - 1) {
                query.append(" AND ");
            }
        }

        query.append(";");

        try (PreparedStatement statement = sqlite.connection().prepareStatement(query.toString())) {
            for (int i = 0; i < values.size(); i++) {
                statement.setObject(i + 1, values.get(i));
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete", e);
        }
    }

    /**
     * A single WHERE condition.
     */
    private record WhereCondition(String column, Object value) {
        private WhereCondition(@NotNull String column, @Nullable Object value) {
            this.column = column;
            this.value = value;
        }

        @Override
        public @NotNull String column() {
            return column;
        }

        @Override
        public @Nullable Object value() {
            return value;
        }
    }
}