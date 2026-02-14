package sqlbuilder.joins;

import sqlbuilder.expressions.Condition;

/**
 * Full Join implementation.
 */
public class FullJoin extends Join {
    public FullJoin(String table, String alias, Condition condition) {
        super(table, alias, condition);
    }

    @Override
    protected String getJoinType() {
        return "FULL OUTER JOIN";
    }
}
