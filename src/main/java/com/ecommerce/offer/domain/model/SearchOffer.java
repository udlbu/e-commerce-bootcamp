package com.ecommerce.offer.domain.model;

import com.ecommerce.product.domain.model.ProductCategory;
import com.ecommerce.shared.domain.PageNumber;
import com.ecommerce.shared.domain.PageSize;

public record SearchOffer(ProductCategory productCategory, PageNumber pageNumber, PageSize pageSize) {
}
