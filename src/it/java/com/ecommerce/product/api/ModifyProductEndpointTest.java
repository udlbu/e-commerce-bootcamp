package com.ecommerce.product.api;

import com.ecommerce.BaseIntegrationTest;
import com.ecommerce.cart.api.dto.CartResponse;
import com.ecommerce.offer.api.dto.AddOfferRequest;
import com.ecommerce.offer.api.dto.OfferResponse;
import com.ecommerce.order.OrderFeeder;
import com.ecommerce.product.api.dto.ModifyProductRequest;
import com.ecommerce.product.api.dto.ProductResponse;
import com.ecommerce.shared.api.dto.Error;
import com.ecommerce.shared.api.dto.ErrorCode;
import com.ecommerce.shared.api.dto.Errors;
import com.ecommerce.user.api.dto.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static com.ecommerce.IntegrationTestData.EMAIL;
import static com.ecommerce.IntegrationTestData.IMAGE;
import static com.ecommerce.IntegrationTestData.IMAGE_NAME_PATTERN;
import static com.ecommerce.IntegrationTestData.PRODUCT_ID;
import static com.ecommerce.IntegrationTestData.USER_ID;
import static com.ecommerce.IntegrationTestData.VERSION;
import static com.ecommerce.shared.CredentialsAbility.credentials;
import static com.ecommerce.shared.api.dto.ErrorCode.FIELD_REQUIRED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

public class ModifyProductEndpointTest extends BaseIntegrationTest implements OrderFeeder {

    @Test
    @DisplayName("should update all product data and increment version")
    public void shouldUpdateAllProductDataAndIncrementVersion() {

        // given
        ResponseEntity<ProductResponse> newProduct = addSampleProduct();

        String MODIFIED_PRODUCT_NAME = "modified-product-name";
        String MODIFIED_PRODUCT_EAN = "222";
        String MODIFIED_PRODUCT_CATEGORY = "BOOKS";
        String MODIFIED_PRODUCT_DESCRIPTION = "modified-product-description";
        ModifyProductRequest modifyProductRequest = new ModifyProductRequest(
                newProduct.getBody().getId(),
                MODIFIED_PRODUCT_NAME,
                MODIFIED_PRODUCT_EAN,
                MODIFIED_PRODUCT_CATEGORY,
                IMAGE,
                MODIFIED_PRODUCT_DESCRIPTION,
                newProduct.getBody().getVersion()
        );

        // verify image was uploaded during adding product
        cdnAbility().verifyUploadImageWasCalledOnce();
        cdnAbility().reset();

        // when
        ResponseEntity<ProductResponse> updatedProduct = productAbility().modifyProduct(modifyProductRequest, credentials(EMAIL));

        // then
        assertTrue(updatedProduct.getStatusCode().is2xxSuccessful());
        assertTrue(updatedProduct.hasBody());
        assertEquals(newProduct.getBody().getId(), updatedProduct.getBody().getId());
        assertEquals(MODIFIED_PRODUCT_NAME, updatedProduct.getBody().getName());
        assertEquals(MODIFIED_PRODUCT_EAN, updatedProduct.getBody().getEan());
        assertEquals(MODIFIED_PRODUCT_CATEGORY, updatedProduct.getBody().getCategory());
        assertTrue(updatedProduct.getBody().getImageUrl().matches("^" + cdnProperties.getImgUrl() + IMAGE_NAME_PATTERN + "$"));
        assertEquals(MODIFIED_PRODUCT_DESCRIPTION, updatedProduct.getBody().getDescription());
        assertEquals(newProduct.getBody().getVersion() + 1, updatedProduct.getBody().getVersion());

        // cdn
        cdnAbility().verifyUploadImageWasCalledOnce();
    }

    @Test
    @DisplayName("should not call CDN service when image is not present in product update request")
    public void shouldNotCallCdnServiceWhenImageIsNotPresent() {

        // given
        ResponseEntity<ProductResponse> newProduct = addSampleProduct();

        String MODIFIED_PRODUCT_NAME = "modified-product-name";
        String MODIFIED_PRODUCT_EAN = "222";
        String MODIFIED_PRODUCT_CATEGORY = "BOOKS";
        String MODIFIED_PRODUCT_DESCRIPTION = "modified-product-description";
        ModifyProductRequest modifyProductRequest = new ModifyProductRequest(
                newProduct.getBody().getId(),
                MODIFIED_PRODUCT_NAME,
                MODIFIED_PRODUCT_EAN,
                MODIFIED_PRODUCT_CATEGORY,
                null,
                MODIFIED_PRODUCT_DESCRIPTION,
                newProduct.getBody().getVersion()
        );

        // verify image was uploaded during adding product
        cdnAbility().verifyUploadImageWasCalledOnce();
        cdnAbility().reset();

        // when
        ResponseEntity<ProductResponse> updatedProduct = productAbility().modifyProduct(modifyProductRequest, credentials(EMAIL));

        // then
        assertTrue(updatedProduct.getStatusCode().is2xxSuccessful());
        assertTrue(updatedProduct.hasBody());
        assertEquals(newProduct.getBody().getId(), updatedProduct.getBody().getId());
        assertEquals(MODIFIED_PRODUCT_NAME, updatedProduct.getBody().getName());
        assertEquals(MODIFIED_PRODUCT_EAN, updatedProduct.getBody().getEan());
        assertEquals(MODIFIED_PRODUCT_CATEGORY, updatedProduct.getBody().getCategory());
        // if image had existed it should not be removed
        assertEquals(newProduct.getBody().getImageUrl(), updatedProduct.getBody().getImageUrl());
        assertEquals(MODIFIED_PRODUCT_DESCRIPTION, updatedProduct.getBody().getDescription());
        assertEquals(newProduct.getBody().getVersion() + 1, updatedProduct.getBody().getVersion());

        // cdn
        cdnAbility().verifyUploadImageWasNotCalled();
    }

    @Test
    @DisplayName("should return UNPROCESSABLE_ENTITY when trying to modify product that was already bought")
    public void shouldReturnUnprocessableEntityWhenTryingToModifyProductThatWasAlreadyBought() {

        // given
        String MODIFIED_PRODUCT_NAME = "modified-product-name";
        String MODIFIED_PRODUCT_EAN = "222";
        String MODIFIED_PRODUCT_CATEGORY = "BOOKS";
        String MODIFIED_PRODUCT_DESCRIPTION = "modified-product-description";
        ModifyProductRequest modifyProductRequest = new ModifyProductRequest(
                PRODUCT_ID,
                MODIFIED_PRODUCT_NAME,
                MODIFIED_PRODUCT_EAN,
                MODIFIED_PRODUCT_CATEGORY,
                null,
                MODIFIED_PRODUCT_DESCRIPTION,
                VERSION
        );

        // add offer
        ResponseEntity<OfferResponse> offerResponse = offerAbility().addOffer(new AddOfferRequest(
                USER_ID,
                BigDecimal.valueOf(10),
                5,
                PRODUCT_ID
        ), credentials(EMAIL));
        assertTrue(offerResponse.getStatusCode().is2xxSuccessful());
        offerAbility().activateOffer(offerResponse.getBody().getId(), credentials(EMAIL));

        // buy offer
        UserResponse buyer = addBuyerUser("buyer");
        CartResponse cart = addOfferToCart(buyer, offerResponse.getBody());
        submitOrder(cart, buyer);

        // when
        ResponseEntity<Errors> response = productAbility().modifyProductError(modifyProductRequest, credentials(EMAIL));

        // then
        assertEquals(UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals(ErrorCode.ORDER_WITH_PRODUCT_EXITS, response.getBody().getErrors().get(0).getCode());
    }

    @Test
    @DisplayName("should throw exception if product was modified by concurrent transaction")
    public void shouldThrowExceptionIfProductWasModifiedByConcurrentTransaction() {

        // given
        ResponseEntity<ProductResponse> newProduct = addSampleProduct();

        // and
        simulateConcurrentUpdate(newProduct.getBody().getId(), newProduct.getBody().getVersion());

        // when
        String MODIFIED_PRODUCT_NAME = "modified-product-name";
        String MODIFIED_PRODUCT_EAN = "1000";
        String MODIFIED_PRODUCT_CATEGORY = "BOOKS";
        ModifyProductRequest modifyProductRequest = new ModifyProductRequest(
                newProduct.getBody().getId(),
                MODIFIED_PRODUCT_NAME,
                MODIFIED_PRODUCT_EAN,
                MODIFIED_PRODUCT_CATEGORY,
                null,
                null,
                newProduct.getBody().getVersion()
        );

        ResponseEntity<Errors> errorResponse = productAbility().modifyProductError(modifyProductRequest, credentials(EMAIL));

        // then
        assertTrue(errorResponse.getStatusCode().is5xxServerError());
        assertTrue(errorResponse.hasBody());
        assertEquals(ErrorCode.CONCURRENT_MODIFICATION_ERROR, errorResponse.getBody().getErrors().get(0).getCode());
    }

    @Test
    @DisplayName("should return BAD_REQUEST error code when request does not contain required data")
    public void shouldReturnBadRequestWhenRequestDoesNotContainRequiredData() {

        // given
        int EXPECTED_ERRORS_NUMBER = 5;

        // when
        ResponseEntity<Errors> response = productAbility().modifyProductError(new ModifyProductRequest(), credentials(EMAIL));

        // then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertTrue(response.hasBody());
        assertEquals(EXPECTED_ERRORS_NUMBER, response.getBody().getErrors().size());
        assertEquals(FIELD_REQUIRED, response.getBody().getErrors().get(0).getCode());
        assertEquals(FIELD_REQUIRED, response.getBody().getErrors().get(1).getCode());
        assertEquals(FIELD_REQUIRED, response.getBody().getErrors().get(2).getCode());
        assertEquals(FIELD_REQUIRED, response.getBody().getErrors().get(3).getCode());
        assertEquals(FIELD_REQUIRED, response.getBody().getErrors().get(4).getCode());
        List<String> missingFields = response.getBody().getErrors().stream().map(Error::getMessage).toList();
        assertTrue(missingFields.contains("ean"));
        assertTrue(missingFields.contains("name"));
        assertTrue(missingFields.contains("id"));
        assertTrue(missingFields.contains("version"));
        assertTrue(missingFields.contains("category"));
    }

    @Test
    @DisplayName("should return NOT_FOUND error code when trying to update not existing product")
    public void shouldReturnNotFoundWhenTryingToUpdateNotExistingProduct() {

        String MODIFIED_PRODUCT_NAME = "modified-product-name";
        String MODIFIED_PRODUCT_EAN = "222";
        String MODIFIED_PRODUCT_CATEGORY = "BOOKS";
        String MODIFIED_PRODUCT_DESCRIPTION = "modified-product-description";
        ModifyProductRequest modifyProductRequest = new ModifyProductRequest(
                -100L,
                MODIFIED_PRODUCT_NAME,
                MODIFIED_PRODUCT_EAN,
                MODIFIED_PRODUCT_CATEGORY,
                IMAGE,
                MODIFIED_PRODUCT_DESCRIPTION,
                0L
        );

        // when
        ResponseEntity<ProductResponse> updatedProduct = productAbility().modifyProduct(modifyProductRequest, credentials(EMAIL));

        // then
        assertEquals(NOT_FOUND, updatedProduct.getStatusCode());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid_token"})
    @NullAndEmptySource
    @DisplayName("should return UNAUTHORIZED error code when request does not contain authentication token")
    public void shouldReturnUnauthorizedWhenRequestDoesNotContainAuthToken(String invalidTokenOrNullOrEmpty) {

        // given
        String MODIFIED_PRODUCT_NAME = "modified-product-name";
        String MODIFIED_PRODUCT_EAN = "222";
        String MODIFIED_PRODUCT_CATEGORY = "BOOKS";
        String MODIFIED_PRODUCT_IMAGE = "modified-product-image";
        String MODIFIED_PRODUCT_DESCRIPTION = "modified-product-description";
        ModifyProductRequest modifyProductRequest = new ModifyProductRequest(
                PRODUCT_ID,
                MODIFIED_PRODUCT_NAME,
                MODIFIED_PRODUCT_EAN,
                MODIFIED_PRODUCT_CATEGORY,
                MODIFIED_PRODUCT_IMAGE,
                MODIFIED_PRODUCT_DESCRIPTION,
                VERSION
        );

        // when
        ResponseEntity<Errors> response = productAbility().modifyProductUnauthorized(modifyProductRequest, invalidTokenOrNullOrEmpty);

        // then
        assertEquals(UNAUTHORIZED, response.getStatusCode());
    }

    private void simulateConcurrentUpdate(Long id, Long version) {
        ModifyProductRequest modifyProductRequest = new ModifyProductRequest(
                id,
                "some-modification",
                "1000",
                "BOOKS",
                null,
                null,
                version
        );
        productAbility().modifyProduct(modifyProductRequest, credentials(EMAIL));
    }
}
