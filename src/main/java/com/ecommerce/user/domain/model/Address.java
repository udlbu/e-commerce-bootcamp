package com.ecommerce.user.domain.model;

import com.ecommerce.shared.domain.Version;

public record Address(AddressId id, Country country, City city, Street street, BuildingNo buildingNo, FlatNo flatNo, PostalCode postalCode, Version version) {
}
