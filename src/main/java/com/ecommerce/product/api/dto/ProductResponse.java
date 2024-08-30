package com.ecommerce.product.api.dto;

import com.ecommerce.product.domain.model.Product;
import lombok.Data;

import static com.ecommerce.shared.domain.ImageResolver.getImageName;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String ean;
    private String category;
    private String imageUrl;
    private String description;
    private Long version;

    public static ProductResponse of(Product product, String cdnUrl) {
        ProductResponse response = new ProductResponse();
        response.setId(product.id().val());
        response.setName(product.name().val());
        response.setEan(product.ean().val());
        response.setCategory(product.category().name());
        response.setImageUrl(product.imageName() != null ? cdnUrl + getImageName(cdnUrl, product.imageName().val()) : null);
        response.setDescription(product.description() != null ? product.description().val() : null);
        response.setVersion(product.version().val());
        return response;
    }
}
