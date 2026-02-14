package sqlbuilder;

import org.junit.jupiter.api.Test;
import sqlbuilder.dialects.SqlDialect;
import sqlbuilder.dialects.*;
import sqlbuilder.expressions.Expression;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SelectBuilderJoinTest {

    @Test
    void testVariousJoinTypes() {
        SelectBuilder builder = new SelectBuilder(new PostgresDialect())
                .from("a")
                .innerJoin("b", Expression.eq("a.id", Expression.column("b.id")))
                .leftJoin("c", Expression.eq("a.id", Expression.column("c.id")))
                .rightJoin("d", Expression.eq("a.id", Expression.column("d.id")))
                .fullJoin("e", Expression.eq("a.id", Expression.column("e.id")));

        String expected = "SELECT * FROM a " +
                "INNER JOIN b ON \"a\".\"id\" = \"b\".\"id\" " +
                "LEFT JOIN c ON \"a\".\"id\" = \"c\".\"id\" " +
                "RIGHT JOIN d ON \"a\".\"id\" = \"d\".\"id\" " +
                "FULL OUTER JOIN e ON \"a\".\"id\" = \"e\".\"id\"";
        assertEquals(expected, builder.build().getStatement());
    }

    @Test
    void testJoinWithAlias() {
        SelectBuilder builder = new SelectBuilder(new PostgresDialect())
                .from("users", "u")
                .join("orders", "o", Expression.eq("u.id", Expression.column("o.user_id")));
        assertEquals("SELECT * FROM users u INNER JOIN orders o ON \"u\".\"id\" = \"o\".\"user_id\"", builder.build().getStatement());
    }
}
