package com.ecommerce.user.config;

import com.ecommerce.product.config.CdnProperties;
import com.ecommerce.user.adapter.keycloak.KeycloakIAMAdapter;
import com.ecommerce.user.domain.port.IAMPort;
import com.ecommerce.user.domain.port.UserRepositoryPort;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({IAMProperties.class, CdnProperties.class})
public class KeycloakConfig {

    @Bean
    public Keycloak keycloak(IAMProperties properties) {
        return KeycloakBuilder.builder()
                .serverUrl(properties.getKeycloakServer())
                .realm(properties.getAdmin().getRealm())
                .clientId(properties.getAdmin().getClientId())
                .grantType(OAuth2Constants.PASSWORD)
                .username(properties.getAdmin().getUsername())
                .password(properties.getAdmin().getPassword())
                .build();
    }

    @Bean
    public IAMPort iam(Keycloak keycloak, IAMProperties properties, UserRepositoryPort userRepository) {
        return new KeycloakIAMAdapter(keycloak, properties, userRepository);
    }
}
