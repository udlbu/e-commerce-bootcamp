package com.ecommerce.offer.api;

import com.ecommerce.BaseIntegrationTest;
import com.ecommerce.cart.api.dto.CartResponse;
import com.ecommerce.offer.api.dto.AddOfferRequest;
import com.ecommerce.offer.api.dto.OfferResponse;
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

import static com.ecommerce.IntegrationTestData.EMAIL;
import static com.ecommerce.IntegrationTestData.OFFER_ID;
import static com.ecommerce.IntegrationTestData.PRODUCT_ID;
import static com.ecommerce.IntegrationTestData.USER_ID;
import static com.ecommerce.shared.CredentialsAbility.credentials;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

public class DeleteOfferEndpointTest extends BaseIntegrationTest
        implements UserFeeder {

    @Test
    @DisplayName("should delete offer with given identifier")
    public void shouldDeleteOffer() {

        // given
        BigDecimal ADDED_OFFER_PRICE = new BigDecimal("9.99");
        Integer ADDED_OFFER_QUANTITY = 3;
        AddOfferRequest offerRequest = new AddOfferRequest(
                USER_ID,
                ADDED_OFFER_PRICE,
                ADDED_OFFER_QUANTITY,
                PRODUCT_ID
        );
        ResponseEntity<OfferResponse> addedOfferResponse = offerAbility().addOffer(offerRequest, credentials(EMAIL));
        assertTrue(addedOfferResponse.getStatusCode().is2xxSuccessful());

        // when
        offerAbility().deleteOffer(addedOfferResponse.getBody().getId(), credentials(EMAIL));

        // then
        assertTrue(addedOfferResponse.getStatusCode().is2xxSuccessful());

        // and when
        ResponseEntity<OfferResponse> deletedOfferResponse = offerAbility().getOffer(addedOfferResponse.getBody().getId());

        // then
        assertEquals(NOT_FOUND, deletedOfferResponse.getStatusCode());
        assertFalse(deletedOfferResponse.hasBody());
    }

    @Test
    @DisplayName("should not delete offer that was already bought")
    public void shouldNotDeleteOfferThatWasAlreadyBought() {

        // given
        BigDecimal ADDED_OFFER_PRICE = new BigDecimal("9.99");
        Integer ADDED_OFFER_QUANTITY = 3;
        AddOfferRequest offerRequest = new AddOfferRequest(
                USER_ID,
                ADDED_OFFER_PRICE,
                ADDED_OFFER_QUANTITY,
                PRODUCT_ID
        );
        ResponseEntity<OfferResponse> offerResponse = offerAbility().addOffer(offerRequest, credentials(EMAIL));
        assertTrue(offerResponse.getStatusCode().is2xxSuccessful());
        offerAbility().activateOffer(offerResponse.getBody().getId(), credentials(EMAIL));

        // buy offer
        UserResponse buyer = addBuyerUser("buyer");
        CartResponse cart = addOfferToCart(buyer, offerResponse.getBody());
        submitOrder(cart, buyer);

        // when
        ResponseEntity<Errors> response = offerAbility().deleteOfferError(offerResponse.getBody().getId(), credentials(EMAIL));

        // then
        assertEquals(UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals(ErrorCode.ORDER_WITH_OFFER_EXITS, response.getBody().getErrors().get(0).getCode());
    }

    @Test
    @DisplayName("should not throw exception when trying to delete non existing offer")
    public void shouldNotThrowExceptionWhenTryingToDeleteNonExistingOffer() {

        // given
        Long NON_EXISTING_OFFER_ID = -10L;

        // when
        ResponseEntity<Void> response = offerAbility().deleteOffer(NON_EXISTING_OFFER_ID, credentials(EMAIL));

        // then
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    @DisplayName("should return FORBIDDEN error code when user is trying to delete another user's offer")
    public void shouldReturnForbiddenErrorCodeWhenUserIsTryingToDeleteAnotherUserOffer() {

        // given
        UserResponse someUserNotOfferOwner = addExampleUser();

        // when
        ResponseEntity<Void> response = offerAbility().deleteOffer(OFFER_ID, credentials(someUserNotOfferOwner.getEmail()));

        // then
        assertEquals(FORBIDDEN, response.getStatusCode());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid_token"})
    @NullAndEmptySource
    @DisplayName("should return UNAUTHORIZED error code when request does not contain authentication token")
    public void shouldReturnUnauthorizedWhenRequestDoesNotContainAuthToken(String invalidTokenOrNullOrEmpty) {

        // when
        ResponseEntity<Errors> response = offerAbility().deleteOfferUnauthorized(OFFER_ID, invalidTokenOrNullOrEmpty);

        // then
        assertEquals(UNAUTHORIZED, response.getStatusCode());
    }
}
