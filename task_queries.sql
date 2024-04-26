-- 1    Вывести все записи из таблицы Users
--SELECT * FROM users;

-- 2    Вывести список пользователей, у которых username содержит букву "A"
--SELECT concat_ws(' ', first_name, last_name) AS username
--FROM users
--WHERE first_name LIKE '%а%' OR last_name LIKE '%а%';

-- 3    Вывести информацию о продуктах с ценой выше 100
--SELECT name, price
--FROM products
--WHERE price > 1000;

-- 4	Вывести пользователей, у которых отсутствуют дополнительные данные (User_Details)
--SELECT u.id, u.first_name || ' ' || u.last_name AS fullname
--FROM users AS u
--WHERE u.id NOT IN (SELECT user_id FROM user_details);

-- 5	Вывести список продуктов и количество пользователей, добавивших их в корзину
--SELECT p.name, count(shc.user_id) AS "user number"
--FROM shopping_card AS shc
--INNER JOIN products AS p ON shc.product_id = p.id
--GROUP BY p.name

-- 6	Найдите общую сумму заказов для каждого пользователя
--SELECT concat_ws(' ', u.first_name, u.last_name) AS username, sum(o.total_price)
--FROM orders AS o
--INNER JOIN users AS u ON o.user_id = u.id
--GROUP BY username

-- 7	Вывести список продуктов, которые имеются в корзине пользователя (по id)
--SELECT p.name
--FROM shopping_card AS shc
--INNER JOIN products AS p ON shc.product_id = p.id
--INNER JOIN users AS u ON shc.user_id = u.id
--WHERE u."id" = 1

-- 8	Вывести список пользователей, у которых есть заказы на сумму более 5000
--SELECT concat_ws(' ', u.first_name, u.last_name) AS username
--FROM orders AS o
--INNER JOIN users AS u ON o.user_id = u.id
--WHERE o.total_price > 5000
--GROUP BY u.id

-- 9	Вывести пользователя, который купил больше всего товаров
--SELECT concat_ws(' ', u.first_name, u.last_name), sum(shc.amount)
--FROM shopping_card AS shc
--INNER JOIN users AS u ON shc.user_id = u.id
--GROUP BY u.id
--ORDER BY sum(shc.amount) DESC
--LIMIT 1

-- 10	Вывести список 10-ти самых дорогих товаров
--SELECT name, price
--FROM products
--ORDER BY price DESC
--LIMIT 10

-- 11	Вывести список товаров с ценой выше средней
--SELECT name, price
--FROM products
--WHERE price > (SELECT avg(price) FROM products)

-- 12	Вывести список пользователей, у которых суммарная стоимость продуктов
-- в корзине превышает среднюю стоимость продуктов в корзине всех пользователей.
--SELECT concat_ws(' ', u.first_name, u.last_name) AS username, avg(shc.amount * p.price)
--FROM shopping_card AS shc
--INNER JOIN users AS u ON shc.user_id = u.id
--INNER JOIN products AS p ON shc.product_id = p.id
--GROUP BY username
--HAVING avg(shc.amount * p.price) > (
--    SELECT avg(p.price * shc.amount)
--    FROM shopping_card AS shc
--    INNER JOIN products AS p ON shc.product_id = p.id
--    );

-- 13	Вывести пользователей, у которых все продукты в корзине имеют цену выше 1000
--SELECT concat_ws(' ', u.first_name, u.last_name)
--FROM shopping_card AS shc
--INNER JOIN products AS p ON shc.product_id = p.id
--INNER JOIN users    AS u ON shc.user_id = u.id
--WHERE p.price > 1000
--GROUP BY u.id

-- 14	Вывести список продуктов, которые есть в корзине у всех пользователей
--SELECT name
--FROM products
--WHERE id IN (
--    SELECT DISTINCT product_id
--    FROM shopping_card
--    GROUP BY product_id
--    HAVING count(DISTINCT user_id) = (SELECT count(DISTINCT user_id) FROM shopping_card)
--);

-- 15	Вывести информацию о пользователях, у которых в корзине присутствуют продукты
-- с общим количеством более 10 единиц.
--SELECT u.id, concat_ws(' ', u.first_name, u.last_name) as name , sum(shc.amount)
--FROM users AS u
--INNER JOIN shopping_card AS shc ON u.id = shc.user_id
--GROUP BY u.id
--HAVING sum(shc.amount) > 10;

-- 16	Вывести пользователя, у которого сумма всех заказов превышает
-- сумму заказов любого другого пользователя.
--SELECT concat_ws(' ', u.first_name, u.last_name) AS fullname, sum(o.total_price)
--FROM users AS u
--INNER JOIN orders AS o ON u.id = o.user_id
--GROUP BY fullname
--ORDER BY sum(o.total_price) DESC
--LIMIT 1

-- 17	Вывести список пользователей, у которых количество продуктов в корзине
-- превышает среднее количество продуктов в корзине всех пользователей.
--SELECT concat_ws(' ', u.first_name, u.last_name) AS fullname
--FROM shopping_card AS shc
--INNER JOIN users AS u ON shc.user_id = u.id
--GROUP BY fullname
--HAVING sum(shc.amount) > (SELECT avg(amount) FROM shopping_card);

-- 18	Вывести продукты, которые есть в корзине только одного пользователя
--SELECT id, name
--FROM products
--WHERE id IN (
--    SELECT product_id
--    FROM shopping_card
--    GROUP BY product_id
--    HAVING count(DISTINCT user_id) = 1
--)

-- 19	Вывести пользователей, у которых суммарная стоимость заказов превышает 1000,
-- и количество заказов более 3.
--SELECT concat_ws(' ', u.first_name, u.last_name) fullname
--FROM users AS u
--INNER JOIN orders AS o ON u."id" = o.user_id
--GROUP BY fullname
--HAVING sum(o.total_price) > 10000 AND count(o."id") > 3

-- 20	Вывести информацию о продукте с наибольшей суммарной стоимостью в корзинах пользователей
--SELECT p.name, p.price
--FROM products AS p
--INNER JOIN shopping_card AS shc ON p."id" = shc.product_id
--ORDER BY p.price DESC
--LIMIT 1
