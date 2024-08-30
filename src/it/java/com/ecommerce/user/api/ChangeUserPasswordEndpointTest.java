package com.ecommerce.user.api;

import com.ecommerce.BaseIntegrationTest;
import com.ecommerce.shared.api.dto.Error;
import com.ecommerce.shared.api.dto.Errors;
import com.ecommerce.user.api.dto.AddUserAddress;
import com.ecommerce.user.api.dto.AddUserRequest;
import com.ecommerce.user.api.dto.ChangePasswordRequest;
import com.ecommerce.user.api.dto.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static com.ecommerce.IntegrationTestData.EMAIL;
import static com.ecommerce.IntegrationTestData.PASSWORD;
import static com.ecommerce.IntegrationTestData.USER_ID;
import static com.ecommerce.shared.CredentialsAbility.credentials;
import static com.ecommerce.shared.api.dto.ErrorCode.FIELD_REQUIRED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

public class ChangeUserPasswordEndpointTest extends BaseIntegrationTest {

    @Test
    @DisplayName("should change user password")
    public void shouldChangeUserPassword() {

        // given
        String NEW_PASSWORD = "new_safe_password";
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

        // and
        ResponseEntity<UserResponse> createUserResponse = userAbility().addUser(userRequest);

        // and
        assertTrue(createUserResponse.getStatusCode().is2xxSuccessful());

        // and user can log in using password before changed
        userAbility().assertUserCanLogIn(ADDED_USER_EMAIL, PASSWORD);

        // when
        ResponseEntity<Void> changePasswordResponse = userAbility().changePassword(createUserResponse.getBody().getId(),
                new ChangePasswordRequest(
                        PASSWORD,
                        NEW_PASSWORD
                ),
                credentials(createUserResponse.getBody().getEmail())
        );

        // then
        assertTrue(changePasswordResponse.getStatusCode().is2xxSuccessful());

        // and user can log in using new password
        userAbility().assertUserCanLogIn(ADDED_USER_EMAIL, NEW_PASSWORD);
    }

    @Test
    @DisplayName("should not change user password when old password does not match")
    public void shouldNotChangeUserPasswordWhenOldPasswordDoesNotMatch() {

        // given
        String NEW_PASSWORD = "new_safe_password";
        String USER_EMAIL = "ann.fox@mail.com";
        String USER_FIRST_NAME = "Ann";
        String USER_LAST_NAME = "Fox";
        String USER_TAX_ID = "TAX_12345";
        String USER_COUNTRY = "Poland";
        String USER_CITY = "Warsaw";
        String USER_STREET = "Komisji Edukacji Narodowej";
        String USER_BUILDING_NO = "101";
        String USER_FLAT_NO = "23b";
        String USER_POSTAL_CODE = "02-791";
        AddUserRequest userRequest = new AddUserRequest(
                USER_EMAIL,
                PASSWORD,
                USER_FIRST_NAME,
                USER_LAST_NAME,
                USER_TAX_ID,
                new AddUserAddress(
                        USER_COUNTRY,
                        USER_CITY,
                        USER_STREET,
                        USER_BUILDING_NO,
                        USER_FLAT_NO,
                        USER_POSTAL_CODE
                )
        );

        // and
        ResponseEntity<UserResponse> createUserResponse = userAbility().addUser(userRequest);

        // and
        assertTrue(createUserResponse.getStatusCode().is2xxSuccessful());

        // when
        ResponseEntity<Errors> changePasswordResponse = userAbility().changePasswordError(createUserResponse.getBody().getId(),
                new ChangePasswordRequest(
                        "wrong_old_password",
                        NEW_PASSWORD
                ),
                credentials(createUserResponse.getBody().getEmail())

        );

        // then
        assertEquals(UNPROCESSABLE_ENTITY, changePasswordResponse.getStatusCode());
    }

    @Test
    @DisplayName("should return FORBIDDEN error code when user is trying to change password of another user's account")
    public void shouldReturnForbiddenWhenUserIsTryingToChangePasswordOfAnotherUser() {

        // given
        String NEW_PASSWORD = "new_safe_password";
        String USER_EMAIL = "ann.fox@mail.com";
        String USER_FIRST_NAME = "Ann";
        String USER_LAST_NAME = "Fox";
        String USER_TAX_ID = "TAX_12345";
        String USER_COUNTRY = "Poland";
        String USER_CITY = "Warsaw";
        String USER_STREET = "Komisji Edukacji Narodowej";
        String USER_BUILDING_NO = "101";
        String USER_FLAT_NO = "23b";
        String USER_POSTAL_CODE = "02-791";
        AddUserRequest userRequest = new AddUserRequest(
                USER_EMAIL,
                PASSWORD,
                USER_FIRST_NAME,
                USER_LAST_NAME,
                USER_TAX_ID,
                new AddUserAddress(
                        USER_COUNTRY,
                        USER_CITY,
                        USER_STREET,
                        USER_BUILDING_NO,
                        USER_FLAT_NO,
                        USER_POSTAL_CODE
                )
        );

        // and
        ResponseEntity<UserResponse> createUserResponse = userAbility().addUser(userRequest);

        // and
        assertTrue(createUserResponse.getStatusCode().is2xxSuccessful());

        // when
        ResponseEntity<Errors> changePasswordResponse = userAbility().changePasswordError(createUserResponse.getBody().getId(),
                new ChangePasswordRequest(
                        PASSWORD,
                        NEW_PASSWORD
                ),
                credentials(EMAIL)

        );

        // then
        assertEquals(FORBIDDEN, changePasswordResponse.getStatusCode());
    }

    @Test
    @DisplayName("should return BAD_REQUEST error code when request does not contain required user data")
    public void shouldReturnBadRequestWhenRequestDoesNotContainRequiredUserData() {

        // given
        int NUM_OF_REQUIRED_FIELDS = 2;
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();

        // when
        ResponseEntity<Errors> response = userAbility().changePasswordError(USER_ID, changePasswordRequest, credentials(EMAIL));

        // then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertTrue(response.hasBody());
        assertEquals(NUM_OF_REQUIRED_FIELDS, response.getBody().getErrors().size());
        assertEquals(FIELD_REQUIRED, response.getBody().getErrors().get(0).getCode());
        assertEquals(FIELD_REQUIRED, response.getBody().getErrors().get(1).getCode());
        List<String> missingFields = response.getBody().getErrors().stream().map(Error::getMessage).toList();
        assertTrue(missingFields.contains("oldPassword"));
        assertTrue(missingFields.contains("newPassword"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid_token"})
    @NullAndEmptySource
    @DisplayName("should return UNAUTHORIZED error code when request does not contain authentication token")
    public void shouldReturnUnauthorizedWhenRequestDoesNotContainAuthToken(String invalidTokenOrNullOrEmpty) {

        // given
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("old_secret", "new_secret");

        // when
        ResponseEntity<Errors> response = userAbility().changePasswordUnauthorized(USER_ID, changePasswordRequest, invalidTokenOrNullOrEmpty);

        // then
        assertEquals(UNAUTHORIZED, response.getStatusCode());
    }
}
