package com.ecommerce.cart.config;

import com.ecommerce.cart.CartFacade;
import com.ecommerce.cart.adapter.CartRepositoryPostgresAdapter;
import com.ecommerce.cart.adapter.SpringJpaCartItemRepository;
import com.ecommerce.cart.adapter.SpringJpaCartRepository;
import com.ecommerce.cart.domain.port.CartRepositoryPort;
import com.ecommerce.offer.adapter.SpringJpaOfferRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CartConfig {

    @Bean
    public CartFacade cartFacade(CartRepositoryPort cartRepositoryPort) {
        return new CartFacade(cartRepositoryPort);
    }

    @Bean
    CartRepositoryPort cartRepository(SpringJpaCartRepository springJpaCartRepository,
                                      SpringJpaOfferRepository springJpaOfferRepository,
                                      SpringJpaCartItemRepository springJpaCartItemRepository) {
        return new CartRepositoryPostgresAdapter(springJpaCartRepository, springJpaOfferRepository, springJpaCartItemRepository);
    }
}
