package com.ecommerce.order.api;

import com.ecommerce.BaseIntegrationTest;
import com.ecommerce.cart.api.dto.CartResponse;
import com.ecommerce.offer.api.dto.OfferResponse;
import com.ecommerce.order.api.dto.OrderLineResponse;
import com.ecommerce.order.api.dto.OrderResponse;
import com.ecommerce.order.api.dto.OrdersPageResponse;
import com.ecommerce.order.api.dto.SearchOrdersRequest;
import com.ecommerce.product.api.dto.ProductResponse;
import com.ecommerce.shared.api.dto.Errors;
import com.ecommerce.user.api.dto.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static com.ecommerce.IntegrationTestData.EMAIL;
import static com.ecommerce.shared.CredentialsAbility.credentials;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public class GetAllOrdersEndpointTest extends BaseIntegrationTest {

    private UserResponse mariaBuyer;
    private OfferResponse iphoneOffer;
    private OfferResponse macbookOffer;
    private OfferResponse ipadOffer;

    private final static Long TOTAL_MARIA_ORDERS_NUMBER = 3L;

    @BeforeEach
    public void setup() {
        UserResponse seller = addSellerUser();
        UserResponse tomBuyer = addBuyerUser("Tom");
        mariaBuyer = addBuyerUser("Maria");

        ProductResponse samsungGalaxy = addSamsungGalaxyProduct();
        OfferResponse samsungGalaxyOffer = addOffer(seller, samsungGalaxy, 6, BigDecimal.valueOf(139));

        ProductResponse iphone = addIphoneProduct();
        iphoneOffer = addOffer(seller, iphone, 10, BigDecimal.valueOf(599));

        ProductResponse macbook = addMacbookProduct();
        macbookOffer = addOffer(seller, macbook, 3, BigDecimal.valueOf(899));

        ProductResponse ipad = addIpadProduct();
        ipadOffer = addOffer(seller, ipad, 3, BigDecimal.valueOf(899));

        CartResponse tomCart = addOfferToCart(tomBuyer, samsungGalaxyOffer);
        submitOrder(tomCart, tomBuyer);

        CartResponse mariaFirstCart = addOfferToCart(mariaBuyer, iphoneOffer);
        submitOrder(mariaFirstCart, mariaBuyer);

        CartResponse mariaSecondCart = addOfferToCart(mariaBuyer, macbookOffer);
        submitOrder(mariaSecondCart, mariaBuyer);

        CartResponse mariaThirdCart = addOfferToCart(mariaBuyer, ipadOffer);
        submitOrder(mariaThirdCart, mariaBuyer);
    }

    @Test
    @DisplayName("should return first page of all user orders when orders number is less than page size")
    public void shouldReturnFirstPageOfAllUserOrdersWhenOrderNumberLessThanPageSize() {

        // given
        int FIRST_PAGE = 0;
        int PAGE_SIZE = 10;

        // when
        ResponseEntity<OrdersPageResponse> response = orderAbility().getOrders(new SearchOrdersRequest(
                mariaBuyer.getId(),
                FIRST_PAGE,
                PAGE_SIZE
        ), credentials(mariaBuyer.getEmail()));

        // then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(response.hasBody());
        assertThatEachOrderHasOnlyOneOrderLine(response.getBody().getOrders());
        assertEquals(TOTAL_MARIA_ORDERS_NUMBER, response.getBody().getTotal());
        // return all three because page size is 10
        assertEquals(TOTAL_MARIA_ORDERS_NUMBER, response.getBody().getOrders().size());
        assertThatOrdersContainsOffer(response.getBody().getOrders(), iphoneOffer);
        assertThatOrdersContainsOffer(response.getBody().getOrders(), macbookOffer);
        assertThatOrdersContainsOffer(response.getBody().getOrders(), ipadOffer);
    }

    @Test
    @DisplayName("should return first page of user orders when orders number is greater than page size")
    public void shouldReturnFirstPageOfUserOrdersWhenOrderNumberGreaterThanPageSize() {

        // given
        int FIRST_PAGE = 0;
        int PAGE_SIZE = 2;

        // when
        ResponseEntity<OrdersPageResponse> response = orderAbility().getOrders(new SearchOrdersRequest(
                mariaBuyer.getId(),
                FIRST_PAGE,
                PAGE_SIZE
        ), credentials(mariaBuyer.getEmail()));

        // then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(response.hasBody());
        assertThatEachOrderHasOnlyOneOrderLine(response.getBody().getOrders());
        assertEquals(TOTAL_MARIA_ORDERS_NUMBER, response.getBody().getTotal());
        // return only 2 out of 3 because page size is 2
        assertEquals(PAGE_SIZE, response.getBody().getOrders().size());
        assertThatOrdersContainsOffer(response.getBody().getOrders(), iphoneOffer);
        assertThatOrdersContainsOffer(response.getBody().getOrders(), macbookOffer);
    }

    @Test
    @DisplayName("should return second page of user orders when orders number is greater than page size")
    public void shouldReturnSecondPageOfUserOrdersWhenOrderNumberGreaterThanPageSize() {

        // given
        int SECOND_PAGE = 1;
        int PAGE_SIZE = 2;

        // when
        ResponseEntity<OrdersPageResponse> response = orderAbility().getOrders(new SearchOrdersRequest(
                mariaBuyer.getId(),
                SECOND_PAGE,
                PAGE_SIZE
        ), credentials(mariaBuyer.getEmail()));

        // then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(response.hasBody());
        assertThatEachOrderHasOnlyOneOrderLine(response.getBody().getOrders());
        assertEquals(TOTAL_MARIA_ORDERS_NUMBER, response.getBody().getTotal());
        // return only 1 out of 3 because page size is 2, the first two are on the first page and second page contains third order only
        assertEquals(1, response.getBody().getOrders().size());
        assertThatOrdersContainsOffer(response.getBody().getOrders(), ipadOffer);
    }

    @Test
    @DisplayName("should return empty page when there is no more orders")
    public void shouldReturnEmptyPageWhenThereIsNoMoreOrder() {

        // given
        int THIRD_PAGE = 2;
        int PAGE_SIZE = 2;

        // when
        ResponseEntity<OrdersPageResponse> response = orderAbility().getOrders(new SearchOrdersRequest(
                mariaBuyer.getId(),
                THIRD_PAGE,
                PAGE_SIZE
        ), credentials(mariaBuyer.getEmail()));

        // then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(response.hasBody());
        assertEquals(TOTAL_MARIA_ORDERS_NUMBER, response.getBody().getTotal());
        assertTrue(response.getBody().getOrders().isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid_token"})
    @NullAndEmptySource
    @DisplayName("should return UNAUTHORIZED error code when request does not contain authentication token")
    public void shouldReturnUnauthorizedWhenRequestDoesNotContainAuthToken(String invalidTokenOrNullOrEmpty) {

        // given
        int FIRST_PAGE = 0;
        int PAGE_SIZE = 2;

        // when
        ResponseEntity<Errors> response = orderAbility().getOrdersUnauthorized(new SearchOrdersRequest(
                mariaBuyer.getId(),
                FIRST_PAGE,
                PAGE_SIZE
        ), invalidTokenOrNullOrEmpty);

        // then
        assertEquals(UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("should return FORBIDDEN error code when user is trying to get another user orders")
    public void shouldReturnForbiddenWhenUserIsTryingToGetAnotherUserOrders() {

        // given
        int FIRST_PAGE = 0;
        int PAGE_SIZE = 2;

        // when
        ResponseEntity<OrdersPageResponse> response = orderAbility().getOrders(new SearchOrdersRequest(
                mariaBuyer.getId(),
                FIRST_PAGE,
                PAGE_SIZE
        ), credentials(EMAIL));

        // then
        assertEquals(FORBIDDEN, response.getStatusCode());
    }

    private void assertThatOrdersContainsOffer(List<OrderResponse> orders, OfferResponse offer) {
        assertTrue(orders.stream()
                .flatMap(it -> it.getLines().stream())
                .map(OrderLineResponse::getOfferId)
                .anyMatch(it -> it.equals(offer.getId())));
    }

    private void assertThatEachOrderHasOnlyOneOrderLine(List<OrderResponse> orders) {
        for (OrderResponse order : orders) {
            assertEquals(1, order.getLines().size());
        }
    }
}
