package com.ecommerce.user.api.dto;

import com.ecommerce.user.domain.model.Address;
import lombok.Data;

@Data
public class AddressResponse {
    private Long id;
    private String country;
    private String city;
    private String street;
    private String buildingNo;
    private String flatNo;
    private String postalCode;
    private Long version;

    public static AddressResponse of(Address address) {
        AddressResponse response = new AddressResponse();
        response.setId(address.id().val());
        response.setCountry(address.country().val());
        response.setCity(address.city().val());
        response.setStreet(address.street().val());
        response.setBuildingNo(address.buildingNo().val());
        response.setFlatNo(address.flatNo() != null ? address.flatNo().val() : null);
        response.setPostalCode(address.postalCode().val());
        response.setVersion(address.version().val());
        return response;
    }
}
