package com.ecommerce.product.api.dto;

import com.ecommerce.product.domain.model.Product;
import com.ecommerce.shared.domain.PageResult;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ProductsPageResponse {

    private List<ProductResponse> products;

    private Long total;

    public static ProductsPageResponse of(PageResult<Product> productsPage, String cdnUrl) {
        return new ProductsPageResponse(
                productsPage.data().stream().map(product -> ProductResponse.of(product, cdnUrl)).toList(),
                productsPage.totalSize().val()
        );
    }
}
