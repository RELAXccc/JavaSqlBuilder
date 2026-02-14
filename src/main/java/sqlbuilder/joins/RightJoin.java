package sqlbuilder.joins;

import sqlbuilder.expressions.Condition;

/**
 * Right Join implementation.
 */
public class RightJoin extends Join {
    public RightJoin(String table, String alias, Condition condition) {
        super(table, alias, condition);
    }

    @Override
    protected String getJoinType() {
        return "RIGHT JOIN";
    }
}
