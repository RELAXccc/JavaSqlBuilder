package sqlbuilder.expressions;

import java.util.List;

/**
 * A condition that negates another condition.
 */
public class NotCondition implements Condition {
    private final Condition condition;

    public NotCondition(Condition condition) {
        this.condition = condition;
    }

    @Override
    public String toSql(sqlbuilder.dialects.SqlDialect dialect) {
        return "NOT " + condition.toSql(dialect);
    }

    @Override
    public List<Object> getParameters() {
        return condition.getParameters();
    }
}
