package dev.vansen.pancakecore.sql.setup;

import dev.vansen.pancakecore.sql.SQLiteManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@SuppressWarnings("unused")
public class SQLiteSetup {
    private File databaseFile;

    /**
     * Creates a new SQLiteSetup instance.
     *
     * @return a new SQLiteSetup instance
     */
    public static SQLiteSetup setup() {
        return new SQLiteSetup();
    }

    /**
     * Creates a new SQLiteManager instance with the given database file.
     *
     * @param file the database file
     * @return a new SQLiteManager instance
     */
    public static SQLiteManager setup(@NotNull File file) {
        return new SQLiteSetup()
                .file(file)
                .start();
    }

    /**
     * Sets the database file for this SQLiteSetup instance.
     *
     * @param file the database file
     * @return this SQLiteSetup instance
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public SQLiteSetup file(@NotNull File file) {
        this.databaseFile = file;
        File parentDir = file.getParentFile();
        if (parentDir != null) {
            parentDir.mkdirs();
        }

        try {
            file.createNewFile();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create database file", e);
        }
        return this;
    }

    /**
     * Starts the SQLite connection and returns a new SQLiteManager instance.
     *
     * @return a new SQLiteManager instance
     * @throws RuntimeException if the connection fails
     */
    public SQLiteManager start() {
        Connection connection;
        try {
            if (!Files.isReadable(databaseFile.toPath()))
                throw new RuntimeException("File is not readable or does not exist (unlikely to happen), path: " + databaseFile.getAbsolutePath());
            connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile.getAbsolutePath());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to SQLite", e);
        }
        return new SQLiteManager(connection);
    }
}
