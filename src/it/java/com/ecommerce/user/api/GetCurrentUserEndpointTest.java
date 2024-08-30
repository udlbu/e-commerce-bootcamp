package com.ecommerce.user.api;

import com.ecommerce.BaseIntegrationTest;
import com.ecommerce.shared.api.dto.Errors;
import com.ecommerce.user.api.dto.UserResponse;
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
import static com.ecommerce.IntegrationTestData.POSTAL_CODE;
import static com.ecommerce.IntegrationTestData.STREET;
import static com.ecommerce.IntegrationTestData.TAX_ID;
import static com.ecommerce.IntegrationTestData.USER_ID;
import static com.ecommerce.IntegrationTestData.VERSION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public class GetCurrentUserEndpointTest extends BaseIntegrationTest {

    @Test
    @DisplayName("should return current authorized user")
    public void shouldReturnCurrentAuthorizedUser() {

        // when
        ResponseEntity<UserResponse> response = userAbility().getCurrentUser(CREDENTIALS);

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

    @ParameterizedTest
    @ValueSource(strings = {"invalid_token"})
    @NullAndEmptySource
    @DisplayName("should return UNAUTHORIZED error code when request does not contain authentication token")
    public void shouldReturnUnauthorizedWhenRequestDoesNotContainAuthToken(String invalidTokenOrNullOrEmpty) {

        // when
        ResponseEntity<Errors> response = userAbility().getCurrentUserUnauthorized(invalidTokenOrNullOrEmpty);

        // then
        assertEquals(UNAUTHORIZED, response.getStatusCode());
    }
}
