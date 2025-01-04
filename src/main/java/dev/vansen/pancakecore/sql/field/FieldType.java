package dev.vansen.pancakecore.sql.field;

import org.jetbrains.annotations.NotNull;

/**
 * Enumeration of field types.
 */
@SuppressWarnings("unused")
public enum FieldType {

    /**
     * Text field.
     */
    TEXT("TEXT"),

    /**
     * Text field, same as TEXT.
     */
    STRING("TEXT"),

    /**
     * Number field.
     */
    INTEGER("INTEGER"),

    /**
     * Decimal field
     */
    DOUBLE("REAL"),

    /**
     * Boolean field (stored as INTEGER with 0 for false, 1 for true).
     */
    BOOLEAN("INTEGER"),

    /**
     * Blob field.
     */
    BLOB("BLOB"),

    /**
     * Null field (used for explicitly setting a value to NULL).
     */
    NULL("NULL");

    private final String sqlType;

    FieldType(@NotNull String sqlType) {
        this.sqlType = sqlType;
    }

    /**
     * Returns the SQL type of the field.
     *
     * @return the SQL type of the field
     */
    public String sqlType() {
        return sqlType;
    }
}