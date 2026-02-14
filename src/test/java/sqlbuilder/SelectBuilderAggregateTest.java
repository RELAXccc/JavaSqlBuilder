package sqlbuilder;

import org.junit.jupiter.api.Test;
import sqlbuilder.dialects.SqlDialect;
import sqlbuilder.expressions.Expression;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SelectBuilderAggregateTest {

    @Test
    void testComplexAggregateQuery() {
        SelectBuilder builder = new SelectBuilder(new SqlDialect.PostgresDialect())
                .select("dept", "AVG(salary)")
                .from("employees")
                .where(Expression.gt("age", 25))
                .groupBy("dept")
                .having(Expression.gt("AVG(salary)", 50000))
                .orderBy("dept").asc();
        
        Query query = builder.build();
        assertEquals("SELECT \"dept\", \"AVG(salary)\" FROM employees WHERE age > ? GROUP BY dept HAVING AVG(salary) > ? ORDER BY dept ASC", query.getStatement());
        assertEquals(List.of(25, 50000), query.getParameters());
    }
}
