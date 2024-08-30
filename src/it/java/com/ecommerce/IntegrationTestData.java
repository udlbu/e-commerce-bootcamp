package com.ecommerce;

import com.ecommerce.order.domain.DeliveryMethod;
import com.ecommerce.order.domain.DeliveryStatus;
import com.ecommerce.order.domain.OrderStatus;
import com.ecommerce.order.domain.PaymentMethod;
import com.ecommerce.order.domain.PaymentStatus;
import com.ecommerce.shared.domain.Credentials;

import java.math.BigDecimal;

public class IntegrationTestData {

    // product
    public static final Long PRODUCT_ID = -1000L;

    public static final String NAME = "Product 1";
    public static final String EAN = "1000";
    // base64 encoded image is sent in add and modify product requests
    public static final String IMAGE = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAABAAEDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD3+iiigD//2Q==";
    public static final String IMAGE_NAME = "sample.jpg";
    public static final String IMAGE_NAME_PATTERN = "[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}.jpg";
    public static final String MOCK_IMAGE_NAME_PATTERN = "^http://localhost:[0-9]{5}/mock/image\\.png$";
    public static final String DESCRIPTION = "Description 1";

    public static final String CATEGORY_BOOKS = "BOOKS";

    public static final String CATEGORY_ELECTRONICS = "ELECTRONICS";

    // user
    public static final Long USER_ID = -1000L;

    public static final String EMAIL = "johnny.lee@mail.com";

    public static final String PASSWORD = "s3cr3t";

    public static final String FIRST_NAME = "Johnny";

    public static final String LAST_NAME = "Lee";

    public static final String TAX_ID = "19027148";

    public static final Long ADDRESS_ID = -1000L;

    public static final String COUNTRY = "England";

    public static final String CITY = "London";

    public static final String STREET = "Euston Rd.";

    public static final String BUILDING_NO = "71";

    public static final String FLAT_NO = "3c";

    public static final String POSTAL_CODE = "GVHC+XW";

    public static final Credentials CREDENTIALS = Credentials.builder()
            .username(EMAIL)
            .password(PASSWORD)
            .build();

    // offer
    public static final Long OFFER_ID = -1000L;

    public static final Integer OFFER_QUANTITY = 15;

    public static final BigDecimal OFFER_PRICE = BigDecimal.valueOf(69.90);

    public static final String OFFER_STATUS = "ACTIVE";

    // order
    public static final PaymentStatus PAYMENT_METHOD_SUCCESS = PaymentStatus.SUCCESS;

    public static final DeliveryStatus DELIVERY_STATUS_NEW = DeliveryStatus.NEW;

    public static final OrderStatus ORDER_STATUS_NEW = OrderStatus.NEW;

    public static final DeliveryMethod DELIVERY_METHOD_FEDEX = DeliveryMethod.FEDEX;

    public static final PaymentMethod PAYMENT_METHOD_CARD = PaymentMethod.CARD;

    // cart
    public static final Integer ITEM_QUANTITY_ONE = 1;

    // shared
    public static final Long VERSION = 0L;

    public static final String DATE = "2023-07-03T19:38:34.127464Z";

    public static final Long NON_EXISTING_ID = -5000L;
}
