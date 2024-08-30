package com.ecommerce.shared;

import com.ecommerce.shared.domain.Credentials;

import static com.ecommerce.IntegrationTestData.PASSWORD;

public class CredentialsAbility {

    public static Credentials credentials(String username) {
        return Credentials.builder()
                .username(username)
                .password(PASSWORD)
                .build();
    }
}
