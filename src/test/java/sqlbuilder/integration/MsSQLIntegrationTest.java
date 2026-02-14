package sqlbuilder.integration;

import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import sqlbuilder.dialects.MsSQLDialect;
import sqlbuilder.dialects.SqlDialect;

@Testcontainers
public class MsSQLIntegrationTest extends BaseIntegrationTest {

    @Container
    private static final MSSQLServerContainer<?> mssql = new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2017-latest")
            .acceptLicense();

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
