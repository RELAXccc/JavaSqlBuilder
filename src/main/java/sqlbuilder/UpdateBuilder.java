package sqlbuilder;

import sqlbuilder.dialects.SqlDialect;
import sqlbuilder.exceptions.ValueCannotBeEmptyException;
import sqlbuilder.expressions.Condition;
import sqlbuilder.expressions.*;

import java.util.*;

/**
 * A builder for creating SQL UPDATE queries in a fluent manner.
 */
public class UpdateBuilder extends AbstractBuilder<UpdateBuilder> {
    private String table;
    private final Map<String, Object> values = new LinkedHashMap<>();
    private final List<Condition> conditions = new ArrayList<>();

    /**
     * Constructs an UpdateBuilder with a specific SQL dialect.
     *
     * @param dialect the SQL dialect to use
     */
    public UpdateBuilder(SqlDialect dialect) {
        super(dialect);
    }

    /**
     * Constructs an UpdateBuilder with a specific SQL dialect and schema.
     *
     * @param dialect the SQL dialect to use
     * @param schema  the database schema name
     */
    public UpdateBuilder(SqlDialect dialect, String schema) {
        super(dialect, schema);
    }

    /**
     * Specifies the table to update.
     *
     * @param table the table name
     * @return this builder instance
     * @throws ValueCannotBeEmptyException if table is null or blank
     */
    public UpdateBuilder table(String table) {
        validateNotEmpty(table, "table");
        this.table = addSchemaToTable(table);
        return self();
    }

    /**
     * Sets a column to a specific value.
     *
     * @param column the column name
     * @param value  the value to set
     * @return this builder instance
     * @throws ValueCannotBeEmptyException if column is null or blank
     */
    public UpdateBuilder set(String column, Object value) {
        validateNotEmpty(column, "column");
        values.put(column, value);
        return self();
    }

    /**
     * Sets multiple columns and their values for the update operation.
     *
     * @param values a map of column names to values
     * @return this builder instance
     */
    public UpdateBuilder set(Map<String, Object> values) {
        if (values != null) {
            this.values.putAll(values);
        }
        return self();
    }

    /**
     * Define conditions to limit the update operation.
     * When called multiple times the conditions are chained together using an AND.
     *
     * @param condition The condition
     * @return this builder instance
     */
    public UpdateBuilder where(Condition condition) {
        if (condition != null) {
            conditions.add(condition);
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
            throw new IllegalStateException("A table to update must be specified");
        }
        if (values.isEmpty()) {
            throw new IllegalStateException("At least one column value must be specified for update");
        }

        StringJoiner statement = new StringJoiner(" ")
                .add("UPDATE")
                .add(table)
                .add("SET");

        StringJoiner setClause = new StringJoiner(", ");
        values.keySet().forEach(column -> setClause.add(dialect.quote(column) + " = ?"));
        statement.add(setClause.toString());

        Condition whereCondition = null;
        if (!conditions.isEmpty()) {
            statement.add("WHERE");
            whereCondition = new CompositeCondition("AND", conditions);
            statement.add(whereCondition.toSql(dialect));
        }

        Query query = new Query(statement.toString());
        values.values().forEach(query::addParameter);
        if (whereCondition != null) {
            whereCondition.getParameters().forEach(query::addParameter);
        }
        return query;
    }
}
