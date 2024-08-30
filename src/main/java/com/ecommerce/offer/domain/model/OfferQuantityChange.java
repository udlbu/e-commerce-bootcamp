package com.ecommerce.offer.domain.model;

import com.ecommerce.shared.domain.Quantity;

public record OfferQuantityChange(Offer offer, Quantity newQuantity) {
}
