package com.ecommerce.infrastructure;

import com.ecommerce.user.domain.port.UserRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.DisableEncodeUrlFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserRepositoryPort userRepository, SessionRegistry sessionRegistry) throws Exception {
        http.addFilterBefore(new SessionToBearerTokenFilter(sessionRegistry), DisableEncodeUrlFilter.class);
        http.csrf(CsrfConfigurer::disable);
        return http.authorizeHttpRequests(customizer ->
                        customizer
                                .requestMatchers(
                                        new AntPathRequestMatcher("/", "GET"),
                                        new AntPathRequestMatcher("/api/authenticate", "POST"), // authenticate user
                                        new AntPathRequestMatcher("/api/users", "POST"), // create user
                                        new AntPathRequestMatcher("/api/offers/search", "POST"), // search for offers
                                        new AntPathRequestMatcher("/api/offers/*", "GET") // get offer details
                                )
                                .permitAll()
                                .requestMatchers("/api/**")
                                .authenticated()
                                .anyRequest()
                                .permitAll()
                )
                .oauth2ResourceServer(resourceServer ->
                        resourceServer.jwt(
                                jwt -> jwt.jwtAuthenticationConverter(
                                        new KeycloakJwtTokenConverter(
                                                new JwtGrantedAuthoritiesConverter(),
                                                userRepository
                                        )
                                )
                        )
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                ).build();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
}