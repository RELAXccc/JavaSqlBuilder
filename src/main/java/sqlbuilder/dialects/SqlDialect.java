package sqlbuilder.dialects;

import java.util.Set;

/**
 * Interface representing a SQL dialect, providing database-specific syntax.
 */
public interface SqlDialect {
    /**
     * Quotes a database identifier (table or column name).
     *
     * @param identifier the identifier to quote
     * @return the quoted identifier
     */
    default String quote(String identifier) {
        if (identifier == null || identifier.isBlank() || identifier.equals("*") || identifier.contains("(")) {
            return identifier;
        }
        if (identifier.contains(".")) {
            String[] parts = identifier.split("\\.");
            StringBuilder quoted = new StringBuilder();
            for (int i = 0; i < parts.length; i++) {
                if (i > 0) quoted.append(".");
                quoted.append(quote(parts[i]));
            }
            return quoted.toString();
        }
        return "\"" + identifier + "\"";
    }

    /**
     * Formats a table identifier based on the provided table context.
     *
     * @param tableIdentifier the table identifier to format
     * @param tableContext    the set of table names in the current context
     * @return the formatted table identifier
     */
    default String formatTableIdentifier(String tableIdentifier, Set<String> tableContext) {
        return tableIdentifier;
    }

    /**
     * Applies paging with an offset to the SQL statement using the dialect specific syntax.
     *
     * @param limit  The limit of how many entries the SQL statement will return
     * @param offset The offset at which the limit counting starts
     * @return the paging statement
     */
    String applyPaging(int limit, int offset);
}
