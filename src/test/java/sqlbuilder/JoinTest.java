package sqlbuilder;

import org.junit.jupiter.api.Test;
import sqlbuilder.dialects.SqlDialect;
import sqlbuilder.dialects.*;
import sqlbuilder.expressions.Expression;
import sqlbuilder.joins.Join;
import sqlbuilder.joins.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JoinTest {

    @Test
    void testInnerJoinToSql() {
        Join join = new InnerJoin("orders", "o", Expression.eq("u.id", Expression.column("o.user_id")));
        assertEquals("INNER JOIN orders o ON \"u\".\"id\" = \"o\".\"user_id\"", join.toSql(new PostgresDialect(), null));
    }

    @Test
    void testJoinWithSchema() {
        Join join = new LeftJoin("orders", "o", Expression.eq("u.id", Expression.column("o.user_id")));
        assertEquals("LEFT JOIN myschema.orders o ON \"u\".\"id\" = \"o\".\"user_id\"", join.toSql(new PostgresDialect(), "myschema"));
    }

    @Test
    void testJoinNoAlias() {
        Join join = new RightJoin("orders", null, Expression.eq("u.id", Expression.column("orders.user_id")));
        assertEquals("RIGHT JOIN orders ON \"u\".\"id\" = \"orders\".\"user_id\"", join.toSql(new PostgresDialect(), ""));
    }

    @Test
    void testJoinParameters() {
        Join join = new InnerJoin("orders", "o", Expression.eq("o.status", "active"));
        assertEquals(List.of("active"), join.getParameters());
    }
}
