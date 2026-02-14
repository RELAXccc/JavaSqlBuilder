package sqlbuilder;

import sqlbuilder.dialects.SqlDialect;
import sqlbuilder.exceptions.ValueCannotBeEmptyException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * A builder for creating SQL INSERT queries in a fluent manner.
 */
public class InsertBuilder extends AbstractBuilder<InsertBuilder> {
    private String table;
    private final Map<String, Object> values = new LinkedHashMap<>();

    /**
     * Constructs an InsertBuilder with a specific SQL dialect.
     *
     * @param dialect the SQL dialect to use
     */
    public InsertBuilder(SqlDialect dialect) {
        super(dialect);
    }

    /**
     * Constructs an InsertBuilder with a specific SQL dialect and schema.
     *
     * @param dialect the SQL dialect to use
     * @param schema  the database schema name
     */
    public InsertBuilder(SqlDialect dialect, String schema) {
        super(dialect, schema);
    }

    /**
     * Specifies the table to insert into.
     *
     * @param table the table name
     * @return this builder instance
     * @throws ValueCannotBeEmptyException if table is null or blank
     */
    public InsertBuilder into(String table) {
        validateNotEmpty(table, "table");
        this.table = addSchemaToTable(table);
        return self();
    }

    /**
     * Sets a column and its value for the insert operation.
     *
     * @param column the column name
     * @param value  the value to insert
     * @return this builder instance
     * @throws ValueCannotBeEmptyException if column is null or blank
     */
    public InsertBuilder value(String column, Object value) {
        validateNotEmpty(column, "column");
        values.put(column, value);
        return self();
    }

    /**
     * Sets multiple columns and their values for the insert operation.
     *
     * @param values a map of column names to values
     * @return this builder instance
     */
    public InsertBuilder values(Map<String, Object> values) {
        if (values != null) {
            this.values.putAll(values);
        }
        return self();
    }

    /**
     * Builds the SQL query.
     *
     * @return the constructed Query object
     * @throws IllegalStateException if no table or values were specified
     */
    @Override
    public Query build() {
        if (table == null || table.isBlank()) {
            throw new IllegalStateException("A table to insert into must be specified");
        }
        if (values.isEmpty()) {
            throw new IllegalStateException("At least one column value must be specified for insert");
        }

        String columns = values.keySet().stream()
                .map(dialect::quote)
                .collect(Collectors.joining(", "));
        String placeHolders = values.keySet().stream()
                .map(v -> "?")
                .collect(Collectors.joining(", "));

        String statement = new StringJoiner(" ")
                .add("INSERT INTO")
                .add(table)
                .add("(" + columns + ")")
                .add("VALUES")
                .add("(" + placeHolders + ")")
                .toString();

        Query query = new Query(statement);
        values.values().forEach(query::addParameter);
        return query;
    }
}
