package com.violetbeach.sharding.hibernate;

import lombok.RequiredArgsConstructor;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.validation.Schema;

@Configuration
@RequiredArgsConstructor
public class HibernateConfig {
    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer() {
        return (properties) -> {
            properties.put(AvailableSettings.STATEMENT_INSPECTOR, new PartitionInspector());
            properties.put(AvailableSettings.PHYSICAL_NAMING_STRATEGY, new SchemaNamingStrategy());
        };
    }

}
