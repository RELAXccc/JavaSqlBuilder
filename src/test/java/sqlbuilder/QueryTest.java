package sqlbuilder;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QueryTest {
    @Test
    void testQueryGetters() {
        Query query = new Query("SELECT * FROM users WHERE id = ?");
        query.addParameter(1);
        
        assertEquals("SELECT * FROM users WHERE id = ?", query.getStatement());
        assertEquals(1, query.getParameters().size());
        assertEquals(1, query.getParameters().get(0));
    }

    @Test
    void testExecuteReturnsNull() {
        Query query = new Query("SELECT 1");
        assertNull(query.execute());
    }
}
