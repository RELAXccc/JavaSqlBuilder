package sqlbuilder.dialects;

import sqlbuilder.exceptions.ValueCannotBeEmptyException;
import java.util.Set;

/**
 * DB2-specific SQL dialect implementation.
 */
public class DB2Dialect implements SqlDialect {
    @Override
    public String applyPaging(int limit, int offset) {
        //TODO implement paging syntax
        return "";
    }

    @Override
    public String formatTableIdentifier(String tableIdentifier, Set<String> tableContext) {
        if (tableIdentifier == null || tableIdentifier.isBlank()) {
            throw new ValueCannotBeEmptyException("tableIdentifier");
        }

        for (String tableName : tableContext) {
            String prefix = tableName + ".";

            if (tableIdentifier.startsWith(prefix)) {
                return tableIdentifier.substring(prefix.length());
            }
        }

        return tableIdentifier;
    }
}
