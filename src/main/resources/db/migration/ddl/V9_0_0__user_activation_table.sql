CREATE TABLE USER_ACTIVATIONS (
    ID SERIAL NOT NULL PRIMARY KEY,
    USER_ID INTEGER NOT NULL REFERENCES USERS,
    TOKEN VARCHAR(200) UNIQUE NOT NULL,
    STATUS VARCHAR(10) NOT NULL,
    CREATED_AT TIMESTAMP NOT NULL,
    UPDATED_AT TIMESTAMP NOT NULL,
    VERSION INTEGER NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX UNIQUE_ACTIVE_USER ON USER_ACTIVATIONS(USER_ID, STATUS) WHERE STATUS = 'ACTIVE';