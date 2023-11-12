package com.violetbeach.sharding.web;

import com.violetbeach.sharding.database.DBContextHolder;
import com.violetbeach.sharding.database.DbInfo;
import com.violetbeach.sharding.database.MultiDataSourceManager;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class ShardingFilter extends OncePerRequestFilter {
    private final MultiDataSourceManager multiDataSourceManager;

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        DbInfo dbInfo = JwtParser.getDbInfoByRequest(request);
        DBContextHolder.setDbInfo(dbInfo);

        // DataSource가 존재하지 않을 경우에 새로 생성해준다.
        multiDataSourceManager.addDataSourceIfAbsent(dbInfo.ip());

        try {
            filterChain.doFilter(request, response);
        } finally {
            // ThreadPool을 사용하기 때문에 다른 요청이 재사용할 수 없도록 반드시 clear()를 호출해야 한다.
            DBContextHolder.clear();
        }
    }

}