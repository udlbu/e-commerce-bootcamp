package com.ecommerce.cart;

import com.ecommerce.cart.api.dto.AddItemToCartRequest;
import com.ecommerce.cart.api.dto.CartResponse;
import com.ecommerce.cart.api.dto.ChangeItemQuantityRequest;
import com.ecommerce.cart.api.dto.RemoveItemFromCartRequest;
import com.ecommerce.shared.HttpAbility;
import com.ecommerce.shared.api.dto.Errors;
import com.ecommerce.shared.domain.Credentials;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class CartAbility extends HttpAbility {

    private final String URL = "/api/carts";

    public CartAbility(TestRestTemplate testRestTemplate, String authServerUrl) {
        super(testRestTemplate, authServerUrl);
    }

    public ResponseEntity<CartResponse> getUserCart(Long userId, Credentials credentials) {
        return testRestTemplate.exchange(URL + "/" + userId, HttpMethod.GET, new HttpEntity<>(accept(credentials)), CartResponse.class);
    }

    public ResponseEntity<Errors> getUserCartUnauthorized(Long userId, String invalidToken) {
        return testRestTemplate.exchange(URL + "/" + userId, HttpMethod.GET, new HttpEntity<>(invalidOrMissingToken(invalidToken)), Errors.class);
    }


    public ResponseEntity<Void> addItemToCart(AddItemToCartRequest addItemToCartRequest, Credentials credentials) {
        return testRestTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(addItemToCartRequest, acceptAndContentType(credentials)), Void.class);
    }

    public ResponseEntity<Errors> addItemToCartError(AddItemToCartRequest addItemToCartRequest, Credentials credentials) {
        return testRestTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(addItemToCartRequest, acceptAndContentType(credentials)), Errors.class);
    }

    public ResponseEntity<Errors> addItemToCartUnauthorized(AddItemToCartRequest addItemToCartRequest, String invalidToken) {
        return testRestTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(addItemToCartRequest, acceptAndContentTypeUnauthorized(invalidToken)), Errors.class);
    }

    public ResponseEntity<Void> removeItemFromCart(RemoveItemFromCartRequest removeItemFromCartRequest, Credentials credentials) {
        return testRestTemplate.exchange(URL + "/remove-item", HttpMethod.POST, new HttpEntity<>(removeItemFromCartRequest, acceptAndContentType(credentials)), Void.class);
    }

    public ResponseEntity<Errors> removeItemFromCartError(RemoveItemFromCartRequest removeItemFromCartRequest, Credentials credentials) {
        return testRestTemplate.exchange(URL + "/remove-item", HttpMethod.POST, new HttpEntity<>(removeItemFromCartRequest, acceptAndContentType(credentials)), Errors.class);
    }

    public ResponseEntity<Errors> removeItemFromCartUnauthorized(RemoveItemFromCartRequest removeItemFromCartRequest, String invalidToken) {
        return testRestTemplate.exchange(URL, HttpMethod.DELETE, new HttpEntity<>(removeItemFromCartRequest, acceptAndContentTypeUnauthorized(invalidToken)), Errors.class);
    }

    public ResponseEntity<Void> changeCartItemQuantity(ChangeItemQuantityRequest changeItemQuantityRequest, Credentials credentials) {
        return testRestTemplate.exchange(URL + "/change-quantity", HttpMethod.PUT, new HttpEntity<>(changeItemQuantityRequest, acceptAndContentType(credentials)), Void.class);
    }

    public ResponseEntity<Void> changeCartItemQuantityUnauthorized(ChangeItemQuantityRequest changeItemQuantityRequest, String invalidToken) {
        return testRestTemplate.exchange(URL + "/change-quantity", HttpMethod.PUT, new HttpEntity<>(changeItemQuantityRequest, acceptAndContentTypeUnauthorized(invalidToken)), Void.class);
    }

    public ResponseEntity<Errors> changeCartItemQuantityError(ChangeItemQuantityRequest changeItemQuantityRequest, Credentials credentials) {
        return testRestTemplate.exchange(URL + "/change-quantity", HttpMethod.PUT, new HttpEntity<>(changeItemQuantityRequest, acceptAndContentType(credentials)), Errors.class);
    }
}
