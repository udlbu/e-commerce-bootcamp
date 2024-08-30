package com.ecommerce.product.api;

import com.ecommerce.BaseIntegrationTest;
import com.ecommerce.product.api.dto.ProductResponse;
import com.ecommerce.shared.api.dto.Errors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.ResponseEntity;

import static com.ecommerce.IntegrationTestData.CATEGORY_ELECTRONICS;
import static com.ecommerce.IntegrationTestData.DESCRIPTION;
import static com.ecommerce.IntegrationTestData.EAN;
import static com.ecommerce.IntegrationTestData.EMAIL;
import static com.ecommerce.IntegrationTestData.IMAGE_NAME;
import static com.ecommerce.IntegrationTestData.NAME;
import static com.ecommerce.IntegrationTestData.NON_EXISTING_ID;
import static com.ecommerce.IntegrationTestData.PRODUCT_ID;
import static com.ecommerce.IntegrationTestData.VERSION;
import static com.ecommerce.shared.CredentialsAbility.credentials;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public class GetProductEndpointTest extends BaseIntegrationTest {

    @Test
    @DisplayName("should return product when product with given id exists in the system")
    public void shouldReturnProductWhenProductWithGivenIdExistsInTheSystem() {

        // when
        ResponseEntity<ProductResponse> response = productAbility().getProduct(PRODUCT_ID, credentials(EMAIL));

        // then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(response.hasBody());
        assertEquals(PRODUCT_ID, response.getBody().getId());
        assertEquals(NAME, response.getBody().getName());
        assertEquals(EAN, response.getBody().getEan());
        assertEquals(CATEGORY_ELECTRONICS, response.getBody().getCategory());
        assertEquals(cdnProperties.getImgUrl() + IMAGE_NAME, response.getBody().getImageUrl());
        assertEquals(DESCRIPTION, response.getBody().getDescription());
        assertEquals(VERSION, response.getBody().getVersion());
    }

    @Test
    @DisplayName("should return NOT_FOUND error code when product with given id does not exists in the system")
    public void shouldReturnEmptyResponseWhenGetProductDoesNotExistInTheSystem() {

        // when
        ResponseEntity<ProductResponse> response = productAbility().getProduct(NON_EXISTING_ID, credentials(EMAIL));

        // then
        assertEquals(NOT_FOUND, response.getStatusCode());
        assertFalse(response.hasBody());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid_token"})
    @NullAndEmptySource
    @DisplayName("should return UNAUTHORIZED error code when request does not contain authentication token")
    public void shouldReturnUnauthorizedWhenRequestDoesNotContainAuthToken(String invalidTokenOrNullOrEmpty) {

        // when
        ResponseEntity<Errors> response = productAbility().getProductUnauthorized(PRODUCT_ID, invalidTokenOrNullOrEmpty);

        // then
        assertEquals(UNAUTHORIZED, response.getStatusCode());
    }
}
