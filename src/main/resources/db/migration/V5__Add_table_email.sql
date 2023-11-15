CREATE TABLE email_retry
(
    id         SERIAL PRIMARY KEY NOT NULL,
    address_to VARCHAR(1000)      NOT NULL,
    subject    VARCHAR(1000)      NOT NULL,
    content    VARCHAR(10000)     NOT NULL,
    retry_time TIMESTAMP          NOT NULL
);