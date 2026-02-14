package sqlbuilder;

import org.junit.jupiter.api.Test;
import sqlbuilder.dialects.SqlDialect;
import sqlbuilder.dialects.*;
import sqlbuilder.expressions.Condition;
import sqlbuilder.expressions.*;
import sqlbuilder.expressions.Expression;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExpressionTest {
    private final SqlDialect dialect = new H2Dialect();

    @Test
    void testAllComparisonOperators() {
        assertEquals("\"col\" = ?", Expression.eq("col", 1).toSql(dialect));
        assertEquals("\"col\" <> ?", Expression.neq("col", 1).toSql(dialect));
        assertEquals("\"col\" < ?", Expression.lt("col", 1).toSql(dialect));
        assertEquals("\"col\" <= ?", Expression.leq("col", 1).toSql(dialect));
        assertEquals("\"col\" > ?", Expression.gt("col", 1).toSql(dialect));
        assertEquals("\"col\" >= ?", Expression.geq("col", 1).toSql(dialect));
        assertEquals("\"col\" LIKE ?", Expression.like("col", "%test%").toSql(dialect));
    }

    @Test
    void testConditionChainOperators() {
        Condition chain = Expression.eq("a", 1).and().neq("b", 2).or().gt("c", 3);
        assertEquals("\"a\" = ? AND \"b\" <> ? OR \"c\" > ?", chain.toSql(dialect));
        assertEquals(List.of(1, 2, 3), chain.getParameters());
    }

    @Test
    void testFullConditionChain() {
        Condition chain = Expression.eq("a", 1)
                .and().lt("b", 2)
                .and().leq("c", 3)
                .and().geq("d", 4)
                .and().like("e", "f")
                .and().isNull("g")
                .and().isNotNull("h");
        
        String expected = "\"a\" = ? AND \"b\" < ? AND \"c\" <= ? AND \"d\" >= ? AND \"e\" LIKE ? AND \"g\" IS NULL AND \"h\" IS NOT NULL";
        assertEquals(expected, chain.toSql(dialect));
    }

    @Test
    void testInAndExistsChain() {
        SelectBuilder sub = new SelectBuilder(dialect).from("sub");
        
        Condition chain = Expression.in("a", 1, 2)
                .and().notIn("b", 3, 4)
                .and().exists(sub)
                .and().notExists(sub);
        
        String expected = "\"a\" IN (?, ?) AND \"b\" NOT IN (?, ?) AND EXISTS (SELECT * FROM sub) AND NOT EXISTS (SELECT * FROM sub)";
        assertEquals(expected, chain.toSql(dialect));
    }

    @Test
    void testNestedNot() {
        Condition cond = Expression.not(Expression.eq("a", 1).and().eq("b", 2));
        assertEquals("NOT \"a\" = ? AND \"b\" = ?", cond.toSql(dialect));
    }

    @Test
    void testColumnWithAlias() {
        assertEquals("\"u\".\"name\"", Expression.column("u", "name").toSql(dialect));
        assertEquals("\"u\".\"name\"", Expression.column("u", "u.name").toSql(dialect));
        assertEquals("\"name\"", Expression.column(null, "name").toSql(dialect));
    }
}
