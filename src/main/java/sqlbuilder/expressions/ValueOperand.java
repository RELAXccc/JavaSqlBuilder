package sqlbuilder.expressions;

import java.util.List;

/**
 * An operand representing a literal value.
 */
public class ValueOperand implements Operand {
    private final Object value;

    public ValueOperand(Object value) {
        this.value = value;
    }

    @Override
    public String toSql(sqlbuilder.dialects.SqlDialect dialect) {
        return "?";
    }

    @Override
    public void addParameters(List<Object> parameters) {
        parameters.add(value);
    }
}
