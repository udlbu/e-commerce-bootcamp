package com.ecommerce.shared.domain;

public class ImageResolver {

    public static String getImageName(String url, String image) {
        if (url.contains("mock")) {
            return "image.png";
        }
        return image;
    }
}
