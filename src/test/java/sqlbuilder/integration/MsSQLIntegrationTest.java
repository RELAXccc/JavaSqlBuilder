package sqlbuilder.integration;

import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.MSSQLServerContainer;
import sqlbuilder.dialects.MsSQLDialect;
import sqlbuilder.dialects.SqlDialect;
import sqlbuilder.integration.util.DockerCheck;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class MsSQLIntegrationTest extends BaseIntegrationTest {

    private static MSSQLServerContainer<?> mssql;

    @BeforeAll
    static void startContainer() {
        assumeTrue(DockerCheck.isDockerAvailable(), "Docker is not available, skipping MsSQL integration test");
        mssql = new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2017-latest")
                .acceptLicense();
        mssql.start();
    }

    @Override
    protected String getJdbcUrl() {
        return mssql.getJdbcUrl();
    }

    @Override
    protected String getUsername() {
        return mssql.getUsername();
    }

    @Override
    protected String getPassword() {
        return mssql.getPassword();
    }

    @Override
    protected SqlDialect createDialect() {
        return new MsSQLDialect();
    }
}
