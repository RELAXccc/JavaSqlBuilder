package sqlbuilder.expressions;

import sqlbuilder.SelectBuilder;
import sqlbuilder.exceptions.ValueCannotBeEmptyException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A condition that checks if a column value is within a set of values or a subquery.
 */
public class InCondition implements Condition {
    protected final String column;
    protected final List<Object> values;
    protected final SelectBuilder subQuery;
    protected final String operator;

    public InCondition(String column, List<Object> values) {
        this(column, values, null, "IN");
    }

    public InCondition(String column, Object... values) {
        this(column, List.of(values));
    }

    public InCondition(String column, SelectBuilder subQuery) {
        this(column, null, subQuery, "IN");
    }

    protected InCondition(String column, List<Object> values, SelectBuilder subQuery, String operator) {
        this.column = column;
        this.values = values;
        this.subQuery = subQuery;
        this.operator = operator;
    }

    @Override
    public String toSql(sqlbuilder.dialects.SqlDialect dialect) {
        StringBuilder sql = new StringBuilder()
                .append(dialect.quote(column))
                .append(" ")
                .append(operator)
                .append(" (");
        if (values != null) {
            if (values.isEmpty()) {
                throw new ValueCannotBeEmptyException("IN-values");
            }

            sql.append(values.stream().map(v -> "?").collect(Collectors.joining(", ")));
        } else {
            sql.append(subQuery.build().getStatement());
        }
        sql.append(")");
        return sql.toString();
    }

    @Override
    public List<Object> getParameters() {
        if (values != null) {
            return values;
        }
        return subQuery.build().getParameters();
    }
}
