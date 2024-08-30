package com.ecommerce.user;

import com.ecommerce.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ActivateUserEndpointTest extends BaseIntegrationTest {

    @Test
    @DisplayName("should activation redirect user on the main page with token as query param")
    public void shouldActivateUser() {
        // when
        ResponseEntity<String> response = userActivationAbility().activateUser("123");

        // then
        assertTrue(response.getStatusCode().is3xxRedirection());
        assertTrue(response.getHeaders().getLocation().getPath().equals("/activated"));
    }
}
