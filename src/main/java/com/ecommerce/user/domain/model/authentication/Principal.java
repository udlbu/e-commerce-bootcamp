package com.ecommerce.user.domain.model.authentication;

import java.util.Objects;

public record Principal(String email, AccessToken accessToken) {

    public Principal(String email) {
        this(email, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Principal principal = (Principal) o;
        return email.equals(principal.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
