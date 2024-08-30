package com.ecommerce.product.api;

import com.ecommerce.BaseIntegrationTest;
import com.ecommerce.offer.api.dto.AddOfferRequest;
import com.ecommerce.offer.api.dto.OfferResponse;
import com.ecommerce.product.api.dto.AddProductRequest;
import com.ecommerce.product.api.dto.ProductResponse;
import com.ecommerce.shared.api.dto.ErrorCode;
import com.ecommerce.shared.api.dto.Errors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static com.ecommerce.IntegrationTestData.EMAIL;
import static com.ecommerce.IntegrationTestData.PRODUCT_ID;
import static com.ecommerce.IntegrationTestData.USER_ID;
import static com.ecommerce.shared.CredentialsAbility.credentials;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

public class DeleteProductEndpointTest extends BaseIntegrationTest {

    @Test
    @DisplayName("should delete product with given identifier")
    public void shouldDeleteProduct() {

        // given
        String PRODUCT_NAME = "product-to-delete";
        String PRODUCT_EAN = "1111111";
        String PRODUCT_CATEGORY = "BEAUTY";
        AddProductRequest productRequest = new AddProductRequest(
                PRODUCT_NAME,
                PRODUCT_EAN,
                PRODUCT_CATEGORY,
                null,
                null
        );
        ResponseEntity<ProductResponse> addedProductResponse = productAbility().addProduct(productRequest, credentials(EMAIL));

        // when
        productAbility().deleteProduct(addedProductResponse.getBody().getId(), credentials(EMAIL));

        // then
        assertTrue(addedProductResponse.getStatusCode().is2xxSuccessful());

        // and when
        ResponseEntity<ProductResponse> deletedProductResponse = productAbility().getProduct(addedProductResponse.getBody().getId(), credentials(EMAIL));

        // then
        assertEquals(NOT_FOUND, deletedProductResponse.getStatusCode());
        assertFalse(deletedProductResponse.hasBody());
    }

    @Test
    @DisplayName("should not throw exception when trying to delete non existing product")
    public void shouldNotThrowExceptionWhenTryingToDeleteNonExistingProduct() {

        // given
        Long NON_EXISTING_PRODUCT_ID = -10L;

        // when
        ResponseEntity<Void> response = productAbility().deleteProduct(NON_EXISTING_PRODUCT_ID, credentials(EMAIL));

        // then
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    @DisplayName("should return UNPROCESSABLE_ENTITY when trying to delete product with existing offer with it")
    public void shouldReturnUnprocessableEntityWhenTryingToDeleteProductWithOffer() {

        // given
        ResponseEntity<OfferResponse> offerResponse = offerAbility().addOffer(new AddOfferRequest(
                USER_ID,
                BigDecimal.valueOf(10),
                5,
                PRODUCT_ID
        ), credentials(EMAIL));
        assertTrue(offerResponse.getStatusCode().is2xxSuccessful());

        // when
        ResponseEntity<Errors> response = productAbility().deleteProductError(PRODUCT_ID, credentials(EMAIL));

        // then
        assertEquals(UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals(ErrorCode.OFFER_WITH_PRODUCT_EXITS, response.getBody().getErrors().get(0).getCode());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid_token"})
    @NullAndEmptySource
    @DisplayName("should return UNAUTHORIZED error code when request does not contain authentication token")
    public void shouldReturnUnauthorizedWhenRequestDoesNotContainAuthToken(String invalidTokenOrNullOrEmpty) {

        // when
        ResponseEntity<Errors> response = productAbility().deleteProductUnauthorized(PRODUCT_ID, invalidTokenOrNullOrEmpty);

        // then
        assertEquals(UNAUTHORIZED, response.getStatusCode());
    }
}
