package com.ecommerce.cart.api;

import com.ecommerce.cart.CartFacade;
import com.ecommerce.cart.api.dto.AddItemToCartRequest;
import com.ecommerce.cart.api.dto.CartResponse;
import com.ecommerce.cart.api.dto.ChangeItemQuantityRequest;
import com.ecommerce.cart.api.dto.RemoveItemFromCartRequest;
import com.ecommerce.cart.domain.model.Cart;
import com.ecommerce.offer.domain.model.OfferId;
import com.ecommerce.product.config.CdnProperties;
import com.ecommerce.shared.domain.AuthorizationFacade;
import com.ecommerce.shared.domain.Quantity;
import com.ecommerce.user.domain.model.UserId;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api")
public class CartEndpoint {

    private final CartFacade facade;

    private final AuthorizationFacade authorizationFacade;

    private final CdnProperties properties;

    public CartEndpoint(CartFacade facade, AuthorizationFacade authorizationFacade, CdnProperties properties) {
        this.facade = facade;
        this.authorizationFacade = authorizationFacade;
        this.properties = properties;
    }

    @GetMapping(value = "/carts/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CartResponse> getUserCart(@PathVariable("userId") Long userId, HttpServletRequest request) {
        authorizationFacade.authorize(new UserId(userId));
        Cart cart = facade.getUserCart(new UserId(userId));
        return ok(CartResponse.of(cart, properties.getImgUrl(request)));
    }

    @PostMapping(value = "/carts", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addItemToCart(@Valid @RequestBody AddItemToCartRequest request) {
        authorizationFacade.authorize(new UserId(request.getUserId()));
        facade.addItem(
                new OfferId(request.getOfferId()),
                new UserId(request.getUserId()),
                new Quantity(request.getQuantity()));
    }

    @PostMapping(value = "/carts/remove-item", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void removeItemFromCart(@Valid @RequestBody RemoveItemFromCartRequest request) {
        authorizationFacade.authorize(new UserId(request.getUserId()));
        facade.removeItem(new OfferId(request.getOfferId()), new UserId(request.getUserId()));
    }

    @PutMapping(value = "/carts/change-quantity", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void changeCartItemQuantity(@Valid @RequestBody ChangeItemQuantityRequest request) {
        authorizationFacade.authorize(new UserId(request.getUserId()));
        facade.changeCartItemQuantity(
                new OfferId(request.getOfferId()),
                new UserId(request.getUserId()),
                new Quantity(request.getQuantity()));
    }
}
