package sqlbuilder.expressions;

import sqlbuilder.SelectBuilder;
import java.util.List;

/**
 * A condition that checks if a column value is NOT within a set of values or a subquery.
 */
public class NotInCondition extends InCondition {
    public NotInCondition(String column, SelectBuilder subQuery) {
        this(column, null, subQuery);
    }

    public NotInCondition(String column, List<Object> values) {
        this(column, values, null);
    }

    public NotInCondition(String column, Object... values) {
        this(column, List.of(values));
    }

    private NotInCondition(String column, List<Object> values, SelectBuilder subQuery) {
        super(column, values, subQuery, "NOT IN");
    }
}
