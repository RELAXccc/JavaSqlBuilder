package sqlbuilder.integration;

import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.OracleContainer;
import sqlbuilder.dialects.OracleDialect;
import sqlbuilder.dialects.SqlDialect;
import sqlbuilder.integration.util.DockerCheck;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class OracleIntegrationTest extends BaseIntegrationTest {

    private static OracleContainer oracle;

    @BeforeAll
    static void startContainer() {
        assumeTrue(DockerCheck.isDockerAvailable(), "Docker is not available, skipping Oracle integration test");
        oracle = new OracleContainer("gvenzl/oracle-free:latest");
        oracle.start();
    }

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
