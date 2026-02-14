package sqlbuilder.integration;

import org.testcontainers.containers.OracleContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import sqlbuilder.dialects.OracleDialect;
import sqlbuilder.dialects.SqlDialect;

@Testcontainers
public class OracleIntegrationTest extends BaseIntegrationTest {

    @Container
    private static final OracleContainer oracle = new OracleContainer("gvenzl/oracle-free:latest");

    @Override
    protected String getJdbcUrl() {
        return oracle.getJdbcUrl();
    }

    @Override
    protected String getUsername() {
        return oracle.getUsername();
    }

    @Override
    protected String getPassword() {
        return oracle.getPassword();
    }

    @Override
    protected SqlDialect createDialect() {
        return new OracleDialect();
    }
}
