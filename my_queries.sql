-- приклад LEFT JOIN для отримання інформації про користувачів та їх додаткових даних
SELECT
    concat_ws(' ', u.first_name, u.last_name) AS username,
    concat_ws(', ', c."name", ud.street, ud.house) AS address,
    ud.ipn, ud.passport
FROM users AS u
LEFT JOIN user_details AS ud ON u.id = ud.user_id
LEFT JOIN cities AS c ON ud.city_id = c."id"

-- приклад INNER JOIN для отрмання інформації про продукти в корзинах
SELECT concat_ws(' ', u.first_name, u.last_name) AS username, p."name"
FROM users AS u
INNER JOIN shopping_card AS shc ON u.id = shc.user_id
INNER JOIN products p ON p.id = shc.product_id;

-- приклад підзапроса для отримання користувачів у яких сума замовлень перевищує конкретне значення
SELECT concat_ws(' ', first_name, last_name) AS username
FROM users
WHERE id IN (SELECT user_id FROM orders WHERE total_price > 20000);

-- приклад корелляційного підзапроса для отримання загальної кількості продуктів в корзині кожного користувача
SELECT u.id, concat_ws(' ', first_name, last_name) AS username,
       (SELECT count(*)
        FROM shopping_card AS shc
        WHERE shc.user_id = u.id) AS total_products_in_shopping_card
FROM users AS u;

-- приклад використання агрегатної функцїї для підрахунку загальної суми замовлень кожного користувача
SELECT u.id, concat_ws(' ', first_name, last_name) AS username, SUM(o.total_price) AS total_spent
FROM users AS u
LEFT JOIN orders AS o ON u.id = o.user_id
GROUP BY u.id;
