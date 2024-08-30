package com.ecommerce.order.api.dto;

import com.ecommerce.order.domain.SearchOrder;
import com.ecommerce.shared.domain.PageNumber;
import com.ecommerce.shared.domain.PageSize;
import com.ecommerce.user.domain.model.UserId;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchOrdersRequest {

    @NotNull
    private Long userId;

    @NotNull
    private Integer page;

    @NotNull
    private Integer pageSize;

    public SearchOrder toDomain() {
        return new SearchOrder(
                new UserId(userId),
                new PageNumber(page),
                new PageSize(pageSize)
        );
    }
}
