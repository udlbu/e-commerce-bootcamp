package com.ecommerce.offer.config;

import com.ecommerce.offer.adapter.OfferRepositoryPostgresAdapter;
import com.ecommerce.offer.adapter.SpringJpaOfferRepository;
import com.ecommerce.offer.domain.OfferFacade;
import com.ecommerce.offer.domain.port.OfferRepositoryPort;
import com.ecommerce.product.adapter.SpringJpaProductRepository;
import com.ecommerce.user.adapter.SpringJpaUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OfferConfig {

    @Bean
    public OfferFacade offerFacade(OfferRepositoryPort offerRepositoryPort) {
        return new OfferFacade(offerRepositoryPort);
    }

    @Bean
    OfferRepositoryPort offerRepository(SpringJpaOfferRepository springJpaOfferRepository,
                                        SpringJpaProductRepository springJpaProductRepository,
                                        SpringJpaUserRepository springJpaUserRepository) {
        return new OfferRepositoryPostgresAdapter(springJpaOfferRepository,
                springJpaProductRepository,
                springJpaUserRepository);
    }
}
