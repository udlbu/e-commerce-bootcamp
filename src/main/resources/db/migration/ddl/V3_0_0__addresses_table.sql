CREATE TABLE ADDRESSES (
    ID SERIAL NOT NULL PRIMARY KEY,
    USER_ID INTEGER NOT NULL REFERENCES USERS,
    COUNTRY VARCHAR(50) NOT NULL,
    CITY VARCHAR(50) NOT NULL,
    STREET VARCHAR(50) NOT NULL,
    BUILDING_NO VARCHAR(10) NOT NULL,
    FLAT_NO VARCHAR(10),
    POSTAL_CODE VARCHAR(10) NOT NULL,
    VERSION INTEGER NOT NULL DEFAULT 0
);