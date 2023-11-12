package com.violetbeach.sharding.database;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@Getter
@ConstructorBinding
@ConfigurationProperties("db-properties")
@RequiredArgsConstructor
@Validated
public class DBProperties {
    private final String defaultHostIp;
    @NotBlank(message = "db.default-partition은 필수 값입니다.")
    private final String defaultPartition;
    @NotBlank(message = "db.default-schema 필수 값입니다.")
    private final String defaultSchema;
    private final String username;
    private final String password;
    private final String driver;
    private final int port = 3306;
}
