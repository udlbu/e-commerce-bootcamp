package com.ecommerce.user.api.dto;

import com.ecommerce.shared.domain.Version;
import com.ecommerce.user.domain.model.Address;
import com.ecommerce.user.domain.model.AddressId;
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

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ModifyUserAddress {

    private Long id;

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

    private Long version;

    public Address toDomain() {
        return new Address(
                new AddressId(id),
                new Country(country),
                new City(city),
                new Street(street),
                new BuildingNo(buildingNo),
                flatNo != null ? new FlatNo(flatNo) : null,
                new PostalCode(postalCode),
                new Version(version)
        );
    }
}
