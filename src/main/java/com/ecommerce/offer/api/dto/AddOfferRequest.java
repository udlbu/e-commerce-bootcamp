package com.ecommerce.offer.api.dto;

import com.ecommerce.offer.domain.model.Offer;
import com.ecommerce.offer.domain.model.OfferPrice;
import com.ecommerce.offer.domain.model.OfferStatus;
import com.ecommerce.shared.domain.Quantity;
import com.ecommerce.product.domain.model.Product;
import com.ecommerce.product.domain.model.ProductId;
import com.ecommerce.user.domain.model.UserId;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AddOfferRequest {

    @NotNull
    private Long userId;

    @NotNull
    private BigDecimal price;

    @NotNull
    private Integer quantity;

    @NotNull
    private Long productId;

    public Offer toDomain() {
        return new Offer(
                null,
                new UserId(userId),
                OfferStatus.INACTIVE,
                new OfferPrice(price),
                new Quantity(quantity),
                null,
                new Product(new ProductId(productId),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                )
        );
    }
}
