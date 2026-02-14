package sqlbuilder.joins;

import sqlbuilder.expressions.Condition;

/**
 * Left Join implementation.
 */
public class LeftJoin extends Join {
    public LeftJoin(String table, String alias, Condition condition) {
        super(table, alias, condition);
    }

    @Override
    protected String getJoinType() {
        return "LEFT JOIN";
    }
}
