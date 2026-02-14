package sqlbuilder;

import org.junit.jupiter.api.Test;
import sqlbuilder.dialects.SqlDialect;
import sqlbuilder.expressions.Condition;
import sqlbuilder.expressions.Expression;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExpressionTest {

    @Test
    void testAllComparisonOperators() {
        assertEquals("col = ?", Expression.eq("col", 1).toSql());
        assertEquals("col <> ?", Expression.neq("col", 1).toSql());
        assertEquals("col < ?", Expression.lt("col", 1).toSql());
        assertEquals("col <= ?", Expression.leq("col", 1).toSql());
        assertEquals("col > ?", Expression.gt("col", 1).toSql());
        assertEquals("col >= ?", Expression.geq("col", 1).toSql());
        assertEquals("col LIKE ?", Expression.like("col", "%test%").toSql());
    }

    @Test
    void testConditionChainOperators() {
        Condition chain = Expression.eq("a", 1).and().neq("b", 2).or().gt("c", 3);
        assertEquals("a = ? AND b <> ? OR c > ?", chain.toSql());
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
        
        String expected = "a = ? AND b < ? AND c <= ? AND d >= ? AND e LIKE ? AND g IS NULL AND h IS NOT NULL";
        assertEquals(expected, chain.toSql());
    }

    @Test
    void testInAndExistsChain() {
        SqlDialect dialect = new SqlDialect.PostgresDialect();
        SelectBuilder sub = new SelectBuilder(dialect).from("sub");
        
        Condition chain = Expression.in("a", 1, 2)
                .and().notIn("b", 3, 4)
                .and().exists(sub)
                .and().notExists(sub);
        
        String expected = "a IN (?, ?) AND b NOT IN (?, ?) AND EXISTS (SELECT * FROM sub) AND NOT EXISTS (SELECT * FROM sub)";
        assertEquals(expected, chain.toSql());
    }

    @Test
    void testNestedNot() {
        Condition cond = Expression.not(Expression.eq("a", 1).and().eq("b", 2));
        assertEquals("NOT a = ? AND b = ?", cond.toSql());
    }

    @Test
    void testColumnWithAlias() {
        assertEquals("u.name", Expression.column("u", "name").toSql());
        assertEquals("u.name", Expression.column("u", "u.name").toSql());
        assertEquals("name", Expression.column(null, "name").toSql());
    }
}
