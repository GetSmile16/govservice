INSERT INTO users (id, email, first_name, last_name, password)
    VALUES (1, 'example@gmail.com', 'admin', 'admin', '$2a$10$zoKxnNqBBWb9Vm9hk5qTieKc4gCIdBC.ukAtrMqooVZlRWk6gXhfC');

INSERT INTO user_role (user_id, roles)
    VALUES (1, 'USER'), (1, 'ADMIN')

