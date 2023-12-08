package com.violetbeach.useindex.inspector;

import com.violetbeach.useindex.entity.MemberFiltering;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.resource.jdbc.spi.StatementInspector;

/**
 * 해당 클래스의 구현은 Hibernate 5.6의 Interceptor에 의존한다.
 */
public class IndexHintInspector implements StatementInspector {
    private static final String ALIAS = StringHelper.generateAlias(MemberFiltering.class.getName(), 0);
    private static final String TARGET = "member " + ALIAS;
    private static final Pattern PATTERN = Pattern.compile(TARGET);
    private static final String INDEX_HINT = " use index (ix_username) ";

    @Override
    public String inspect(String sql) {
        Matcher matcher = PATTERN.matcher(sql);
        if(matcher.find()) {
            String group = matcher.group();
            return sql.replace(group, group + INDEX_HINT);
        }
        return sql;
    }
}
