package com.ecommerce.offer.api;

import com.ecommerce.BaseIntegrationTest;
import com.ecommerce.cart.api.dto.CartResponse;
import com.ecommerce.offer.api.dto.AddOfferRequest;
import com.ecommerce.offer.api.dto.ModifyOfferRequest;
import com.ecommerce.offer.api.dto.OfferResponse;
import com.ecommerce.product.api.dto.ProductResponse;
import com.ecommerce.shared.Assertions;
import com.ecommerce.shared.api.dto.Error;
import com.ecommerce.shared.api.dto.ErrorCode;
import com.ecommerce.shared.api.dto.Errors;
import com.ecommerce.user.UserFeeder;
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
import static com.ecommerce.IntegrationTestData.NON_EXISTING_ID;
import static com.ecommerce.IntegrationTestData.PRODUCT_ID;
import static com.ecommerce.IntegrationTestData.USER_ID;
import static com.ecommerce.IntegrationTestData.VERSION;
import static com.ecommerce.shared.CredentialsAbility.credentials;
import static com.ecommerce.shared.api.dto.ErrorCode.FIELD_REQUIRED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

public class ModifyOfferEndpointTest extends BaseIntegrationTest implements UserFeeder {

    @Test
    @DisplayName("should update offer data and increment version")
    public void shouldUpdateAllOfferDataAndIncrementVersion() {

        // given
        ResponseEntity<OfferResponse> newOffer = addSampleOffer();
        ResponseEntity<ProductResponse> newProduct = addSampleProduct();

        BigDecimal MODIFIED_OFFER_PRICE = BigDecimal.valueOf(43);
        Integer MODIFIED_OFFER_QUANTITY = 99;
        Long MODIFIED_OFFER_PRODUCT = newProduct.getBody().getId();

        ModifyOfferRequest modifyOfferRequest = new ModifyOfferRequest(
                newOffer.getBody().getId(),
                newOffer.getBody().getUserId(),
                newOffer.getBody().getVersion(),
                MODIFIED_OFFER_PRICE,
                MODIFIED_OFFER_QUANTITY,
                MODIFIED_OFFER_PRODUCT
        );

        // when
        ResponseEntity<OfferResponse> updatedOffer = offerAbility().modifyOffer(modifyOfferRequest, credentials(EMAIL));

        // then
        assertTrue(updatedOffer.getStatusCode().is2xxSuccessful());
        assertTrue(updatedOffer.hasBody());
        assertEquals(newOffer.getBody().getId(), updatedOffer.getBody().getId());
        Assertions.assertBigDecimalEquals(MODIFIED_OFFER_PRICE, updatedOffer.getBody().getPrice());
        assertEquals(MODIFIED_OFFER_QUANTITY, updatedOffer.getBody().getQuantity());
        assertEquals(newOffer.getBody().getUserId(), updatedOffer.getBody().getUserId());
        assertEquals(newOffer.getBody().getStatus(), updatedOffer.getBody().getStatus());
        assertEquals(newOffer.getBody().getVersion() + 1, updatedOffer.getBody().getVersion());
        assertEquals(newProduct.getBody().getId(), updatedOffer.getBody().getProduct().getId());
        assertEquals(newProduct.getBody().getName(), updatedOffer.getBody().getProduct().getName());
        assertEquals(newProduct.getBody().getEan(), updatedOffer.getBody().getProduct().getEan());
        assertEquals(newProduct.getBody().getImageUrl(), updatedOffer.getBody().getProduct().getImageUrl());
        assertEquals(newProduct.getBody().getDescription(), updatedOffer.getBody().getProduct().getDescription());
        assertEquals(newProduct.getBody().getVersion(), updatedOffer.getBody().getProduct().getVersion());
    }

    @Test
    @DisplayName("should not update offer when all modifiable fields are null in modification request")
    public void shouldNotUpdateOfferWhenAllModifiableFieldsAreNullInRequest() {

        // given
        ResponseEntity<OfferResponse> newOffer = addSampleOffer();
        ModifyOfferRequest modifyOfferRequest = new ModifyOfferRequest(
                newOffer.getBody().getId(),
                newOffer.getBody().getUserId(),
                newOffer.getBody().getVersion(),
                null,
                null,
                null
        );

        // when
        ResponseEntity<OfferResponse> updatedOffer = offerAbility().modifyOffer(modifyOfferRequest, credentials(EMAIL));

        // then
        assertTrue(updatedOffer.getStatusCode().is2xxSuccessful());
        assertTrue(updatedOffer.hasBody());
        assertEquals(newOffer.getBody().getId(), updatedOffer.getBody().getId());
        Assertions.assertBigDecimalEquals(newOffer.getBody().getPrice(), updatedOffer.getBody().getPrice());
        assertEquals(newOffer.getBody().getQuantity(), updatedOffer.getBody().getQuantity());
        assertEquals(newOffer.getBody().getUserId(), updatedOffer.getBody().getUserId());
        assertEquals(newOffer.getBody().getStatus(), updatedOffer.getBody().getStatus());
        assertEquals(newOffer.getBody().getVersion(), updatedOffer.getBody().getVersion());
        assertEquals(newOffer.getBody().getProduct().getId(), updatedOffer.getBody().getProduct().getId());
        assertEquals(newOffer.getBody().getProduct().getName(), updatedOffer.getBody().getProduct().getName());
        assertEquals(newOffer.getBody().getProduct().getEan(), updatedOffer.getBody().getProduct().getEan());
        assertEquals(newOffer.getBody().getProduct().getImageUrl(), updatedOffer.getBody().getProduct().getImageUrl());
        assertEquals(newOffer.getBody().getProduct().getDescription(), updatedOffer.getBody().getProduct().getDescription());
        assertEquals(newOffer.getBody().getProduct().getVersion(), updatedOffer.getBody().getProduct().getVersion());
    }

    @Test
    @DisplayName("should not modify offer product when offer was already bought")
    public void shouldNotModifyOfferProductWhenOfferWasAlreadyBought() {

        // given
        ResponseEntity<OfferResponse> offer = addSampleOffer();
        offerAbility().activateOffer(offer.getBody().getId(), credentials(EMAIL));

        // buy offer
        UserResponse buyer = addBuyerUser("buyer");
        CartResponse cart = addOfferToCart(buyer, offer.getBody());
        submitOrder(cart, buyer);

        // fetch offer to get the latest version number because it was modified by activation and submit
        ResponseEntity<OfferResponse> actualOffer = offerAbility().getOffer(offer.getBody().getId());

        // new product
        ResponseEntity<ProductResponse> newProduct = addSampleProduct();
        Long MODIFIED_OFFER_PRODUCT = newProduct.getBody().getId();

        // modify offer with new product
        ModifyOfferRequest modifyOfferRequest = new ModifyOfferRequest(
                actualOffer.getBody().getId(),
                actualOffer.getBody().getUserId(),
                actualOffer.getBody().getVersion(),
                actualOffer.getBody().getPrice(),
                actualOffer.getBody().getQuantity(),
                MODIFIED_OFFER_PRODUCT
        );

        // when
        ResponseEntity<Errors> response = offerAbility().modifyOfferError(modifyOfferRequest, credentials(EMAIL));

        // then
        assertEquals(UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals(ErrorCode.ORDER_WITH_OFFER_EXITS, response.getBody().getErrors().get(0).getCode());
    }

    @Test
    @DisplayName("should not modify offer price when offer was already bought")
    public void shouldNotModifyOfferPriceWhenOfferWasAlreadyBought() {
        // given
        ResponseEntity<OfferResponse> offer = addSampleOffer();
        offerAbility().activateOffer(offer.getBody().getId(), credentials(EMAIL));

        // buy offer
        UserResponse buyer = addBuyerUser("buyer");
        CartResponse cart = addOfferToCart(buyer, offer.getBody());
        submitOrder(cart, buyer);

        // fetch offer to get the latest version number because it was modified by activation and submit
        ResponseEntity<OfferResponse> actualOffer = offerAbility().getOffer(offer.getBody().getId());

        // modify offer with new product
        BigDecimal NEW_PRICE = BigDecimal.valueOf(100);
        ModifyOfferRequest modifyOfferRequest = new ModifyOfferRequest(
                actualOffer.getBody().getId(),
                actualOffer.getBody().getUserId(),
                actualOffer.getBody().getVersion(),
                NEW_PRICE,
                actualOffer.getBody().getQuantity(),
                actualOffer.getBody().getProduct().getId()
        );

        // when
        ResponseEntity<Errors> response = offerAbility().modifyOfferError(modifyOfferRequest, credentials(EMAIL));

        // then
        assertEquals(UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals(ErrorCode.ORDER_WITH_OFFER_EXITS, response.getBody().getErrors().get(0).getCode());
    }

    @Test
    @DisplayName("should throw exception if product was modified by concurrent transaction")
    public void shouldThrowExceptionIfProductWasModifiedByConcurrentTransaction() {

        // given
        ResponseEntity<OfferResponse> newOffer = addSampleOffer();

        // and
        simulateConcurrentUpdate(newOffer.getBody().getId(), newOffer.getBody().getUserId(), newOffer.getBody().getVersion());

        // when
        BigDecimal MODIFIED_OFFER_PRICE = BigDecimal.valueOf(43);
        Integer MODIFIED_OFFER_QUANTITY = 99;

        ModifyOfferRequest modifyOfferRequest = new ModifyOfferRequest(
                newOffer.getBody().getId(),
                newOffer.getBody().getUserId(),
                newOffer.getBody().getVersion(),
                MODIFIED_OFFER_PRICE,
                MODIFIED_OFFER_QUANTITY,
                null
        );

        ResponseEntity<Errors> errorResponse = offerAbility().modifyOfferError(modifyOfferRequest, credentials(EMAIL));

        // then
        assertTrue(errorResponse.getStatusCode().is5xxServerError());
        assertTrue(errorResponse.hasBody());
        assertEquals(ErrorCode.CONCURRENT_MODIFICATION_ERROR, errorResponse.getBody().getErrors().get(0).getCode());
    }


    @Test
    @DisplayName("should return BAD_REQUEST error code when request does not contain required data")
    public void shouldReturnBadRequestWhenRequestDoesNotContainRequiredData() {

        // given
        int EXPECTED_ERRORS_NUMBER = 3;

        // when
        ResponseEntity<Errors> response = offerAbility().modifyOfferError(new ModifyOfferRequest(), credentials(EMAIL));

        // then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertTrue(response.hasBody());
        assertEquals(EXPECTED_ERRORS_NUMBER, response.getBody().getErrors().size());
        assertEquals(FIELD_REQUIRED, response.getBody().getErrors().get(0).getCode());
        assertEquals(FIELD_REQUIRED, response.getBody().getErrors().get(1).getCode());
        assertEquals(FIELD_REQUIRED, response.getBody().getErrors().get(2).getCode());
        List<String> missingFields = response.getBody().getErrors().stream().map(Error::getMessage).toList();
        assertTrue(missingFields.contains("offerId"));
        assertTrue(missingFields.contains("userId"));
        assertTrue(missingFields.contains("version"));
    }

    @Test
    @DisplayName("should return NOT_FOUND error code when trying to update not existing offer")
    public void shouldReturnNotFoundWhenTryingToUpdateNotExistingOffer() {

        ModifyOfferRequest modifyOfferRequest = new ModifyOfferRequest(
                NON_EXISTING_ID,
                USER_ID,
                VERSION,
                null,
                null,
                null
        );

        // when
        ResponseEntity<OfferResponse> updatedOffer = offerAbility().modifyOffer(modifyOfferRequest, credentials(EMAIL));

        // then
        assertEquals(NOT_FOUND, updatedOffer.getStatusCode());
        assertFalse(updatedOffer.hasBody());
    }

    @Test
    @DisplayName("should return error when trying to modify offer with non existing product")
    public void shouldReturnErrorWhenTryingToModifyOfferWithNonExistingProduct() {

        // given
        ResponseEntity<OfferResponse> newOffer = addSampleOffer();
        ModifyOfferRequest modifyOfferRequest = new ModifyOfferRequest(
                newOffer.getBody().getId(),
                newOffer.getBody().getUserId(),
                newOffer.getBody().getVersion(),
                null,
                null,
                NON_EXISTING_ID
        );

        // when
        ResponseEntity<OfferResponse> updatedOffer = offerAbility().modifyOffer(modifyOfferRequest, credentials(EMAIL));

        // then
        assertTrue(updatedOffer.getStatusCode().is5xxServerError());
    }

    @Test
    @DisplayName("should return FORBIDDEN error code when user is to modify another user's offer")
    public void shouldReturnForbiddenErrorCodeWhenUserIsTryingToModifyAnotherUserOffer() {

        // given
        ResponseEntity<OfferResponse> newOffer = addSampleOffer();

        BigDecimal NEW_PRICE = BigDecimal.valueOf(100);

        ModifyOfferRequest modifyOfferRequest = new ModifyOfferRequest(
                newOffer.getBody().getId(),
                newOffer.getBody().getUserId(),
                newOffer.getBody().getVersion(),
                NEW_PRICE,
                newOffer.getBody().getQuantity(),
                newOffer.getBody().getProduct().getId()
        );

        UserResponse someUserNotOfferOwner = addExampleUser();

        // when
        ResponseEntity<OfferResponse> response = offerAbility().modifyOffer(modifyOfferRequest, credentials(someUserNotOfferOwner.getEmail()));

        // then
        assertEquals(FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("should return FORBIDDEN error code when userId in request body is not id of offer owner")
    public void shouldReturnForbiddenErrorCodeWhenUserIdInRequestIsNotIdOfOfferOwner() {

        // given
        ResponseEntity<OfferResponse> newOffer = addSampleOffer();

        BigDecimal NEW_PRICE = BigDecimal.valueOf(100);

        Long SOME_WRONG_USER_ID = 10L;

        ModifyOfferRequest modifyOfferRequest = new ModifyOfferRequest(
                newOffer.getBody().getId(),
                SOME_WRONG_USER_ID,
                newOffer.getBody().getVersion(),
                NEW_PRICE,
                newOffer.getBody().getQuantity(),
                newOffer.getBody().getProduct().getId()
        );

        // when
        ResponseEntity<OfferResponse> response = offerAbility().modifyOffer(modifyOfferRequest, credentials(EMAIL));

        // then
        assertEquals(FORBIDDEN, response.getStatusCode());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid_token"})
    @NullAndEmptySource
    @DisplayName("should return UNAUTHORIZED error code when request does not contain authentication token")
    public void shouldReturnUnauthorizedWhenRequestDoesNotContainAuthToken(String invalidTokenOrNullOrEmpty) {

        // given
        ResponseEntity<OfferResponse> newOffer = addSampleOffer();

        BigDecimal NEW_PRICE = BigDecimal.valueOf(100);

        ModifyOfferRequest modifyOfferRequest = new ModifyOfferRequest(
                newOffer.getBody().getId(),
                newOffer.getBody().getUserId(),
                newOffer.getBody().getVersion(),
                NEW_PRICE,
                newOffer.getBody().getQuantity(),
                newOffer.getBody().getProduct().getId()
        );

        // when
        ResponseEntity<Errors> response = offerAbility().modifyOfferUnauthorized(modifyOfferRequest, invalidTokenOrNullOrEmpty);

        // then
        assertEquals(UNAUTHORIZED, response.getStatusCode());
    }

    private ResponseEntity<OfferResponse> addSampleOffer() {
        AddOfferRequest offerRequest = new AddOfferRequest(
                USER_ID,
                new BigDecimal("9.99"),
                3,
                PRODUCT_ID
        );
        return offerAbility().addOffer(offerRequest, credentials(EMAIL));
    }

    private void simulateConcurrentUpdate(Long id, Long userId, Long version) {
        ModifyOfferRequest modifyOfferRequest = new ModifyOfferRequest(
                id,
                userId,
                version,
                BigDecimal.valueOf(1.99),
                null,
                null
        );
        offerAbility().modifyOffer(modifyOfferRequest, credentials(EMAIL));
    }
}
