package sqlbuilder.expressions;

import sqlbuilder.SelectBuilder;
import java.util.List;

/**
 * A condition that checks if a subquery returns any results.
 */
public class ExistsCondition implements Condition {
    protected final SelectBuilder subQuery;
    protected final String operator;

    public ExistsCondition(SelectBuilder subQuery) {
        this(subQuery, "EXISTS");
    }

    protected ExistsCondition(SelectBuilder subQuery, String operator) {
        this.subQuery = subQuery;
        this.operator = operator;
    }

    @Override
    public String toSql(sqlbuilder.dialects.SqlDialect dialect) {
        return operator + " (" + subQuery.build().getStatement() + ")";
    }

    @Override
    public List<Object> getParameters() {
        return subQuery.build().getParameters();
    }
}
