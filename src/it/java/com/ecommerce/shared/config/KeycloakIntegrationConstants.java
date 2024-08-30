package com.ecommerce.shared.config;

public interface KeycloakIntegrationConstants {

    String KEYCLOAK_IMAGE = "quay.io/keycloak/keycloak"; // the same as in the compose file
    String KEYCLOAK_IMAGE_VERSION = "22.0.1";
    String KEYCLOAK_REALM = "testing-realm";
    String KEYCLOAK_ADMIN_USERNAME = "admin_user";
    String KEYCLOAK_ADMIN_PASSWORD = "admin_pwd";
    String KEYCLOAK_USER_ROLE = "user";
    String KEYCLOAK_CLIENT_ID = "ecommerce-app-id";
}
