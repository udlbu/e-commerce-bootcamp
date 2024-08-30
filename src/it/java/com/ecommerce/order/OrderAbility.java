package com.ecommerce.order;

import com.ecommerce.order.api.dto.OrderResponse;
import com.ecommerce.order.api.dto.OrdersPageResponse;
import com.ecommerce.order.api.dto.SearchOrdersRequest;
import com.ecommerce.order.api.dto.SubmitOrderRequest;
import com.ecommerce.shared.HttpAbility;
import com.ecommerce.shared.api.dto.Errors;
import com.ecommerce.shared.domain.Credentials;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class OrderAbility extends HttpAbility {

    private final String URL = "/api/orders";

    public OrderAbility(TestRestTemplate testRestTemplate, String authServerUrl) {
        super(testRestTemplate, authServerUrl);
    }

    public ResponseEntity<Long> submitOrder(SubmitOrderRequest submitOrderRequest, Credentials credentials) {
        return testRestTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(submitOrderRequest, acceptAndContentType(credentials)), Long.class);
    }

    public ResponseEntity<Errors> submitOrderError(SubmitOrderRequest submitOrderRequest, Credentials credentials) {
        return testRestTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(submitOrderRequest, acceptAndContentType(credentials)), Errors.class);
    }

    public ResponseEntity<Errors> submitOrderUnauthorized(SubmitOrderRequest submitOrderRequest, String invalidToken) {
        return testRestTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(submitOrderRequest, acceptAndContentTypeUnauthorized(invalidToken)), Errors.class);
    }

    public ResponseEntity<OrderResponse> getOrder(Long id, Credentials credentials) {
        return testRestTemplate.exchange(URL + "/" + id, HttpMethod.GET, new HttpEntity<>(accept(credentials)), OrderResponse.class);
    }

    public ResponseEntity<Errors> getOrderUnauthorized(Long id, String invalidToken) {
        return testRestTemplate.exchange(URL + "/" + id, HttpMethod.GET, new HttpEntity<>(acceptUnauthorized(invalidToken)), Errors.class);
    }

    public ResponseEntity<Void> deleteOrder(Long offerId, Credentials credentials) {
        return testRestTemplate.exchange(URL + "/" + offerId, HttpMethod.DELETE, new HttpEntity<>(authorization(credentials)), Void.class);
    }

    public ResponseEntity<Errors> deleteOrderUnauthorized(Long offerId, String invalidToken) {
        return testRestTemplate.exchange(URL + "/" + offerId, HttpMethod.DELETE, new HttpEntity<>(invalidOrMissingToken(invalidToken)), Errors.class);
    }

    public ResponseEntity<OrderResponse> cancelOrder(Long orderId, Credentials credentials) {
        return testRestTemplate.exchange(URL + "/" + orderId + "/cancel", HttpMethod.PUT, new HttpEntity<>(accept(credentials)), OrderResponse.class);
    }

    public ResponseEntity<Errors> cancelOrderUnauthorized(Long orderId, String invalidToken) {
        return testRestTemplate.exchange(URL + "/" + orderId + "/cancel", HttpMethod.PUT, new HttpEntity<>(acceptUnauthorized(invalidToken)), Errors.class);
    }

    public ResponseEntity<Errors> cancelOrderError(Long orderId, Credentials credentials) {
        return testRestTemplate.exchange(URL + "/" + orderId + "/cancel", HttpMethod.PUT, new HttpEntity<>(accept(credentials)), Errors.class);
    }

    public ResponseEntity<OrdersPageResponse> getOrders(SearchOrdersRequest searchOrdersRequest, Credentials credentials) {
        return testRestTemplate.exchange(URL + "/search", HttpMethod.POST, new HttpEntity<>(searchOrdersRequest, acceptAndContentType(credentials)), OrdersPageResponse.class);
    }

    public ResponseEntity<Errors> getOrdersUnauthorized(SearchOrdersRequest searchOrdersRequest, String invalidToken) {
        return testRestTemplate.exchange(URL + "/search", HttpMethod.POST, new HttpEntity<>(searchOrdersRequest, acceptAndContentTypeUnauthorized(invalidToken)), Errors.class);
    }
}
