package com.ecommerce.offer.api.dto;

import com.ecommerce.offer.domain.model.Offer;
import com.ecommerce.offer.domain.model.OfferId;
import com.ecommerce.offer.domain.model.OfferPrice;
import com.ecommerce.shared.domain.Quantity;
import com.ecommerce.product.domain.model.Product;
import com.ecommerce.product.domain.model.ProductId;
import com.ecommerce.shared.domain.Version;
import com.ecommerce.user.domain.model.UserId;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModifyOfferRequest {

    @NotNull
    private Long offerId;
    @NotNull
    private Long userId;

    @NotNull
    private Long version;

    private BigDecimal price;

    private Integer quantity;

    private Long productId;

    public Offer toDomain() {
        return new Offer(
                new OfferId(offerId),
                new UserId(userId),
                null,
                price != null ? new OfferPrice(price) : null,
                quantity != null ? new Quantity(quantity) : null,
                new Version(version),
                productId != null ? new Product(new ProductId(productId),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                ) : null
        );
    }
}
