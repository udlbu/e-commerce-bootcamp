package com.ecommerce.order.api;

import com.ecommerce.BaseIntegrationTest;
import com.ecommerce.cart.api.dto.CartResponse;
import com.ecommerce.offer.api.dto.OfferResponse;
import com.ecommerce.order.api.dto.OrderResponse;
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
import static com.ecommerce.shared.CredentialsAbility.credentials;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public class DeleteOrderEndpointTest extends BaseIntegrationTest {

    @Test
    @DisplayName("should delete order with given identifier")
    public void shouldOrderOrder() {

        // given
        UserResponse seller = addSellerUser();
        UserResponse buyer = addBuyerUser("George");
        ProductResponse iphone = addIphoneProduct();
        OfferResponse iphoneOffer = addOffer(seller, iphone, 10, BigDecimal.valueOf(599));
        CartResponse cart = addOfferToCart(buyer, iphoneOffer);
        Long orderId = submitOrder(cart, buyer);

        // when
        ResponseEntity<Void> response = orderAbility().deleteOrder(orderId, credentials(buyer.getEmail()));

        // then
        assertTrue(response.getStatusCode().is2xxSuccessful());

        // and when
        ResponseEntity<OrderResponse> deletedOrderResponse = orderAbility().getOrder(orderId, credentials(buyer.getEmail()));

        // then
        assertEquals(NOT_FOUND, deletedOrderResponse.getStatusCode());
        assertFalse(deletedOrderResponse.hasBody());
    }

    @Test
    @DisplayName("should not throw exception when trying to delete non existing order")
    public void shouldNotThrowExceptionWhenTryingToDeleteNonExistingOrder() {

        // given
        Long NON_EXISTING_ORDER_ID = -10L;

        // when
        ResponseEntity<Void> response = orderAbility().deleteOrder(NON_EXISTING_ORDER_ID, credentials(EMAIL));

        // then
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid_token"})
    @NullAndEmptySource
    @DisplayName("should return UNAUTHORIZED error code when request does not contain authentication token")
    public void shouldReturnUnauthorizedWhenRequestDoesNotContainAuthToken(String invalidTokenOrNullOrEmpty) {

        // given
        long ORDER_ID = 1000L;

        // when
        ResponseEntity<Errors> response = orderAbility().deleteOrderUnauthorized(ORDER_ID, invalidTokenOrNullOrEmpty);

        // then
        assertEquals(UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("should return FORBIDDEN when user is trying to delete another user's order")
    public void shouldReturnForbiddenWhenUserIsTryingToDeleteAnotherUserOrder(){

        // given
        UserResponse seller = addSellerUser();
        UserResponse buyer = addBuyerUser("George");
        ProductResponse iphone = addIphoneProduct();
        OfferResponse iphoneOffer = addOffer(seller, iphone, 10, BigDecimal.valueOf(599));
        CartResponse cart = addOfferToCart(buyer, iphoneOffer);
        Long orderId = submitOrder(cart, buyer);

        // when
        ResponseEntity<Void> response = orderAbility().deleteOrder(orderId, credentials(EMAIL));

        // then
        assertEquals(FORBIDDEN, response.getStatusCode());
    }

}
