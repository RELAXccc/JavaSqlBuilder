package sqlbuilder.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import sqlbuilder.Query;
import sqlbuilder.dialects.SqlDialect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AbstractDatabaseTest {

    protected Connection connection;
    protected SqlDialect dialect;

    protected abstract String getJdbcUrl();
    protected abstract String getUsername();
    protected abstract String getPassword();
    protected abstract SqlDialect createDialect();

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection(getJdbcUrl(), getUsername(), getPassword());
        dialect = createDialect();
        createSchema();
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    private void createSchema() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            try {
                stmt.execute("DROP TABLE users");
            } catch (SQLException e) {
                // Ignore if table doesn't exist
            }
            
            String id = dialect.quote("id");
            String name = dialect.quote("name");
            String email = dialect.quote("email");
            
            stmt.execute("CREATE TABLE users (" + id + " INT PRIMARY KEY, " + name + " VARCHAR(100), " + email + " VARCHAR(100))");
        }
    }

    protected void execute(Query query) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(query.getStatement())) {
            List<Object> params = query.getParameters();
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            pstmt.execute();
        }
    }

    protected List<Map<String, Object>> query(Query query) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(query.getStatement())) {
            List<Object> params = query.getParameters();
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                List<Map<String, Object>> results = new ArrayList<>();
                int columnCount = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    java.util.Map<String, Object> row = new java.util.HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(rs.getMetaData().getColumnName(i).toLowerCase(), rs.getObject(i));
                    }
                    results.add(row);
                }
                return results;
            }
        }
    }
}
