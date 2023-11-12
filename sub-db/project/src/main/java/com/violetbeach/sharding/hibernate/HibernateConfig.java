package com.violetbeach.sharding.hibernate;

import lombok.RequiredArgsConstructor;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class HibernateConfig {
    private final PartitionInspector partitionInspector;

    @Bean
    public HibernatePropertiesCustomizer configureStatementInspector() {
        return (properties) -> properties.put(AvailableSettings.STATEMENT_INSPECTOR, partitionInspector);
    }

}
