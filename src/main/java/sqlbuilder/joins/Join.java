package sqlbuilder.joins;

import sqlbuilder.dialects.SqlDialect;
import sqlbuilder.expressions.Condition;
import java.util.List;

/**
 * Represents a SQL JOIN operation.
 */
public abstract class Join {
    protected final String table;
    protected final String alias;
    protected final Condition condition;

    protected Join(String table, String alias, Condition condition) {
        this.table = table;
        this.alias = alias;
        this.condition = condition;
    }

    /**
     * Returns the SQL join type.
     *
     * @return the join type string
     */
    protected abstract String getJoinType();

    /**
     * Generates the SQL for the join.
     *
     * @param dialect the SQL dialect
     * @param schema  the database schema
     * @return the SQL string
     */
    public String toSql(SqlDialect dialect, String schema) {
        StringBuilder sql = new StringBuilder(getJoinType()).append(" ");
        if (schema != null && !schema.isBlank()) {
            sql.append(schema).append(".");
        }
        sql.append(table);
        if (alias != null && !alias.isBlank()) {
            sql.append(" ").append(alias);
        }
        sql.append(" ON ").append(condition.toSql(dialect));
        return sql.toString();
    }

    /**
     * Returns the parameters for the join condition.
     *
     * @return the list of parameters
     */
    public List<Object> getParameters() {
        return condition.getParameters();
    }
}
