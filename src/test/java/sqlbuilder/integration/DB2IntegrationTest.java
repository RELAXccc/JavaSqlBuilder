package sqlbuilder.integration;

import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.Db2Container;
import sqlbuilder.dialects.DB2Dialect;
import sqlbuilder.dialects.SqlDialect;
import sqlbuilder.integration.util.DockerCheck;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class DB2IntegrationTest extends BaseIntegrationTest {

    private static Db2Container db2;

    @BeforeAll
    static void startContainer() {
        assumeTrue(DockerCheck.isDockerAvailable(), "Docker is not available, skipping DB2 integration test");
        db2 = new Db2Container("icr.io/db2_community/db2:11.5.9.0")
                .acceptLicense();
        db2.start();
    }

    @Override
    protected String getJdbcUrl() {
        return db2.getJdbcUrl();
    }

    @Override
    protected String getUsername() {
        return db2.getUsername();
    }

    @Override
    protected String getPassword() {
        return db2.getPassword();
    }

    @Override
    protected SqlDialect createDialect() {
        return new DB2Dialect();
    }
}
