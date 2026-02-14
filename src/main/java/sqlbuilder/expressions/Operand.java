package sqlbuilder.expressions;

import java.util.List;

/**
 * Interface representing a SQL operand (column, value, parameter).
 */
public interface Operand {
    /**
     * Converts the operand to its SQL string representation.
     *
     * @return the SQL string
     */
    String toSql();

    /**
     * Adds the parameters associated with this operand to the provided list.
     *
     * @param parameters the list of parameters to add to
     */
    void addParameters(List<Object> parameters);
}
