package sqlbuilder.expressions;

/**
 * A condition that checks if a column is NOT NULL.
 */
public class NotNullCondition extends NullCondition {
    public NotNullCondition(String column) {
        super(column);
    }

    @Override
    public String toSql() {
        return column + " IS NOT NULL";
    }
}
