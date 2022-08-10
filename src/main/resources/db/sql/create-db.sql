CREATE TABLE payment
(
    id    BIGINT PRIMARY KEY,
    price DECIMAL(30, 8) NOT NULL
);

CREATE TABLE product
(
    id          BIGINT PRIMARY KEY,
    name        VARCHAR(255)   NOT NULL,
    description VARCHAR(255),
    stock       BIGINT         NOT NULL,
    price       DECIMAL(30, 8) NOT NULL,
    version     BIGINT         NOT NULL
);