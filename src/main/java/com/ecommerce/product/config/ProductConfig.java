package com.ecommerce.product.config;

import com.ecommerce.product.adapter.ProductRepositoryPostgresAdapter;
import com.ecommerce.product.adapter.SpringJpaProductRepository;
import com.ecommerce.product.adapter.bunnycdn.BunnyCdnAdapter;
import com.ecommerce.product.domain.ProductFacade;
import com.ecommerce.product.domain.port.CdnPort;
import com.ecommerce.product.domain.port.ProductRepositoryPort;
import com.ecommerce.product.domain.service.ImageNameGenerator;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductConfig {

    @Bean
    public ProductFacade productFacade(ProductRepositoryPort productRepository,
                                       CdnPort cdn,
                                       ImageNameGenerator imageNameGenerator,
                                       CdnProperties properties) {
        return new ProductFacade(productRepository, cdn, imageNameGenerator, properties);
    }

    @Bean
    ProductRepositoryPort productRepository(SpringJpaProductRepository springDataProductRepository) {
        return new ProductRepositoryPostgresAdapter(springDataProductRepository);
    }

    @Bean
    CdnPort cdn(CdnProperties properties, MeterRegistry meterRegistry) {
        return new BunnyCdnAdapter(properties, meterRegistry);
    }

    @Bean
    ImageNameGenerator imageNameGenerator() {
        return new ImageNameGenerator();
    }
}
