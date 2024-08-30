package com.ecommerce.order.api.dto;

import com.ecommerce.cart.domain.model.CartId;
import com.ecommerce.order.domain.DeliveryMethod;
import com.ecommerce.order.domain.PaymentMethod;
import com.ecommerce.order.domain.SubmitOrderCommand;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmitOrderRequest {

    @NotNull
    private Long cartId;

    @NotNull
    private String deliveryMethod;

    @NotNull
    private String paymentMethod;

    public SubmitOrderCommand toDomain() {
        return new SubmitOrderCommand(
                new CartId(cartId),
                DeliveryMethod.valueOf(deliveryMethod),
                PaymentMethod.valueOf(paymentMethod)
        );
    }
}
