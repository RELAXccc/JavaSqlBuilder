package sqlbuilder;

import org.junit.jupiter.api.Test;
import sqlbuilder.dialects.SqlDialect;
import sqlbuilder.exceptions.ValueCannotBeEmptyException;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DialectTest {

    @Test
    void testPostgresDialect() {
        SqlDialect dialect = new SqlDialect.PostgresDialect();
        assertEquals("LIMIT 10 OFFSET 5", dialect.applyPaging(10, 5));
        assertEquals("\"user\"", dialect.quote("user"));
    }

    @Test
    void testOracleDialect() {
        SqlDialect dialect = new SqlDialect.OracleDialect();
        assertEquals("LIMIT 10 OFFSET 5", dialect.applyPaging(10, 5));
    }

    @Test
    void testH2Dialect() {
        SqlDialect dialect = new SqlDialect.H2Dialect();
        assertEquals("LIMIT 10 OFFSET 5", dialect.applyPaging(10, 5));
    }

    @Test
    void testMsSQLDialect() {
        SqlDialect dialect = new SqlDialect.MsSQLDialect();
        assertEquals("", dialect.applyPaging(10, 5)); // Current placeholder behavior
    }

    @Test
    void testDB2Dialect() {
        SqlDialect dialect = new SqlDialect.DB2Dialect();
        assertEquals("", dialect.applyPaging(10, 5));
        
        Set<String> context = Set.of("users");
        assertEquals("id", dialect.formatTableIdentifier("users.id", context));
        assertEquals("other.id", dialect.formatTableIdentifier("other.id", context));
    }

    @Test
    void testDB2DialectExceptions() {
        SqlDialect.DB2Dialect dialect = new SqlDialect.DB2Dialect();
        assertThrows(ValueCannotBeEmptyException.class, () -> dialect.formatTableIdentifier("", Collections.emptySet()));
    }
}
