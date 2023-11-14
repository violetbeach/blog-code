package com.violetbeach.sharding.module.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataSourceCreator {
    private final DBProperties dbProperties;

    public DataSource generateDataSource(String ip) {
        HikariConfig hikariConfig = initConfig(ip);
        return new HikariDataSource(hikariConfig);
    }

    public DataSource defaultDataSource() {
        String defaultHostIp = dbProperties.getDefaultHostIp();
        String defaultHostPartition = dbProperties.getDefaultPartition();

        HikariConfig hikariConfig = initConfig(defaultHostIp);
        HikariDataSource datasource = new HikariDataSource(hikariConfig);
        // JPA 엔터티 로딩 시 파티션 저장 필요
        DBContextHolder.setDbInfo(new DbInfo(defaultHostIp, defaultHostPartition));
        return datasource;
    }

    private HikariConfig initConfig(String hostIp) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(getConnectionString(hostIp));
        hikariConfig.setUsername(dbProperties.getUsername());
        hikariConfig.setPassword(dbProperties.getPassword());
        hikariConfig.setDriverClassName(dbProperties.getDriver());
        return hikariConfig;
    }

    public String getConnectionString(String hostname) {
        StringBuilder sb = new StringBuilder()
            .append("jdbc:mysql://")
            .append(hostname)
            .append(":").append(dbProperties.getPort())
            .append("/").append(dbProperties.getDefaultSchema());
        return sb.toString();
    }
}
