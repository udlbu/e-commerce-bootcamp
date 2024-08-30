package com.ecommerce.product.domain;

import com.ecommerce.product.config.CdnProperties;
import com.ecommerce.product.domain.model.Image;
import com.ecommerce.product.domain.model.ImageName;
import com.ecommerce.product.domain.model.Product;
import com.ecommerce.product.domain.model.ProductId;
import com.ecommerce.product.domain.port.CdnPort;
import com.ecommerce.product.domain.port.ProductRepositoryPort;
import com.ecommerce.product.domain.service.ImageNameGenerator;
import com.ecommerce.shared.domain.PageResult;

import java.util.List;

public class ProductFacade {

    private final ProductRepositoryPort productRepository;

    private final CdnPort cdn;

    private final ImageNameGenerator imageNameGenerator;

    private final CdnProperties properties;

    private final static ImageName MOCK_IMAGE = new ImageName("image.png");

    public ProductFacade(ProductRepositoryPort productRepository,
                         CdnPort cdn,
                         ImageNameGenerator imageNameGenerator, CdnProperties properties) {
        this.productRepository = productRepository;
        this.cdn = cdn;
        this.imageNameGenerator = imageNameGenerator;
        this.properties = properties;
    }

    public Product getProduct(ProductId id) {
        return productRepository.findProductById(id);
    }

    public PageResult<Product> getProducts(Integer page, Integer pageSize) {
        return productRepository.findProducts(page, pageSize);
    }

    public Product addProduct(Product product, Image image) {
        if (image != null) {
            if (properties.getEnabled()) {
                ImageName imageName = imageNameGenerator.generate();
                Product savedProduct = productRepository.saveProduct(product.withImageName(imageName));
                cdn.uploadImage(image, imageName);
                return savedProduct;
            } else {
                return productRepository.saveProduct(product.withImageName(MOCK_IMAGE));
            }

        }
        return productRepository.saveProduct(product);
    }

    public void modifyProduct(Product product, Image image) {
        if (image != null) {
            if (properties.getEnabled()) {
                // Consider adding image only if it does not exist in CDN
                ImageName imageName = imageNameGenerator.generate();
                productRepository.updateProduct(product.withImageName(imageName));
                cdn.uploadImage(image, imageName);
            } else {
                productRepository.updateProduct(product.withImageName(MOCK_IMAGE));
            }
            return;
        }
        productRepository.updateProduct(product);
    }

    public void deleteProduct(ProductId id) {
        productRepository.deleteProductById(id);
    }
}
