CREATE TABLE users
(
    id         SERIAL PRIMARY KEY,
    email      VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(255)        NOT NULL,
    last_name  VARCHAR(255)        NOT NULL,
    patronymic VARCHAR(255)        NOT NULL,
    password   VARCHAR(1000)       NOT NULL
);

CREATE TABLE user_role
(
    user_id BIGINT,
    roles   VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users (id)
);
