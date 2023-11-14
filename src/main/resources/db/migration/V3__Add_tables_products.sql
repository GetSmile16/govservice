CREATE TABLE products
(
    id           SERIAL PRIMARY KEY NOT NULL,
    product_name VARCHAR(1000)      NOT NULL
);

CREATE TABLE season_products
(
    id              SERIAL PRIMARY KEY NOT NULL,
    remaining_count INT                NOT NULL,
    product_id      BIGINT UNIQUE,
    FOREIGN KEY (product_id) REFERENCES products (id)
);

CREATE TABLE users_products
(
    id              SERIAL PRIMARY KEY NOT NULL,
    product_id      BIGINT             NOT NULL,
    user_id         BIGINT             NOT NULL,
    date_of_created TIMESTAMP          NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
);