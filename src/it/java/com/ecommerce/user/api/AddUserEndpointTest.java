package com.ecommerce.user.api;

import com.ecommerce.BaseIntegrationTest;
import com.ecommerce.shared.api.dto.Error;
import com.ecommerce.shared.api.dto.ErrorCode;
import com.ecommerce.shared.api.dto.Errors;
import com.ecommerce.user.api.dto.AddUserAddress;
import com.ecommerce.user.api.dto.AddUserRequest;
import com.ecommerce.user.api.dto.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static com.ecommerce.IntegrationTestData.PASSWORD;
import static com.ecommerce.shared.api.dto.ErrorCode.FIELD_REQUIRED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

public class AddUserEndpointTest extends BaseIntegrationTest {

    @Test
    @DisplayName("should add new user with all data")
    public void shouldAddNewUserWithAllData() {

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
        // when
        ResponseEntity<UserResponse> response = userAbility().addUser(userRequest);

        // then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        userAbility().assertThatUserIsInactive(response.getBody().getId());
        assertTrue(response.hasBody());
        assertNotNull(response.getBody().getId());
        assertEquals(ADDED_USER_EMAIL, response.getBody().getEmail());
        assertEquals(ADDED_USER_FIRST_NAME, response.getBody().getFirstName());
        assertEquals(ADDED_USER_LAST_NAME, response.getBody().getLastName());
        assertEquals(ADDED_USER_TAX_ID, response.getBody().getTaxId());
        assertNotNull(response.getBody().getVersion());
        assertNotNull(response.getBody().getAddress().getId());
        assertEquals(ADDED_USER_COUNTRY, response.getBody().getAddress().getCountry());
        assertEquals(ADDED_USER_CITY, response.getBody().getAddress().getCity());
        assertEquals(ADDED_USER_STREET, response.getBody().getAddress().getStreet());
        assertEquals(ADDED_USER_BUILDING_NO, response.getBody().getAddress().getBuildingNo());
        assertEquals(ADDED_USER_FLAT_NO, response.getBody().getAddress().getFlatNo());
        assertEquals(ADDED_USER_POSTAL_CODE, response.getBody().getAddress().getPostalCode());
        assertNotNull(response.getBody().getAddress().getVersion());

        // and assert that user has been added to Keycloak
        UserRepresentation keycloakUser = userAbility().getKeycloakUser(ADDED_USER_EMAIL);
        assertNotNull(keycloakUser.getId());
        assertEquals(ADDED_USER_EMAIL, keycloakUser.getEmail());
        assertEquals(ADDED_USER_EMAIL, keycloakUser.getUsername());
        assertEquals(ADDED_USER_FIRST_NAME, keycloakUser.getFirstName());
        assertEquals(ADDED_USER_LAST_NAME, keycloakUser.getLastName());
        assertEquals(true, keycloakUser.isEmailVerified());
        assertEquals(true, keycloakUser.isEnabled());
        userAbility().assetThatKeycloakUserHasUserRole(ADDED_USER_EMAIL);

        // and activation exist and is NEW
        userActivationAbility().assertThatUserActivationIsNew(response.getBody().getId());

        // and then activate user account
        String token = userActivationAbility().getNewUserToken(response.getBody().getId());
        assertNotNull(token);

        userActivationAbility().activateUser(token);

        // then account is active and token changed status to activated
        userActivationAbility().assertThatUserActivationIsActivated(response.getBody().getId());
        userAbility().assertThatUserIsActive(response.getBody().getId());

        // and assert that user can log in
        userAbility().assertUserCanLogIn(ADDED_USER_EMAIL, PASSWORD);

        // and
        assertEquals(1, inMemoryActivationEmailSender().mails.size());
        assertEquals("Ann", inMemoryActivationEmailSender().mails.get(0).user());
        assertEquals("ann.fox@mail.com", inMemoryActivationEmailSender().mails.get(0).recipient());
        assertEquals("Account activation", inMemoryActivationEmailSender().mails.get(0).subject());
        assertNotNull(inMemoryActivationEmailSender().mails.get(0).token());
    }

    @Test
    @DisplayName("should add new user with only required data")
    public void shouldAddNewUserWithOnlyRequiredData() {

        // given
        String ADDED_USER_EMAIL = "tommy.jones@mail.com";
        String ADDED_USER_FIRST_NAME = "Tommy";
        String ADDED_USER_LAST_NAME = "Jones";
        AddUserRequest userRequest = new AddUserRequest(
                ADDED_USER_EMAIL,
                PASSWORD,
                ADDED_USER_FIRST_NAME,
                ADDED_USER_LAST_NAME,
                null,
                null
        );
        // when
        ResponseEntity<UserResponse> response = userAbility().addUser(userRequest);

        // then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        userAbility().assertThatUserIsInactive(response.getBody().getId());
        assertTrue(response.hasBody());
        assertNotNull(response.getBody().getId());
        assertEquals(ADDED_USER_EMAIL, response.getBody().getEmail());
        assertEquals(ADDED_USER_FIRST_NAME, response.getBody().getFirstName());
        assertEquals(ADDED_USER_LAST_NAME, response.getBody().getLastName());
        assertNull(response.getBody().getTaxId());
        assertNotNull(response.getBody().getVersion());
        assertNull(response.getBody().getAddress());

        // and activation exist and is NEW
        userActivationAbility().assertThatUserActivationIsNew(response.getBody().getId());

        // and then activate user account
        String token = userActivationAbility().getNewUserToken(response.getBody().getId());
        assertNotNull(token);

        userActivationAbility().activateUser(token);

        // then account is active and token changed status to activated
        userActivationAbility().assertThatUserActivationIsActivated(response.getBody().getId());
        userAbility().assertThatUserIsActive(response.getBody().getId());

        // and
        assertEquals(1, inMemoryActivationEmailSender().mails.size());
        assertEquals("Tommy", inMemoryActivationEmailSender().mails.get(0).user());
        assertEquals("tommy.jones@mail.com", inMemoryActivationEmailSender().mails.get(0).recipient());
        assertEquals("Account activation", inMemoryActivationEmailSender().mails.get(0).subject());
        assertNotNull(inMemoryActivationEmailSender().mails.get(0).token());
    }

    @Test
    @DisplayName("should not add user when user with the same email address already exist")
    public void shouldNotAddUserWhenUserWithTheSameEmailAddressAlreadyExist() {

        // given
        String ADDED_USER_EMAIL = "carol.knowles@mail.com";
        String ADDED_USER_FIRST_NAME = "Carol";
        String ADDED_USER_LAST_NAME = "Knowles";
        AddUserRequest userRequest = new AddUserRequest(
                ADDED_USER_EMAIL,
                PASSWORD,
                ADDED_USER_FIRST_NAME,
                ADDED_USER_LAST_NAME,
                null,
                null
        );
        ResponseEntity<UserResponse> response = userAbility().addUser(userRequest);

        // when
        String ANOTHER_USER_FIRST_NAME = "Carol Jerry";
        String ANOTHER_USER_LAST_NAME = "Cnowles";
        AddUserRequest sameEmailUserRequest = new AddUserRequest(
                ADDED_USER_EMAIL,
                PASSWORD,
                ANOTHER_USER_FIRST_NAME,
                ANOTHER_USER_LAST_NAME,
                null,
                null
        );
        ResponseEntity<UserResponse> duplicated = userAbility().addUser(sameEmailUserRequest);

        // then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(UNPROCESSABLE_ENTITY, duplicated.getStatusCode());
    }

    @Test
    @DisplayName("should return BAD_REQUEST error code and not add user when given email does not match email pattern")
    public void shouldNotAddUserWhenGivenEmailDoesNotMAtchEmailPattern() {

        // given
        String ADDED_USER_EMAIL = "tim.bones.com";
        String ADDED_USER_FIRST_NAME = "Tim";
        String ADDED_USER_LAST_NAME = "Bones";
        AddUserRequest userRequest = new AddUserRequest(
                ADDED_USER_EMAIL,
                PASSWORD,
                ADDED_USER_FIRST_NAME,
                ADDED_USER_LAST_NAME,
                null,
                null
        );
        // when
        ResponseEntity<Errors> response = userAbility().addUserError(userRequest);

        // then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertEquals(FIELD_REQUIRED, response.getBody().getErrors().get(0).getCode());
        assertEquals("email", response.getBody().getErrors().get(0).getMessage());
    }

    @Test
    @DisplayName("should return BAD_REQUEST error code when request does not contain required user data")
    public void shouldReturnBadRequestWhenRequestDoesNotContainRequiredUserData() {

        // given
        int NUM_OF_REQUIRED_FIELDS = 4;
        AddUserRequest userRequest = new AddUserRequest();

        // when
        ResponseEntity<Errors> response = userAbility().addUserError(userRequest);

        // then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertTrue(response.hasBody());
        assertEquals(NUM_OF_REQUIRED_FIELDS, response.getBody().getErrors().size());
        assertEquals(FIELD_REQUIRED, response.getBody().getErrors().get(0).getCode());
        assertEquals(FIELD_REQUIRED, response.getBody().getErrors().get(1).getCode());
        assertEquals(FIELD_REQUIRED, response.getBody().getErrors().get(2).getCode());
        List<String> missingFields = response.getBody().getErrors().stream().map(Error::getMessage).toList();
        assertTrue(missingFields.contains("email"));
        assertTrue(missingFields.contains("firstName"));
        assertTrue(missingFields.contains("lastName"));
    }

    @Test
    @DisplayName("should return BAD_REQUEST error code when request does not contain required address data")
    public void shouldReturnBadRequestWhenRequestDoesNotContainRequiredAddressData() {

        // given
        int NUM_OF_REQUIRED_FIELDS = 5;
        String ADDED_USER_EMAIL = "ann.fox@mail.com";
        String ADDED_USER_FIRST_NAME = "Ann";
        String ADDED_USER_LAST_NAME = "Fox";
        AddUserRequest userRequest = new AddUserRequest(
                ADDED_USER_EMAIL,
                PASSWORD,
                ADDED_USER_FIRST_NAME,
                ADDED_USER_LAST_NAME,
                null,
                new AddUserAddress()
        );

        // when
        ResponseEntity<Errors> response = userAbility().addUserError(userRequest);

        // then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertTrue(response.hasBody());
        assertEquals(NUM_OF_REQUIRED_FIELDS, response.getBody().getErrors().size());
        assertEquals(FIELD_REQUIRED, response.getBody().getErrors().get(0).getCode());
        assertEquals(FIELD_REQUIRED, response.getBody().getErrors().get(1).getCode());
        assertEquals(FIELD_REQUIRED, response.getBody().getErrors().get(2).getCode());
        assertEquals(FIELD_REQUIRED, response.getBody().getErrors().get(3).getCode());
        assertEquals(FIELD_REQUIRED, response.getBody().getErrors().get(4).getCode());
        List<String> missingFields = response.getBody().getErrors().stream().map(Error::getMessage).toList();
        assertTrue(missingFields.contains("address.country"));
        assertTrue(missingFields.contains("address.city"));
        assertTrue(missingFields.contains("address.street"));
        assertTrue(missingFields.contains("address.buildingNo"));
        assertTrue(missingFields.contains("address.postalCode"));
    }

    @Test
    @DisplayName("should return SERVER_ERROR in appropriate error data structure when problem occurs in external services")
    public void shouldReturnServerErrorWhenProblemOccursInExternalServices() {

        // given
        String ADDED_USER_EMAIL = "steve.star@mail.com";
        String ADDED_USER_FIRST_NAME = "very-long-name very-long-name very-long-name very-long-name very-long-name very-long-name very-long-name";
        String ADDED_USER_LAST_NAME = "Star";
        AddUserRequest userRequest = new AddUserRequest(
                ADDED_USER_EMAIL,
                PASSWORD,
                ADDED_USER_FIRST_NAME,
                ADDED_USER_LAST_NAME,
                null,
                null
        );
        // when
        ResponseEntity<Errors> response = userAbility().addUserError(userRequest);

        // then
        assertTrue(response.getStatusCode().is5xxServerError());
        assertTrue(response.hasBody());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals(ErrorCode.SERVER_ERROR, response.getBody().getErrors().get(0).getCode());
    }
}
