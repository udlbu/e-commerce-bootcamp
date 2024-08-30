package com.ecommerce.product.adapter;

import com.ecommerce.product.domain.exceptions.ProductCannotBeDeletedException;
import com.ecommerce.product.domain.exceptions.ProductCannotBeModifiedException;
import com.ecommerce.product.domain.model.Product;
import com.ecommerce.product.domain.model.ProductId;
import com.ecommerce.product.domain.port.ProductRepositoryPort;
import com.ecommerce.shared.domain.PageResult;
import com.ecommerce.shared.domain.TotalSize;
import com.ecommerce.shared.domain.exception.ConcurrentModification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static java.lang.String.format;

@Transactional
public class ProductRepositoryPostgresAdapter implements ProductRepositoryPort {

    private final SpringJpaProductRepository springJpaProductRepository;

    public ProductRepositoryPostgresAdapter(SpringJpaProductRepository springDataProductRepository) {
        this.springJpaProductRepository = springDataProductRepository;
    }

    @Override
    public Product findProductById(ProductId id) {
        return springJpaProductRepository.findById(id.val()).map(ProductEntity::toDomain).orElse(null);
    }

    @Override
    public PageResult<Product> findProducts(Integer page, Integer pageSize) {
        Page<ProductEntity> productsPage = springJpaProductRepository.findAll(
                PageRequest.of(page, pageSize, Sort.Direction.ASC, "id")
        );
        return new PageResult<>(
                productsPage.map(ProductEntity::toDomain).toList(),
                new TotalSize(productsPage.getTotalElements())
        );
    }

    @Override
    public Product saveProduct(Product product) {
        ProductEntity entity = ProductEntity.newProduct(product);
        return springJpaProductRepository.save(entity).toDomain();
    }

    @Override
    public void deleteProductById(ProductId id) {
        Long count = springJpaProductRepository.countOffersWithProduct(id.val());
        if (count > 0) {
            throw new ProductCannotBeDeletedException("Product cannot be deleted because there are offers with it");
        }
        springJpaProductRepository.deleteById(id.val());

    }

    @Override
    public void updateProduct(Product product) {
        Long count = springJpaProductRepository.countOrderLinesWithProduct(product.id().val());
        if (count > 0) {
            throw new ProductCannotBeModifiedException("Product cannot be modified because there are orders with it");
        }
        springJpaProductRepository.findById(product.id().val()).ifPresent(entity -> {
            if (!Objects.equals(entity.getVersion(), product.version().val())) {
                throw new ConcurrentModification(format("Stale object: <%s>", product.id()));
            }
            try {
                entity.merge(product);
            } catch (ObjectOptimisticLockingFailureException ex) {
                throw new ConcurrentModification(format("Concurrent modification occurred: <%s>", product.id()), ex);
            }
        });
    }
}
