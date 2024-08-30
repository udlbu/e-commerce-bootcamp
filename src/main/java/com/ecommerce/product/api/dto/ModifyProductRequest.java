package com.ecommerce.product.api.dto;

import com.ecommerce.product.domain.model.Description;
import com.ecommerce.product.domain.model.Ean;
import com.ecommerce.product.domain.model.Image;
import com.ecommerce.product.domain.model.Name;
import com.ecommerce.product.domain.model.Product;
import com.ecommerce.product.domain.model.ProductCategory;
import com.ecommerce.product.domain.model.ProductId;
import com.ecommerce.shared.domain.Version;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModifyProductRequest {

    @NotNull
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String ean;

    @NotNull
    private String category;

    private String image;
    private String description;

    @NotNull
    private Long version;

    public Product toDomain() {
        return new Product(
                new ProductId(id),
                new Name(name),
                new Ean(ean),
                ProductCategory.valueOf(category),
                null,
                description != null ? new Description(description) : null,
                new Version(version)
        );
    }

    public Image toImage() {
        return image != null ? new Image(image) : null;
    }
}
