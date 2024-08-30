package com.ecommerce.shared.config;

import com.ecommerce.mail.domain.ActivationEmailSender;
import com.ecommerce.shared.FixedTimeProvider;
import com.ecommerce.shared.TestDataCleaner;
import com.ecommerce.shared.domain.TimeProvider;
import com.ecommerce.user.config.IAMProperties;
import org.keycloak.admin.client.Keycloak;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class Config {

    @Bean
    @Profile("integration")
    public TimeProvider fixedTimeProvider() {
        return new FixedTimeProvider();
    }

    @Bean
    @Profile("integration")
    public TestDataCleaner databaseCleaner(JdbcTemplate jdbcTemplate, Keycloak keycloakApi, IAMProperties properties) {
        return new TestDataCleaner(jdbcTemplate, keycloakApi, properties);
    }

    @Bean
    @Profile("integration")
    public ActivationEmailSender activationEmailSender() {
        return new InMemoryActivationEmailSender();
    }
}
