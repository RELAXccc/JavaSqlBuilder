package sqlbuilder.expressions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public interface Condition {
    public String toSql();
    List<Object> getParameters();

    default ConditionChain and() {
        return new ConditionChain(this, "AND");
    }

    default ConditionChain or() {
        return new ConditionChain(this, "OR");
    }

    class ConditionChain {
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

        public Condition not(Condition condition) {
            return createCompositeCondition(Expression.not(condition));
        }

        private Condition createCompositeCondition(Condition expression) {
            return new CompositeCondition(chainingOperator, leftCondition, expression);
        }
    }

    class CompositeCondition implements Condition {
        private final String type;
        private final List<Condition> conditions;

        public CompositeCondition(String type, Condition... conditions) {
            this.type = type;
            this.conditions = List.of(conditions);
        }

        public CompositeCondition(String type, List<Condition> conditions) {
            this.type = type;
            this.conditions = conditions;
        }

        @Override
        public String toSql() {
            if(conditions.isEmpty()) {
                return "";
            }

            return conditions.stream()
                    .map(Condition::toSql)
                    .collect(Collectors.joining(" " + type + " "));
        }

        @Override
        public List<Object> getParameters() {
            return conditions.stream()
                    .flatMap(condition -> condition.getParameters().stream())
                    .toList();
        }
    }
}

class ComparisionCondition implements Condition {
    private final Operand column;
    private final String operator;
    private final Operand comparisonValue;

    public ComparisionCondition(Operand column, String operator, Operand comparisonValue) {
        this.column = column;
        this.operator = operator;
        this.comparisonValue = comparisonValue;
    }

    @Override
    public String toSql() {
        return new StringJoiner(" ")
                .add(column.toSql())
                .add(operator)
                .add(comparisonValue.toSql())
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

class NullCondition implements Condition {
    protected final String column;

    public NullCondition(String column) {
        this.column = column;
    }

    @Override
    public String toSql() {
        return column + " IS NULL";
    }

    @Override
    public List<Object> getParameters() {
        // IS NULL has no parameters
        return List.of();
    }
}

class NotNullCondition extends NullCondition {
    public NotNullCondition(String column) {
        super(column);
    }

    @Override
    public String toSql() {
        return column + "IS NOT NULL";
    }
}

class NotCondition implements Condition {
    private final Condition condition;

    public NotCondition(Condition condition) {
        this.condition = condition;
    }

    @Override
    public String toSql() {
        return "NOT " + condition.toSql();
    }

    @Override
    public List<Object> getParameters() {
        return condition.getParameters();
    }
}

class SimpleCondition implements Condition {
    private final String column;
    private final String comparisonOperator;
    private final Operand comparisonValue;

    public SimpleCondition(String column, String operator, Operand value) {
        this.column = column;
        this.comparisonOperator = operator;
        this.comparisonValue = value;
    }

    @Override
    public String toSql() {
        return new StringJoiner(" ")
                .add(column)
                .add(comparisonOperator)
                .add("?")
                .toString();
    }

    @Override
    public List<Object> getParameters() {
        return Collections.singletonList(comparisonValue);
    }
}