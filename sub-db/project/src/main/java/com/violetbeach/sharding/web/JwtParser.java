package com.violetbeach.sharding.web;

import com.violetbeach.sharding.database.DbInfo;
import javax.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;

/**
 * Jwt에서 ip를 추출하는 클래스
 * 임시로 헤더 값을 사용해서 추출
 */
@UtilityClass
public class JwtParser {

    public DbInfo getDbInfoByRequest(HttpServletRequest request) {
        String ip = request.getHeader("ip");
        String partition = request.getHeader("partition");
        return new DbInfo(ip, partition);
    }

}
