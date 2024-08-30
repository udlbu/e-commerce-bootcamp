package com.ecommerce.product.domain.port;

import com.ecommerce.product.domain.model.Image;
import com.ecommerce.product.domain.model.ImageName;

public interface CdnPort {

    void uploadImage(Image image, ImageName imageName);
}
