INSERT INTO season_products (id, remaining_count)
VALUES (1, 0);

SELECT setval('season_products_id_seq', 1);

INSERT INTO products (id, product_name, season_product_id)
VALUES (1, 'Оформление летнего пособия на отдых', 1),
       (2, 'Получение паспорта', NULL);

SELECT setval('products_id_seq', 2);