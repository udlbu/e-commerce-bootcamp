package com.ecommerce.order.api;

import com.ecommerce.BaseIntegrationTest;
import com.ecommerce.cart.api.dto.CartResponse;
import com.ecommerce.offer.api.dto.OfferResponse;
import com.ecommerce.order.api.dto.OrderResponse;
import com.ecommerce.order.domain.OrderStatus;
import com.ecommerce.product.api.dto.ProductResponse;
import com.ecommerce.shared.api.dto.Errors;
import com.ecommerce.user.api.dto.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static com.ecommerce.IntegrationTestData.EMAIL;
import static com.ecommerce.IntegrationTestData.NON_EXISTING_ID;
import static com.ecommerce.shared.CredentialsAbility.credentials;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public class CancelOrderEndpointTest extends BaseIntegrationTest {

    @Test
    @DisplayName("should cancel and return cancelled order")
    public void shouldCancelAndReturnCancelledOrder() {

        // given
        UserResponse seller = addSellerUser();
        UserResponse buyer = addBuyerUser("Charles");
        ProductResponse iphone = addIphoneProduct();
        OfferResponse iphoneOffer = addOffer(seller, iphone, 10, BigDecimal.valueOf(599));
        CartResponse cart = addOfferToCart(buyer, iphoneOffer);
        Long orderId = submitOrder(cart, buyer);

        // make sure that order is not cancelled
        ResponseEntity<OrderResponse> newOrder = orderAbility().getOrder(orderId, credentials(buyer.getEmail()));
        assertEquals(OrderStatus.NEW.name(), newOrder.getBody().getStatus());

        // when
        ResponseEntity<OrderResponse> cancelled = orderAbility().cancelOrder(orderId, credentials(buyer.getEmail()));

        // then
        assertTrue(cancelled.getStatusCode().is2xxSuccessful());
        assertTrue(cancelled.hasBody());
        assertEquals(OrderStatus.CANCELLED.name(), cancelled.getBody().getStatus());
        assertEquals(newOrder.getBody().getVersion() + 1, cancelled.getBody().getVersion());

        // and double cancel should neither change order's state nor throw exception
        ResponseEntity<OrderResponse> doublyCancelled = orderAbility().cancelOrder(orderId, credentials(buyer.getEmail()));

        // then
        assertTrue(doublyCancelled.getStatusCode().is2xxSuccessful());
        assertTrue(doublyCancelled.hasBody());
        assertEquals(OrderStatus.CANCELLED.name(), doublyCancelled.getBody().getStatus());
        assertEquals(newOrder.getBody().getVersion() + 1, doublyCancelled.getBody().getVersion());
    }

    @Test
    @DisplayName("should return NOT_FOUND error code when trying to cancel non existing order")
    public void shouldReturnEmptyResponseTryingToCancelNonExistingOrder() {

        // when
        ResponseEntity<Errors> response = orderAbility().cancelOrderError(NON_EXISTING_ID, credentials(EMAIL));

        // then
        assertEquals(NOT_FOUND, response.getStatusCode());
        assertFalse(response.hasBody());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid_token"})
    @NullAndEmptySource
    @DisplayName("should return UNAUTHORIZED error code when request does not contain authentication token")
    public void shouldReturnUnauthorizedWhenRequestDoesNotContainAuthToken(String invalidTokenOrNullOrEmpty) {

        // given
        long ORDER_ID = 1000L;

        // when
        ResponseEntity<Errors> response = orderAbility().cancelOrderUnauthorized(ORDER_ID, invalidTokenOrNullOrEmpty);

        // then
        assertEquals(UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("should return FORBIDDEN when user is trying to cancel another user's order")
    public void shouldReturnForbiddenWhenUserIsTryingToCancelAnotherUserOrder() {

        // given
        UserResponse seller = addSellerUser();
        UserResponse buyer = addBuyerUser("Charles");
        ProductResponse iphone = addIphoneProduct();
        OfferResponse iphoneOffer = addOffer(seller, iphone, 10, BigDecimal.valueOf(599));
        CartResponse cart = addOfferToCart(buyer, iphoneOffer);
        Long orderId = submitOrder(cart, buyer);

        // when
        ResponseEntity<OrderResponse> response = orderAbility().cancelOrder(orderId, credentials(EMAIL));

        // then
        assertEquals(FORBIDDEN, response.getStatusCode());
    }
}
