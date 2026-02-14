package sqlbuilder.expressions;

import java.util.List;

/**
 * An operand representing a database column.
 */
public class ColumnOperand implements Operand {
    private final String columnName;

    public ColumnOperand(String columnName) {
        this.columnName = columnName;
    }

    @Override
    public String toSql(sqlbuilder.dialects.SqlDialect dialect) {
        if (columnName.contains(".")) {
            String[] parts = columnName.split("\\.");
            return parts[0] + "." + dialect.quote(parts[1]);
        }
        return dialect.quote(columnName);
    }

    @Override
    public void addParameters(List<Object> parameters) {
        // Columns don't have parameters
    }
}
