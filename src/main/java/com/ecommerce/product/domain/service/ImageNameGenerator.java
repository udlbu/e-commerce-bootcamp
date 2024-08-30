package com.ecommerce.product.domain.service;

import com.ecommerce.product.domain.model.ImageName;

import java.util.UUID;

public class ImageNameGenerator {

    public ImageName generate() {
        // to simplify we handle only jpg image files
        return new ImageName(UUID.randomUUID() + ".jpg");
    }
}
