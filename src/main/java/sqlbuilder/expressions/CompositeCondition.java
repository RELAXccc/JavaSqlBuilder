package sqlbuilder.expressions;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A condition composed of multiple sub-conditions joined by an operator (AND/OR).
 */
public class CompositeCondition implements Condition {
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
    public String toSql(sqlbuilder.dialects.SqlDialect dialect) {
        if (conditions.isEmpty()) {
            return "";
        }

        return conditions.stream()
                .map(c -> c.toSql(dialect))
                .collect(Collectors.joining(" " + type + " "));
    }

    @Override
    public List<Object> getParameters() {
        return conditions.stream()
                .flatMap(condition -> condition.getParameters().stream())
                .toList();
    }
}
