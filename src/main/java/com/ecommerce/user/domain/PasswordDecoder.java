package com.ecommerce.user.domain;

import org.apache.commons.codec.digest.DigestUtils;

public class PasswordDecoder {

    public static String decode(String plain) {
        return DigestUtils.sha256Hex(plain);
    }
}
