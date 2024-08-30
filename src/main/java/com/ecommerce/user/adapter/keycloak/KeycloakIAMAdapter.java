package com.ecommerce.user.adapter.keycloak;

import com.ecommerce.user.config.IAMProperties;
import com.ecommerce.user.domain.exceptions.IAMException;
import com.ecommerce.user.domain.exceptions.IAMUserAlreadyExistException;
import com.ecommerce.user.domain.model.Email;
import com.ecommerce.user.domain.model.Password;
import com.ecommerce.user.domain.model.User;
import com.ecommerce.user.domain.model.UserId;
import com.ecommerce.user.domain.model.authentication.AccessToken;
import com.ecommerce.user.domain.port.IAMPort;
import com.ecommerce.user.domain.port.UserRepositoryPort;
import jakarta.ws.rs.core.Response;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.List;

import static java.util.List.of;

public class KeycloakIAMAdapter implements IAMPort {

    private final static Logger LOG = LoggerFactory.getLogger(KeycloakIAMAdapter.class);

    private static final String REALM_USER_ROLE = "user";
    private final Keycloak keycloakApi;
    private final IAMProperties properties;

    private final UserRepositoryPort userRepository;

    public KeycloakIAMAdapter(Keycloak keycloakApi, IAMProperties properties, UserRepositoryPort userRepository) {
        this.keycloakApi = keycloakApi;
        this.properties = properties;
        this.userRepository = userRepository;
    }

    @Override
    public AccessToken authenticate(String username, String password) {
        Keycloak keycloakUserApi = KeycloakBuilder.builder()
                .realm(properties.getRealm())
                .serverUrl(properties.getKeycloakServer())
                .clientId(properties.getAdmin().getClientId())
                .grantType(OAuth2Constants.PASSWORD)
                .username(username)
                .password(password)
                .build();
        // consider killing other active keycloak sessions for user
        AccessTokenResponse token = keycloakUserApi.tokenManager().getAccessToken();
        return new AccessToken(
                token.getToken(),
                token.getExpiresIn(),
                token.getRefreshToken(),
                token.getRefreshExpiresIn()
        );
    }

    @Override
    public void logout(User user) {
        UserRepresentation keycloakUser = getKeycloakUser(user.email().val());
        keycloakApi.realm(properties.getRealm()).users().get(keycloakUser.getId()).logout();
    }

    @Override
    public void addUser(User user, Password password) {
        Response response = createUser(user, password);
        validateCreateUserResponse(response);
        String userId = getKeycloakUserId(response);
        assignRoleToUser(userId);
    }

    @Override
    public void deleteUser(UserId id) {
        User user = userRepository.findUserById(id);
        deleteUser(user);
    }

    @Override
    public void deleteUser(Email email) {
        User user = userRepository.findUserByEmail(email);
        deleteUser(user);
    }

    @Override
    public void changePassword(User user, Password newPassword) {
        UserRepresentation keycloakUser = getKeycloakUser(user.email().val());
        try {
            keycloakApi.realm(properties.getRealm())
                    .users()
                    .get(keycloakUser.getId())
                    .resetPassword(createUserCredentialRequest(newPassword.val()));
        } catch (Exception e) {
            LOG.error("Unexpected error during resetting user password in Keycloak", e);
            throw new IAMException();
        }
    }

    private void deleteUser(User user) {
        if (user == null) {
            return;
        }
        UserRepresentation keycloakUser = getKeycloakUser(user.email().val());
        try (Response response = keycloakApi.realm(properties.getRealm())
                .users().delete(keycloakUser.getId())) {
            if (isClientError(response) || isServerError(response)) {
                throw new IAMException();
            }
        } catch (Exception e) {
            LOG.error("Unexpected error during deleting user in Keycloak", e);
            throw new IAMException();
        }
    }

    private Response createUser(User user, Password password) {
        try {
            return keycloakApi.realm(properties.getRealm())
                    .users()
                    .create(createUserRequest(user, password));
        } catch (Exception e) {
            LOG.error("Unexpected error during creating user in Keycloak", e);
            throw new IAMException();
        }
    }

    private void assignRoleToUser(String userId) {
        try {
            RoleRepresentation userRole = getUserRole();
            keycloakApi.realm(properties.getRealm())
                    .users()
                    .get(userId)
                    .roles()
                    .realmLevel()
                    .add(of(userRole));
        } catch (Exception e) {
            LOG.error("Unexpected error during assigning role to the user in Keycloak", e);
            throw new IAMException();
        }
    }

    private UserRepresentation getKeycloakUser(String email) {
        List<UserRepresentation> users = keycloakApi.realm(properties.getRealm())
                .users()
                .searchByEmail(email, true);
        Assert.isTrue(users.size() == 1, "Email is unique, only one user can exist with the given email address.");
        return users.get(0);
    }

    private UserRepresentation createUserRequest(User user, Password password) {
        UserRepresentation request = new UserRepresentation();
        request.setFirstName(user.firstName().val());
        request.setLastName(user.lastName().val());
        request.setCredentials(of(createUserCredentialRequest(password.val())));
        request.setUsername(user.email().val());
        request.setEmail(user.email().val());
        request.setEmailVerified(true);
        request.setEnabled(true);
        request.setRealmRoles(of(REALM_USER_ROLE));
        return request;
    }

    private static CredentialRepresentation createUserCredentialRequest(String password) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType("password");
        credential.setValue(password);
        return credential;
    }

    private void validateCreateUserResponse(Response response) {
        if (!isClientError(response) && !isServerError(response)) {
            return;
        }
        if (response.getStatus() == 409) {
            throw new IAMUserAlreadyExistException("User with given email already exists");
        }
        throw new IAMException();
    }

    private RoleRepresentation getUserRole() {
        return keycloakApi.realm(properties.getRealm()).roles()
                .get(REALM_USER_ROLE)
                .toRepresentation();
    }

    private String getKeycloakUserId(Response response) {
        String[] tokens = response.getLocation().getPath().split("/");
        return tokens[tokens.length - 1];
    }

    private boolean isClientError(Response response) {
        return response.getStatus() >= 400 && response.getStatus() <= 499;
    }

    private boolean isServerError(Response response) {
        return response.getStatus() >= 500;
    }
}
