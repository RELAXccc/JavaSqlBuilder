package sqlbuilder.expressions;

import sqlbuilder.SelectBuilder;

/**
 * A condition that checks if a subquery does NOT return any results.
 */
public class NotExistsCondition extends ExistsCondition {
    public NotExistsCondition(SelectBuilder subQuery) {
        super(subQuery, "NOT EXISTS");
    }
}
