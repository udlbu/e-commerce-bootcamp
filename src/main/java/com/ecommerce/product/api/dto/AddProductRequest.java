package com.ecommerce.product.api.dto;

import com.ecommerce.product.domain.model.Description;
import com.ecommerce.product.domain.model.Ean;
import com.ecommerce.product.domain.model.Image;
import com.ecommerce.product.domain.model.Name;
import com.ecommerce.product.domain.model.Product;
import com.ecommerce.product.domain.model.ProductCategory;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddProductRequest {
    @NotNull
    private String name;
    @NotNull
    private String ean;
    @NotNull
    private String category;
    private String image;
    private String description;

    public Product toDomain() {
        return new Product(
                null,
                new Name(name),
                new Ean(ean),
                ProductCategory.valueOf(category),
                null,
                description != null ? new Description(description) : null,
                null
        );
    }

    public Image toImage() {
        return image != null && !image.trim().isEmpty() ? new Image(image) : null;
    }
}
