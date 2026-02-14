package sqlbuilder.integration;

import sqlbuilder.dialects.H2Dialect;
import sqlbuilder.dialects.SqlDialect;

public class H2IntegrationTest extends BaseIntegrationTest {

    @Override
    protected String getJdbcUrl() {
        return "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    }

    @Override
    protected String getUsername() {
        return "sa";
    }

    @Override
    protected String getPassword() {
        return "";
    }

    @Override
    protected SqlDialect createDialect() {
        return new H2Dialect();
    }
}
