package com.ecommerce.cart.api.dto;

import com.ecommerce.cart.domain.model.Cart;
import lombok.Data;

import java.util.Comparator;
import java.util.List;

@Data
public class CartResponse {

    private Long id;
    private Long userId;
    private Long version;
    private List<CartItemResponse> items;

    public static CartResponse of(Cart cart, String cdnUrl) {
        if (cart == null) {
            return new CartResponse();
        }
        CartResponse response = new CartResponse();
        response.setId(cart.id().val());
        response.setUserId(cart.userId().val());
        response.setVersion(cart.version().val());
        if (cart.items() != null) {
            response.setItems(
                    cart.items()
                            .stream()
                            .map(item -> CartItemResponse.of(item, cdnUrl))
                            .sorted(Comparator.comparing(CartItemResponse::getId))
                            .toList()
            );
        }
        return response;
    }
}
