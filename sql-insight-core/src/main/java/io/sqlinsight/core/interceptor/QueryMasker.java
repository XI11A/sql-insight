package io.sqlinsight.core.interceptor;

import java.util.regex.Pattern;

public class QueryMasker {
    // Escaped string literal patterns
    private static final Pattern STRING_LITERAL = Pattern.compile("'(?:[^']|'')*'");
    // Safe number matching for typical inline params 
    private static final Pattern NUMBER_LITERAL = Pattern.compile("(?<=[=<>*,\\s\\(])-?\\d+(?:\\.\\d+)?\\b");

    public static String mask(String sql) {
        if (sql == null) return null;
        
        String masked = STRING_LITERAL.matcher(sql).replaceAll("?");
        masked = NUMBER_LITERAL.matcher(masked).replaceAll("?");
        
        return masked;
    }
}
