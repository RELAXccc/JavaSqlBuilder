package sqlbuilder.expressions;

import sqlbuilder.SelectBuilder;
import java.util.List;

/**
 * A utility for chaining conditions fluently.
 */
public class ConditionChain {
    private final Condition leftCondition;
    private final String chainingOperator;

    public ConditionChain(Condition leftCondition, String chainingOperator) {
        this.leftCondition = leftCondition;
        this.chainingOperator = chainingOperator;
    }

    public Condition eq(String column, Object value) {
        return createCompositeCondition(Expression.eq(column, value));
    }

    public Condition neq(String column, Object value) {
        return createCompositeCondition(Expression.neq(column, value));
    }

    public Condition lt(String column, Object value) {
        return createCompositeCondition(Expression.lt(column, value));
    }

    public Condition leq(String column, Object value) {
        return createCompositeCondition(Expression.leq(column, value));
    }

    public Condition gt(String column, Object value) {
        return createCompositeCondition(Expression.gt(column, value));
    }

    public Condition geq(String column, Object value) {
        return createCompositeCondition(Expression.geq(column, value));
    }

    public Condition like(String column, Object value) {
        return createCompositeCondition(Expression.like(column, value));
    }

    public Condition isNull(String column) {
        return createCompositeCondition(Expression.isNull(column));
    }

    public Condition isNotNull(String column) {
        return createCompositeCondition(Expression.isNotNull(column));
    }

    public Condition not(Condition condition) {
        return createCompositeCondition(Expression.not(condition));
    }

    public Condition in(String column, List<Object> values) {
        return createCompositeCondition(Expression.in(column, values));
    }

    public Condition in(String column, Object... values) {
        return in(column, List.of(values));
    }

    public Condition in(String column, SelectBuilder subQuery) {
        return createCompositeCondition(Expression.in(column, subQuery));
    }

    public Condition notIn(String column, List<Object> values) {
        return createCompositeCondition(Expression.notIn(column, values));
    }

    public Condition notIn(String column, Object... values) {
        return notIn(column, List.of(values));
    }

    public Condition notIn(String column, SelectBuilder subQuery) {
        return createCompositeCondition(Expression.notIn(column, subQuery));
    }

    public Condition exists(SelectBuilder subQuery) {
        return createCompositeCondition(Expression.exists(subQuery));
    }

    public Condition notExists(SelectBuilder subQuery) {
        return createCompositeCondition(Expression.notExists(subQuery));
    }

    private Condition createCompositeCondition(Condition expression) {
        return new CompositeCondition(chainingOperator, leftCondition, expression);
    }
}
