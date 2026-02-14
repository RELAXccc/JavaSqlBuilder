package sqlbuilder.integration;

import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.PostgreSQLContainer;
import sqlbuilder.dialects.PostgresDialect;
import sqlbuilder.dialects.SqlDialect;
import sqlbuilder.integration.util.DockerCheck;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class PostgresIntegrationTest extends BaseIntegrationTest {

    private static PostgreSQLContainer<?> postgres;

    @BeforeAll
    static void startContainer() {
        assumeTrue(DockerCheck.isDockerAvailable(), "Docker is not available, skipping Postgres integration test");
        postgres = new PostgreSQLContainer<>("postgres:16-alpine");
        postgres.start();
    }

    @Override
    protected String getJdbcUrl() {
        return postgres.getJdbcUrl();
    }

    @Override
    protected String getUsername() {
        return postgres.getUsername();
    }

    @Override
    protected String getPassword() {
        return postgres.getPassword();
    }

    @Override
    protected SqlDialect createDialect() {
        return new PostgresDialect();
    }
}
