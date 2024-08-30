package com.ecommerce.cart.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddItemToCartRequest {

    @NotNull
    private Long offerId;

    @NotNull
    private Long userId;

    @NotNull
    @Min(value = 1)
    private Integer quantity;
}
