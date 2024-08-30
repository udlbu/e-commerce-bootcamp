package com.ecommerce.cart.domain.port;

import com.ecommerce.cart.domain.model.Cart;
import com.ecommerce.cart.domain.model.CartId;
import com.ecommerce.offer.domain.model.OfferId;
import com.ecommerce.shared.domain.Quantity;
import com.ecommerce.user.domain.model.UserId;

public interface CartRepositoryPort {

    Cart findByUserId(UserId id);

    void addItem(OfferId offerId, UserId userId, Quantity quantity);

    void removeItem(OfferId offerId, UserId userId);

    void changeCartItemQuantity(OfferId offerId, UserId userId, Quantity quantity);

    void deleteByUserId(UserId userId);

    Cart findAndLockByCartId(CartId cartId);

    void clearCartItems(CartId cartId);
}
