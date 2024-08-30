package com.ecommerce.user.api;

import com.ecommerce.BaseIntegrationTest;
import com.ecommerce.shared.api.dto.Errors;
import com.ecommerce.user.api.dto.AddUserRequest;
import com.ecommerce.user.api.dto.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.ResponseEntity;

import static com.ecommerce.IntegrationTestData.EMAIL;
import static com.ecommerce.IntegrationTestData.PASSWORD;
import static com.ecommerce.IntegrationTestData.USER_ID;
import static com.ecommerce.shared.CredentialsAbility.credentials;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public class DeleteUserEndpointTest extends BaseIntegrationTest {

    @Test
    @DisplayName("should delete user and user's address")
    public void shouldDeleteUserWithAddress() {

        // given
        String USER_EMAIL = "joe.hanks@mail.com";
        String USER_FIRST_NAME = "Joe";
        String USER_LAST_NAME = "Hanks";
        AddUserRequest userRequest = new AddUserRequest(
                USER_EMAIL,
                PASSWORD,
                USER_FIRST_NAME,
                USER_LAST_NAME,
                null,
                null
        );

        ResponseEntity<UserResponse> addedUserResponse = userAbility().addUser(userRequest);

        // when
        userAbility().deleteUser(addedUserResponse.getBody().getId(), credentials(USER_EMAIL));

        // then
        assertTrue(addedUserResponse.getStatusCode().is2xxSuccessful());


        // assert that user was removed
        userAbility().assertThatUserIsRemoved(addedUserResponse.getBody().getId());

        // assert that address was removed as well
        userAbility().assertThatUserAddressIsRemoved(addedUserResponse.getBody().getId());

        // and assert that the user has been deleted from Keycloak
        userAbility().assertThatUserIsDeletedFromKeycloak(USER_EMAIL);
    }

    @Test
    @DisplayName("should return FORBIDDEN when user is trying to remove not its own account")
    public void shouldReturnForbiddenWhenUserIsTryingToRemoveNotItsOwnAccount() {

        // given
        String USER_EMAIL = "joe.hanks@mail.com";
        String USER_FIRST_NAME = "Joe";
        String USER_LAST_NAME = "Hanks";
        AddUserRequest userRequest = new AddUserRequest(
                USER_EMAIL,
                PASSWORD,
                USER_FIRST_NAME,
                USER_LAST_NAME,
                null,
                null
        );

        ResponseEntity<UserResponse> addedUserResponse = userAbility().addUser(userRequest);

        // when
        ResponseEntity<Void> response = userAbility().deleteUser(addedUserResponse.getBody().getId(), credentials(EMAIL));

        // then
        assertEquals(FORBIDDEN, response.getStatusCode());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid_token"})
    @NullAndEmptySource
    @DisplayName("should return UNAUTHORIZED error code when request does not contain authentication token")
    public void shouldReturnUnauthorizedWhenRequestDoesNotContainAuthToken(String invalidTokenOrNullOrEmpty) {

        // when
        ResponseEntity<Errors> response = userAbility().deleteUserUnauthorized(USER_ID, invalidTokenOrNullOrEmpty);

        // then
        assertEquals(UNAUTHORIZED, response.getStatusCode());
    }
}
