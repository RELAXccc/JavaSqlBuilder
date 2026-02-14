package sqlbuilder;

import sqlbuilder.dialects.SqlDialect;
import sqlbuilder.exceptions.ValueCannotBeEmptyException;

/**
 * Base class for SQL builders, providing common fields and utility methods.
 *
 * @param <T> the type of the builder for fluent chaining
 */
public abstract class AbstractBuilder<T extends AbstractBuilder<T>> {
    protected final SqlDialect dialect;
    protected final String schema;

    protected AbstractBuilder(SqlDialect dialect) {
        this(dialect, null);
    }

    protected AbstractBuilder(SqlDialect dialect, String schema) {
        this.dialect = dialect;
        this.schema = schema;
    }

    /**
     * Adds the schema prefix to a table name if a schema is defined.
     *
     * @param table the table name
     * @return the table name with schema prefix if applicable
     */
    protected String addSchemaToTable(String table) {
        if (schema == null || schema.isBlank()) {
            return table;
        }
        return schema + "." + table;
    }

    /**
     * Validates that a value is not null or blank.
     *
     * @param value the value to check
     * @param name  the name of the value (for the exception message)
     * @throws ValueCannotBeEmptyException if the value is null or blank
     */
    protected void validateNotEmpty(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new ValueCannotBeEmptyException(name);
        }
    }

    /**
     * Builds the SQL query.
     *
     * @return the constructed Query object
     */
    public abstract Query build();

    /**
     * Returns this builder instance cast to the specific builder type.
     *
     * @return this instance
     */
    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }
}
