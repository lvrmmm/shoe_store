-- Проверка заполнения БД 

-- 1. Количество строк во всех таблицах
SELECT 'roles' AS table_name, COUNT(*) AS total_count FROM roles
UNION ALL
SELECT 'suppliers', COUNT(*) FROM suppliers
UNION ALL
SELECT 'manufacturers', COUNT(*) FROM manufacturers
UNION ALL
SELECT 'categories', COUNT(*) FROM categories
UNION ALL
SELECT 'units', COUNT(*) FROM units
UNION ALL
SELECT 'order_statuses', COUNT(*) FROM order_statuses
UNION ALL
SELECT 'pickup_points', COUNT(*) FROM pickup_points
UNION ALL
SELECT 'users', COUNT(*) FROM users
UNION ALL
SELECT 'products', COUNT(*) FROM products
UNION ALL
SELECT 'orders', COUNT(*) FROM orders
UNION ALL
SELECT 'order_items', COUNT(*) FROM order_items
ORDER BY table_name;

-- Ожидаемо:
-- roles = 3
-- suppliers = 2
-- manufacturers = 6
-- categories = 2
-- units = 1
-- order_statuses = 2
-- pickup_points = 36
-- users = 10
-- products = 30
-- orders = 10
-- order_items = 20


-- 2. Проверка пользователей
SELECT
    u.full_name,
    u.login,
    u.password,
    r.role_name
FROM users u
JOIN roles r ON r.role_id = u.role_id
ORDER BY r.role_name, u.full_name, u.login;


-- 3. Проверка товаров
SELECT
    p.product_article,
    p.product_name,
    u.unit_name,
    p.price,
    s.supplier_name,
    m.manufacturer_name,
    c.category_name,
    p.current_discount,
    p.quantity_in_stock,
    p.description,
    COALESCE(p.photo, '') AS photo
FROM products p
JOIN units u ON u.unit_id = p.unit_id
JOIN suppliers s ON s.supplier_id = p.supplier_id
JOIN manufacturers m ON m.manufacturer_id = p.manufacturer_id
JOIN categories c ON c.category_id = p.category_id
ORDER BY p.product_article;


-- 4. Проверка пунктов выдачи
SELECT
    point_id,
    postal_code,
    city,
    street,
    house_number,
    full_address
FROM pickup_points
ORDER BY point_id;


-- 5. Проверка заказов
SELECT
    o.order_id,
    o.order_date,
    o.delivery_date,
    pp.full_address,
    u.full_name,
    o.pickup_code,
    os.status_name
FROM orders o
JOIN pickup_points pp ON pp.point_id = o.point_id
JOIN users u ON u.user_id = o.user_id
JOIN order_statuses os ON os.status_id = o.status_id
ORDER BY o.order_id;


-- 6. Проверка состава заказов в формате, похожем на исходную таблицу
SELECT
    oi.order_id,
    string_agg(oi.product_article || ', ' || oi.quantity, ', ' ORDER BY oi.order_item_id) AS order_items_text
FROM order_items oi
GROUP BY oi.order_id
ORDER BY oi.order_id;


-- 7. Проверка: у каждого заказа должно быть по 2 позиции
SELECT
    o.order_id,
    COUNT(oi.order_item_id) AS items_count
FROM orders o
LEFT JOIN order_items oi ON oi.order_id = o.order_id
GROUP BY o.order_id
ORDER BY o.order_id;


-- 8. Проверка: общее количество товаров в каждом заказе
SELECT
    o.order_id,
    SUM(oi.quantity) AS total_quantity
FROM orders o
JOIN order_items oi ON oi.order_id = o.order_id
GROUP BY o.order_id
ORDER BY o.order_id;

-- 11. Проверка внешних ключей через читаемый вывод
SELECT
    tc.table_name,
    kcu.column_name,
    ccu.table_name AS referenced_table,
    ccu.column_name AS referenced_column
FROM information_schema.table_constraints tc
JOIN information_schema.key_column_usage kcu
    ON tc.constraint_name = kcu.constraint_name
   AND tc.table_schema = kcu.table_schema
JOIN information_schema.constraint_column_usage ccu
    ON ccu.constraint_name = tc.constraint_name
   AND ccu.table_schema = tc.table_schema
WHERE tc.constraint_type = 'FOREIGN KEY'
  AND tc.table_schema = 'public'
ORDER BY tc.table_name, kcu.column_name;

