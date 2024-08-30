package com.ecommerce.user.api.dto;

import com.ecommerce.shared.domain.Version;
import com.ecommerce.user.domain.model.FirstName;
import com.ecommerce.user.domain.model.LastName;
import com.ecommerce.user.domain.model.TaxId;
import com.ecommerce.user.domain.model.User;
import com.ecommerce.user.domain.model.UserId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ModifyUserRequest {

    @NotNull
    private Long id;

    @NotNull
    @Email
    private String email;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    private String taxId;

    @NotNull
    private Long version;

    @Valid
    private ModifyUserAddress address;

    public User toDomain() {
        return new User(
                new UserId(id),
                new com.ecommerce.user.domain.model.Email(email),
                new FirstName(firstName),
                new LastName(lastName),
                taxId != null ? new TaxId(taxId) : null,
                new Version(version),
                address != null ? address.toDomain() : null
        );
    }
}
