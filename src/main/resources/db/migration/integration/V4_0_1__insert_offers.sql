do $$
DECLARE
   JOHNNY_ID INTEGER;
   PRODUCT_ID INTEGER;
BEGIN
   SELECT ID
   INTO JOHNNY_ID
   FROM USERS WHERE EMAIL = 'johnny.lee@mail.com';

   SELECT ID
   INTO PRODUCT_ID
   FROM PRODUCTS WHERE EAN = '1000';

   INSERT INTO OFFERS (ID, USER_ID, PRODUCT_ID, STATUS, PRICE, QUANTITY) VALUES (-1000, JOHNNY_ID, PRODUCT_ID, 'ACTIVE', '69.90', 15);
END
$$;