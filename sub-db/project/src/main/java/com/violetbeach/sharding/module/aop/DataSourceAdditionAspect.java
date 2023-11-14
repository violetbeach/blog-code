package com.violetbeach.sharding.module.aop;

import com.violetbeach.sharding.module.database.DBContextHolder;
import com.violetbeach.sharding.module.database.DbInfo;
import com.violetbeach.sharding.module.database.MultiDataSourceManager;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Aspect
@Component
@ConditionalOnBean(LoadDbInfoProcess.class)
@RequiredArgsConstructor
public class DataSourceAdditionAspect {
    private final LoadDbInfoProcess loadDbInfoProcess;
    private final MultiDataSourceManager multiDataSourceManager;

    @Around("@annotation(com.violetbeach.sharding.module.aop.Sharding)")
    public void execute(ProceedingJoinPoint joinPoint) throws Throwable {
        DbInfo dbInfo = loadDbInfoProcess.loadDbInfo();
        DBContextHolder.setDbInfo(dbInfo);
        // DataSource가 존재하지 않을 경우에 새로 생성해준다.
        try {
            multiDataSourceManager.addDataSourceIfAbsent(dbInfo.ip());
            joinPoint.proceed();
        } finally {
            DBContextHolder.clear();
        }
    }

}
