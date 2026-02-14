package sqlbuilder.expressions;

import java.util.List;

/**
 * An operand representing a positional parameter.
 */
public class Parameter implements Operand {
    private final int index;

    public Parameter(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toSql(sqlbuilder.dialects.SqlDialect dialect) {
        return "?";
    }

    @Override
    public void addParameters(List<Object> parameters) {
        // Positional parameters are usually handled separately or added here if value is known
    }
}
