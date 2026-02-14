package sqlbuilder.dialects;

/**
 * Oracle-specific SQL dialect implementation.
 */
public class OracleDialect implements SqlDialect {
    @Override
    public String applyPaging(int limit, int offset) {
        return "LIMIT " + limit + " OFFSET " + offset;
    }
}
