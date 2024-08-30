package com.ecommerce.cart.api;

import com.ecommerce.BaseIntegrationTest;
import com.ecommerce.cart.api.dto.AddItemToCartRequest;
import com.ecommerce.cart.api.dto.CartResponse;
import com.ecommerce.cart.api.dto.ChangeItemQuantityRequest;
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

import java.util.List;

import static com.ecommerce.IntegrationTestData.CATEGORY_ELECTRONICS;
import static com.ecommerce.IntegrationTestData.DESCRIPTION;
import static com.ecommerce.IntegrationTestData.EAN;
import static com.ecommerce.IntegrationTestData.EMAIL;
import static com.ecommerce.IntegrationTestData.IMAGE_NAME;
import static com.ecommerce.IntegrationTestData.ITEM_QUANTITY_ONE;
import static com.ecommerce.IntegrationTestData.NAME;
import static com.ecommerce.IntegrationTestData.NON_EXISTING_ID;
import static com.ecommerce.IntegrationTestData.OFFER_ID;
import static com.ecommerce.IntegrationTestData.OFFER_PRICE;
import static com.ecommerce.IntegrationTestData.USER_ID;
import static com.ecommerce.shared.Assertions.assertBigDecimalEquals;
import static com.ecommerce.shared.CredentialsAbility.credentials;
import static com.ecommerce.shared.api.dto.ErrorCode.FIELD_REQUIRED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

public class ChangeCartItemQuantityEndpointTest extends BaseIntegrationTest implements UserFeeder {

    @Test
    @DisplayName("should change cart item quantity")
    public void shouldChangeCartItemQuantity() {

        // given
        int ONE = 1;
        cartAbility().addItemToCart(new AddItemToCartRequest(
                OFFER_ID,
                USER_ID,
                ITEM_QUANTITY_ONE
        ), credentials(EMAIL));
        ResponseEntity<CartResponse> cartResponse = cartAbility().getUserCart(USER_ID, credentials(EMAIL));
        assertEquals(ONE, cartResponse.getBody().getItems().size());
        assertEquals(ONE, cartResponse.getBody().getItems().get(0).getQuantity());
        int NEW_QUANTITY = 10;

        // when
        ResponseEntity<Void> response = cartAbility().changeCartItemQuantity(new ChangeItemQuantityRequest(
                USER_ID,
                OFFER_ID,
                NEW_QUANTITY

        ), credentials(EMAIL));

        // then
        assertTrue(response.getStatusCode().is2xxSuccessful());

        ResponseEntity<CartResponse> modifiedCartResponse = cartAbility().getUserCart(USER_ID, credentials(EMAIL));
        assertTrue(modifiedCartResponse.getStatusCode().is2xxSuccessful());
        assertEquals(USER_ID, modifiedCartResponse.getBody().getUserId());
        assertEquals(cartResponse.getBody().getVersion(), modifiedCartResponse.getBody().getVersion());
        assertEquals(1, modifiedCartResponse.getBody().getItems().size());
        assertNotNull(modifiedCartResponse.getBody().getItems().get(0).getId());
        assertEquals(NEW_QUANTITY, modifiedCartResponse.getBody().getItems().get(0).getQuantity());
        assertNotNull(modifiedCartResponse.getBody().getItems().get(0).getVersion());
        assertEquals(OFFER_ID, modifiedCartResponse.getBody().getItems().get(0).getOfferId());
        assertBigDecimalEquals(OFFER_PRICE, modifiedCartResponse.getBody().getItems().get(0).getPrice());
        assertEquals(NAME, modifiedCartResponse.getBody().getItems().get(0).getProductName());
        assertEquals(EAN, modifiedCartResponse.getBody().getItems().get(0).getEan());
        assertEquals(CATEGORY_ELECTRONICS, modifiedCartResponse.getBody().getItems().get(0).getCategory());
        assertEquals(cdnProperties.getImgUrl() + IMAGE_NAME, modifiedCartResponse.getBody().getItems().get(0).getImageUrl());
        assertEquals(DESCRIPTION, modifiedCartResponse.getBody().getItems().get(0).getDescription());
        assertEquals(cartResponse.getBody().getItems().get(0).getVersion() + 1, modifiedCartResponse.getBody().getItems().get(0).getVersion());

    }

    @Test
    @DisplayName("should not change cart item quantity when offer does not exist in cart")
    public void shouldNotChangeCartItemQuantityWhenOfferDoesNotExistInCart() {

        // given
        int ONE = 1;
        cartAbility().addItemToCart(new AddItemToCartRequest(
                OFFER_ID,
                USER_ID,
                ITEM_QUANTITY_ONE
        ), credentials(EMAIL));
        ResponseEntity<CartResponse> cartResponse = cartAbility().getUserCart(USER_ID, credentials(EMAIL));
        assertEquals(ONE, cartResponse.getBody().getItems().size());
        assertEquals(ONE, cartResponse.getBody().getItems().get(0).getQuantity());


        // when
        ResponseEntity<Void> response = cartAbility().changeCartItemQuantity(new ChangeItemQuantityRequest(
                USER_ID,
                NON_EXISTING_ID,
                10

        ), credentials(EMAIL));

        // then
        assertTrue(response.getStatusCode().is2xxSuccessful());

        ResponseEntity<CartResponse> modifiedCartResponse = cartAbility().getUserCart(USER_ID, credentials(EMAIL));
        assertTrue(modifiedCartResponse.getStatusCode().is2xxSuccessful());
        assertEquals(ONE, modifiedCartResponse.getBody().getItems().size());
        assertEquals(ONE, modifiedCartResponse.getBody().getItems().get(0).getQuantity());
    }

    @Test
    @DisplayName("should return UNPROCESSABLE_ENTITY when trying to change item quantity and user cart does not exist")
    public void shouldReturnUnprocessableEntityWhenTryingToChangeItemQuantityAndUserCartDoesNotExist() {

        // given
        int ONE = 1;

        // when
        ResponseEntity<Errors> response = cartAbility().changeCartItemQuantityError(new ChangeItemQuantityRequest(
                USER_ID,
                OFFER_ID,
                ONE

        ), credentials(EMAIL));

        // then
        assertEquals(UNPROCESSABLE_ENTITY, response.getStatusCode());
    }

    @Test
    @DisplayName("should return BAD_REQUEST error code when request does not contain required data")
    public void shouldReturnBadRequestWhenRequestDoesNotContainRequiredData() {

        // when
        ResponseEntity<Errors> response = cartAbility().changeCartItemQuantityError(new ChangeItemQuantityRequest(
                null,
                null,
                null
        ), credentials(EMAIL));

        // then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertTrue(response.hasBody());
        assertEquals(3, response.getBody().getErrors().size());
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
    @DisplayName("should return FORBIDDEN error code when user is trying to change item quantity in another user's cart")
    public void shouldReturnForbiddenWhenUserIsTryingToChangeItemQuantityInAnotherUserCart() {

        // given
        UserResponse someUserNotCartOwner = addExampleUser();

        // when
        ResponseEntity<Void> response = cartAbility().changeCartItemQuantity(new ChangeItemQuantityRequest(
                USER_ID,
                OFFER_ID,
                10

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
        Integer NEW_QUANTITY = 19;

        // when
        ResponseEntity<Void> response = cartAbility().changeCartItemQuantityUnauthorized(new ChangeItemQuantityRequest(
                USER_ID,
                OFFER_ID,
                NEW_QUANTITY

        ), invalidTokenOrNullOrEmpty);

        // then
        assertEquals(UNAUTHORIZED, response.getStatusCode());
    }
}
