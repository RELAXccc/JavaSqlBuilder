package sqlbuilder;

import org.junit.jupiter.api.Test;
import sqlbuilder.dialects.SqlDialect;
import sqlbuilder.dialects.*;
import sqlbuilder.expressions.Expression;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SelectBuilderTest {

    @Test
    void testBasicSelect() {
        SelectBuilder builder = new SelectBuilder(new PostgresDialect());
        Query query = builder.from("users").build();
        assertEquals("SELECT * FROM users", query.getStatement());
    }

    @Test
    void testSelectColumns() {
        SelectBuilder builder = new SelectBuilder(new PostgresDialect());
        Query query = builder.select("id", "name").from("users").build();
        assertEquals("SELECT \"id\", \"name\" FROM users", query.getStatement());
    }

    @Test
    void testSelectDistinct() {
        SelectBuilder builder = new SelectBuilder(new PostgresDialect());
        Query query = builder.selectDistinct("role").from("users").build();
        assertEquals("SELECT DISTINCT \"role\" FROM users", query.getStatement());
    }

    @Test
    void testWhereClause() {
        SelectBuilder builder = new SelectBuilder(new PostgresDialect());
        Query query = builder.from("users")
                .where(Expression.eq("id", 1))
                .build();
        assertEquals("SELECT * FROM users WHERE \"id\" = ?", query.getStatement());
        assertEquals(List.of(1), query.getParameters());
    }

    @Test
    void testComplexWhereClause() {
        SelectBuilder builder = new SelectBuilder(new PostgresDialect());
        Query query = builder.from("users")
                .where(Expression.eq("status", "active").and().gt("age", 18))
                .build();
        assertEquals("SELECT * FROM users WHERE \"status\" = ? AND \"age\" > ?", query.getStatement());
        assertEquals(List.of("active", 18), query.getParameters());
    }

    @Test
    void testInCondition() {
        SelectBuilder builder = new SelectBuilder(new PostgresDialect());
        Query query = builder.from("users")
                .where(Expression.in("status", "active", "pending", "archived"))
                .build();
        assertEquals("SELECT * FROM users WHERE \"status\" IN (?, ?, ?)", query.getStatement());
        assertEquals(List.of("active", "pending", "archived"), query.getParameters());
    }

    @Test
    void testInWithSubquery() {
        SqlDialect dialect = new PostgresDialect();
        SelectBuilder subQuery = new SelectBuilder(dialect).select("id").from("active_users");
        SelectBuilder builder = new SelectBuilder(dialect).from("orders")
                .where(Expression.in("user_id", subQuery));
        Query query = builder.build();
        assertEquals("SELECT * FROM orders WHERE \"user_id\" IN (SELECT \"id\" FROM active_users)", query.getStatement());
    }

    @Test
    void testExistsCondition() {
        SqlDialect dialect = new PostgresDialect();
        SelectBuilder subQuery = new SelectBuilder(dialect).from("orders").where(Expression.eq("user_id", Expression.column("users.id")));
        SelectBuilder builder = new SelectBuilder(dialect).from("users")
                .where(Expression.exists(subQuery));
        Query query = builder.build();
        assertEquals("SELECT * FROM users WHERE EXISTS (SELECT * FROM orders WHERE \"user_id\" = \"users\".\"id\")", query.getStatement());
    }

    @Test
    void testNotCondition() {
        SelectBuilder builder = new SelectBuilder(new PostgresDialect());
        Query query = builder.from("users")
                .where(Expression.not(Expression.eq("status", "deleted")))
                .build();
        assertEquals("SELECT * FROM users WHERE NOT \"status\" = ?", query.getStatement());
        assertEquals(List.of("deleted"), query.getParameters());
    }

    @Test
    void testConditionChaining() {
        SelectBuilder builder = new SelectBuilder(new PostgresDialect());
        Query query = builder.from("users")
                .where(Expression.eq("status", "active").and().in("role", "admin", "editor"))
                .build();
        assertEquals("SELECT * FROM users WHERE \"status\" = ? AND \"role\" IN (?, ?)", query.getStatement());
        assertEquals(List.of("active", "admin", "editor"), query.getParameters());
    }

    @Test
    void testJoin() {
        SelectBuilder builder = new SelectBuilder(new PostgresDialect());
        Query query = builder.select("u.name", "o.order_date")
                .from("users", "u")
                .join("orders", "o", Expression.eq("u.id", Expression.column("o.user_id")))
                .build();
        assertEquals("SELECT \"u\".\"name\", \"o\".\"order_date\" FROM users u INNER JOIN orders o ON \"u\".\"id\" = \"o\".\"user_id\"", query.getStatement());
    }

    @Test
    void testLeftJoin() {
        SelectBuilder builder = new SelectBuilder(new PostgresDialect());
        Query query = builder.select("u.name", "o.id")
                .from("users", "u")
                .leftJoin("orders", "o", Expression.eq("u.id", Expression.column("o.user_id")))
                .build();
        assertEquals("SELECT \"u\".\"name\", \"o\".\"id\" FROM users u LEFT JOIN orders o ON \"u\".\"id\" = \"o\".\"user_id\"", query.getStatement());
    }

    @Test
    void testRightJoin() {
        SelectBuilder builder = new SelectBuilder(new PostgresDialect());
        Query query = builder.select("u.name", "o.id")
                .from("users", "u")
                .rightJoin("orders", "o", Expression.eq("u.id", Expression.column("o.user_id")))
                .build();
        assertEquals("SELECT \"u\".\"name\", \"o\".\"id\" FROM users u RIGHT JOIN orders o ON \"u\".\"id\" = \"o\".\"user_id\"", query.getStatement());
    }

    @Test
    void testFullJoin() {
        SelectBuilder builder = new SelectBuilder(new PostgresDialect());
        Query query = builder.select("u.name", "o.id")
                .from("users", "u")
                .fullJoin("orders", "o", Expression.eq("u.id", Expression.column("o.user_id")))
                .build();
        assertEquals("SELECT \"u\".\"name\", \"o\".\"id\" FROM users u FULL OUTER JOIN orders o ON \"u\".\"id\" = \"o\".\"user_id\"", query.getStatement());
    }

    @Test
    void testOrderByLimitOffset() {
        SelectBuilder builder = new SelectBuilder(new PostgresDialect());
        Query query = builder.from("products")
                .orderBy("price").desc()
                .limit(10)
                .offset(5)
                .build();
        assertEquals("SELECT * FROM products ORDER BY \"price\" DESC LIMIT 10 OFFSET 5", query.getStatement());
    }

    @Test
    void testOracleDialectPaging() {
        SelectBuilder builder = new SelectBuilder(new OracleDialect());
        Query query = builder.from("tasks")
                .limit(5)
                .offset(0)
                .build();
        assertEquals("SELECT * FROM tasks LIMIT 5 OFFSET 0", query.getStatement());
    }

    @Test
    void testNullConditions() {
        SelectBuilder builder = new SelectBuilder(new PostgresDialect());
        Query query = builder.from("users")
                .where(Expression.isNull("deleted_at"))
                .where(Expression.isNotNull("email"))
                .build();
        assertEquals("SELECT * FROM users WHERE \"deleted_at\" IS NULL AND \"email\" IS NOT NULL", query.getStatement());
    }

    @Test
    void testMultipleJoins() {
        SelectBuilder builder = new SelectBuilder(new PostgresDialect());
        Query query = builder.select("u.name", "o.id", "p.name")
                .from("users", "u")
                .join("orders", "o", Expression.eq("u.id", Expression.column("o.user_id")))
                .join("order_items", "oi", Expression.eq("o.id", Expression.column("oi.order_id")))
                .join("products", "p", Expression.eq("oi.product_id", Expression.column("p.id")))
                .build();
        assertEquals("SELECT \"u\".\"name\", \"o\".\"id\", \"p\".\"name\" FROM users u INNER JOIN orders o ON \"u\".\"id\" = \"o\".\"user_id\" INNER JOIN order_items oi ON \"o\".\"id\" = \"oi\".\"order_id\" INNER JOIN products p ON \"oi\".\"product_id\" = \"p\".\"id\"", query.getStatement());
    }

    @Test
    void testSchema() {
        SelectBuilder builder = new SelectBuilder(new PostgresDialect(), "myschema");
        Query query = builder.from("users").build();
        assertEquals("SELECT * FROM myschema.users", query.getStatement());
    }

    @Test
    void testSchemaWithJoin() {
        SelectBuilder builder = new SelectBuilder(new PostgresDialect(), "myschema");
        Query query = builder.from("users", "u")
                .join("orders", "o", Expression.eq("u.id", Expression.column("o.user_id")))
                .build();
        assertEquals("SELECT * FROM myschema.users u INNER JOIN myschema.orders o ON \"u\".\"id\" = \"o\".\"user_id\"", query.getStatement());
    }

    @Test
    void testGroupBy() {
        SelectBuilder builder = new SelectBuilder(new PostgresDialect());
        Query query = builder.select("department", "COUNT(*)")
                .from("employees")
                .groupBy("department")
                .build();
        assertEquals("SELECT \"department\", COUNT(*) FROM employees GROUP BY \"department\"", query.getStatement());
    }

    @Test
    void testGroupByHaving() {
        SelectBuilder builder = new SelectBuilder(new PostgresDialect());
        Query query = builder.select("department", "COUNT(*)")
                .from("employees")
                .groupBy("department")
                .having(Expression.gt("COUNT(*)", 5))
                .build();
        assertEquals("SELECT \"department\", COUNT(*) FROM employees GROUP BY \"department\" HAVING COUNT(*) > ?", query.getStatement());
        assertEquals(List.of(5), query.getParameters());
    }

    @Test
    void testOrderByAsc() {
        SelectBuilder builder = new SelectBuilder(new PostgresDialect());
        Query query = builder.from("users").orderBy("name").asc().build();
        assertEquals("SELECT * FROM users ORDER BY \"name\" ASC", query.getStatement());
    }

    @Test
    void testMultipleOrderByFails() {
        SelectBuilder builder = new SelectBuilder(new PostgresDialect());
        builder.orderBy("name").asc();
        org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class, builder::desc);
    }

    @Test
    void testNoTableFails() {
        SelectBuilder builder = new SelectBuilder(new PostgresDialect());
        org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    void testEmptyColumnsFails() {
        SelectBuilder builder = new SelectBuilder(new PostgresDialect());
        org.junit.jupiter.api.Assertions.assertThrows(sqlbuilder.exceptions.ValueCannotBeEmptyException.class, () -> builder.select());
    }

    @Test
    void testComplexJoinWithWhereAndGroupBy() {
        SelectBuilder builder = new SelectBuilder(new PostgresDialect());
        Query query = builder.select("u.name", "SUM(o.amount)")
                .from("users", "u")
                .leftJoin("orders", "o", Expression.eq("u.id", Expression.column("o.user_id")))
                .where(Expression.eq("u.status", "active"))
                .groupBy("u.name")
                .having(Expression.gt("SUM(o.amount)", 1000))
                .orderBy("u.name").asc()
                .limit(10)
                .build();
        
        String expected = "SELECT \"u\".\"name\", SUM(o.amount) FROM users u LEFT JOIN orders o ON \"u\".\"id\" = \"o\".\"user_id\" WHERE \"u\".\"status\" = ? GROUP BY \"u\".\"name\" HAVING SUM(o.amount) > ? ORDER BY \"u\".\"name\" ASC LIMIT 10 OFFSET 0";
        assertEquals(expected, query.getStatement());
        assertEquals(List.of("active", 1000), query.getParameters());
    }

    @Test
    void testH2DialectPaging() {
        SelectBuilder builder = new SelectBuilder(new H2Dialect());
        Query query = builder.from("users").limit(10).offset(20).build();
        assertEquals("SELECT * FROM users LIMIT 10 OFFSET 20", query.getStatement());
    }

    @Test
    void testMultipleTables() {
        SelectBuilder builder = new SelectBuilder(new PostgresDialect());
        Query query = builder.from(new String[]{"table1", "table2"}).build();
        assertEquals("SELECT * FROM table1, table2", query.getStatement());
    }

    @Test
    void testFromWithAlias() {
        SelectBuilder builder = new SelectBuilder(new PostgresDialect());
        Query query = builder.from("users", "u").build();
        assertEquals("SELECT * FROM users u", query.getStatement());
    }

    @Test
    void testMultipleOrderBy() {
        SelectBuilder builder = new SelectBuilder(new PostgresDialect());
        Query query = builder.from("users").orderBy("name", "age").desc().build();
        assertEquals("SELECT * FROM users ORDER BY \"name\", \"age\" DESC", query.getStatement());
    }

    @Test
    void testInvalidLimitAndOffset() {
        SelectBuilder builder = new SelectBuilder(new PostgresDialect());
        Query query = builder.from("users").limit(0).offset(-5).build();
        assertEquals("SELECT * FROM users", query.getStatement());
    }

    @Test
    void testWhereNullConditionIgnored() {
        SelectBuilder builder = new SelectBuilder(new PostgresDialect());
        Query query = builder.from("users").where(null).build();
        assertEquals("SELECT * FROM users", query.getStatement());
    }

    @Test
    void testFromEmptyFails() {
        SelectBuilder builder = new SelectBuilder(new PostgresDialect());
        assertThrows(sqlbuilder.exceptions.ValueCannotBeEmptyException.class, () -> builder.from());
        assertThrows(sqlbuilder.exceptions.ValueCannotBeEmptyException.class, () -> builder.from(""));
        assertThrows(sqlbuilder.exceptions.ValueCannotBeEmptyException.class, () -> builder.from((String)null));
    }
}
