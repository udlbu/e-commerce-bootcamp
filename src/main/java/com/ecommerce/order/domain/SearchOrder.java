package com.ecommerce.order.domain;

import com.ecommerce.shared.domain.PageNumber;
import com.ecommerce.shared.domain.PageSize;
import com.ecommerce.user.domain.model.UserId;

public record SearchOrder(UserId userId, PageNumber pageNumber, PageSize pageSize) {
}
