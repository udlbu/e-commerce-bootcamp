package com.ecommerce.user.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "iam")
@Data
public class IAMProperties {
    private String keycloakServer;
    private String realm;
    private IAMAdmin admin = new IAMAdmin();

    @Data
    public static class IAMAdmin {
        private String realm;
        private String clientId;
        private String username;
        private String password;
    }
}