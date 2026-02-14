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
    public String toSql() {
        return "NOT " + condition.toSql();
    }

    @Override
    public List<Object> getParameters() {
        return condition.getParameters();
    }
}
