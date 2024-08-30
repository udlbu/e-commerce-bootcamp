CREATE TABLE USERS (
    ID SERIAL NOT NULL PRIMARY KEY,
    EMAIL VARCHAR(50) UNIQUE NOT NULL,
    FIRST_NAME VARCHAR(50) NOT NULL,
    LAST_NAME VARCHAR(50) NOT NULL,
    TAX_ID VARCHAR(50),
    VERSION INTEGER NOT NULL DEFAULT 0,
    STATUS VARCHAR(10) NOT NULL
);