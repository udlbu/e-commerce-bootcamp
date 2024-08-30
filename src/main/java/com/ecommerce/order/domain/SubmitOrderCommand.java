package com.ecommerce.order.domain;

import com.ecommerce.cart.domain.model.CartId;

public record SubmitOrderCommand(CartId cartId, DeliveryMethod deliveryMethod, PaymentMethod paymentMethod) {
}
