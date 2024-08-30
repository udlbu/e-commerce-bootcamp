package com.ecommerce.shared;

import junit.framework.AssertionFailedError;

import java.math.BigDecimal;

public class Assertions {

    public static void assertBigDecimalEquals(BigDecimal expected, BigDecimal actual) {
        int res = expected.compareTo(actual);
        if (res != 0) {
            throw new AssertionFailedError(String.format("%s is not equals to %s", expected, actual));
        }
    }
}
