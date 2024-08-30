package com.ecommerce.order.api;

import com.ecommerce.BaseIntegrationTest;
import com.ecommerce.cart.api.dto.CartResponse;
import com.ecommerce.offer.api.dto.OfferResponse;
import com.ecommerce.order.api.dto.OrderResponse;
import com.ecommerce.order.domain.PaymentMethod;
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

import static com.ecommerce.IntegrationTestData.DATE;
import static com.ecommerce.IntegrationTestData.DELIVERY_METHOD_FEDEX;
import static com.ecommerce.IntegrationTestData.DELIVERY_STATUS_NEW;
import static com.ecommerce.IntegrationTestData.EMAIL;
import static com.ecommerce.IntegrationTestData.NON_EXISTING_ID;
import static com.ecommerce.IntegrationTestData.ORDER_STATUS_NEW;
import static com.ecommerce.IntegrationTestData.PAYMENT_METHOD_SUCCESS;
import static com.ecommerce.IntegrationTestData.VERSION;
import static com.ecommerce.shared.CredentialsAbility.credentials;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public class GetOrderEndpointTest extends BaseIntegrationTest {

    @Test
    @DisplayName("should return order when order with given id exists in the system")
    public void shouldReturnOrderWhenOrderWithGivenIdExistsInTheSystem() {

        // given
        int BOUGHT_ITEMS_QUANTITY = 1;
        int BOUGHT_IPHONE_QUANTITY = 1;
        UserResponse seller = addSellerUser();
        UserResponse buyer = addBuyerUser("Chris");
        ProductResponse iphone = addIphoneProduct();
        OfferResponse iphoneOffer = addOffer(seller, iphone, 10, BigDecimal.valueOf(599));
        CartResponse cart = addOfferToCart(buyer, iphoneOffer);
        Long orderId = submitOrder(cart, buyer);

        // when
        ResponseEntity<OrderResponse> response = orderAbility().getOrder(orderId, credentials(buyer.getEmail()));

        // then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(response.hasBody());

        assertEquals(orderId, response.getBody().getId());
        assertEquals(DELIVERY_METHOD_FEDEX.name(), response.getBody().getDeliveryMethod());
        assertEquals(DELIVERY_STATUS_NEW.name(), response.getBody().getDeliveryStatus());
        assertEquals(PaymentMethod.CARD.name(), response.getBody().getPaymentMethod());
        assertEquals(PAYMENT_METHOD_SUCCESS.name(), response.getBody().getPaymentStatus());
        assertEquals(ORDER_STATUS_NEW.name(), response.getBody().getStatus());
        assertEquals(DATE, response.getBody().getCreatedAt().toString());
        assertEquals(DATE, response.getBody().getUpdatedAt().toString());
        assertEquals(VERSION, response.getBody().getVersion());
        assertEquals(BOUGHT_ITEMS_QUANTITY, response.getBody().getLines().size());
        assertNotNull(response.getBody().getLines().get(0).getId());
        assertEquals(iphoneOffer.getId(), response.getBody().getLines().get(0).getOfferId());
        assertEquals(iphoneOffer.getPrice(), response.getBody().getLines().get(0).getOfferPrice());
        assertEquals(iphone.getId(), response.getBody().getLines().get(0).getProductId());
        assertEquals(iphone.getName(), response.getBody().getLines().get(0).getProductName());
        assertEquals(iphone.getEan(), response.getBody().getLines().get(0).getProductEan());
        assertEquals(iphone.getCategory(), response.getBody().getLines().get(0).getProductCategory());
        assertEquals(iphone.getImageUrl(), response.getBody().getLines().get(0).getImageUrl());
        assertEquals(BOUGHT_IPHONE_QUANTITY, response.getBody().getLines().get(0).getQuantity());
        assertEquals(VERSION, response.getBody().getLines().get(0).getVersion());
    }

    @Test
    @DisplayName("should return NOT_FOUND error code when order with given id does not exists in the system")
    public void shouldReturnEmptyResponseWhenOrderDoesNotExistInTheSystem() {

        // when
        ResponseEntity<OrderResponse> response = orderAbility().getOrder(NON_EXISTING_ID, credentials(EMAIL));

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
        ResponseEntity<Errors> response = orderAbility().getOrderUnauthorized(ORDER_ID, invalidTokenOrNullOrEmpty);

        // then
        assertEquals(UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("should return FORBIDDEN error code when user is trying to get another user's order")
    public void shouldReturnForbiddenWhenUserIsTryingToGetAnotherUserOrder() {

        // given
        UserResponse seller = addSellerUser();
        UserResponse buyer = addBuyerUser("Chris");
        ProductResponse iphone = addIphoneProduct();
        OfferResponse iphoneOffer = addOffer(seller, iphone, 10, BigDecimal.valueOf(599));
        CartResponse cart = addOfferToCart(buyer, iphoneOffer);
        Long orderId = submitOrder(cart, buyer);

        // when
        ResponseEntity<OrderResponse> response = orderAbility().getOrder(orderId, credentials(EMAIL));

        // then
        assertEquals(FORBIDDEN, response.getStatusCode());
    }
}
