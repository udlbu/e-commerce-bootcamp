package com.ecommerce.shared.domain;

import java.util.List;

public record PageResult<T>(List<T> data, TotalSize totalSize) {
}
