package com.violetbeach.sharding.module.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

@Slf4j
@Configuration
public class MultiDataSourceManager {
    // 동시성을 보장해야 하므로 ConcurrentHashMap을 사용한다.
    private final Map<Object, Object> dataSourceMap = new ConcurrentHashMap<>();

    private AbstractRoutingDataSource multiDataSource;
    private final DataSourceCreator dataSourceCreator;

    public MultiDataSourceManager(DataSourceCreator dataSourceCreator) {
        MultiDataSource multiDataSource = new MultiDataSource();
        // AbstractRoutingDataSource의 targetDataSource를 지정
        multiDataSource.setTargetDataSources(dataSourceMap);
        // Key 대상이 없을 경우 호출되는 DataBase 지정 (해당 프로젝트에서는 Key가 없으면 예외가 터지도록 설계)
        multiDataSource.setDefaultTargetDataSource(dataSourceCreator.defaultDataSource());
        this.multiDataSource = multiDataSource;
        this.dataSourceCreator = dataSourceCreator;
    }

    @Bean
    public AbstractRoutingDataSource multiDataSource() {
        return multiDataSource;
    }

    public void addDataSourceIfAbsent(String ip) {
        if (!this.dataSourceMap.containsKey(ip)) {
            DataSource newDataSource = dataSourceCreator.generateDataSource(ip);
            try (Connection connection = newDataSource.getConnection()) {
                dataSourceMap.put(ip, newDataSource);
                multiDataSource.afterPropertiesSet();
            } catch (SQLException e) {
                log.error("datasource connection failed ip: {}", ip);
                throw new IllegalArgumentException("Connection failed.");
            }
        }
    }

}
