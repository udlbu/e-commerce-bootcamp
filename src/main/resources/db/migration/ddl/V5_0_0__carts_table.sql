CREATE TABLE CARTS (
    ID SERIAL NOT NULL PRIMARY KEY,
    USER_ID INTEGER NOT NULL REFERENCES USERS,
    VERSION INTEGER NOT NULL DEFAULT 0
);