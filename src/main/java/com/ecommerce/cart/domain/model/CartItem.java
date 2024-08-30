package com.ecommerce.cart.domain.model;

import com.ecommerce.offer.domain.model.Offer;
import com.ecommerce.shared.domain.Quantity;
import com.ecommerce.shared.domain.Version;

public record CartItem(CartItemId id, Offer offer, Quantity quantity, Version version) {
}
