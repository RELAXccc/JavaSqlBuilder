package sqlbuilder.expressions;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * A condition that compares two operands using an operator.
 */
public class ComparisonCondition implements Condition {
    private final Operand column;
    private final String operator;
    private final Operand comparisonValue;

    public ComparisonCondition(Operand column, String operator, Operand comparisonValue) {
        this.column = column;
        this.operator = operator;
        this.comparisonValue = comparisonValue;
    }

    @Override
    public String toSql(sqlbuilder.dialects.SqlDialect dialect) {
        return new StringJoiner(" ")
                .add(column.toSql(dialect))
                .add(operator)
                .add(comparisonValue.toSql(dialect))
                .toString();
    }

    @Override
    public List<Object> getParameters() {
        List<Object> params = new ArrayList<>();
        column.addParameters(params);
        comparisonValue.addParameters(params);
        return params;
    }
}
