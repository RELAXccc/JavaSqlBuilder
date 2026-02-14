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

    /**
     * Constructs a Join.
     *
     * @param table     the table to join
     * @param alias     the table alias
     * @param condition the join condition
     */
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
        sql.append(" ON ").append(condition.toSql());
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

    /**
     * Inner Join implementation.
     */
    public static class InnerJoin extends Join {
        public InnerJoin(String table, String alias, Condition condition) {
            super(table, alias, condition);
        }

        @Override
        protected String getJoinType() {
            return "INNER JOIN";
        }
    }

    /**
     * Left Join implementation.
     */
    public static class LeftJoin extends Join {
        public LeftJoin(String table, String alias, Condition condition) {
            super(table, alias, condition);
        }

        @Override
        protected String getJoinType() {
            return "LEFT JOIN";
        }
    }

    /**
     * Right Join implementation.
     */
    public static class RightJoin extends Join {
        public RightJoin(String table, String alias, Condition condition) {
            super(table, alias, condition);
        }

        @Override
        protected String getJoinType() {
            return "RIGHT JOIN";
        }
    }

    /**
     * Full Join implementation.
     */
    public static class FullJoin extends Join {
        public FullJoin(String table, String alias, Condition condition) {
            super(table, alias, condition);
        }

        @Override
        protected String getJoinType() {
            return "FULL OUTER JOIN";
        }
    }
}
