package sqlbuilder;

import org.junit.jupiter.api.Test;
import sqlbuilder.dialects.SqlDialect;
import sqlbuilder.dialects.*;
import sqlbuilder.expressions.Expression;

import static org.junit.jupiter.api.Assertions.*;

class CrudBuilderTest {

    @Test
    void testInsertBuilder() {
        InsertBuilder builder = new InsertBuilder(new OracleDialect());
        Query query = builder
                .into("USERS")
                .value("NAME", "John Doe")
                .value("AGE", 30)
                .build();

        assertEquals("INSERT INTO USERS (\"NAME\", \"AGE\") VALUES (?, ?)", query.getStatement());
        assertEquals(2, query.getParameters().size());
        assertEquals("John Doe", query.getParameters().get(0));
        assertEquals(30, query.getParameters().get(1));
    }

    @Test
    void testUpdateBuilder() {
        UpdateBuilder builder = new UpdateBuilder(new OracleDialect());
        Query query = builder
                .table("USERS")
                .set("NAME", "Jane Doe")
                .set("AGE", 25)
                .where(Expression.eq("ID", 1))
                .build();

        assertEquals("UPDATE USERS SET \"NAME\" = ?, \"AGE\" = ? WHERE \"ID\" = ?", query.getStatement());
        assertEquals(3, query.getParameters().size());
        assertEquals("Jane Doe", query.getParameters().get(0));
        assertEquals(25, query.getParameters().get(1));
        assertEquals(1, query.getParameters().get(2));
    }

    @Test
    void testDeleteBuilder() {
        DeleteBuilder builder = new DeleteBuilder(new OracleDialect());
        Query query = builder
                .from("USERS")
                .where(Expression.eq("ID", 1))
                .build();

        assertEquals("DELETE FROM USERS WHERE \"ID\" = ?", query.getStatement());
        assertEquals(1, query.getParameters().size());
        assertEquals(1, query.getParameters().get(0));
    }

    @Test
    void testInsertWithSchema() {
        InsertBuilder builder = new InsertBuilder(new OracleDialect(), "MY_SCHEMA");
        Query query = builder
                .into("USERS")
                .value("NAME", "John Doe")
                .build();

        assertEquals("INSERT INTO MY_SCHEMA.USERS (\"NAME\") VALUES (?)", query.getStatement());
    }

    @Test
    void testUpdateWithoutWhere() {
        UpdateBuilder builder = new UpdateBuilder(new OracleDialect());
        Query query = builder
                .table("USERS")
                .set("ACTIVE", true)
                .build();

        assertEquals("UPDATE USERS SET \"ACTIVE\" = ?", query.getStatement());
    }

    @Test
    void testDeleteWithoutWhere() {
        DeleteBuilder builder = new DeleteBuilder(new OracleDialect());
        Query query = builder
                .from("USERS")
                .build();

        assertEquals("DELETE FROM USERS", query.getStatement());
    }

    @Test
    void testBulkInsertAndUpdate() {
        java.util.Map<String, Object> values = new java.util.LinkedHashMap<>();
        values.put("NAME", "John");
        values.put("AGE", 20);

        InsertBuilder insertBuilder = new InsertBuilder(new OracleDialect());
        Query insertQuery = insertBuilder.into("USERS").values(values).build();
        assertEquals("INSERT INTO USERS (\"NAME\", \"AGE\") VALUES (?, ?)", insertQuery.getStatement());

        UpdateBuilder updateBuilder = new UpdateBuilder(new OracleDialect());
        Query updateQuery = updateBuilder.table("USERS").set(values).where(Expression.eq("ID", 1)).build();
        assertEquals("UPDATE USERS SET \"NAME\" = ?, \"AGE\" = ? WHERE \"ID\" = ?", updateQuery.getStatement());
    }

    @Test
    void testInsertBuilderMissingTable() {
        InsertBuilder builder = new InsertBuilder(new OracleDialect());
        builder.value("NAME", "John");
        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    void testInsertBuilderMissingValues() {
        InsertBuilder builder = new InsertBuilder(new OracleDialect());
        builder.into("USERS");
        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    void testUpdateBuilderMissingTable() {
        UpdateBuilder builder = new UpdateBuilder(new OracleDialect());
        builder.set("NAME", "John");
        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    void testUpdateBuilderMissingValues() {
        UpdateBuilder builder = new UpdateBuilder(new OracleDialect());
        builder.table("USERS");
        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    void testDeleteBuilderMissingTable() {
        DeleteBuilder builder = new DeleteBuilder(new OracleDialect());
        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    void testInsertBuilderEmptyTableName() {
        InsertBuilder builder = new InsertBuilder(new OracleDialect());
        assertThrows(sqlbuilder.exceptions.ValueCannotBeEmptyException.class, () -> builder.into(""));
    }

    @Test
    void testUpdateBuilderEmptyColumn() {
        UpdateBuilder builder = new UpdateBuilder(new OracleDialect());
        assertThrows(sqlbuilder.exceptions.ValueCannotBeEmptyException.class, () -> builder.set("", "value"));
    }
}
