package sqlbuilder.dialects;

/**
 * H2-specific SQL dialect implementation.
 */
public class H2Dialect implements SqlDialect {
    @Override
    public String applyPaging(int limit, int offset) {
        return "LIMIT " + limit + " OFFSET " + offset;
    }
}
