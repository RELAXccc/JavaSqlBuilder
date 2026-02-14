package sqlbuilder.joins;

import sqlbuilder.expressions.Condition;

/**
 * Inner Join implementation.
 */
public class InnerJoin extends Join {
    public InnerJoin(String table, String alias, Condition condition) {
        super(table, alias, condition);
    }

    @Override
    protected String getJoinType() {
        return "INNER JOIN";
    }
}
