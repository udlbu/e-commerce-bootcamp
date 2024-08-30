package com.ecommerce.cart.domain.model;

import com.ecommerce.offer.domain.model.Offer;
import com.ecommerce.shared.domain.Quantity;

public record CartItemQuantityToOffer(Quantity cartQuantity, Offer offer) {
}