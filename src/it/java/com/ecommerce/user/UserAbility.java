package com.ecommerce.user;

import com.ecommerce.shared.HttpAbility;
import com.ecommerce.shared.api.dto.Errors;
import com.ecommerce.shared.domain.Credentials;
import com.ecommerce.user.api.dto.AddUserRequest;
import com.ecommerce.user.api.dto.ChangePasswordRequest;
import com.ecommerce.user.api.dto.ModifyUserRequest;
import com.ecommerce.user.api.dto.UserResponse;
import com.ecommerce.user.domain.model.Status;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.UserSessionRepresentation;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static com.ecommerce.shared.config.KeycloakIntegrationConstants.KEYCLOAK_REALM;
import static com.ecommerce.shared.config.KeycloakIntegrationConstants.KEYCLOAK_USER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserAbility extends HttpAbility {

    private final String URL = "/api/users";

    private final JdbcTemplate jdbcTemplate;

    private final Keycloak keycloakApi;

    public UserAbility(TestRestTemplate testRestTemplate, String authServerUrl, JdbcTemplate jdbcTemplate, Keycloak keycloakApi) {
        super(testRestTemplate, authServerUrl);
        this.jdbcTemplate = jdbcTemplate;
        this.keycloakApi = keycloakApi;
    }

    public ResponseEntity<UserResponse> getUser(Long id, Credentials credentials) {
        return testRestTemplate.exchange(URL + "/" + id, HttpMethod.GET, new HttpEntity<>(accept(credentials)), UserResponse.class);
    }

    public ResponseEntity<Errors> getUserUnauthorized(Long id, String invalidToken) {
        return testRestTemplate.exchange(URL + "/" + id, HttpMethod.GET, new HttpEntity<>(acceptUnauthorized(invalidToken)), Errors.class);
    }

    public ResponseEntity<UserResponse> getCurrentUser(Credentials credentials) {
        return testRestTemplate.exchange(URL + "/current", HttpMethod.GET, new HttpEntity<>(accept(credentials)), UserResponse.class);
    }

    public ResponseEntity<UserResponse> getCurrentUser(String sessionId) {
        return testRestTemplate.exchange(URL + "/current", HttpMethod.GET, new HttpEntity<>(accept(sessionId)), UserResponse.class);
    }

    public ResponseEntity<Errors> getCurrentUserUnauthorized(String invalidToken) {
        return testRestTemplate.exchange(URL + "/current", HttpMethod.GET, new HttpEntity<>(acceptUnauthorized(invalidToken)), Errors.class);
    }

    public ResponseEntity<UserResponse> addUser(AddUserRequest userRequest) {
        return testRestTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(userRequest, acceptAndContentTypeUnauthorized()), UserResponse.class);
    }

    public ResponseEntity<Errors> addUserError(AddUserRequest userRequest) {
        return testRestTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(userRequest, acceptAndContentTypeUnauthorized()), Errors.class);
    }

    public ResponseEntity<Void> deleteUser(Long id, Credentials credentials) {
        return testRestTemplate.exchange(URL + "/" + id, HttpMethod.DELETE, new HttpEntity<>(authorization(credentials)), Void.class);
    }

    public ResponseEntity<Errors> deleteUserUnauthorized(Long id, String invalidToken) {
        return testRestTemplate.exchange(URL + "/" + id, HttpMethod.DELETE, new HttpEntity<>(invalidOrMissingToken(invalidToken)), Errors.class);
    }

    public ResponseEntity<UserResponse> modifyUser(ModifyUserRequest userRequest, Credentials credentials) {
        return testRestTemplate.exchange(URL, HttpMethod.PUT, new HttpEntity<>(userRequest, acceptAndContentType(credentials)), UserResponse.class);
    }

    public ResponseEntity<Errors> modifyUserError(ModifyUserRequest userRequest, Credentials credentials) {
        return testRestTemplate.exchange(URL, HttpMethod.PUT, new HttpEntity<>(userRequest, acceptAndContentType(credentials)), Errors.class);
    }

    public ResponseEntity<Errors> modifyUserUnauthorized(ModifyUserRequest userRequest, String invalidToken) {
        return testRestTemplate.exchange(URL, HttpMethod.PUT, new HttpEntity<>(userRequest, acceptAndContentTypeUnauthorized(invalidToken)), Errors.class);
    }

    public ResponseEntity<Void> changePassword(Long id, ChangePasswordRequest passwordRequest, Credentials credentials) {
        return testRestTemplate.exchange(URL + "/" + id + "/change-password", HttpMethod.PUT, new HttpEntity<>(passwordRequest, acceptAndContentType(credentials)), Void.class);
    }

    public ResponseEntity<Errors> changePasswordError(Long id, ChangePasswordRequest passwordRequest, Credentials credentials) {
        return testRestTemplate.exchange(URL + "/" + id + "/change-password", HttpMethod.PUT, new HttpEntity<>(passwordRequest, acceptAndContentType(credentials)), Errors.class);
    }

    public ResponseEntity<Errors> changePasswordUnauthorized(Long id, ChangePasswordRequest passwordRequest, String invalidToken) {
        return testRestTemplate.exchange(URL + "/" + id + "/change-password", HttpMethod.PUT, new HttpEntity<>(passwordRequest, acceptAndContentTypeUnauthorized(invalidToken)), Errors.class);
    }

    public UserRepresentation getKeycloakUser(String email) {
        return getKeycloakUsers(email).get(0);
    }

    public void assetThatKeycloakUserHasUserRole(String email) {
        UserRepresentation user = getKeycloakUser(email);
        List<RoleRepresentation> roles = keycloakApi
                .realm(KEYCLOAK_REALM)
                .users()
                .get(user.getId())
                .roles()
                .getAll()
                .getRealmMappings();
        assertTrue(roles.stream().anyMatch(it -> it.getName().equals(KEYCLOAK_USER_ROLE)));
    }

    public void assertThatUserAddressIsRemoved(Long userId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM app.ADDRESSES WHERE USER_ID = ?",
                Integer.class,
                userId);

        assertEquals(0, count);
    }

    public void assertThatUserIsRemoved(Long userId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM app.USERS WHERE ID = ?",
                Integer.class,
                userId);

        assertEquals(0, count);
    }

    public void assertThatUserIsDeletedFromKeycloak(String email) {
        List<UserRepresentation> keycloakUsers = getKeycloakUsers(email);
        assertTrue(keycloakUsers.isEmpty());
    }

    public void assertThatUserSessionExists(String email) {
        List<UserRepresentation> keycloakUsers = getKeycloakUsers(email);
        assertEquals(1, keycloakUsers.size());
        List<UserSessionRepresentation> sessions = getUserSessions(keycloakUsers.get(0).getId());
        assertEquals(1, sessions.size());
    }

    public void assertThatUserSessionDoesNotExist(String email) {
        List<UserRepresentation> keycloakUsers = getKeycloakUsers(email);
        assertEquals(1, keycloakUsers.size());
        List<UserSessionRepresentation> sessions = getUserSessions(keycloakUsers.get(0).getId());
        assertEquals(0, sessions.size());
    }

    private List<UserSessionRepresentation> getUserSessions(String userId) {
        return keycloakApi.realm(KEYCLOAK_REALM)
                .users()
                .get(userId)
                .getUserSessions();
    }

    private List<UserRepresentation> getKeycloakUsers(String email) {
        return keycloakApi.realm(KEYCLOAK_REALM)
                .users()
                .searchByEmail(email, true);
    }

    public void assertThatUserIsInactive(Long userId) {
        String status = jdbcTemplate.queryForObject(
                "SELECT STATUS FROM app.USERS WHERE ID = ?",
                String.class,
                userId);
        assertEquals(Status.INACTIVE.name(), status);
    }

    public void assertThatUserIsActive(Long userId) {
        String status = jdbcTemplate.queryForObject(
                "SELECT STATUS FROM app.USERS WHERE ID = ?",
                String.class,
                userId);
        assertEquals(Status.ACTIVE.name(), status);
    }
}
