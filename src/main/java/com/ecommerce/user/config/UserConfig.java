package com.ecommerce.user.config;

import com.ecommerce.mail.domain.ActivationEmailSender;
import com.ecommerce.shared.domain.TimeProvider;
import com.ecommerce.user.AuthenticationFacade;
import com.ecommerce.user.UserFacade;
import com.ecommerce.user.adapter.SpringJpaAddressRepository;
import com.ecommerce.user.adapter.SpringJpaUserActivationRepository;
import com.ecommerce.user.adapter.SpringJpaUserRepository;
import com.ecommerce.user.adapter.UserActivationRepositoryPostgresAdapter;
import com.ecommerce.user.adapter.UserRepositoryPostgresAdapter;
import com.ecommerce.user.domain.ActivationTokenGenerator;
import com.ecommerce.user.domain.port.IAMPort;
import com.ecommerce.user.domain.port.UserActivationRepositoryPort;
import com.ecommerce.user.domain.port.UserRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfig {


    @Bean
    public UserActivationRepositoryPort userActivationRepository(SpringJpaUserActivationRepository springJpaUserActivationRepository) {
        return new UserActivationRepositoryPostgresAdapter(springJpaUserActivationRepository);
    }

    @Bean
    public ActivationTokenGenerator activationTokenGenerator(UserActivationRepositoryPort userActivationRepository, TimeProvider timeProvider) {
        return new ActivationTokenGenerator(userActivationRepository, timeProvider);
    }

    @Bean
    public UserFacade userFacade(UserRepositoryPort userRepository,
                                 UserActivationRepositoryPort userActivationRepository,
                                 IAMPort iam,
                                 ActivationTokenGenerator generator,
                                 TimeProvider timeProvider,
                                 ActivationEmailSender mailSender) {
        return new UserFacade(userRepository, userActivationRepository, iam, generator, timeProvider, mailSender);
    }

    @Bean
    public AuthenticationFacade authenticationFacade(IAMPort iam, UserRepositoryPort userRepository) {
        return new AuthenticationFacade(iam, userRepository);
    }

    @Bean
    public UserRepositoryPort userRepository(SpringJpaUserRepository springDataUserRepository,
                                             SpringJpaAddressRepository springJpaAddressRepository,
                                             SpringJpaUserActivationRepository springJpaUserActivationRepository) {
        return new UserRepositoryPostgresAdapter(springDataUserRepository, springJpaAddressRepository, springJpaUserActivationRepository);
    }
}
