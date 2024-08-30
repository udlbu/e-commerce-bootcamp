package com.ecommerce.product.api;

import com.ecommerce.BaseIntegrationTest;
import com.ecommerce.product.api.dto.AddProductRequest;
import com.ecommerce.product.api.dto.ProductResponse;
import com.ecommerce.shared.api.dto.Error;
import com.ecommerce.shared.api.dto.ErrorCode;
import com.ecommerce.shared.api.dto.Errors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static com.ecommerce.IntegrationTestData.EMAIL;
import static com.ecommerce.IntegrationTestData.IMAGE;
import static com.ecommerce.IntegrationTestData.IMAGE_NAME_PATTERN;
import static com.ecommerce.shared.CredentialsAbility.credentials;
import static com.ecommerce.shared.api.dto.ErrorCode.FIELD_REQUIRED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public class AddProductEndpointTest extends BaseIntegrationTest {

    @Test
    @DisplayName("should add new product with all data")
    public void shouldAddNewProductWithAllData() {

        // given
        String ADDED_PRODUCT_NAME = "added-product-name";
        String ADDED_PRODUCT_EAN = "1112233";
        String ADDED_PRODUCT_CATEGORY = "BEAUTY";
        String ADDED_PRODUCT_DESCRIPTION = "added-product-description";
        AddProductRequest productRequest = new AddProductRequest(
                ADDED_PRODUCT_NAME,
                ADDED_PRODUCT_EAN,
                ADDED_PRODUCT_CATEGORY,
                IMAGE,
                ADDED_PRODUCT_DESCRIPTION
        );

        // when
        ResponseEntity<ProductResponse> response = productAbility().addProduct(productRequest, credentials(EMAIL));

        // then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(response.hasBody());
        assertNotNull(response.getBody().getId());
        assertEquals(ADDED_PRODUCT_NAME, response.getBody().getName());
        assertEquals(ADDED_PRODUCT_EAN, response.getBody().getEan());
        assertEquals(ADDED_PRODUCT_CATEGORY, response.getBody().getCategory());
        assertTrue(response.getBody().getImageUrl().matches("^" + cdnProperties.getImgUrl() + IMAGE_NAME_PATTERN + "$"));
        assertEquals(ADDED_PRODUCT_DESCRIPTION, response.getBody().getDescription());
        assertNotNull(response.getBody().getVersion());

        // and verify that image is uploaded to CDN
        cdnAbility().verifyUploadImageWasCalledOnce();
    }

    @Test
    @DisplayName("should add new product with only required data")
    public void shouldAddNewProductWithOnlyRequiredData() {

        // given
        String ADDED_PRODUCT_NAME = "another-product-name";
        String ADDED_PRODUCT_EAN = "333444239";
        String ADDED_PRODUCT_CATEGORY = "BEAUTY";
        AddProductRequest productRequest = new AddProductRequest(
                ADDED_PRODUCT_NAME,
                ADDED_PRODUCT_EAN,
                ADDED_PRODUCT_CATEGORY,
                null,
                null
        );

        // when
        ResponseEntity<ProductResponse> response = productAbility().addProduct(productRequest, credentials(EMAIL));

        // then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(response.hasBody());
        assertNotNull(response.getBody().getId());
        assertEquals(ADDED_PRODUCT_NAME, response.getBody().getName());
        assertEquals(ADDED_PRODUCT_EAN, response.getBody().getEan());
        assertEquals(ADDED_PRODUCT_CATEGORY, response.getBody().getCategory());
        assertNull(response.getBody().getImageUrl());
        assertNull(response.getBody().getDescription());
        assertNotNull(response.getBody().getVersion());

        // and
        cdnAbility().verifyUploadImageWasNotCalled();
    }

    @Test
    @DisplayName("should return BAD_REQUEST error code when request does not contain required data")
    public void shouldReturnBadRequestWhenRequestDoesNotContainRequiredData() {

        // given
        String ADDED_PRODUCT_NAME = "yet-another-product-name";
        AddProductRequest productRequest = new AddProductRequest(
                ADDED_PRODUCT_NAME,
                null,
                null,
                null,
                null
        );

        // when
        ResponseEntity<Errors> response = productAbility().addProductError(productRequest, credentials(EMAIL));

        // then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertTrue(response.hasBody());
        assertEquals(2, response.getBody().getErrors().size());
        assertEquals(FIELD_REQUIRED, response.getBody().getErrors().get(0).getCode());
        assertEquals(FIELD_REQUIRED, response.getBody().getErrors().get(1).getCode());
        List<String> missingFields = response.getBody().getErrors().stream().map(Error::getMessage).toList();
        assertTrue(missingFields.contains("ean"));
        assertTrue(missingFields.contains("category"));
        assertFalse(response.getBody().getErrors().get(0).getPath().isEmpty());
        assertFalse(response.getBody().getErrors().get(1).getPath().isEmpty());
    }

    @Test
    @DisplayName("should return SERVER_ERROR in appropriate error data structure when problem occurs in external services")
    public void shouldReturnServerErrorWhenProblemOccursInExternalServices() {

        // given
        String ADDED_PRODUCT_NAME = "too-long-product-name-to-fit-in-database-column-so-that-the-persisting-is-not-possible";
        String ADDED_PRODUCT_EAN = "234523542";
        String ADDED_PRODUCT_CATEGORY = "BEAUTY";
        AddProductRequest productRequest = new AddProductRequest(
                ADDED_PRODUCT_NAME,
                ADDED_PRODUCT_EAN,
                ADDED_PRODUCT_CATEGORY,
                null,
                null
        );

        // when
        ResponseEntity<Errors> response = productAbility().addProductError(productRequest, credentials(EMAIL));

        // then
        assertTrue(response.getStatusCode().is5xxServerError());
        assertTrue(response.hasBody());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals(ErrorCode.SERVER_ERROR, response.getBody().getErrors().get(0).getCode());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid_token"})
    @NullAndEmptySource
    @DisplayName("should return UNAUTHORIZED error code when request does not contain authentication token")
    public void shouldReturnUnauthorizedWhenRequestDoesNotContainAuthToken(String invalidTokenOrNullOrEmpty) {

        //given
        String ADDED_PRODUCT_NAME = "added-product-name";
        String ADDED_PRODUCT_EAN = "1112233";
        String ADDED_PRODUCT_CATEGORY = "BEAUTY";
        String ADDED_PRODUCT_IMAGE = "added-product-image";
        String ADDED_PRODUCT_DESCRIPTION = "added-product-description";
        AddProductRequest productRequest = new AddProductRequest(
                ADDED_PRODUCT_NAME,
                ADDED_PRODUCT_EAN,
                ADDED_PRODUCT_CATEGORY,
                ADDED_PRODUCT_IMAGE,
                ADDED_PRODUCT_DESCRIPTION
        );

        // when
        ResponseEntity<Errors> response = productAbility().addProductUnauthorized(productRequest, invalidTokenOrNullOrEmpty);

        // then
        assertEquals(UNAUTHORIZED, response.getStatusCode());
    }
}
