package com.ecommerce.infrastructure;

import com.ecommerce.shared.domain.exception.UserRemovedFromSystemException;
import com.ecommerce.user.domain.model.Email;
import com.ecommerce.user.domain.model.User;
import com.ecommerce.user.domain.port.UserRepositoryPort;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KeycloakJwtTokenConverter implements Converter<Jwt, UsernamePasswordAuthenticationToken> {

    private static final String REALM_ACCESS = "realm_access";
    private static final String ROLES = "roles";
    private static final String ROLE_PREFIX = "ROLE_";

    private static final String EMAIL = "email";
    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter;
    private final UserRepositoryPort userRepository;

    public KeycloakJwtTokenConverter(JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter,
                                     UserRepositoryPort userRepository
    ) {
        this.jwtGrantedAuthoritiesConverter = jwtGrantedAuthoritiesConverter;
        this.userRepository = userRepository;
    }

    @Override
    public UsernamePasswordAuthenticationToken convert(@NonNull Jwt jwt) {
        Stream<SimpleGrantedAuthority> accesses = Optional.of(jwt)
                .map(it -> it.getClaimAsMap(REALM_ACCESS))
                .map(realmAccess -> realmAccess.get(ROLES))
                .stream()
                .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role))
                .distinct();

        Set<GrantedAuthority> authorities = Stream
                .concat(jwtGrantedAuthoritiesConverter.convert(jwt).stream(), accesses)
                .collect(Collectors.toSet());

        String principalClaimName = Optional.ofNullable(jwt.getClaimAsString(EMAIL))
                .orElse(jwt.getClaimAsString(JwtClaimNames.SUB));

        User user = userRepository.findUserByEmail(new Email(principalClaimName));
        if (user == null) {
            throw new UserRemovedFromSystemException("User has been removed from the system but still exist in IAM provider");
        }
        return new UsernamePasswordAuthenticationToken(user, null, authorities);
    }
}