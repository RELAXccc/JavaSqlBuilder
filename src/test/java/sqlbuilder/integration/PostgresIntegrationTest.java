package sqlbuilder.integration;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import sqlbuilder.dialects.PostgresDialect;
import sqlbuilder.dialects.SqlDialect;

@Testcontainers
public class PostgresIntegrationTest extends BaseIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

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
