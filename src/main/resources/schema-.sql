CREATE TABLE USERS (
                       USER_ID VARCHAR(50) PRIMARY KEY,
                       PASSWORD VARCHAR(100),
                       ROLE VARCHAR(20)
);

CREATE TABLE API_PERMISSIONS (
                                 ID BIGINT PRIMARY KEY AUTO_INCREMENT,
                                 ROLES VARCHAR(255),
                                 METHODS VARCHAR(10),
                                 PATH VARCHAR(255)
);
