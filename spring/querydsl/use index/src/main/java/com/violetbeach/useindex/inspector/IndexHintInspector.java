package com.violetbeach.useindex.inspector;

import org.hibernate.resource.jdbc.spi.StatementInspector;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 해당 클래스의 구현은 Hibernate 5.6의 Interceptor에 의존한다.
 */
public class IndexHintInspector implements StatementInspector {
    private static final String START_IDENTIFIER = "@@IndexHint_START@@";
    private static final String END_IDENTIFIER = "@@IndexHint_End@@";
    private static final Pattern PATTERN = Pattern.compile(START_IDENTIFIER + "(.+)" + END_IDENTIFIER, Pattern.CASE_INSENSITIVE);

    @Override
    public String inspect(String sql) {
        Matcher matcher = PATTERN.matcher(sql);
        if(matcher.find()) {
            String group = matcher.group(1);
            return sql.replaceAll("(?i)where", group + " where");
        }
        return sql;
    }

    public static String getInspectorIndexHint(String indexHint) {
        return START_IDENTIFIER + indexHint + END_IDENTIFIER;
    }
}
