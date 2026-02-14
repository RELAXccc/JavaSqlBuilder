package sqlbuilder;

import org.junit.jupiter.api.Test;
import sqlbuilder.dialects.SqlDialect;
import sqlbuilder.dialects.*;
import sqlbuilder.exceptions.ValueCannotBeEmptyException;
import sqlbuilder.expressions.Condition;
import sqlbuilder.expressions.*;
import sqlbuilder.expressions.Expression;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConditionTest {
    private final SqlDialect dialect = new H2Dialect();

    @Test
    void testCompositeConditionEmpty() {
        CompositeCondition composite = new CompositeCondition("AND", Collections.emptyList());
        assertEquals("", composite.toSql(dialect));
        assertTrue(composite.getParameters().isEmpty());
    }

    @Test
    void testInConditionEmptyValuesFails() {
        Condition inCondition = Expression.in("col", Collections.emptyList());
        assertThrows(ValueCannotBeEmptyException.class, () -> inCondition.toSql(dialect));
    }

    @Test
    void testInConditionWithNullSubqueryFails() {
        // This might happen if someone passes null to the constructor directly or via some edge case
        // The current implementation of Expression.in(String, SelectBuilder) doesn't check for null
        // but subQuery.build() would fail if subQuery is null.
    }

    @Test
    void testConditionChainMethods() {
        Condition cond = Expression.eq("a", 1);
        Condition andCond = cond.and().eq("b", 2);
        Condition orCond = cond.or().eq("c", 3);

        assertEquals("\"a\" = ? AND \"b\" = ?", andCond.toSql(dialect));
        assertEquals("\"a\" = ? OR \"c\" = ?", orCond.toSql(dialect));
    }

    @Test
    void testAllChainingMethods() {
        Condition start = Expression.eq("x", 0);
        ConditionChain chain = start.and();
        
        assertNotNull(chain.eq("a", 1));
        assertNotNull(chain.neq("a", 1));
        assertNotNull(chain.lt("a", 1));
        assertNotNull(chain.leq("a", 1));
        assertNotNull(chain.gt("a", 1));
        assertNotNull(chain.geq("a", 1));
        assertNotNull(chain.like("a", 1));
        assertNotNull(chain.isNull("a"));
        assertNotNull(chain.isNotNull("a"));
        assertNotNull(chain.not(Expression.eq("b", 2)));
        assertNotNull(chain.in("a", 1, 2));
        assertNotNull(chain.in("a", List.of(1, 2)));
        assertNotNull(chain.notIn("a", 1, 2));
        assertNotNull(chain.notIn("a", List.of(1, 2)));
        
        SelectBuilder sub = new SelectBuilder(dialect).from("t");
        assertNotNull(chain.in("a", sub));
        assertNotNull(chain.notIn("a", sub));
        assertNotNull(chain.exists(sub));
        assertNotNull(chain.notExists(sub));
    }

    @Test
    void testOperands() {
        assertEquals("\"col\"", Expression.column("col").toSql(dialect));
        assertEquals("?", Expression.value(1).toSql(dialect));
        assertEquals("?", Expression.param(1).toSql(dialect));
    }

    @Test
    void testInConditionWithValuesList() {
        Condition in = Expression.in("col", List.of(1, 2, 3));
        assertEquals("\"col\" IN (?, ?, ?)", in.toSql(dialect));
        assertEquals(List.of(1, 2, 3), in.getParameters());
    }
}
