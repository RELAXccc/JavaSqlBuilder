package sqlbuilder.expressions;

import java.util.List;

/**
 * A condition that checks if a column is NULL.
 */
public class NullCondition implements Condition {
    protected final String column;

    public NullCondition(String column) {
        this.column = column;
    }

    @Override
    public String toSql() {
        return column + " IS NULL";
    }

    @Override
    public List<Object> getParameters() {
        return List.of();
    }
}
