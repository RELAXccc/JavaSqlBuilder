package sqlbuilder.dialects;

/**
 * Microsoft SQL Server-specific SQL dialect implementation.
 */
public class MsSQLDialect implements SqlDialect {
    @Override
    public String applyPaging(int limit, int offset) {
        //TODO implement paging syntax
        return "";
    }
}
