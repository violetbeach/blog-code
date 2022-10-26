package study.datajpa.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import study.datajpa.common.Events;

@Configuration
@RequiredArgsConstructor
public class EventConfiguration {
    private final ApplicationContext applicationContext;

    @Bean
    public InitializingBean eventsInitializer() {
        return () -> Events.setPublisher(applicationContext);
    }
}
