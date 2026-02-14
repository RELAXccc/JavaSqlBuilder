package sqlbuilder;

import sqlbuilder.dialects.SqlDialect;
import sqlbuilder.exceptions.ValueCannotBeEmptyException;
import sqlbuilder.expressions.Condition;
import sqlbuilder.expressions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * A builder for creating SQL DELETE queries in a fluent manner.
 */
public class DeleteBuilder extends AbstractBuilder<DeleteBuilder> {
    private String table;
    private final List<Condition> conditions = new ArrayList<>();

    /**
     * Constructs a DeleteBuilder with a specific SQL dialect.
     *
     * @param dialect the SQL dialect to use
     */
    public DeleteBuilder(SqlDialect dialect) {
        super(dialect);
    }

    /**
     * Constructs a DeleteBuilder with a specific SQL dialect and schema.
     *
     * @param dialect the SQL dialect to use
     * @param schema  the database schema name
     */
    public DeleteBuilder(SqlDialect dialect, String schema) {
        super(dialect, schema);
    }

    /**
     * Specifies the table to delete from.
     *
     * @param table the table name
     * @return this builder instance
     * @throws ValueCannotBeEmptyException if table is null or blank
     */
    public DeleteBuilder from(String table) {
        validateNotEmpty(table, "table");
        this.table = addSchemaToTable(table);
        return self();
    }

    /**
     * Define conditions to limit the delete operation.
     * When called multiple times the conditions are chained together using an AND.
     *
     * @param condition The condition
     * @return this builder instance
     */
    public DeleteBuilder where(Condition condition) {
        if (condition != null) {
            conditions.add(condition);
        }
        return self();
    }

    /**
     * Builds the SQL query.
     *
     * @return the constructed Query object
     * @throws IllegalStateException if no table was specified
     */
    @Override
    public Query build() {
        if (table == null || table.isBlank()) {
            throw new IllegalStateException("A table to delete from must be specified");
        }

        StringJoiner statement = new StringJoiner(" ")
                .add("DELETE FROM")
                .add(table);

        Condition whereCondition = null;
        if (!conditions.isEmpty()) {
            statement.add("WHERE");
            whereCondition = new CompositeCondition("AND", conditions);
            statement.add(whereCondition.toSql(dialect));
        }

        Query query = new Query(statement.toString());
        if (whereCondition != null) {
            whereCondition.getParameters().forEach(query::addParameter);
        }
        return query;
    }
}
