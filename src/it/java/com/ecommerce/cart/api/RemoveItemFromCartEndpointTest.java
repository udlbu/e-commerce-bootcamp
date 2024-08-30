package com.ecommerce.cart.api;

import com.ecommerce.BaseIntegrationTest;
import com.ecommerce.cart.api.dto.AddItemToCartRequest;
import com.ecommerce.cart.api.dto.CartResponse;
import com.ecommerce.cart.api.dto.RemoveItemFromCartRequest;
import com.ecommerce.offer.api.dto.AddOfferRequest;
import com.ecommerce.offer.api.dto.OfferResponse;
import com.ecommerce.shared.api.dto.Error;
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
import static com.ecommerce.IntegrationTestData.ITEM_QUANTITY_ONE;
import static com.ecommerce.IntegrationTestData.OFFER_ID;
import static com.ecommerce.IntegrationTestData.PRODUCT_ID;
import static com.ecommerce.IntegrationTestData.USER_ID;
import static com.ecommerce.shared.CredentialsAbility.credentials;
import static com.ecommerce.shared.api.dto.ErrorCode.FIELD_REQUIRED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

public class RemoveItemFromCartEndpointTest extends BaseIntegrationTest implements UserFeeder {

    @Test
    @DisplayName("should remove item from cart when there are 2 items inside")
    public void shouldRemoveItemFromCartWhenThereAreTwoItemsInside() {

        // given
        int EXPECTED_CART_ITEMS_SIZE = 1;
        cartAbility().addItemToCart(new AddItemToCartRequest(
                OFFER_ID,
                USER_ID,
                ITEM_QUANTITY_ONE
        ), credentials(EMAIL));
        // new offer
        ResponseEntity<OfferResponse> offerResponse = offerAbility().addOffer(new AddOfferRequest(
                USER_ID,
                BigDecimal.valueOf(10),
                5,
                PRODUCT_ID
        ), credentials(EMAIL));
        cartAbility().addItemToCart(new AddItemToCartRequest(
                offerResponse.getBody().getId(),
                USER_ID,
                ITEM_QUANTITY_ONE
        ), credentials(EMAIL));
        verifyThatCartHasTwoItems();

        // when
        ResponseEntity<Void> response = cartAbility().removeItemFromCart(new RemoveItemFromCartRequest(
                USER_ID,
                OFFER_ID
        ), credentials(EMAIL));

        // then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        ResponseEntity<CartResponse> cartResponse = cartAbility().getUserCart(USER_ID, credentials(EMAIL));
        assertEquals(USER_ID, cartResponse.getBody().getUserId());
        assertEquals(EXPECTED_CART_ITEMS_SIZE, cartResponse.getBody().getItems().size());
    }

    @Test
    @DisplayName("should remove item from cart when there is 1 item inside")
    public void shouldRemoveItemFromCartWhenThereIsOneItemInside() {

        // given
        cartAbility().addItemToCart(new AddItemToCartRequest(
                OFFER_ID,
                USER_ID,
                ITEM_QUANTITY_ONE
        ), credentials(EMAIL));
        verifyThatCartHasOneItem();

        // when
        ResponseEntity<Void> response = cartAbility().removeItemFromCart(new RemoveItemFromCartRequest(
                USER_ID,
                OFFER_ID
        ), credentials(EMAIL));

        // then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        ResponseEntity<CartResponse> cartResponse = cartAbility().getUserCart(USER_ID, credentials(EMAIL));
        assertTrue(cartResponse.getBody().getItems().isEmpty());
    }

    @Test
    @DisplayName("should return UNPROCESSABLE_ENTITY when trying to remove item and user cart does not exist")
    public void shouldReturnUnprocessableEntityWhenTryingToRemoveItemAndUserCartDoesNotExist() {

        // when
        ResponseEntity<Errors> response = cartAbility().removeItemFromCartError(new RemoveItemFromCartRequest(
                USER_ID,
                OFFER_ID
        ), credentials(EMAIL));

        // then
        assertEquals(UNPROCESSABLE_ENTITY, response.getStatusCode());
    }

    @Test
    @DisplayName("should return BAD_REQUEST error code when request does not contain required data")
    public void shouldReturnBadRequestWhenRequestDoesNotContainRequiredData() {

        // given
        RemoveItemFromCartRequest request = new RemoveItemFromCartRequest(
                null,
                null
        );

        // when
        ResponseEntity<Errors> response = cartAbility().removeItemFromCartError(request, credentials(EMAIL));

        // then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertTrue(response.hasBody());
        assertEquals(2, response.getBody().getErrors().size());
        assertEquals(FIELD_REQUIRED, response.getBody().getErrors().get(0).getCode());
        assertEquals(FIELD_REQUIRED, response.getBody().getErrors().get(1).getCode());
        List<String> missingFields = response.getBody().getErrors().stream().map(Error::getMessage).toList();
        assertTrue(missingFields.contains("userId"));
        assertTrue(missingFields.contains("offerId"));
        assertFalse(response.getBody().getErrors().get(0).getPath().isEmpty());
        assertFalse(response.getBody().getErrors().get(1).getPath().isEmpty());
    }

    @Test
    @DisplayName("should return FORBIDDEN when user is trying to remove cart item from another user's cart")
    public void shouldReturnForbiddenWhenUserIsTryingToRemoveCartItemFromAnotherUserCart() {

        // given
        UserResponse userNotCartOwner = addExampleUser();

        // when
        ResponseEntity<Void> response = cartAbility().removeItemFromCart(
                new RemoveItemFromCartRequest(OFFER_ID, USER_ID),
                credentials(userNotCartOwner.getEmail()));

        // then
        assertEquals(FORBIDDEN, response.getStatusCode());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid_token"})
    @NullAndEmptySource
    @DisplayName("should return UNAUTHORIZED error code when request does not contain authentication token")
    public void shouldReturnUnauthorizedWhenRequestDoesNotContainAuthToken(String invalidTokenOrNullOrEmpty) {

        // given
        RemoveItemFromCartRequest request = new RemoveItemFromCartRequest(
                USER_ID,
                OFFER_ID
        );

        // when
        ResponseEntity<Errors> response = cartAbility().removeItemFromCartUnauthorized(request, invalidTokenOrNullOrEmpty);

        // then
        assertEquals(UNAUTHORIZED, response.getStatusCode());
    }

    private void verifyThatCartHasTwoItems() {
        ResponseEntity<CartResponse> cartResponse = cartAbility().getUserCart(USER_ID, credentials(EMAIL));
        assertEquals(2, cartResponse.getBody().getItems().size());
    }

    private void verifyThatCartHasOneItem() {
        ResponseEntity<CartResponse> cartResponse = cartAbility().getUserCart(USER_ID, credentials(EMAIL));
        assertEquals(1, cartResponse.getBody().getItems().size());
    }
}
