package com.ecommerce.cart.api.dto;

import com.ecommerce.cart.domain.model.CartItem;
import lombok.Data;

import java.math.BigDecimal;

import static com.ecommerce.shared.domain.ImageResolver.getImageName;

@Data
public class CartItemResponse {

    private Long id;
    private Integer quantity;
    private Long version;
    // offer and product data
    private Long offerId;
    private BigDecimal price;
    private String productName;
    private String ean;
    private String category;
    private String imageUrl;
    private String description;

    public static CartItemResponse of(CartItem cartItem, String cdnUrl) {
        CartItemResponse response = new CartItemResponse();
        response.setId(cartItem.id().val());
        response.setQuantity(cartItem.quantity().val());
        response.setVersion(cartItem.version().val());
        response.setOfferId(cartItem.offer().id().val());
        response.setPrice(cartItem.offer().price().val());
        response.setProductName(cartItem.offer().product().name().val());
        response.setEan(cartItem.offer().product().ean().val());
        response.setCategory(cartItem.offer().product().category().name());
        response.setImageUrl(cdnUrl + getImageName(cdnUrl, cartItem.offer().product().imageName().val()));
        response.setDescription(cartItem.offer().product().description().val());
        return response;
    }
}
