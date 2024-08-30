package com.ecommerce.offer.domain.model;

import com.ecommerce.product.domain.model.Product;
import com.ecommerce.shared.domain.Quantity;
import com.ecommerce.shared.domain.Version;
import com.ecommerce.user.domain.model.UserId;

public record Offer(OfferId id, UserId userId, OfferStatus status, OfferPrice price, Quantity quantity, Version version, Product product) {

    public boolean isActive() {
        return status == OfferStatus.ACTIVE;
    }
}

