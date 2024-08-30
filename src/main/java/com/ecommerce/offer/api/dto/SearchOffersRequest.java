package com.ecommerce.offer.api.dto;

import com.ecommerce.offer.domain.model.SearchOffer;
import com.ecommerce.product.domain.model.ProductCategory;
import com.ecommerce.shared.domain.PageNumber;
import com.ecommerce.shared.domain.PageSize;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchOffersRequest {

    private String productCategory;

    @NotNull
    private Integer page;

    @NotNull
    private Integer pageSize;

    public SearchOffer toDomain() {
        return new SearchOffer(
                productCategory != null ? ProductCategory.valueOf(productCategory) : null,
                new PageNumber(page),
                new PageSize(pageSize));
    }
}
