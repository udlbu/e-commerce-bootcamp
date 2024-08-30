package com.ecommerce.order.domain;

import com.ecommerce.offer.domain.model.Offer;
import com.ecommerce.shared.domain.Quantity;
import com.ecommerce.shared.domain.Version;

public record OrderLine(OrderLineId id, Offer offer, Quantity quantity, Version version) {
}
