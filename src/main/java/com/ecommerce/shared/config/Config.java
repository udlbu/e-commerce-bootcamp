package com.ecommerce.shared.config;

import com.ecommerce.shared.domain.DefaultTimeProvider;
import com.ecommerce.shared.domain.TimeProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public TimeProvider timeProvider() {
        return new DefaultTimeProvider();
    }
}
