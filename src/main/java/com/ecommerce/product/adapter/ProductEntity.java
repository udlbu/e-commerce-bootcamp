package com.ecommerce.product.adapter;

import com.ecommerce.product.domain.model.Description;
import com.ecommerce.product.domain.model.Ean;
import com.ecommerce.product.domain.model.ImageName;
import com.ecommerce.product.domain.model.Name;
import com.ecommerce.product.domain.model.Product;
import com.ecommerce.product.domain.model.ProductCategory;
import com.ecommerce.product.domain.model.ProductId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Data;

@Entity
@Table(name = "PRODUCTS")
@Data
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "NAME", length = 50, nullable = false)
    private String name;
    @Column(name = "EAN", length = 20, nullable = false)
    private String ean;

    @Column(name = "CATEGORY", length = 50, nullable = false)
    private String category;

    @Column(name = "IMAGE_NAME", length = 100)
    private String imageName;

    @Column(name = "DESCRIPTION", length = 500)
    private String description;
    @Version
    @Column(name = "VERSION")
    private Long version;

    public Product toDomain() {
        return new Product(
                new ProductId(id),
                new Name(name),
                new Ean(ean),
                ProductCategory.valueOf(category),
                imageName != null ? new ImageName(imageName) : null,
                description != null ? new Description(description) : null,
                new com.ecommerce.shared.domain.Version(version)
        );
    }

    public static ProductEntity newProduct(Product product) {
        ProductEntity entity = new ProductEntity();
        entity.setName(product.name().val());
        entity.setEan(product.ean().val());
        entity.setCategory(product.category().name());
        entity.setImageName(product.imageName() != null ? product.imageName().val() : null);
        entity.setDescription(product.description() != null ? product.description().val() : null);
        return entity;
    }

    public ProductEntity merge(Product product) {
        setName(product.name().val());
        setEan(product.ean().val());
        setCategory(product.category().name());
        setImageName(product.imageName() != null ? product.imageName().val() : this.imageName);
        setDescription(product.description() != null ? product.description().val() : null);
        return this;
    }
}
