package com.ecommerce.order.api;

import com.ecommerce.BaseIntegrationTest;
import com.ecommerce.cart.api.dto.AddItemToCartRequest;
import com.ecommerce.cart.api.dto.CartResponse;
import com.ecommerce.offer.api.dto.OfferResponse;
import com.ecommerce.offer.domain.model.OfferStatus;
import com.ecommerce.order.adapter.OrderEntity;
import com.ecommerce.order.adapter.OrderLineEntity;
import com.ecommerce.order.api.dto.SubmitOrderRequest;
import com.ecommerce.order.domain.DeliveryMethod;
import com.ecommerce.order.domain.DeliveryStatus;
import com.ecommerce.order.domain.OrderStatus;
import com.ecommerce.order.domain.PaymentMethod;
import com.ecommerce.order.domain.PaymentStatus;
import com.ecommerce.product.api.dto.ProductResponse;
import com.ecommerce.shared.api.dto.Error;
import com.ecommerce.shared.api.dto.ErrorCode;
import com.ecommerce.shared.api.dto.Errors;
import com.ecommerce.user.api.dto.UserResponse;
import com.ecommerce.user.domain.model.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static com.ecommerce.IntegrationTestData.DATE;
import static com.ecommerce.IntegrationTestData.DELIVERY_METHOD_FEDEX;
import static com.ecommerce.IntegrationTestData.EMAIL;
import static com.ecommerce.IntegrationTestData.ITEM_QUANTITY_ONE;
import static com.ecommerce.IntegrationTestData.PAYMENT_METHOD_CARD;
import static com.ecommerce.IntegrationTestData.VERSION;
import static com.ecommerce.shared.CredentialsAbility.credentials;
import static com.ecommerce.shared.api.dto.ErrorCode.FIELD_REQUIRED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class SubmitOrderEndpointTest extends BaseIntegrationTest {

    private final int INITIAL_IPHONE_OFFER_STOCK_QUANTITY = 10;
    private final int INITIAL_MACBOOK_OFFER_STOCK_QUANTITY = 1;
    private final BigDecimal IPHONE_PRICE = BigDecimal.valueOf(599);
    private final BigDecimal MACBOOK_PRICE = BigDecimal.valueOf(299);
    private UserResponse buyer;
    private UserResponse seller;
    private OfferResponse iphoneOffer;
    private OfferResponse macbookOffer;
    private CartResponse cart;

    @BeforeEach
    public void setup() {
        seller = addSellerUser();
        buyer = addBuyerUser("Ann");
        ProductResponse iphone = addIphoneProduct();
        ProductResponse macbook = addMacbookProduct();
        iphoneOffer = addOffer(seller, iphone, INITIAL_IPHONE_OFFER_STOCK_QUANTITY, IPHONE_PRICE);
        macbookOffer = addOffer(seller, macbook, INITIAL_MACBOOK_OFFER_STOCK_QUANTITY, MACBOOK_PRICE);
        cart = addTwoIphoneAndMacbookToCart(buyer, iphoneOffer, macbookOffer);
    }

    @Test
    @DisplayName("should submit order, decrement stock and clear cart")
    public void shouldSubmitOrderAndDecrementStock() {

        // given
        int ORDERED_MACBOOK_QUANTITY = 1;
        int ORDERED_IPHONE_QUANTITY = 2;

        // when
        ResponseEntity<Long> submittedOrderId = orderAbility().submitOrder(new SubmitOrderRequest(
                cart.getId(),
                DELIVERY_METHOD_FEDEX.name(),
                PAYMENT_METHOD_CARD.name()
        ), credentials(buyer.getEmail()));

        // then
        OrderEntity actualOrder = springJpaOrderRepository.findById(Objects.requireNonNull(submittedOrderId.getBody())).get();
        List<OrderLineEntity> orderLines = springJpaOrderLineRepository.findByOrderId(actualOrder.getId());

        assertNotNull(actualOrder.getId());
        assertEquals(DeliveryMethod.FEDEX.name(), actualOrder.getDeliveryMethod());
        assertEquals(DeliveryStatus.NEW.name(), actualOrder.getDeliveryStatus());
        assertEquals(PaymentMethod.CARD.name(), actualOrder.getPaymentMethod());
        assertEquals(PaymentStatus.SUCCESS.name(), actualOrder.getPaymentStatus());
        assertEquals(OrderStatus.NEW.name(), actualOrder.getStatus());
        assertEquals(DATE, actualOrder.getCreatedAt().toString());
        assertEquals(DATE, actualOrder.getUpdatedAt().toString());
        assertEquals(VERSION, actualOrder.getVersion());
        // assert order line 1 is as expected
        OrderLineEntity macbookOrderLine = orderLines.stream().filter(it -> it.getOffer().getId().equals(macbookOffer.getId())).findFirst().get();
        assertEquals(macbookOffer.getId(), macbookOrderLine.getOffer().getId());
        assertEquals(actualOrder.getId(), macbookOrderLine.getOrder().getId());
        assertEquals(ORDERED_MACBOOK_QUANTITY, macbookOrderLine.getQuantity());
        assertEquals(VERSION, macbookOrderLine.getVersion());
        // assert order line 1 is as expected
        OrderLineEntity iphoneOrderLine = orderLines.stream().filter(it -> it.getOffer().getId().equals(iphoneOffer.getId())).findFirst().get();
        assertEquals(iphoneOffer.getId(), iphoneOrderLine.getOffer().getId());
        assertEquals(actualOrder.getId(), iphoneOrderLine.getOrder().getId());
        assertEquals(ORDERED_IPHONE_QUANTITY, iphoneOrderLine.getQuantity());
        assertEquals(VERSION, iphoneOrderLine.getVersion());

        // assert that offers quantity have been decremented
        OfferResponse iphoneOfferDecremented = offerAbility().getOffer(iphoneOffer.getId()).getBody();
        assertEquals(INITIAL_IPHONE_OFFER_STOCK_QUANTITY - ORDERED_IPHONE_QUANTITY, iphoneOfferDecremented.getQuantity());

        OfferResponse macbookOfferDecremented = offerAbility().getOffer(macbookOffer.getId()).getBody();
        assertEquals(INITIAL_MACBOOK_OFFER_STOCK_QUANTITY - ORDERED_MACBOOK_QUANTITY, macbookOfferDecremented.getQuantity());

        // assert that cart has been cleared
        CartResponse clearedCart = cartAbility().getUserCart(buyer.getId(), credentials(buyer.getEmail())).getBody();
        assertNotNull(clearedCart);
        assertTrue(clearedCart.getItems().isEmpty());
    }

    @Test
    @DisplayName("should offer change status to inactive when offer run out of stock and should not be able to activate")
    public void shouldOfferChangeStatusToInactiveWhenOfferRunOutOfStock() {

        // given
        OfferResponse macbookOfferBeforeBuyOrder = offerAbility().getOffer(macbookOffer.getId()).getBody();
        assertEquals(1, Objects.requireNonNull(macbookOfferBeforeBuyOrder).getQuantity());
        assertEquals(OfferStatus.ACTIVE.name(), Objects.requireNonNull(macbookOfferBeforeBuyOrder).getStatus());

        // when
        ResponseEntity<Long> submittedOrderId = orderAbility().submitOrder(new SubmitOrderRequest(
                cart.getId(),
                DELIVERY_METHOD_FEDEX.name(),
                PAYMENT_METHOD_CARD.name()
        ), credentials(buyer.getEmail()));
        assertNotNull(submittedOrderId);

        // then
        OfferResponse macbookOfferAfterBuyOrder = offerAbility().getOffer(macbookOffer.getId()).getBody();
        assertEquals(0, Objects.requireNonNull(macbookOfferAfterBuyOrder).getQuantity());
        assertEquals(OfferStatus.INACTIVE.name(), Objects.requireNonNull(macbookOfferAfterBuyOrder).getStatus());

        // and when trying to activate
        ResponseEntity<Errors> response = offerAbility().activateOfferError(macbookOffer.getId(), credentials(seller.getEmail()));

        // then
        assertEquals(UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals(ErrorCode.OFFER_RUN_OUT_OF_STOCK, response.getBody().getErrors().get(0).getCode());
    }

    @Test
    @DisplayName("should not offer change status to inactive when offer is bought but does not run out of stock")
    public void shouldNotOfferChangeStatusToInactiveWhenOfferWasBoughtButDoesNotRunOutOfStock() {

        // given
        OfferResponse iphoneOfferBeforeBuyOrder = offerAbility().getOffer(iphoneOffer.getId()).getBody();
        assertEquals(10, Objects.requireNonNull(iphoneOfferBeforeBuyOrder).getQuantity());
        assertEquals(OfferStatus.ACTIVE.name(), Objects.requireNonNull(iphoneOfferBeforeBuyOrder).getStatus());

        // when
        ResponseEntity<Long> submittedOrderId = orderAbility().submitOrder(new SubmitOrderRequest(
                cart.getId(),
                DELIVERY_METHOD_FEDEX.name(),
                PAYMENT_METHOD_CARD.name()
        ), credentials(buyer.getEmail()));
        assertNotNull(submittedOrderId);

        // then
        OfferResponse iphoneOfferAfterBuyOrder = offerAbility().getOffer(iphoneOffer.getId()).getBody();
        assertEquals(8, Objects.requireNonNull(iphoneOfferAfterBuyOrder).getQuantity());
        assertEquals(OfferStatus.ACTIVE.name(), Objects.requireNonNull(iphoneOfferAfterBuyOrder).getStatus());
    }

    @Test
    @DisplayName("should return BAD_REQUEST error code when request does not contain required data")
    public void shouldReturnBadRequestWhenRequestDoesNotContainRequiredData() {

        // when
        ResponseEntity<Errors> response = orderAbility().submitOrderError(new SubmitOrderRequest(
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
        assertTrue(missingFields.contains("cartId"));
        assertTrue(missingFields.contains("deliveryMethod"));
        assertTrue(missingFields.contains("paymentMethod"));
        assertFalse(response.getBody().getErrors().get(0).getPath().isEmpty());
        assertFalse(response.getBody().getErrors().get(1).getPath().isEmpty());
        assertFalse(response.getBody().getErrors().get(2).getPath().isEmpty());
    }

    @Test
    @DisplayName("should not create order when the offer of the cart item became inactive")
    public void shouldNotCreateOrderWhenOfferOfCartItemBecameInactive() {

        // then deactivate iphone offer
        offerAbility().deactivateOffer(iphoneOffer.getId(), credentials(seller.getEmail()));

        // when
        ResponseEntity<Errors> response = orderAbility().submitOrderError(new SubmitOrderRequest(
                cart.getId(),
                DELIVERY_METHOD_FEDEX.name(),
                PAYMENT_METHOD_CARD.name()
        ), credentials(buyer.getEmail()));

        // then
        assertEquals(CONFLICT, response.getStatusCode());
    }

    @Test
    @DisplayName("should not create order when the offer of the cart item run out of stock")
    public void shouldNotCreateOrderWhenOfferOfCartItemRunOutOfStock() {

        // then someone has made the macbook offer run out of stock
        offerAbility().forceRunOutOfStock(macbookOffer.getId());

        // when
        ResponseEntity<Errors> response = orderAbility().submitOrderError(new SubmitOrderRequest(
                cart.getId(),
                DELIVERY_METHOD_FEDEX.name(),
                PAYMENT_METHOD_CARD.name()
        ), credentials(buyer.getEmail()));

        // then
        assertEquals(CONFLICT, response.getStatusCode());
    }

    @Test
    @DisplayName("should not create order when the cart has been removed")
    public void shouldNotCreateOrderWhenCartHasBeenRemoved() {

        // given the cart suddenly being deleted in another browser tab
        cartRepository.deleteByUserId(new UserId(buyer.getId()));

        // when
        ResponseEntity<Errors> response = orderAbility().submitOrderError(new SubmitOrderRequest(
                cart.getId(),
                DELIVERY_METHOD_FEDEX.name(),
                PAYMENT_METHOD_CARD.name()
        ), credentials(buyer.getEmail()));

        // then
        assertEquals(UNPROCESSABLE_ENTITY, response.getStatusCode());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid_token"})
    @NullAndEmptySource
    @DisplayName("should return UNAUTHORIZED error code when request does not contain authentication token")
    public void shouldReturnUnauthorizedWhenRequestDoesNotContainAuthToken(String invalidTokenOrNullOrEmpty) {

        // when
        ResponseEntity<Errors> response = orderAbility().submitOrderUnauthorized(new SubmitOrderRequest(
                cart.getId(),
                DELIVERY_METHOD_FEDEX.name(),
                PAYMENT_METHOD_CARD.name()
        ), invalidTokenOrNullOrEmpty);

        // then
        assertEquals(UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("should return FORBIDDEN error code when user is trying to submit order on behalf of another user")
    public void shouldReturnForbiddenWhenUserIsTryingToSubmitOrderOnBehalfOfAnotherUserCart() {

        // when
        ResponseEntity<Errors> response = orderAbility().submitOrderError(new SubmitOrderRequest(
                cart.getId(),
                DELIVERY_METHOD_FEDEX.name(),
                PAYMENT_METHOD_CARD.name()
        ), credentials(EMAIL));

        // then
        assertEquals(FORBIDDEN, response.getStatusCode());
    }

    // there are two more problems here. Do you know what ?
    // Problem is passing only cartId because the cart fetched during submit might be modified in the background hasn't seen by buyer
    // we need to pass not only cart id but also offers and theirs versions and quantity
    // that is we need to pass all the information buyer see in the cart page to be sure that one is buying exactly what he/she wants.

    private CartResponse addTwoIphoneAndMacbookToCart(UserResponse buyer, OfferResponse iphoneOffer, OfferResponse macbookOffer) {
        cartAbility().addItemToCart(new AddItemToCartRequest(
                iphoneOffer.getId(),
                buyer.getId(),
                ITEM_QUANTITY_ONE
        ), credentials(buyer.getEmail()));
        cartAbility().addItemToCart(new AddItemToCartRequest(
                iphoneOffer.getId(),
                buyer.getId(),
                ITEM_QUANTITY_ONE
        ), credentials(buyer.getEmail()));
        cartAbility().addItemToCart(new AddItemToCartRequest(
                macbookOffer.getId(),
                buyer.getId(),
                ITEM_QUANTITY_ONE
        ), credentials(buyer.getEmail()));
        return cartAbility().getUserCart(buyer.getId(), credentials(buyer.getEmail())).getBody();
    }
}
