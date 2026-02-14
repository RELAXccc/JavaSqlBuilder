package sqlbuilder.integration;

import org.junit.jupiter.api.Test;
import sqlbuilder.InsertBuilder;
import sqlbuilder.Query;
import sqlbuilder.SelectBuilder;
import sqlbuilder.expressions.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class BaseIntegrationTest extends AbstractDatabaseTest {

    @Test
    void testInsertAndSelect() throws SQLException {
        // 1. Insert data using framework
        InsertBuilder insert = new InsertBuilder(dialect)
                .into("users")
                .value("id", 1)
                .value("name", "John Doe")
                .value("email", "john@example.com");
        
        execute(insert.build());

        InsertBuilder insert2 = new InsertBuilder(dialect)
                .into("users")
                .value("id", 2)
                .value("name", "Jane Doe")
                .value("email", "jane@example.com");
        
        execute(insert2.build());

        // 2. Select data using framework
        SelectBuilder select = new SelectBuilder(dialect)
                .from("users")
                .select("id", "name", "email")
                .where(Expression.eq("id", 1));
        
        List<Map<String, Object>> results = query(select.build());

        assertEquals(1, results.size());
        assertEquals(1, ((Number) results.get(0).get("id")).intValue());
        assertEquals("John Doe", results.get(0).get("name"));
        assertEquals("john@example.com", results.get(0).get("email"));
    }

    @Test
    void testComplexWhere() throws SQLException {
        execute(new InsertBuilder(dialect).into("users").value("id", 3).value("name", "Alice").value("email", "alice@example.com").build());
        execute(new InsertBuilder(dialect).into("users").value("id", 4).value("name", "Bob").value("email", "bob@example.com").build());

        SelectBuilder select = new SelectBuilder(dialect)
                .from("users")
                .select("name")
                .where(Expression.eq("id", 3).or().eq("name", "Bob"));

        List<Map<String, Object>> results = query(select.build());

        assertEquals(2, results.size());
    }
}
