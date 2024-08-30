package com.ecommerce.user.domain.model;

import com.ecommerce.shared.domain.Version;

public record User(UserId id, Email email, FirstName firstName, LastName lastName, TaxId taxId, Version version, Address address) {
}
