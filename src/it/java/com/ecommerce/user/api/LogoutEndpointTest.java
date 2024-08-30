package com.ecommerce.user.api;

import com.ecommerce.BaseIntegrationTest;
import com.ecommerce.user.api.dto.CredentialsRequest;
import com.ecommerce.user.api.dto.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.session.SessionRegistry;

import static com.ecommerce.IntegrationTestData.EMAIL;
import static com.ecommerce.IntegrationTestData.PASSWORD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LogoutEndpointTest extends BaseIntegrationTest {

    @Autowired
    private SessionRegistry sessionRegistry;

    @Test
    @DisplayName("should return UNAUTHORIZED when user logged out and tried to access protected resource")
    public void shouldReturnUnauthorizedWhenUserLoggedOutAndTriedToAccessProtectedResource() {

        // given already authenticated user
        ResponseEntity<Void> authenticateResponse = authenticationAbility().authenticate(new CredentialsRequest(EMAIL, PASSWORD));
        assertTrue(authenticateResponse.getStatusCode().is2xxSuccessful());
        String authenticatedSessionId = sessionCookie(authenticateResponse);
        userAbility().assertThatUserSessionExists(EMAIL);

        // and user can access to protected resource
        ResponseEntity<UserResponse> currentUser = userAbility().getCurrentUser(authenticatedSessionId);
        assertEquals(HttpStatus.OK, currentUser.getStatusCode());

        // when
        ResponseEntity<Void> logoutResponse = authenticationAbility().logout(authenticatedSessionId);

        // then
        assertEquals(HttpStatus.OK, logoutResponse.getStatusCode());

        // and
        ResponseEntity<UserResponse> currentUserUnauthorized = userAbility().getCurrentUser(sessionCookie(authenticateResponse));
        assertEquals(HttpStatus.UNAUTHORIZED, currentUserUnauthorized.getStatusCode());
        userAbility().assertThatUserSessionDoesNotExist(EMAIL);
    }

    @Test
    @DisplayName("should return UNAUTHORIZED when unauthorized user invoked logout endpoint")
    public void shouldReturnUnauthorizedWhenUnauthorizedUserInvokedLogoutEndpoint() {

        // given already authenticated user
        ResponseEntity<Void> authenticateResponse = authenticationAbility().authenticate(new CredentialsRequest(EMAIL, PASSWORD));
        assertTrue(authenticateResponse.getStatusCode().is2xxSuccessful());
        String authenticatedSessionId = sessionCookie(authenticateResponse);

        // who already log out
        ResponseEntity<Void> logoutResponse = authenticationAbility().logout(authenticatedSessionId);
        assertEquals(HttpStatus.OK, logoutResponse.getStatusCode());

        // when user is trying to log out one again
        ResponseEntity<Void> logoutResponseDuplicate = authenticationAbility().logout(authenticatedSessionId);

        // then
        assertEquals(HttpStatus.UNAUTHORIZED, logoutResponseDuplicate.getStatusCode());
    }
}
