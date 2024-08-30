package com.ecommerce.cart.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RemoveItemFromCartRequest {

    @NotNull
    private Long offerId;

    @NotNull
    private Long userId;
}
