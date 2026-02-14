package sqlbuilder.integration;

import org.testcontainers.containers.Db2Container;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import sqlbuilder.dialects.DB2Dialect;
import sqlbuilder.dialects.SqlDialect;

@Testcontainers
public class DB2IntegrationTest extends BaseIntegrationTest {

    @Container
    private static final Db2Container db2 = new Db2Container("icr.io/db2_community/db2:11.5.9.0")
            .acceptLicense();

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
