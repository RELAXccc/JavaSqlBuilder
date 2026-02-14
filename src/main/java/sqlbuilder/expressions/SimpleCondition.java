package sqlbuilder.expressions;

import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

/**
 * A simple condition comparing a column to a single value.
 */
public class SimpleCondition implements Condition {
    private final String column;
    private final String comparisonOperator;
    private final Operand comparisonValue;

    public SimpleCondition(String column, String operator, Operand value) {
        this.column = column;
        this.comparisonOperator = operator;
        this.comparisonValue = value;
    }

    @Override
    public String toSql(sqlbuilder.dialects.SqlDialect dialect) {
        return new StringJoiner(" ")
                .add(dialect.quote(column))
                .add(comparisonOperator)
                .add("?")
                .toString();
    }

    @Override
    public List<Object> getParameters() {
        return Collections.singletonList(comparisonValue);
    }
}
