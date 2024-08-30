package com.ecommerce.product.domain.port;

import com.ecommerce.product.domain.model.Product;
import com.ecommerce.product.domain.model.ProductId;
import com.ecommerce.shared.domain.PageResult;

import java.util.List;

public interface ProductRepositoryPort {

    Product findProductById(ProductId id);

    PageResult<Product> findProducts(Integer page, Integer pageSize);

    Product saveProduct(Product product);

    void deleteProductById(ProductId id);

    void updateProduct(Product product);
}
