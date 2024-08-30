package com.ecommerce.user.api;

import com.ecommerce.BaseIntegrationTest;
import com.ecommerce.shared.api.dto.Errors;
import com.ecommerce.user.api.dto.AddUserRequest;
import com.ecommerce.user.api.dto.UserResponse;
import com.ecommerce.user.domain.model.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.ResponseEntity;

import static com.ecommerce.IntegrationTestData.ADDRESS_ID;
import static com.ecommerce.IntegrationTestData.BUILDING_NO;
import static com.ecommerce.IntegrationTestData.CITY;
import static com.ecommerce.IntegrationTestData.COUNTRY;
import static com.ecommerce.IntegrationTestData.CREDENTIALS;
import static com.ecommerce.IntegrationTestData.EMAIL;
import static com.ecommerce.IntegrationTestData.FIRST_NAME;
import static com.ecommerce.IntegrationTestData.FLAT_NO;
import static com.ecommerce.IntegrationTestData.LAST_NAME;
import static com.ecommerce.IntegrationTestData.PASSWORD;
import static com.ecommerce.IntegrationTestData.POSTAL_CODE;
import static com.ecommerce.IntegrationTestData.STREET;
import static com.ecommerce.IntegrationTestData.TAX_ID;
import static com.ecommerce.IntegrationTestData.USER_ID;
import static com.ecommerce.IntegrationTestData.VERSION;
import static com.ecommerce.shared.CredentialsAbility.credentials;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public class GetUserEndpointTest extends BaseIntegrationTest {

    @Test
    @DisplayName("should return user when user with given id exists in the system")
    public void shouldReturnUserWhenUserWithGivenIdExistsInTheSystem() {

        // when
        ResponseEntity<UserResponse> response = userAbility().getUser(USER_ID, CREDENTIALS);

        // then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(response.hasBody());
        assertEquals(USER_ID, response.getBody().getId());
        assertEquals(EMAIL, response.getBody().getEmail());
        assertEquals(FIRST_NAME, response.getBody().getFirstName());
        assertEquals(LAST_NAME, response.getBody().getLastName());
        assertEquals(TAX_ID, response.getBody().getTaxId());
        assertEquals(VERSION, response.getBody().getVersion());
        assertEquals(ADDRESS_ID, response.getBody().getAddress().getId());
        assertEquals(COUNTRY, response.getBody().getAddress().getCountry());
        assertEquals(CITY, response.getBody().getAddress().getCity());
        assertEquals(STREET, response.getBody().getAddress().getStreet());
        assertEquals(BUILDING_NO, response.getBody().getAddress().getBuildingNo());
        assertEquals(FLAT_NO, response.getBody().getAddress().getFlatNo());
        assertEquals(POSTAL_CODE, response.getBody().getAddress().getPostalCode());
        assertEquals(VERSION, response.getBody().getAddress().getVersion());
    }

    @Test
    @DisplayName("should return UNAUTHORIZED error code when user with given id does not exists in the system but still exist in IAM provider")
    public void shouldReturnUnauthorizedResponseWhenGetUserDoesNotExistInTheSystem() {

        // given user
        String EMAIL = "marry.paul@mail.com";
        String FIRST_NAME = "Marry";
        String LAST_NAME = "Paul";
        AddUserRequest userRequest = new AddUserRequest(
                EMAIL,
                PASSWORD,
                FIRST_NAME,
                LAST_NAME,
                null,
                null
        );
        ResponseEntity<UserResponse> addedUserResponse = userAbility().addUser(userRequest);
        assertTrue(addedUserResponse.getStatusCode().is2xxSuccessful());

        // and user is accidentally deleted from system but not from Keycloak
        UserId userId = new UserId(addedUserResponse.getBody().getId());
        userActivationRepository.deleteByUserId(userId);
        userRepository.deleteUserById(userId);

        // when
        ResponseEntity<UserResponse> response = userAbility().getUser(addedUserResponse.getBody().getId(), credentials(EMAIL));

        // then
        assertEquals(UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.hasBody());
    }

    @Test
    @DisplayName("should return FORBIDDEN error code when user is trying to fetch not its own account")
    public void shouldReturnForbiddenResponseCodeWhenUserIsTryingToFetchNotItsOwnAccount() {

        // given
        String TOMMY_EMAIL = "tommy.cole@mail.com";
        String TOMMY = "Tommy";
        String JONES = "Cole";
        AddUserRequest userRequest = new AddUserRequest(
                TOMMY_EMAIL,
                PASSWORD,
                TOMMY,
                JONES,
                null,
                null
        );
        ResponseEntity<UserResponse> addedUserResponse = userAbility().addUser(userRequest);
        assertTrue(addedUserResponse.getStatusCode().is2xxSuccessful());

        Long ANOTHER_USER_ID = 1L;

        // when
        ResponseEntity<UserResponse> response = userAbility().getUser(ANOTHER_USER_ID, credentials(TOMMY_EMAIL));

        // then
        assertEquals(FORBIDDEN, response.getStatusCode());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid_token"})
    @NullAndEmptySource
    @DisplayName("should return UNAUTHORIZED error code when request does not contain authentication token")
    public void shouldReturnUnauthorizedWhenRequestDoesNotContainAuthToken(String invalidTokenOrNullOrEmpty) {

        // when
        ResponseEntity<Errors> response = userAbility().getUserUnauthorized(USER_ID, invalidTokenOrNullOrEmpty);

        // then
        assertEquals(UNAUTHORIZED, response.getStatusCode());
    }
}
