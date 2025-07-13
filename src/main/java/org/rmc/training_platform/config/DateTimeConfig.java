package org.rmc.training_platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class DateTimeConfig {

    @Bean
    Clock clock() {
        return Clock.systemDefaultZone();
    }

}
