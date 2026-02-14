package sqlbuilder.expressions;

import sqlbuilder.SelectBuilder;
import java.util.List;

/**
 * Interface representing a SQL condition for WHERE and HAVING clauses.
 */
public interface Condition {
    /**
     * Converts the condition to its SQL string representation.
     *
     * @return the SQL string
     */
    String toSql();

    /**
     * Returns the list of parameters associated with this condition.
     *
     * @return the parameters
     */
    List<Object> getParameters();

    /**
     * Chains this condition with another using an AND operator.
     *
     * @return a condition chain
     */
    default ConditionChain and() {
        return new ConditionChain(this, "AND");
    }

    /**
     * Chains this condition with another using an OR operator.
     *
     * @return a condition chain
     */
    default ConditionChain or() {
        return new ConditionChain(this, "OR");
    }
}
