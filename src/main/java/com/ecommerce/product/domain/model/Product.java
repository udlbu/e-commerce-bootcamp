package com.ecommerce.product.domain.model;

import com.ecommerce.shared.domain.Version;

public record Product(ProductId id,
                      Name name,
                      Ean ean,
                      ProductCategory category,
                      ImageName imageName,
                      Description description,
                      Version version) {
    public Product withImageName(ImageName imageName) {
        return new Product(
                id,
                name,
                ean,
                category,
                imageName,
                description,
                version
        );
    }
}
