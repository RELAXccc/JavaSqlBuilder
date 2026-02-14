package sqlbuilder;

import sqlbuilder.dialects.SqlDialect;
import sqlbuilder.exceptions.ValueCannotBeEmptyException;
import sqlbuilder.expressions.Condition;
import sqlbuilder.expressions.*;
import sqlbuilder.joins.*;

import java.util.*;

/**
 * A builder for creating SQL SELECT queries in a fluent manner.
 */
public class SelectBuilder extends AbstractBuilder<SelectBuilder> {
    private static final String ERROR_MESSAGE_MULTIPLE_ORDER_DIRECTION_CALLS = "order direction can only be set once. Multiple calls of desc() or asc() are not allowed!";

    private final List<String> columns = new ArrayList<>();
    private final List<String> tables = new ArrayList<>();
    private final Set<String> tablesContext = new HashSet<>();

    private final List<Join> joins = new ArrayList<>();
    private final List<Condition> conditions = new ArrayList<>();
    private final List<String> groupColumns = new ArrayList<>();
    private Condition havingCondition = null;
    private final List<String> orderColumns = new ArrayList<>();
    private String orderDirection = null;
    private boolean distinct = false;

    private int limit = -1;
    private int offset = 0;

    /**
     * Constructs a SelectBuilder with a specific SQL dialect.
     *
     * @param dialect the SQL dialect to use
     */
    public SelectBuilder(SqlDialect dialect) {
        super(dialect);
    }

    /**
     * Constructs a SelectBuilder with a specific SQL dialect and schema.
     *
     * @param dialect the SQL dialect to use
     * @param schema  the database schema name
     */
    public SelectBuilder(SqlDialect dialect, String schema) {
        super(dialect, schema);
    }

    /**
     * Specifies the columns to select.
     *
     * @param columns the columns to select
     * @return this builder instance
     * @throws ValueCannotBeEmptyException if columns are empty
     */
    public SelectBuilder select(String... columns) {
        if (columns.length == 0) {
            throw new ValueCannotBeEmptyException("columns");
        }

        Arrays.stream(columns)
                .map(dialect::quote)
                .forEach(this.columns::add);
        return self();
    }

    /**
     * Specifies the columns to select with a DISTINCT modifier.
     *
     * @param columns the columns to select
     * @return this builder instance
     */
    public SelectBuilder selectDistinct(String... columns) {
        select(columns);
        distinct = true;
        return self();
    }

    /**
     * Adds a DISTINCT modifier to the current selection.
     *
     * @return this builder instance
     */
    public SelectBuilder distinct() {
        distinct = true;
        return self();
    }

    /**
     * Specifies the tables to select from.
     *
     * @param tables the tables to select from
     * @return this builder instance
     * @throws ValueCannotBeEmptyException if tables are empty
     */
    public SelectBuilder from(String... tables) {
        if (tables.length == 0) {
            throw new ValueCannotBeEmptyException("tables");
        }

        List<String> tableList = Arrays.asList(tables);
        this.tables.addAll(tableList.stream().map(this::addSchemaToTable).toList());
        this.tablesContext.addAll(tableList);
        return self();
    }

    /**
     * Specifies a table to select from.
     *
     * @param table the table name
     * @return this builder instance
     * @throws ValueCannotBeEmptyException if table is null or blank
     */
    public SelectBuilder from(String table) {
        validateNotEmpty(table, "table");

        this.tables.add(addSchemaToTable(table));
        this.tablesContext.add(table);
        return self();
    }

    /**
     * Specifies a table with an alias to select from.
     *
     * @param table the table name
     * @param alias the table alias
     * @return this builder instance
     */
    public SelectBuilder from(String table, String alias) {
        if (alias == null || alias.isBlank()) {
            return from(table);
        }

        this.tables.add(addSchemaToTable(table) + " " + alias);
        this.tablesContext.add(table);
        return self();
    }

    /**
     * Adds an INNER JOIN to the query.
     *
     * @param table         the table to join
     * @param joinCondition the join condition
     * @return this builder instance
     */
    public SelectBuilder join(String table, Condition joinCondition) {
        return join(table, null, joinCondition);
    }

    /**
     * Adds an INNER JOIN to the query with an alias.
     *
     * @param table         the table to join
     * @param alias         the table alias
     * @param joinCondition the join condition
     * @return this builder instance
     */
    public SelectBuilder join(String table, String alias, Condition joinCondition) {
        return innerJoin(table, alias, joinCondition);
    }

    /**
     * Adds an INNER JOIN to the query.
     *
     * @param table         the table to join
     * @param joinCondition the join condition
     * @return this builder instance
     */
    public SelectBuilder innerJoin(String table, Condition joinCondition) {
        return innerJoin(table, null, joinCondition);
    }

    /**
     * Adds an INNER JOIN to the query with an alias.
     *
     * @param table         the table to join
     * @param alias         the table alias
     * @param joinCondition the join condition
     * @return this builder instance
     */
    public SelectBuilder innerJoin(String table, String alias, Condition joinCondition) {
        tablesContext.add(table);
        joins.add(new InnerJoin(table, alias, joinCondition));
        return self();
    }

    /**
     * Adds a LEFT JOIN to the query.
     *
     * @param table         the table to join
     * @param joinCondition the join condition
     * @return this builder instance
     */
    public SelectBuilder leftJoin(String table, Condition joinCondition) {
        return leftJoin(table, null, joinCondition);
    }

    /**
     * Adds a LEFT JOIN to the query with an alias.
     *
     * @param table         the table to join
     * @param alias         the table alias
     * @param joinCondition the join condition
     * @return this builder instance
     */
    public SelectBuilder leftJoin(String table, String alias, Condition joinCondition) {
        tablesContext.add(table);
        joins.add(new LeftJoin(table, alias, joinCondition));
        return self();
    }

    /**
     * Adds a RIGHT JOIN to the query.
     *
     * @param table         the table to join
     * @param joinCondition the join condition
     * @return this builder instance
     */
    public SelectBuilder rightJoin(String table, Condition joinCondition) {
        return rightJoin(table, null, joinCondition);
    }

    /**
     * Adds a RIGHT JOIN to the query with an alias.
     *
     * @param table         the table to join
     * @param alias         the table alias
     * @param joinCondition the join condition
     * @return this builder instance
     */
    public SelectBuilder rightJoin(String table, String alias, Condition joinCondition) {
        tablesContext.add(table);
        joins.add(new RightJoin(table, alias, joinCondition));
        return self();
    }

    /**
     * Adds a FULL JOIN to the query.
     *
     * @param table         the table to join
     * @param joinCondition the join condition
     * @return this builder instance
     */
    public SelectBuilder fullJoin(String table, Condition joinCondition) {
        return fullJoin(table, null, joinCondition);
    }

    /**
     * Adds a FULL JOIN to the query with an alias.
     *
     * @param table         the table to join
     * @param alias         the table alias
     * @param joinCondition the join condition
     * @return this builder instance
     */
    public SelectBuilder fullJoin(String table, String alias, Condition joinCondition) {
        tablesContext.add(table);
        joins.add(new FullJoin(table, alias, joinCondition));
        return self();
    }

    /**
     * Define conditions to limit the query result.
     * When called multiple times the conditions are chained together using an AND.
     *
     * @param condition The condition
     * @return this builder instance
     */
    public SelectBuilder where(Condition condition) {
        if (condition == null) {
            return self();
        }

        conditions.add(condition);
        return self();
    }

    /**
     * Defines the columns to group the result by.
     *
     * @param columns the columns to group by
     * @return this builder instance
     */
    public SelectBuilder groupBy(String... columns) {
        groupColumns.addAll(List.of(columns));
        return self();
    }

    /**
     * Defines the condition for the HAVING clause.
     *
     * @param condition the condition for HAVING
     * @return this builder instance
     */
    public SelectBuilder having(Condition condition) {
        this.havingCondition = condition;
        return self();
    }

    /**
     * Sets the columns to order the result by.
     *
     * @param columns the columns to order by
     * @return this builder instance
     */
    public SelectBuilder orderBy(String... columns) {
        orderColumns.addAll(List.of(columns));
        return self();
    }

    /**
     * Sets the order direction to descending.
     *
     * @return this builder instance
     * @throws IllegalStateException if order direction was already set
     */
    public SelectBuilder desc() {
        if (orderDirection != null) {
            throw new IllegalStateException(ERROR_MESSAGE_MULTIPLE_ORDER_DIRECTION_CALLS);
        }

        orderDirection = "DESC";
        return self();
    }

    /**
     * Sets the order direction to ascending.
     *
     * @return this builder instance
     * @throws IllegalStateException if order direction was already set
     */
    public SelectBuilder asc() {
        if (orderDirection != null) {
            throw new IllegalStateException(ERROR_MESSAGE_MULTIPLE_ORDER_DIRECTION_CALLS);
        }

        orderDirection = "ASC";
        return self();
    }

    /**
     * Sets the limit for how many rows the SQL statement will return.
     *
     * @param limit The number of rows. All values smaller than 1 are interpreted as no limit
     * @return this builder instance
     */
    public SelectBuilder limit(int limit) {
        this.limit = limit < 1 ? -1 : limit;
        return self();
    }

    /**
     * Sets the offset where the limit starts.
     *
     * @param offset The offset. All values smaller than 1 are interpreted as an offset of 0
     * @return this builder instance
     */
    public SelectBuilder offset(int offset) {
        this.offset = offset < 1 ? 0 : offset;
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
        if (tables.isEmpty()) {
            throw new IllegalStateException("A table to select from must be specified");
        }

        if (columns.isEmpty()) {
            columns.add("*");
        }

        StringJoiner statement = new StringJoiner(" ")
                .add("SELECT");
        if (distinct) {
            statement.add("DISTINCT");
        }
        statement.add(String.join(", ", columns))
                .add("FROM")
                .add(String.join(", ", tables));

        joins.forEach(join -> statement.add(join.toSql(dialect, schema)));

        Condition whereCondition = null;
        if (!conditions.isEmpty()) {
            statement.add("WHERE");
            whereCondition = new CompositeCondition("AND", conditions);
            statement.add(whereCondition.toSql(dialect));
        }

        if (!groupColumns.isEmpty()) {
            String quotedGroups = groupColumns.stream()
                    .map(dialect::quote)
                    .collect(java.util.stream.Collectors.joining(", "));
            statement.add("GROUP BY").add(quotedGroups);
        }

        if (havingCondition != null) {
            statement.add("HAVING").add(havingCondition.toSql(dialect));
        }

        if (!orderColumns.isEmpty()) {
            if (orderDirection == null) {
                orderDirection = "DESC";
            }

            String quotedOrders = orderColumns.stream()
                    .map(dialect::quote)
                    .collect(java.util.stream.Collectors.joining(", "));
            statement.add("ORDER BY").add(quotedOrders).add(orderDirection);
        }

        if (limit > -1) {
            statement.add(dialect.applyPaging(limit, offset));
        }

        Query query = new Query(statement.toString());
        joins.forEach(join -> join.getParameters().forEach(query::addParameter));
        if (whereCondition != null) {
            whereCondition.getParameters().forEach(query::addParameter);
        }
        if (havingCondition != null) {
            havingCondition.getParameters().forEach(query::addParameter);
        }
        return query;
    }
}
