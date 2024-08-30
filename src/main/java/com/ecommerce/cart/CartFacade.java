package com.ecommerce.cart;

import com.ecommerce.cart.domain.model.Cart;
import com.ecommerce.cart.domain.port.CartRepositoryPort;
import com.ecommerce.offer.domain.model.OfferId;
import com.ecommerce.shared.domain.Quantity;
import com.ecommerce.user.domain.model.UserId;

public class CartFacade {

    private final CartRepositoryPort cartRepository;

    public CartFacade(CartRepositoryPort cartRepository) {
        this.cartRepository = cartRepository;
    }

    public Cart getUserCart(UserId userId) {
        return cartRepository.findByUserId(userId);
    }

    public void addItem(OfferId offerId, UserId userId, Quantity quantity) {
        cartRepository.addItem(offerId, userId, quantity);
    }

    public void removeItem(OfferId offerId, UserId userId) {
        cartRepository.removeItem(offerId, userId);
    }

    public void changeCartItemQuantity(OfferId offerId, UserId userId, Quantity quantity) {
        cartRepository.changeCartItemQuantity(offerId, userId, quantity);
    }
}
