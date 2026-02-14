package sqlbuilder.dialects;

/**
 * PostgreSQL-specific SQL dialect implementation.
 */
public class PostgresDialect implements SqlDialect {
    @Override
    public String applyPaging(int limit, int offset) {
        return "LIMIT " + limit + " OFFSET " + offset;
    }
}
