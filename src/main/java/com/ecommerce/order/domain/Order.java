package com.ecommerce.order.domain;

import com.ecommerce.offer.domain.model.OfferId;
import com.ecommerce.shared.domain.Version;
import com.ecommerce.user.domain.model.UserId;

import java.util.List;

public record Order(OrderId id,
                    UserId buyerId,
                    DeliveryMethod deliveryMethod,
                    DeliveryStatus deliveryStatus,
                    PaymentMethod paymentMethod,
                    PaymentStatus paymentStatus,
                    OrderStatus status,
                    CreatedAt createdAt,
                    UpdatedAt updatedAt,
                    Version version,
                    List<OrderLine> lines) {
    public List<OfferId> offerIds() {
        return lines.stream()
                .map(it -> it.offer().id())
                .toList();
    }
}
