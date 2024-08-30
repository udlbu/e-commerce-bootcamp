package com.ecommerce.user.api.dto;

import com.ecommerce.user.domain.model.Address;
import com.ecommerce.user.domain.model.BuildingNo;
import com.ecommerce.user.domain.model.City;
import com.ecommerce.user.domain.model.Country;
import com.ecommerce.user.domain.model.FlatNo;
import com.ecommerce.user.domain.model.PostalCode;
import com.ecommerce.user.domain.model.Street;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddUserAddress {

    @NotNull
    private String country;

    @NotNull
    private String city;

    @NotNull
    private String street;

    @NotNull
    private String buildingNo;

    private String flatNo;

    @NotNull
    private String postalCode;

    public Address toDomain() {
        return new Address(
                null,
                new Country(country),
                new City(city),
                new Street(street),
                new BuildingNo(buildingNo),
                flatNo != null ? new FlatNo(flatNo) : null,
                new PostalCode(postalCode),
                null
        );
    }
}
