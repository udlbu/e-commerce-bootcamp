package com.ecommerce.cart.api;

import com.ecommerce.BaseIntegrationTest;
import com.ecommerce.cart.api.dto.AddItemToCartRequest;
import com.ecommerce.cart.api.dto.CartItemResponse;
import com.ecommerce.cart.api.dto.CartResponse;
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
import static com.ecommerce.IntegrationTestData.NON_EXISTING_ID;
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

public class AddItemToCartEndpointTest extends BaseIntegrationTest implements UserFeeder {

    @ParameterizedTest
    @ValueSource(ints = {1, 3})
    @DisplayName("should add item to new cart")
    public void shouldAddItemToNewCart(Integer itemQuantity) {

        // given
        int EXPECTED_CART_ITEMS_SIZE = 1;
        int EXPECTED_CART_ITEM_QUANTITY = itemQuantity;

        // when
        ResponseEntity<Void> response = cartAbility().addItemToCart(new AddItemToCartRequest(
                OFFER_ID,
                USER_ID,
                itemQuantity
        ), credentials(EMAIL));

        // then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        ResponseEntity<CartResponse> cartResponse = cartAbility().getUserCart(USER_ID, credentials(EMAIL));
        assertEquals(USER_ID, cartResponse.getBody().getUserId());
        assertEquals(EXPECTED_CART_ITEMS_SIZE, cartResponse.getBody().getItems().size());
        assertEquals(EXPECTED_CART_ITEM_QUANTITY, cartResponse.getBody().getItems().get(0).getQuantity());
        assertEquals(OFFER_ID, cartResponse.getBody().getItems().get(0).getOfferId());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3})
    @DisplayName("should add item to the existing cart")
    public void shouldAddItemToTheExistingCart(Integer itemQuantity) {

        // given
        int EXPECTED_CART_ITEMS_SIZE = 2;
        int EXPECTED_FIRST_OFFER_CART_ITEM_QUANTITY = 1;
        int EXPECTED_SECOND_OFFER_CART_ITEM_QUANTITY = itemQuantity;
        cartAbility().addItemToCart(new AddItemToCartRequest(
                OFFER_ID,
                USER_ID,
                ITEM_QUANTITY_ONE
        ), credentials(EMAIL));

        ResponseEntity<OfferResponse> offerResponse = offerAbility().addOffer(new AddOfferRequest(
                USER_ID,
                BigDecimal.valueOf(10),
                5,
                PRODUCT_ID
        ), credentials(EMAIL));

        // when
        ResponseEntity<Void> response = cartAbility().addItemToCart(new AddItemToCartRequest(
                offerResponse.getBody().getId(),
                USER_ID,
                itemQuantity
        ), credentials(EMAIL));

        // then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        ResponseEntity<CartResponse> cartResponse = cartAbility().getUserCart(USER_ID, credentials(EMAIL));
        assertEquals(USER_ID, cartResponse.getBody().getUserId());
        assertEquals(EXPECTED_CART_ITEMS_SIZE, cartResponse.getBody().getItems().size());
        CartItemResponse firstOfferCartItem = cartResponse.getBody().getItems().stream().filter(item -> item.getOfferId().equals(OFFER_ID)).findFirst().get();
        assertEquals(EXPECTED_FIRST_OFFER_CART_ITEM_QUANTITY, firstOfferCartItem.getQuantity());
        CartItemResponse secondOfferCartItem = cartResponse.getBody().getItems().stream().filter(item -> item.getOfferId().equals(offerResponse.getBody().getId())).findFirst().get();
        assertEquals(EXPECTED_SECOND_OFFER_CART_ITEM_QUANTITY, secondOfferCartItem.getQuantity());
        List<Long> offersIds = cartResponse.getBody().getItems().stream().map(CartItemResponse::getOfferId).toList();
        assertTrue(offersIds.contains(OFFER_ID));
        assertTrue(offersIds.contains(offerResponse.getBody().getId()));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3})
    @DisplayName("should add item and increment quantity when the offer has been added before")
    public void shouldAddItemAndIncrementQuantityWhenOfferHasBeenAddedBefore(Integer secondRequestQuantity) {

        // given
        int firstRequestQuantity = 1;

        // add first offer
        cartAbility().addItemToCart(new AddItemToCartRequest(
                OFFER_ID,
                USER_ID,
                firstRequestQuantity
        ), credentials(EMAIL));

        // when
        ResponseEntity<Void> response = cartAbility().addItemToCart(new AddItemToCartRequest(
                OFFER_ID,
                USER_ID,
                secondRequestQuantity
        ), credentials(EMAIL));

        // then
        int EXPECTED_CART_ITEMS_SIZE = 1;
        int EXPECTED_CART_ITEM_QUANTITY = firstRequestQuantity + secondRequestQuantity;
        assertTrue(response.getStatusCode().is2xxSuccessful());
        ResponseEntity<CartResponse> cartResponse = cartAbility().getUserCart(USER_ID, credentials(EMAIL));
        assertEquals(USER_ID, cartResponse.getBody().getUserId());
        assertEquals(EXPECTED_CART_ITEMS_SIZE, cartResponse.getBody().getItems().size());
        assertEquals(EXPECTED_CART_ITEM_QUANTITY, cartResponse.getBody().getItems().get(0).getQuantity());
        assertEquals(OFFER_ID, cartResponse.getBody().getItems().get(0).getOfferId());
    }

    @Test
    @DisplayName("should return UNPROCESSABLE_ENTITY when trying to add non existing offer")
    public void shouldReturnUnprocessableEntityWhenTryingToAddNonExistingOffer() {

        // given
        AddItemToCartRequest request = new AddItemToCartRequest(
                NON_EXISTING_ID,
                USER_ID,
                ITEM_QUANTITY_ONE
        );
        // when
        ResponseEntity<Void> response = cartAbility().addItemToCart(request, credentials(EMAIL));

        // then
        assertEquals(UNPROCESSABLE_ENTITY, response.getStatusCode());
    }

    @Test
    @DisplayName("should return BAD_REQUEST error code when request does not contain required data")
    public void shouldReturnBadRequestWhenRequestDoesNotContainRequiredData() {

        // given
        Integer EXPECTED_NUMBER_OF_REQUIRED_FIELDS = 3;
        AddItemToCartRequest request = new AddItemToCartRequest(
                null,
                null,
                null
        );

        // when
        ResponseEntity<Errors> response = cartAbility().addItemToCartError(request, credentials(EMAIL));

        // then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertTrue(response.hasBody());
        assertEquals(EXPECTED_NUMBER_OF_REQUIRED_FIELDS, response.getBody().getErrors().size());
        assertEquals(FIELD_REQUIRED, response.getBody().getErrors().get(0).getCode());
        assertEquals(FIELD_REQUIRED, response.getBody().getErrors().get(1).getCode());
        assertEquals(FIELD_REQUIRED, response.getBody().getErrors().get(2).getCode());
        List<String> missingFields = response.getBody().getErrors().stream().map(Error::getMessage).toList();
        assertTrue(missingFields.contains("userId"));
        assertTrue(missingFields.contains("offerId"));
        assertTrue(missingFields.contains("quantity"));
        assertFalse(response.getBody().getErrors().get(0).getPath().isEmpty());
        assertFalse(response.getBody().getErrors().get(1).getPath().isEmpty());
        assertFalse(response.getBody().getErrors().get(2).getPath().isEmpty());
    }

    @Test
    @DisplayName("should return BAD_REQUEST error code when request quantity is zero")
    public void shouldReturnBadRequestWhenRequestQuantityIsZero() {

        // given
        Integer EXPECTED_NUMBER_OF_REQUIRED_FIELDS = 1;
        AddItemToCartRequest request = new AddItemToCartRequest(
                OFFER_ID,
                USER_ID,
                0
        );

        // when
        ResponseEntity<Errors> response = cartAbility().addItemToCartError(request, credentials(EMAIL));

        // then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertTrue(response.hasBody());
        assertEquals(EXPECTED_NUMBER_OF_REQUIRED_FIELDS, response.getBody().getErrors().size());
        assertEquals(FIELD_REQUIRED, response.getBody().getErrors().get(0).getCode());
        List<String> missingFields = response.getBody().getErrors().stream().map(Error::getMessage).toList();
        assertTrue(missingFields.contains("quantity"));
        assertFalse(response.getBody().getErrors().get(0).getPath().isEmpty());
    }

    @Test
    @DisplayName("should return FORBIDDEN error code when user is trying to add item to another user's cart")
    public void shouldReturnForbiddenWhenUserIsTryingToAddItemToAnotherUserCart() {

        // given
        UserResponse someUserNotCartOwner = addExampleUser();

        // when
        ResponseEntity<Void> response = cartAbility().addItemToCart(new AddItemToCartRequest(
                OFFER_ID,
                USER_ID,
                ITEM_QUANTITY_ONE
        ), credentials(someUserNotCartOwner.getEmail()));

        // then
        assertEquals(FORBIDDEN, response.getStatusCode());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid_token"})
    @NullAndEmptySource
    @DisplayName("should return UNAUTHORIZED error code when request does not contain authentication token")
    public void shouldReturnUnauthorizedWhenRequestDoesNotContainAuthToken(String invalidTokenOrNullOrEmpty) {

        // given
        AddItemToCartRequest request = new AddItemToCartRequest(
                null,
                null,
                null
        );

        // when
        ResponseEntity<Errors> response = cartAbility().addItemToCartUnauthorized(request, invalidTokenOrNullOrEmpty);

        // then
        assertEquals(UNAUTHORIZED, response.getStatusCode());
    }
}
