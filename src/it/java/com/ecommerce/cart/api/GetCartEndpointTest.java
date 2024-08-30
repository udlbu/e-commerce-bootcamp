package com.ecommerce.cart.api;

import com.ecommerce.BaseIntegrationTest;
import com.ecommerce.cart.api.dto.AddItemToCartRequest;
import com.ecommerce.cart.api.dto.CartResponse;
import com.ecommerce.shared.api.dto.Errors;
import com.ecommerce.user.UserFeeder;
import com.ecommerce.user.api.dto.UserResponse;
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
import static com.ecommerce.IntegrationTestData.ITEM_QUANTITY_ONE;
import static com.ecommerce.IntegrationTestData.NAME;
import static com.ecommerce.IntegrationTestData.OFFER_ID;
import static com.ecommerce.IntegrationTestData.OFFER_PRICE;
import static com.ecommerce.IntegrationTestData.USER_ID;
import static com.ecommerce.IntegrationTestData.VERSION;
import static com.ecommerce.shared.Assertions.assertBigDecimalEquals;
import static com.ecommerce.shared.CredentialsAbility.credentials;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public class GetCartEndpointTest extends BaseIntegrationTest implements UserFeeder {

    @Test
    @DisplayName("should return user's cart")
    public void shouldReturnUserCart() {

        // given
        int EXPECTED_CART_ITEMS_SIZE = 1;
        int EXPECTED_CART_ITEM_QUANTITY = 1;
        cartAbility().addItemToCart(new AddItemToCartRequest(
                OFFER_ID,
                USER_ID,
                ITEM_QUANTITY_ONE
        ), credentials(EMAIL));

        // when
        ResponseEntity<CartResponse> response = cartAbility().getUserCart(USER_ID, credentials(EMAIL));

        // then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(response.hasBody());
        assertNotNull(response.getBody().getId());
        assertEquals(USER_ID, response.getBody().getUserId());
        assertEquals(VERSION, response.getBody().getVersion());
        assertEquals(EXPECTED_CART_ITEMS_SIZE, response.getBody().getItems().size());
        assertNotNull(response.getBody().getItems().get(0).getId());
        assertEquals(EXPECTED_CART_ITEM_QUANTITY, response.getBody().getItems().get(0).getQuantity());
        assertNotNull(response.getBody().getItems().get(0).getVersion());
        assertEquals(OFFER_ID, response.getBody().getItems().get(0).getOfferId());
        assertBigDecimalEquals(OFFER_PRICE, response.getBody().getItems().get(0).getPrice());
        assertEquals(NAME, response.getBody().getItems().get(0).getProductName());
        assertEquals(EAN, response.getBody().getItems().get(0).getEan());
        assertEquals(CATEGORY_ELECTRONICS, response.getBody().getItems().get(0).getCategory());
        assertEquals(cdnProperties.getImgUrl() + IMAGE_NAME, response.getBody().getItems().get(0).getImageUrl());
        assertEquals(DESCRIPTION, response.getBody().getItems().get(0).getDescription());
    }

    @Test
    @DisplayName("should return empty cart when cart does not exists for given user")
    public void shouldReturnEmptyCartWhenCartDoesNotExistForGivenUser() {

        // given
        UserResponse userWithoutCart = addExampleUser();

        // when
        ResponseEntity<CartResponse> response = cartAbility().getUserCart(userWithoutCart.getId(), credentials(userWithoutCart.getEmail()));

        // then
        assertEquals(OK, response.getStatusCode());
        assertNull(response.getBody().getId());
        assertNull(response.getBody().getUserId());
        assertNull(response.getBody().getVersion());
        assertNull(response.getBody().getItems());
    }

    @Test
    @DisplayName("should return FORBIDDEN error code when user is trying to get another user's cart")
    public void shouldReturnForbiddenWhenUserIsTryingToGetAnotherUserCart() {

        // given
        UserResponse userNotCartOwner = addExampleUser();

        // when
        ResponseEntity<CartResponse> response = cartAbility().getUserCart(USER_ID, credentials(userNotCartOwner.getEmail()));

        // then
        assertEquals(FORBIDDEN, response.getStatusCode());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid_token"})
    @NullAndEmptySource
    @DisplayName("should return UNAUTHORIZED error code when request does not contain authentication token")
    public void shouldReturnUnauthorizedWhenRequestDoesNotContainAuthToken(String invalidTokenOrNullOrEmpty) {

        // when
        ResponseEntity<Errors> response = cartAbility().getUserCartUnauthorized(USER_ID, invalidTokenOrNullOrEmpty);

        // then
        assertEquals(UNAUTHORIZED, response.getStatusCode());
    }
}
