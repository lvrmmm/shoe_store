-- ЗАПОЛНЕНИЕ ТАБЛИЦ ДАННЫМИ

-- 1. Справочные таблицы

INSERT INTO roles (role_name) VALUES
('Администратор'),
('Менеджер'),
('Авторизированный клиент')
ON CONFLICT (role_name) DO NOTHING;

INSERT INTO suppliers (supplier_name) VALUES
('Kari'),
('Обувь для вас')
ON CONFLICT (supplier_name) DO NOTHING;

INSERT INTO manufacturers (manufacturer_name) VALUES
('Kari'),
('Marco Tozzi'),
('Рос'),
('Rieker'),
('Alessio Nesca'),
('CROSBY')
ON CONFLICT (manufacturer_name) DO NOTHING;

INSERT INTO categories (category_name) VALUES
('Женская обувь'),
('Мужская обувь')
ON CONFLICT (category_name) DO NOTHING;

INSERT INTO units (unit_name) VALUES
('шт.')
ON CONFLICT (unit_name) DO NOTHING;

INSERT INTO order_statuses (status_name) VALUES
('Новый'),
('Завершен')
ON CONFLICT (status_name) DO NOTHING;

-- 2. Пункты выдачи

INSERT INTO pickup_points (postal_code, city, street, house_number) VALUES
('420151','Лесной','Вишневая','32'),
('125061','Лесной','Подгорная','8'),
('630370','Лесной','Шоссейная','24'),
('400562','Лесной','Зеленая','32'),
('614510','Лесной','Маяковского','47'),
('410542','Лесной','Светлая','46'),
('620839','Лесной','Цветочная','8'),
('443890','Лесной','Коммунистическая','1'),
('603379','Лесной','Спортивная','46'),
('603721','Лесной','Гоголя','41'),
('410172','Лесной','Северная','13'),
('614611','Лесной','Молодежная','50'),
('454311','Лесной','Новая','19'),
('660007','Лесной','Октябрьская','19'),
('603036','Лесной','Садовая','4'),
('394060','Лесной','Фрунзе','43'),
('410661','Лесной','Школьная','50'),
('625590','Лесной','Коммунистическая','20'),
('625683','Лесной','8 Марта','б/н'),
('450983','Лесной','Комсомольская','26'),
('394782','Лесной','Чехова','3'),
('603002','Лесной','Дзержинского','28'),
('450558','Лесной','Набережная','30'),
('344288','Лесной','Чехова','1'),
('614164','Лесной','Степная','30'),
('394242','Лесной','Коммунистическая','43'),
('660540','Лесной','Солнечная','25'),
('125837','Лесной','Шоссейная','40'),
('125703','Лесной','Партизанская','49'),
('625283','Лесной','Победы','46'),
('614753','Лесной','Полевая','35'),
('426030','Лесной','Маяковского','44'),
('450375','Лесной','Клубная','44'),
('625560','Лесной','Некрасова','12'),
('630201','Лесной','Комсомольская','17'),
('190949','Лесной','Мичурина','26')
ON CONFLICT (full_address) DO NOTHING;

-- 3. Пользователи

INSERT INTO users (full_name, login, password, role_id)
SELECT 'Никифорова Весения Николаевна', '94d5ous@gmail.com', 'uzWC67', role_id
FROM roles
WHERE role_name = 'Администратор'
ON CONFLICT (login) DO NOTHING;

INSERT INTO users (full_name, login, password, role_id)
SELECT 'Сазонов Руслан Германович', 'uth4iz@mail.com', '2L6KZG', role_id
FROM roles
WHERE role_name = 'Администратор'
ON CONFLICT (login) DO NOTHING;

INSERT INTO users (full_name, login, password, role_id)
SELECT 'Одинцов Серафим Артёмович', 'yzls62@outlook.com', 'JlFRCZ', role_id
FROM roles
WHERE role_name = 'Администратор'
ON CONFLICT (login) DO NOTHING;

INSERT INTO users (full_name, login, password, role_id)
SELECT 'Степанов Михаил Артёмович', '1diph5e@tutanota.com', '8ntwUp', role_id
FROM roles
WHERE role_name = 'Менеджер'
ON CONFLICT (login) DO NOTHING;

INSERT INTO users (full_name, login, password, role_id)
SELECT 'Ворсин Петр Евгеньевич', 'tjde7c@yahoo.com', 'YOyhfR', role_id
FROM roles
WHERE role_name = 'Менеджер'
ON CONFLICT (login) DO NOTHING;

INSERT INTO users (full_name, login, password, role_id)
SELECT 'Старикова Елена Павловна', 'wpmrc3do@tutanota.com', 'RSbvHv', role_id
FROM roles
WHERE role_name = 'Менеджер'
ON CONFLICT (login) DO NOTHING;

INSERT INTO users (full_name, login, password, role_id)
SELECT 'Михайлюк Анна Вячеславовна', '5d4zbu@tutanota.com', 'rwVDh9', role_id
FROM roles
WHERE role_name = 'Авторизированный клиент'
ON CONFLICT (login) DO NOTHING;

INSERT INTO users (full_name, login, password, role_id)
SELECT 'Ситдикова Елена Анатольевна', 'ptec8ym@yahoo.com', 'LdNyos', role_id
FROM roles
WHERE role_name = 'Авторизированный клиент'
ON CONFLICT (login) DO NOTHING;

INSERT INTO users (full_name, login, password, role_id)
SELECT 'Ворсин Петр Евгеньевич', '1qz4kw@mail.com', 'gynQMT', role_id
FROM roles
WHERE role_name = 'Авторизированный клиент'
ON CONFLICT (login) DO NOTHING;

INSERT INTO users (full_name, login, password, role_id)
SELECT 'Старикова Елена Павловна', '4np6se@mail.com', 'AtnDjr', role_id
FROM roles
WHERE role_name = 'Авторизированный клиент'
ON CONFLICT (login) DO NOTHING;

-- 4. Товары

INSERT INTO products (
    product_article, product_name, unit_id, price, supplier_id,
    manufacturer_id, category_id, current_discount,
    quantity_in_stock, description, photo
)
SELECT
    'A112T4', 'Ботинки',
    (SELECT unit_id FROM units WHERE unit_name = 'шт.'),
    4990.00,
    (SELECT supplier_id FROM suppliers WHERE supplier_name = 'Kari'),
    (SELECT manufacturer_id FROM manufacturers WHERE manufacturer_name = 'Kari'),
    (SELECT category_id FROM categories WHERE category_name = 'Женская обувь'),
    3, 6,
    'Женские Ботинки демисезонные kari',
    '1.jpg'
ON CONFLICT (product_article) DO NOTHING;

INSERT INTO products
SELECT 'F635R4','Ботинки',
(SELECT unit_id FROM units WHERE unit_name='шт.'),
3244.00,
(SELECT supplier_id FROM suppliers WHERE supplier_name='Обувь для вас'),
(SELECT manufacturer_id FROM manufacturers WHERE manufacturer_name='Marco Tozzi'),
(SELECT category_id FROM categories WHERE category_name='Женская обувь'),
2,13,'Ботинки Marco Tozzi женские демисезонные, размер 39, цвет бежевый','2.jpg'
ON CONFLICT (product_article) DO NOTHING;

INSERT INTO products
SELECT 'H782T5','Туфли',
(SELECT unit_id FROM units WHERE unit_name='шт.'),
4499.00,
(SELECT supplier_id FROM suppliers WHERE supplier_name='Kari'),
(SELECT manufacturer_id FROM manufacturers WHERE manufacturer_name='Kari'),
(SELECT category_id FROM categories WHERE category_name='Мужская обувь'),
4,5,'Туфли kari мужские классика MYZ21AW-450A, размер 43, цвет: черный','3.jpg'
ON CONFLICT (product_article) DO NOTHING;

INSERT INTO products
SELECT 'G783F5','Ботинки',
(SELECT unit_id FROM units WHERE unit_name='шт.'),
5900.00,
(SELECT supplier_id FROM suppliers WHERE supplier_name='Kari'),
(SELECT manufacturer_id FROM manufacturers WHERE manufacturer_name='Рос'),
(SELECT category_id FROM categories WHERE category_name='Мужская обувь'),
2,8,'Мужские ботинки Рос-Обувь кожаные с натуральным мехом','4.jpg'
ON CONFLICT (product_article) DO NOTHING;

INSERT INTO products
SELECT 'J384T6','Ботинки',
(SELECT unit_id FROM units WHERE unit_name='шт.'),
3800.00,
(SELECT supplier_id FROM suppliers WHERE supplier_name='Обувь для вас'),
(SELECT manufacturer_id FROM manufacturers WHERE manufacturer_name='Rieker'),
(SELECT category_id FROM categories WHERE category_name='Мужская обувь'),
2,16,'B3430/14 Полуботинки мужские Rieker','5.jpg'
ON CONFLICT (product_article) DO NOTHING;

INSERT INTO products
SELECT 'D572U8','Кроссовки',
(SELECT unit_id FROM units WHERE unit_name='шт.'),
4100.00,
(SELECT supplier_id FROM suppliers WHERE supplier_name='Обувь для вас'),
(SELECT manufacturer_id FROM manufacturers WHERE manufacturer_name='Рос'),
(SELECT category_id FROM categories WHERE category_name='Мужская обувь'),
3,6,'129615-4 Кроссовки мужские','6.jpg'
ON CONFLICT (product_article) DO NOTHING;

INSERT INTO products
SELECT 'F572H7','Туфли',
(SELECT unit_id FROM units WHERE unit_name='шт.'),
2700.00,
(SELECT supplier_id FROM suppliers WHERE supplier_name='Kari'),
(SELECT manufacturer_id FROM manufacturers WHERE manufacturer_name='Marco Tozzi'),
(SELECT category_id FROM categories WHERE category_name='Женская обувь'),
2,14,'Туфли Marco Tozzi женские летние, размер 39, цвет черный','7.jpg'
ON CONFLICT (product_article) DO NOTHING;

INSERT INTO products
SELECT 'D329H3','Полуботинки',
(SELECT unit_id FROM units WHERE unit_name='шт.'),
1890.00,
(SELECT supplier_id FROM suppliers WHERE supplier_name='Обувь для вас'),
(SELECT manufacturer_id FROM manufacturers WHERE manufacturer_name='Alessio Nesca'),
(SELECT category_id FROM categories WHERE category_name='Женская обувь'),
4,4,'Полуботинки Alessio Nesca женские 3-30797-47, размер 37, цвет: бордовый','8.jpg'
ON CONFLICT (product_article) DO NOTHING;

INSERT INTO products
SELECT 'B320R5','Туфли',
(SELECT unit_id FROM units WHERE unit_name='шт.'),
4300.00,
(SELECT supplier_id FROM suppliers WHERE supplier_name='Kari'),
(SELECT manufacturer_id FROM manufacturers WHERE manufacturer_name='Rieker'),
(SELECT category_id FROM categories WHERE category_name='Женская обувь'),
2,6,'Туфли Rieker женские демисезонные, размер 41, цвет коричневый','9.jpg'
ON CONFLICT (product_article) DO NOTHING;

INSERT INTO products
SELECT 'G432E4','Туфли',
(SELECT unit_id FROM units WHERE unit_name='шт.'),
2800.00,
(SELECT supplier_id FROM suppliers WHERE supplier_name='Kari'),
(SELECT manufacturer_id FROM manufacturers WHERE manufacturer_name='Kari'),
(SELECT category_id FROM categories WHERE category_name='Женская обувь'),
3,15,'Туфли kari женские TR-YR-413017, размер 37, цвет: черный','10.jpg'
ON CONFLICT (product_article) DO NOTHING;

INSERT INTO products
SELECT 'S213E3','Полуботинки',
(SELECT unit_id FROM units WHERE unit_name='шт.'),
2156.00,
(SELECT supplier_id FROM suppliers WHERE supplier_name='Обувь для вас'),
(SELECT manufacturer_id FROM manufacturers WHERE manufacturer_name='CROSBY'),
(SELECT category_id FROM categories WHERE category_name='Мужская обувь'),
3,6,'407700/01-01 Полуботинки мужские CROSBY',NULL
ON CONFLICT (product_article) DO NOTHING;

INSERT INTO products
SELECT 'E482R4','Полуботинки',
(SELECT unit_id FROM units WHERE unit_name='шт.'),
1800.00,
(SELECT supplier_id FROM suppliers WHERE supplier_name='Kari'),
(SELECT manufacturer_id FROM manufacturers WHERE manufacturer_name='Kari'),
(SELECT category_id FROM categories WHERE category_name='Женская обувь'),
2,14,'Полуботинки kari женские MYZ20S-149, размер 41, цвет: черный',NULL
ON CONFLICT (product_article) DO NOTHING;

INSERT INTO products
SELECT 'S634B5','Кеды',
(SELECT unit_id FROM units WHERE unit_name='шт.'),
5500.00,
(SELECT supplier_id FROM suppliers WHERE supplier_name='Обувь для вас'),
(SELECT manufacturer_id FROM manufacturers WHERE manufacturer_name='CROSBY'),
(SELECT category_id FROM categories WHERE category_name='Мужская обувь'),
3,0,'Кеды Caprice мужские демисезонные, размер 42, цвет черный',NULL
ON CONFLICT (product_article) DO NOTHING;

INSERT INTO products
SELECT 'K345R4','Полуботинки',
(SELECT unit_id FROM units WHERE unit_name='шт.'),
2100.00,
(SELECT supplier_id FROM suppliers WHERE supplier_name='Обувь для вас'),
(SELECT manufacturer_id FROM manufacturers WHERE manufacturer_name='CROSBY'),
(SELECT category_id FROM categories WHERE category_name='Мужская обувь'),
2,3,'407700/01-02 Полуботинки мужские CROSBY',NULL
ON CONFLICT (product_article) DO NOTHING;

INSERT INTO products
SELECT 'O754F4','Туфли',
(SELECT unit_id FROM units WHERE unit_name='шт.'),
5400.00,
(SELECT supplier_id FROM suppliers WHERE supplier_name='Обувь для вас'),
(SELECT manufacturer_id FROM manufacturers WHERE manufacturer_name='Rieker'),
(SELECT category_id FROM categories WHERE category_name='Женская обувь'),
4,18,'Туфли женские демисезонные Rieker артикул 55073-68/37',NULL
ON CONFLICT (product_article) DO NOTHING;

INSERT INTO products
SELECT 'G531F4','Ботинки',
(SELECT unit_id FROM units WHERE unit_name='шт.'),
6600.00,
(SELECT supplier_id FROM suppliers WHERE supplier_name='Kari'),
(SELECT manufacturer_id FROM manufacturers WHERE manufacturer_name='Kari'),
(SELECT category_id FROM categories WHERE category_name='Женская обувь'),
12,9,'Ботинки женские зимние ROMER арт. 893167-01 Черный',NULL
ON CONFLICT (product_article) DO NOTHING;

INSERT INTO products
SELECT 'J542F5','Тапочки',
(SELECT unit_id FROM units WHERE unit_name='шт.'),
500.00,
(SELECT supplier_id FROM suppliers WHERE supplier_name='Kari'),
(SELECT manufacturer_id FROM manufacturers WHERE manufacturer_name='Kari'),
(SELECT category_id FROM categories WHERE category_name='Мужская обувь'),
13,0,'Тапочки мужские Арт.70701-55-67син р.41',NULL
ON CONFLICT (product_article) DO NOTHING;

INSERT INTO products
SELECT 'B431R5','Ботинки',
(SELECT unit_id FROM units WHERE unit_name='шт.'),
2700.00,
(SELECT supplier_id FROM suppliers WHERE supplier_name='Обувь для вас'),
(SELECT manufacturer_id FROM manufacturers WHERE manufacturer_name='Rieker'),
(SELECT category_id FROM categories WHERE category_name='Мужская обувь'),
2,5,'Мужские кожаные ботинки/мужские ботинки',NULL
ON CONFLICT (product_article) DO NOTHING;

INSERT INTO products
SELECT 'P764G4','Туфли',
(SELECT unit_id FROM units WHERE unit_name='шт.'),
6800.00,
(SELECT supplier_id FROM suppliers WHERE supplier_name='Kari'),
(SELECT manufacturer_id FROM manufacturers WHERE manufacturer_name='CROSBY'),
(SELECT category_id FROM categories WHERE category_name='Женская обувь'),
15,15,'Туфли женские, ARGO, размер 38',NULL
ON CONFLICT (product_article) DO NOTHING;

INSERT INTO products
SELECT 'C436G5','Ботинки',
(SELECT unit_id FROM units WHERE unit_name='шт.'),
10200.00,
(SELECT supplier_id FROM suppliers WHERE supplier_name='Kari'),
(SELECT manufacturer_id FROM manufacturers WHERE manufacturer_name='Alessio Nesca'),
(SELECT category_id FROM categories WHERE category_name='Женская обувь'),
15,9,'Ботинки женские, ARGO, размер 40',NULL
ON CONFLICT (product_article) DO NOTHING;

INSERT INTO products
SELECT 'F427R5','Ботинки',
(SELECT unit_id FROM units WHERE unit_name='шт.'),
11800.00,
(SELECT supplier_id FROM suppliers WHERE supplier_name='Обувь для вас'),
(SELECT manufacturer_id FROM manufacturers WHERE manufacturer_name='Rieker'),
(SELECT category_id FROM categories WHERE category_name='Женская обувь'),
15,11,'Ботинки на молнии с декоративной пряжкой FRAU',NULL
ON CONFLICT (product_article) DO NOTHING;

INSERT INTO products
SELECT 'N457T5','Полуботинки',
(SELECT unit_id FROM units WHERE unit_name='шт.'),
4600.00,
(SELECT supplier_id FROM suppliers WHERE supplier_name='Kari'),
(SELECT manufacturer_id FROM manufacturers WHERE manufacturer_name='CROSBY'),
(SELECT category_id FROM categories WHERE category_name='Женская обувь'),
3,13,'Полуботинки Ботинки черные зимние, мех',NULL
ON CONFLICT (product_article) DO NOTHING;

INSERT INTO products
SELECT 'D364R4','Туфли',
(SELECT unit_id FROM units WHERE unit_name='шт.'),
12400.00,
(SELECT supplier_id FROM suppliers WHERE supplier_name='Kari'),
(SELECT manufacturer_id FROM manufacturers WHERE manufacturer_name='Kari'),
(SELECT category_id FROM categories WHERE category_name='Женская обувь'),
16,5,'Туфли Luiza Belly женские Kate-lazo черные из натуральной замши',NULL
ON CONFLICT (product_article) DO NOTHING;

INSERT INTO products
SELECT 'S326R5','Тапочки',
(SELECT unit_id FROM units WHERE unit_name='шт.'),
9900.00,
(SELECT supplier_id FROM suppliers WHERE supplier_name='Обувь для вас'),
(SELECT manufacturer_id FROM manufacturers WHERE manufacturer_name='CROSBY'),
(SELECT category_id FROM categories WHERE category_name='Мужская обувь'),
17,15,'Мужские кожаные тапочки Профиль С.Дали',NULL
ON CONFLICT (product_article) DO NOTHING;

INSERT INTO products
SELECT 'L754R4','Полуботинки',
(SELECT unit_id FROM units WHERE unit_name='шт.'),
1700.00,
(SELECT supplier_id FROM suppliers WHERE supplier_name='Kari'),
(SELECT manufacturer_id FROM manufacturers WHERE manufacturer_name='Kari'),
(SELECT category_id FROM categories WHERE category_name='Женская обувь'),
2,7,'Полуботинки kari женские WB2020SS-26, размер 38, цвет: черный',NULL
ON CONFLICT (product_article) DO NOTHING;

INSERT INTO products
SELECT 'M542T5','Кроссовки',
(SELECT unit_id FROM units WHERE unit_name='шт.'),
2800.00,
(SELECT supplier_id FROM suppliers WHERE supplier_name='Обувь для вас'),
(SELECT manufacturer_id FROM manufacturers WHERE manufacturer_name='Rieker'),
(SELECT category_id FROM categories WHERE category_name='Мужская обувь'),
18,3,'Кроссовки мужские TOFA',NULL
ON CONFLICT (product_article) DO NOTHING;

INSERT INTO products
SELECT 'D268G5','Туфли',
(SELECT unit_id FROM units WHERE unit_name='шт.'),
4399.00,
(SELECT supplier_id FROM suppliers WHERE supplier_name='Обувь для вас'),
(SELECT manufacturer_id FROM manufacturers WHERE manufacturer_name='Rieker'),
(SELECT category_id FROM categories WHERE category_name='Женская обувь'),
3,12,'Туфли Rieker женские демисезонные, размер 36, цвет коричневый',NULL
ON CONFLICT (product_article) DO NOTHING;

INSERT INTO products
SELECT 'T324F5','Сапоги',
(SELECT unit_id FROM units WHERE unit_name='шт.'),
4699.00,
(SELECT supplier_id FROM suppliers WHERE supplier_name='Kari'),
(SELECT manufacturer_id FROM manufacturers WHERE manufacturer_name='CROSBY'),
(SELECT category_id FROM categories WHERE category_name='Женская обувь'),
2,5,'Сапоги замша Цвет: синий',NULL
ON CONFLICT (product_article) DO NOTHING;

INSERT INTO products
SELECT 'K358H6','Тапочки',
(SELECT unit_id FROM units WHERE unit_name='шт.'),
599.00,
(SELECT supplier_id FROM suppliers WHERE supplier_name='Kari'),
(SELECT manufacturer_id FROM manufacturers WHERE manufacturer_name='Rieker'),
(SELECT category_id FROM categories WHERE category_name='Мужская обувь'),
20,2,'Тапочки мужские син р.41',NULL
ON CONFLICT (product_article) DO NOTHING;

INSERT INTO products
SELECT 'H535R5','Ботинки',
(SELECT unit_id FROM units WHERE unit_name='шт.'),
2300.00,
(SELECT supplier_id FROM suppliers WHERE supplier_name='Обувь для вас'),
(SELECT manufacturer_id FROM manufacturers WHERE manufacturer_name='Rieker'),
(SELECT category_id FROM categories WHERE category_name='Женская обувь'),
2,7,'Женские Ботинки демисезонные',NULL
ON CONFLICT (product_article) DO NOTHING;

-- 5. Заказы

INSERT INTO orders (order_id, order_date, delivery_date, point_id, user_id, pickup_code, status_id)
SELECT 1, '2025-02-27', '2025-04-20',
(SELECT point_id FROM pickup_points WHERE postal_code='420151' AND street='Вишневая' AND house_number='32'),
(SELECT user_id FROM users WHERE login='1diph5e@tutanota.com'),
'901',
(SELECT status_id FROM order_statuses WHERE status_name='Завершен')
ON CONFLICT (order_id) DO NOTHING;

INSERT INTO orders
SELECT 2, '2022-09-28', '2025-04-21',
(SELECT point_id FROM pickup_points WHERE postal_code='410172' AND street='Северная' AND house_number='13'),
(SELECT user_id FROM users WHERE login='94d5ous@gmail.com'),
'902',
(SELECT status_id FROM order_statuses WHERE status_name='Завершен')
ON CONFLICT (order_id) DO NOTHING;

INSERT INTO orders
SELECT 3, '2025-03-21', '2025-04-22',
(SELECT point_id FROM pickup_points WHERE postal_code='125061' AND street='Подгорная' AND house_number='8'),
(SELECT user_id FROM users WHERE login='uth4iz@mail.com'),
'903',
(SELECT status_id FROM order_statuses WHERE status_name='Завершен')
ON CONFLICT (order_id) DO NOTHING;

INSERT INTO orders
SELECT 4, '2025-02-20', '2025-04-23',
(SELECT point_id FROM pickup_points WHERE postal_code='410172' AND street='Северная' AND house_number='13'),
(SELECT user_id FROM users WHERE login='yzls62@outlook.com'),
'904',
(SELECT status_id FROM order_statuses WHERE status_name='Завершен')
ON CONFLICT (order_id) DO NOTHING;

INSERT INTO orders
SELECT 5, '2025-03-17', '2025-04-24',
(SELECT point_id FROM pickup_points WHERE postal_code='125061' AND street='Подгорная' AND house_number='8'),
(SELECT user_id FROM users WHERE login='1diph5e@tutanota.com'),
'905',
(SELECT status_id FROM order_statuses WHERE status_name='Завершен')
ON CONFLICT (order_id) DO NOTHING;

INSERT INTO orders
SELECT 6, '2025-03-01', '2025-04-25',
(SELECT point_id FROM pickup_points WHERE postal_code='603036' AND street='Садовая' AND house_number='4'),
(SELECT user_id FROM users WHERE login='94d5ous@gmail.com'),
'906',
(SELECT status_id FROM order_statuses WHERE status_name='Завершен')
ON CONFLICT (order_id) DO NOTHING;

INSERT INTO orders
SELECT 7, '2025-02-28', '2025-04-26',
(SELECT point_id FROM pickup_points WHERE postal_code='630370' AND street='Шоссейная' AND house_number='24'),
(SELECT user_id FROM users WHERE login='uth4iz@mail.com'),
'907',
(SELECT status_id FROM order_statuses WHERE status_name='Завершен')
ON CONFLICT (order_id) DO NOTHING;

INSERT INTO orders
SELECT 8, '2025-03-31', '2025-04-27',
(SELECT point_id FROM pickup_points WHERE postal_code='625683' AND street='8 Марта' AND house_number='б/н'),
(SELECT user_id FROM users WHERE login='yzls62@outlook.com'),
'908',
(SELECT status_id FROM order_statuses WHERE status_name='Новый')
ON CONFLICT (order_id) DO NOTHING;

INSERT INTO orders
SELECT 9, '2025-04-02', '2025-04-28',
(SELECT point_id FROM pickup_points WHERE postal_code='614510' AND street='Маяковского' AND house_number='47'),
(SELECT user_id FROM users WHERE login='1diph5e@tutanota.com'),
'909',
(SELECT status_id FROM order_statuses WHERE status_name='Новый')
ON CONFLICT (order_id) DO NOTHING;

INSERT INTO orders
SELECT 10, '2025-04-03', '2025-04-29',
(SELECT point_id FROM pickup_points WHERE postal_code='625683' AND street='8 Марта' AND house_number='б/н'),
(SELECT user_id FROM users WHERE login='1diph5e@tutanota.com'),
'910',
(SELECT status_id FROM order_statuses WHERE status_name='Новый')
ON CONFLICT (order_id) DO NOTHING;

-- 6. Состав заказов

INSERT INTO order_items (order_id, product_article, quantity) VALUES
(1, 'A112T4', 2),
(1, 'F635R4', 2),
(2, 'H782T5', 1),
(2, 'G783F5', 1),
(3, 'J384T6', 10),
(3, 'D572U8', 10),
(4, 'F572H7', 5),
(4, 'D329H3', 4),
(5, 'A112T4', 2),
(5, 'F635R4', 2),
(6, 'H782T5', 1),
(6, 'G783F5', 1),
(7, 'J384T6', 10),
(7, 'D572U8', 10),
(8, 'F572H7', 5),
(8, 'D329H3', 4),
(9, 'B320R5', 5),
(9, 'G432E4', 1),
(10, 'S213E3', 5),
(10, 'E482R4', 5)
ON CONFLICT (order_id, product_article) DO NOTHING;
