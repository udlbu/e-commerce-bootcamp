package com.ecommerce.user.api;

import com.ecommerce.BaseIntegrationTest;
import com.ecommerce.shared.api.dto.Errors;
import com.ecommerce.user.api.dto.AddUserAddress;
import com.ecommerce.user.api.dto.AddUserRequest;
import com.ecommerce.user.api.dto.CredentialsRequest;
import com.ecommerce.user.api.dto.UserResponse;
import com.ecommerce.user.domain.model.authentication.Principal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;

import java.util.List;

import static com.ecommerce.IntegrationTestData.EMAIL;
import static com.ecommerce.IntegrationTestData.PASSWORD;
import static com.ecommerce.infrastructure.Constants.X_SESSION_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthenticationEndpointTest extends BaseIntegrationTest {

    @Autowired
    private SessionRegistry sessionRegistry;

    @Test
    @DisplayName("should authenticate user and save authentication in related http session in order to use it to access protected resource")
    public void shouldAuthenticateUserAndSaveAuthenticationInRelatedHttpSession() {

        // given
        CredentialsRequest request = new CredentialsRequest(EMAIL, PASSWORD);

        // when
        ResponseEntity<Void> authenticateResponse = authenticationAbility().authenticate(request);

        // then
        assertTrue(authenticateResponse.getStatusCode().is2xxSuccessful());

        // and when use authenticated session to fetch current user (protected resource)
        ResponseEntity<UserResponse> currentUser = userAbility().getCurrentUser(xSessionIdCookie(authenticateResponse));

        // then
        assertEquals(HttpStatus.OK, currentUser.getStatusCode());
    }

    @Test
    @DisplayName("should not authenticate user when newly created account had not been activated")
    public void shouldNotAuthenticateUserWhenAccountIsInactive() {

        // given
        String ADDED_USER_EMAIL = "ann.fox@mail.com";
        String ADDED_USER_FIRST_NAME = "Ann";
        String ADDED_USER_LAST_NAME = "Fox";
        String ADDED_USER_TAX_ID = "TAX_12345";
        String ADDED_USER_COUNTRY = "Poland";
        String ADDED_USER_CITY = "Warsaw";
        String ADDED_USER_STREET = "Komisji Edukacji Narodowej";
        String ADDED_USER_BUILDING_NO = "101";
        String ADDED_USER_FLAT_NO = "23b";
        String ADDED_USER_POSTAL_CODE = "02-791";
        AddUserRequest userRequest = new AddUserRequest(
                ADDED_USER_EMAIL,
                PASSWORD,
                ADDED_USER_FIRST_NAME,
                ADDED_USER_LAST_NAME,
                ADDED_USER_TAX_ID,
                new AddUserAddress(
                        ADDED_USER_COUNTRY,
                        ADDED_USER_CITY,
                        ADDED_USER_STREET,
                        ADDED_USER_BUILDING_NO,
                        ADDED_USER_FLAT_NO,
                        ADDED_USER_POSTAL_CODE
                )
        );
        ResponseEntity<UserResponse> response = userAbility().addUser(userRequest);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        CredentialsRequest request = new CredentialsRequest("ann.fox@mail.com", PASSWORD);

        // when
        ResponseEntity<Errors> authenticateResponse = authenticationAbility().authenticateError(request);

        // then
        assertEquals(HttpStatus.FORBIDDEN, authenticateResponse.getStatusCode());
        assertEquals("Account <ann.fox@mail.com> is inactive", authenticateResponse.getBody().getErrors().get(0).getMessage());
    }

    @Test
    @DisplayName("should store only one user's authenticated session")
    public void shouldStoreOnlyOneUserAuthenticatedSession() {

        // given already authenticated user
        ResponseEntity<Void> authenticateResponse = authenticationAbility().authenticate(new CredentialsRequest(EMAIL, PASSWORD));
        assertTrue(authenticateResponse.getStatusCode().is2xxSuccessful());

        // when
        ResponseEntity<Void> authenticateResponseFromAnotherBrowser = authenticationAbility().authenticate(new CredentialsRequest(EMAIL, PASSWORD));

        // then
        assertTrue(authenticateResponseFromAnotherBrowser.getStatusCode().is2xxSuccessful());

        // and
        assertEquals(1, sessionRegistry.getAllPrincipals().size());
        Principal principal = (Principal) sessionRegistry.getAllPrincipals().get(0);
        assertEquals(EMAIL, principal.email());
        assertNotNull(principal.accessToken());
        List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, true);
        assertEquals(1, sessions.size());
        assertEquals(xSessionIdCookie(authenticateResponseFromAnotherBrowser), X_SESSION_ID + "=" + sessions.get(0).getSessionId());
    }

    @Test
    @DisplayName("should return UNAUTHORIZED error code when user entered invalid password during authentication")
    public void shouldReturnUnauthorizedWhenUserEnteredInvalidPasswordDuringAuthentication() {

        // given
        CredentialsRequest request = new CredentialsRequest(EMAIL, "invalid_password");

        // when
        ResponseEntity<Void> authenticateResponse = authenticationAbility().authenticate(request);

        // then
        assertEquals(HttpStatus.UNAUTHORIZED, authenticateResponse.getStatusCode());
    }

    private static String xSessionIdCookie(ResponseEntity<Void> authenticateResponse) {
        return authenticateResponse.getHeaders().get("Set-Cookie").get(0).split(";")[0];
    }
}
