package com.ecommerce.user.api;

import com.ecommerce.BaseIntegrationTest;
import com.ecommerce.shared.api.dto.Error;
import com.ecommerce.shared.api.dto.ErrorCode;
import com.ecommerce.shared.api.dto.Errors;
import com.ecommerce.user.api.dto.AddUserAddress;
import com.ecommerce.user.api.dto.AddUserRequest;
import com.ecommerce.user.api.dto.ModifyUserAddress;
import com.ecommerce.user.api.dto.ModifyUserRequest;
import com.ecommerce.user.api.dto.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;

import static com.ecommerce.IntegrationTestData.EMAIL;
import static com.ecommerce.IntegrationTestData.PASSWORD;
import static com.ecommerce.shared.CredentialsAbility.credentials;
import static com.ecommerce.shared.api.dto.ErrorCode.FIELD_REQUIRED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public class ModifyUserEndpointTest extends BaseIntegrationTest {

    @Test
    @DisplayName("should update all user data and increment version")
    public void shouldUpdateAllUserDataAndIncrementVersion() {

        // given
        String EMAIL = "mike1@mail.com";
        ResponseEntity<UserResponse> newUser = addMikeUser(EMAIL);

        String MODIFIED_USER_FIRST_NAME = "Michael modified";
        String MODIFIED_USER_LAST_NAME = "Molo modified";
        String MODIFIED_USER_TAX_ID = "200200200";
        String MODIFIED_USER_COUNTRY = "France";
        String MODIFIED_USER_CITY = "Paris";
        String MODIFIED_USER_STREET = "French Street";
        String MODIFIED_USER_BUILDING_NO = "100";
        String MODIFIED_USER_FLAT_NO = "20";
        String MODIFIED_USER_POSTAL_CODE = "XAQ-WWR";

        ModifyUserRequest modifyUserRequest = new ModifyUserRequest(
                newUser.getBody().getId(),
                newUser.getBody().getEmail(),
                MODIFIED_USER_FIRST_NAME,
                MODIFIED_USER_LAST_NAME,
                MODIFIED_USER_TAX_ID,
                newUser.getBody().getVersion(),
                new ModifyUserAddress(
                        newUser.getBody().getAddress().getId(),
                        MODIFIED_USER_COUNTRY,
                        MODIFIED_USER_CITY,
                        MODIFIED_USER_STREET,
                        MODIFIED_USER_BUILDING_NO,
                        MODIFIED_USER_FLAT_NO,
                        MODIFIED_USER_POSTAL_CODE,
                        newUser.getBody().getAddress().getVersion()
                )

        );

        // when
        ResponseEntity<UserResponse> updatedUser = userAbility().modifyUser(modifyUserRequest, credentials(EMAIL));

        // then
        assertTrue(updatedUser.getStatusCode().is2xxSuccessful());
        assertTrue(updatedUser.hasBody());
        assertEquals(newUser.getBody().getId(), updatedUser.getBody().getId());
        assertEquals(newUser.getBody().getEmail(), updatedUser.getBody().getEmail());
        assertEquals(MODIFIED_USER_FIRST_NAME, updatedUser.getBody().getFirstName());
        assertEquals(MODIFIED_USER_LAST_NAME, updatedUser.getBody().getLastName());
        assertEquals(MODIFIED_USER_TAX_ID, updatedUser.getBody().getTaxId());
        assertEquals(newUser.getBody().getVersion() + 1, updatedUser.getBody().getVersion());
        assertEquals(MODIFIED_USER_COUNTRY, updatedUser.getBody().getAddress().getCountry());
        assertEquals(MODIFIED_USER_CITY, updatedUser.getBody().getAddress().getCity());
        assertEquals(MODIFIED_USER_STREET, updatedUser.getBody().getAddress().getStreet());
        assertEquals(MODIFIED_USER_BUILDING_NO, updatedUser.getBody().getAddress().getBuildingNo());
        assertEquals(MODIFIED_USER_FLAT_NO, updatedUser.getBody().getAddress().getFlatNo());
        assertEquals(MODIFIED_USER_POSTAL_CODE, updatedUser.getBody().getAddress().getPostalCode());
        assertEquals(newUser.getBody().getAddress().getVersion() + 1, updatedUser.getBody().getAddress().getVersion());
    }

    @Test
    @DisplayName("should update single field in user entity")
    public void shouldUpdateSingleFieldInUserEntity() {

        // given
        String MIKE_EMAIL = "mike2@mail.com";
        ResponseEntity<UserResponse> newUser = addMikeUser(MIKE_EMAIL);

        String MODIFIED_USER_TAX_ID = "666555444";

        ModifyUserRequest modifyUserRequest = new ModifyUserRequest(
                newUser.getBody().getId(),
                newUser.getBody().getEmail(),
                newUser.getBody().getFirstName(),
                newUser.getBody().getLastName(),
                MODIFIED_USER_TAX_ID,
                newUser.getBody().getVersion(),
                new ModifyUserAddress(
                        newUser.getBody().getAddress().getId(),
                        newUser.getBody().getAddress().getCountry(),
                        newUser.getBody().getAddress().getCity(),
                        newUser.getBody().getAddress().getStreet(),
                        newUser.getBody().getAddress().getBuildingNo(),
                        newUser.getBody().getAddress().getFlatNo(),
                        newUser.getBody().getAddress().getPostalCode(),
                        newUser.getBody().getAddress().getVersion()
                )
        );

        // when
        ResponseEntity<UserResponse> updatedUser = userAbility().modifyUser(modifyUserRequest, credentials(MIKE_EMAIL));

        // then
        assertTrue(updatedUser.getStatusCode().is2xxSuccessful());
        assertTrue(updatedUser.hasBody());
        assertEquals(newUser.getBody().getId(), updatedUser.getBody().getId());
        assertEquals(newUser.getBody().getEmail(), updatedUser.getBody().getEmail());
        assertEquals(newUser.getBody().getFirstName(), updatedUser.getBody().getFirstName());
        assertEquals(newUser.getBody().getLastName(), updatedUser.getBody().getLastName());
        assertEquals(MODIFIED_USER_TAX_ID, updatedUser.getBody().getTaxId());
        assertEquals(newUser.getBody().getVersion() + 1, updatedUser.getBody().getVersion());
        assertEquals(newUser.getBody().getAddress().getCountry(), updatedUser.getBody().getAddress().getCountry());
        assertEquals(newUser.getBody().getAddress().getCity(), updatedUser.getBody().getAddress().getCity());
        assertEquals(newUser.getBody().getAddress().getStreet(), updatedUser.getBody().getAddress().getStreet());
        assertEquals(newUser.getBody().getAddress().getBuildingNo(), updatedUser.getBody().getAddress().getBuildingNo());
        assertEquals(newUser.getBody().getAddress().getFlatNo(), updatedUser.getBody().getAddress().getFlatNo());
        assertEquals(newUser.getBody().getAddress().getPostalCode(), updatedUser.getBody().getAddress().getPostalCode());
        assertEquals(newUser.getBody().getAddress().getVersion(), updatedUser.getBody().getAddress().getVersion());
    }

    @Test
    @DisplayName("should update single field in user's address entity")
    public void shouldUpdateSingleFieldInUserAddressEntity() {

        // given
        String MIKE_EMAIL = "mike3@mail.com";
        ResponseEntity<UserResponse> newUser = addMikeUser(MIKE_EMAIL);

        String MODIFIED_USER_BUILDING_NO = "101";

        ModifyUserRequest modifyUserRequest = new ModifyUserRequest(
                newUser.getBody().getId(),
                newUser.getBody().getEmail(),
                newUser.getBody().getFirstName(),
                newUser.getBody().getLastName(),
                newUser.getBody().getTaxId(),
                newUser.getBody().getVersion(),
                new ModifyUserAddress(
                        newUser.getBody().getAddress().getId(),
                        newUser.getBody().getAddress().getCountry(),
                        newUser.getBody().getAddress().getCity(),
                        newUser.getBody().getAddress().getStreet(),
                        MODIFIED_USER_BUILDING_NO,
                        newUser.getBody().getAddress().getFlatNo(),
                        newUser.getBody().getAddress().getPostalCode(),
                        newUser.getBody().getAddress().getVersion()
                )
        );

        // when
        ResponseEntity<UserResponse> updatedUser = userAbility().modifyUser(modifyUserRequest, credentials(MIKE_EMAIL));

        // then
        assertTrue(updatedUser.getStatusCode().is2xxSuccessful());
        assertTrue(updatedUser.hasBody());
        assertEquals(newUser.getBody().getId(), updatedUser.getBody().getId());
        assertEquals(newUser.getBody().getEmail(), updatedUser.getBody().getEmail());
        assertEquals(newUser.getBody().getFirstName(), updatedUser.getBody().getFirstName());
        assertEquals(newUser.getBody().getLastName(), updatedUser.getBody().getLastName());
        assertEquals(newUser.getBody().getTaxId(), updatedUser.getBody().getTaxId());
        assertEquals(newUser.getBody().getVersion(), updatedUser.getBody().getVersion());
        assertEquals(newUser.getBody().getAddress().getCountry(), updatedUser.getBody().getAddress().getCountry());
        assertEquals(newUser.getBody().getAddress().getCity(), updatedUser.getBody().getAddress().getCity());
        assertEquals(newUser.getBody().getAddress().getStreet(), updatedUser.getBody().getAddress().getStreet());
        assertEquals(MODIFIED_USER_BUILDING_NO, updatedUser.getBody().getAddress().getBuildingNo());
        assertEquals(newUser.getBody().getAddress().getFlatNo(), updatedUser.getBody().getAddress().getFlatNo());
        assertEquals(newUser.getBody().getAddress().getPostalCode(), updatedUser.getBody().getAddress().getPostalCode());
        assertEquals(newUser.getBody().getAddress().getVersion() + 1, updatedUser.getBody().getAddress().getVersion());
    }

    @Test
    @DisplayName("should add address to user")
    public void shouldAddAddressToUser() {

        // given
        ResponseEntity<UserResponse> newUser = addJessicaUser();

        String ADDED_USER_COUNTRY = "England";
        String ADDED_USER_CITY = "Manchester";
        String ADDED_USER_STREET = "New Rd.";
        String ADDED_USER_BUILDING_NO = "1";
        String ADDED_USER_POSTAL_CODE = "AA-A1";

        ModifyUserRequest modifyUserRequest = new ModifyUserRequest(
                newUser.getBody().getId(),
                newUser.getBody().getEmail(),
                newUser.getBody().getFirstName(),
                newUser.getBody().getLastName(),
                null,
                newUser.getBody().getVersion(),
                new ModifyUserAddress(
                        null,
                        ADDED_USER_COUNTRY,
                        ADDED_USER_CITY,
                        ADDED_USER_STREET,
                        ADDED_USER_BUILDING_NO,
                        null,
                        ADDED_USER_POSTAL_CODE,
                        null
                )

        );

        // when
        ResponseEntity<UserResponse> updatedUser = userAbility().modifyUser(modifyUserRequest, credentials(newUser.getBody().getEmail()));

        // then
        assertTrue(updatedUser.getStatusCode().is2xxSuccessful());
        assertTrue(updatedUser.hasBody());
        assertEquals(newUser.getBody().getId(), updatedUser.getBody().getId());
        assertEquals(newUser.getBody().getEmail(), updatedUser.getBody().getEmail());
        assertEquals(newUser.getBody().getFirstName(), updatedUser.getBody().getFirstName());
        assertEquals(newUser.getBody().getLastName(), updatedUser.getBody().getLastName());
        assertEquals(newUser.getBody().getTaxId(), updatedUser.getBody().getTaxId());
        assertEquals(newUser.getBody().getVersion(), updatedUser.getBody().getVersion());
        assertEquals(ADDED_USER_COUNTRY, updatedUser.getBody().getAddress().getCountry());
        assertEquals(ADDED_USER_CITY, updatedUser.getBody().getAddress().getCity());
        assertEquals(ADDED_USER_STREET, updatedUser.getBody().getAddress().getStreet());
        assertEquals(ADDED_USER_BUILDING_NO, updatedUser.getBody().getAddress().getBuildingNo());
        assertNull(updatedUser.getBody().getAddress().getFlatNo());
        assertEquals(ADDED_USER_POSTAL_CODE, updatedUser.getBody().getAddress().getPostalCode());
        assertEquals(0, updatedUser.getBody().getAddress().getVersion());
    }

    @Test
    @DisplayName("should remove user's address")
    public void shouldRemoveUserAddress() {

        // given
        ResponseEntity<UserResponse> newUser = addSimonUser();

        ModifyUserRequest modifyUserRequest = new ModifyUserRequest(
                newUser.getBody().getId(),
                newUser.getBody().getEmail(),
                newUser.getBody().getFirstName(),
                newUser.getBody().getLastName(),
                newUser.getBody().getTaxId(),
                newUser.getBody().getVersion(),
                null
        );

        // when
        ResponseEntity<UserResponse> updatedUser = userAbility().modifyUser(modifyUserRequest, credentials(newUser.getBody().getEmail()));

        // then
        assertTrue(updatedUser.getStatusCode().is2xxSuccessful());
        assertTrue(updatedUser.hasBody());
        assertEquals(newUser.getBody().getId(), updatedUser.getBody().getId());
        assertEquals(newUser.getBody().getEmail(), updatedUser.getBody().getEmail());
        assertEquals(newUser.getBody().getFirstName(), updatedUser.getBody().getFirstName());
        assertEquals(newUser.getBody().getLastName(), updatedUser.getBody().getLastName());
        assertEquals(newUser.getBody().getTaxId(), updatedUser.getBody().getTaxId());
        assertEquals(newUser.getBody().getVersion(), updatedUser.getBody().getVersion());
        assertNotNull(newUser.getBody().getAddress());
        assertNull(updatedUser.getBody().getAddress());

        // and
        userAbility().assertThatUserAddressIsRemoved(newUser.getBody().getId());
    }

    @Test
    @DisplayName("should return FORBIDDEN error code when user is trying to modify another user's account")
    public void shouldReturnForbiddenWhenUserIsTryingToModifyAnotherUserAccount() {

        // given
        String MIKE_EMAIL = "mike2@mail.com";
        ResponseEntity<UserResponse> newUser = addMikeUser(MIKE_EMAIL);

        String MODIFIED_USER_TAX_ID = "666555444";

        ModifyUserRequest modifyUserRequest = new ModifyUserRequest(
                newUser.getBody().getId(),
                newUser.getBody().getEmail(),
                newUser.getBody().getFirstName(),
                newUser.getBody().getLastName(),
                MODIFIED_USER_TAX_ID,
                newUser.getBody().getVersion(),
                new ModifyUserAddress(
                        newUser.getBody().getAddress().getId(),
                        newUser.getBody().getAddress().getCountry(),
                        newUser.getBody().getAddress().getCity(),
                        newUser.getBody().getAddress().getStreet(),
                        newUser.getBody().getAddress().getBuildingNo(),
                        newUser.getBody().getAddress().getFlatNo(),
                        newUser.getBody().getAddress().getPostalCode(),
                        newUser.getBody().getAddress().getVersion()
                )
        );

        // when
        ResponseEntity<UserResponse> updatedUser = userAbility().modifyUser(modifyUserRequest, credentials(EMAIL));

        // then
        assertEquals(FORBIDDEN, updatedUser.getStatusCode());
    }

    @Test
    @DisplayName("should throw exception if user was modified by concurrent transaction")
    public void shouldThrowExceptionIfUserWasModifiedByConcurrentTransaction() {

        // given
        ResponseEntity<UserResponse> newUser = addMikeUser("mike4@mail.com");

        // and
        simulateConcurrentUpdate(newUser.getBody().getId(), newUser.getBody().getEmail(), newUser.getBody().getVersion());

        // when
        String MODIFIED_USER_TAX_ID = "176111";
        ModifyUserRequest modifyUserRequest = new ModifyUserRequest(
                newUser.getBody().getId(),
                newUser.getBody().getEmail(),
                newUser.getBody().getFirstName(),
                newUser.getBody().getLastName(),
                MODIFIED_USER_TAX_ID,
                newUser.getBody().getVersion(),
                null
        );

        ResponseEntity<Errors> errorResponse = userAbility().modifyUserError(modifyUserRequest, credentials(newUser.getBody().getEmail()));

        // then
        assertTrue(errorResponse.getStatusCode().is5xxServerError());
        assertTrue(errorResponse.hasBody());
        assertEquals(ErrorCode.CONCURRENT_MODIFICATION_ERROR, errorResponse.getBody().getErrors().get(0).getCode());
    }

    @Test
    @DisplayName("should return BAD_REQUEST error code when request does not contain required data")
    public void shouldReturnBadRequestWhenRequestDoesNotContainRequiredData() {

        // given
        int EXPECTED_ERRORS_NUMBER = 10;

        // when
        ResponseEntity<Errors> response = userAbility().modifyUserError(new ModifyUserRequest(
                null,
                null,
                null,
                null,
                null,
                null,
                new ModifyUserAddress()
        ), credentials(EMAIL));

        // then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertTrue(response.hasBody());
        assertEquals(EXPECTED_ERRORS_NUMBER, response.getBody().getErrors().size());
        assertTrue(response.getBody().getErrors().stream().allMatch(error -> Objects.equals(error.getCode(), FIELD_REQUIRED)));
        List<String> missingFields = response.getBody().getErrors().stream().map(Error::getMessage).toList();
        assertTrue(missingFields.contains("id"));
        assertTrue(missingFields.contains("email"));
        assertTrue(missingFields.contains("firstName"));
        assertTrue(missingFields.contains("lastName"));
        assertTrue(missingFields.contains("version"));
        assertTrue(missingFields.contains("address.country"));
        assertTrue(missingFields.contains("address.city"));
        assertTrue(missingFields.contains("address.street"));
        assertTrue(missingFields.contains("address.buildingNo"));
        assertTrue(missingFields.contains("address.postalCode"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid_token"})
    @NullAndEmptySource
    @DisplayName("should return UNAUTHORIZED error code when request does not contain authentication token")
    public void shouldReturnUnauthorizedWhenRequestDoesNotContainAuthToken(String invalidTokenOrNullOrEmpty) {

        // given
        ResponseEntity<UserResponse> newUser = addMikeUser("mike5@mail.com");

        String MODIFIED_USER_EMAIL = "mike.mollo.modified5@mail.com";

        ModifyUserRequest modifyUserRequest = new ModifyUserRequest(
                newUser.getBody().getId(),
                MODIFIED_USER_EMAIL,
                newUser.getBody().getFirstName(),
                newUser.getBody().getLastName(),
                newUser.getBody().getTaxId(),
                newUser.getBody().getVersion(),
                new ModifyUserAddress(
                        newUser.getBody().getAddress().getId(),
                        newUser.getBody().getAddress().getCountry(),
                        newUser.getBody().getAddress().getCity(),
                        newUser.getBody().getAddress().getStreet(),
                        newUser.getBody().getAddress().getBuildingNo(),
                        newUser.getBody().getAddress().getFlatNo(),
                        newUser.getBody().getAddress().getPostalCode(),
                        newUser.getBody().getAddress().getVersion()
                )
        );

        // when
        ResponseEntity<Errors> response = userAbility().modifyUserUnauthorized(modifyUserRequest, invalidTokenOrNullOrEmpty);

        // then
        assertEquals(UNAUTHORIZED, response.getStatusCode());
    }

    private ResponseEntity<UserResponse> addMikeUser(String email) {
        String ADDED_USER_FIRST_NAME = "Mike";
        String ADDED_USER_LAST_NAME = "Mollo";
        String ADDED_USER_TAX_ID = "10200300";
        String ADDED_USER_COUNTRY = "England";
        String ADDED_USER_CITY = "Manchester";
        String ADDED_USER_STREET = "Ashley Rd.";
        String ADDED_USER_BUILDING_NO = "10";
        String ADDED_USER_FLAT_NO = "9";
        String ADDED_USER_POSTAL_CODE = "FF-RRE";
        AddUserRequest addUserRequest = new AddUserRequest(
                email,
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
        return userAbility().addUser(addUserRequest);
    }

    private ResponseEntity<UserResponse> addJessicaUser() {
        String ADDED_USER_EMAIL = "jessica.thomas@mail.com";
        String ADDED_USER_FIRST_NAME = "Jessica";
        String ADDED_USER_LAST_NAME = "Thomas";

        AddUserRequest addUserRequest = new AddUserRequest(
                ADDED_USER_EMAIL,
                PASSWORD,
                ADDED_USER_FIRST_NAME,
                ADDED_USER_LAST_NAME,
                null,
                null
        );
        ResponseEntity<UserResponse> response = userAbility().addUser(addUserRequest);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        return response;
    }

    private ResponseEntity<UserResponse> addSimonUser() {
        String ADDED_USER_EMAIL = "simon.smith@mail.com";
        String ADDED_USER_FIRST_NAME = "Simon";
        String ADDED_USER_LAST_NAME = "Smith";
        String ADDED_USER_TAX_ID = "3241234";
        String ADDED_USER_COUNTRY = "England";
        String ADDED_USER_CITY = "London";
        String ADDED_USER_STREET = "Chelsea Rd.";
        String ADDED_USER_BUILDING_NO = "100";
        String ADDED_USER_FLAT_NO = "1";
        String ADDED_USER_POSTAL_CODE = "AE-EWE";
        AddUserRequest addUserRequest = new AddUserRequest(
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
        ResponseEntity<UserResponse> response = userAbility().addUser(addUserRequest);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        return response;
    }

    private void simulateConcurrentUpdate(Long id, String email, Long version) {
        ModifyUserRequest modifyUserRequest = new ModifyUserRequest(
                id,
                email,
                "Micky",
                "Molly",
                null,
                version,
                null
        );
        userAbility().modifyUser(modifyUserRequest, credentials(email));
    }
}
