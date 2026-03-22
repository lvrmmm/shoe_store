-- Скрипт создания таблиц для БД shoe_store

-- 1. Справочные таблицы
CREATE TABLE roles (
    role_id SERIAL PRIMARY KEY,
    role_name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE suppliers (
    supplier_id SERIAL PRIMARY KEY,
    supplier_name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE manufacturers (
    manufacturer_id SERIAL PRIMARY KEY,
    manufacturer_name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE categories (
    category_id SERIAL PRIMARY KEY,
    category_name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE units (
    unit_id SERIAL PRIMARY KEY,
    unit_name VARCHAR(20) NOT NULL UNIQUE
);

-- 2. Пункты выдачи
CREATE TABLE pickup_points (
    point_id SERIAL PRIMARY KEY,
    postal_code VARCHAR(10) NOT NULL,
    city VARCHAR(100) NOT NULL,
    street VARCHAR(255) NOT NULL,
    house_number VARCHAR(20) NOT NULL,
    full_address TEXT GENERATED ALWAYS AS
    (
        postal_code || ', г. ' || city || ', ул. ' || street || ', ' || house_number
    ) STORED UNIQUE
);

-- 3. Статусы заказов
CREATE TABLE order_statuses (
    status_id SERIAL PRIMARY KEY,
    status_name VARCHAR(50) NOT NULL UNIQUE
);

-- 4. Пользователи
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    login VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role_id INTEGER NOT NULL,
    CONSTRAINT fk_users_roles
        FOREIGN KEY (role_id)
        REFERENCES roles(role_id)
);

-- 5. Товары
CREATE TABLE products (
    product_article VARCHAR(50) PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    unit_id INTEGER NOT NULL,
    price DECIMAL(10, 2) NOT NULL CHECK (price > 0),
    supplier_id INTEGER NOT NULL,
    manufacturer_id INTEGER NOT NULL,
    category_id INTEGER NOT NULL,
    current_discount DECIMAL(5, 2) DEFAULT 0 CHECK (current_discount >= 0 AND current_discount <= 100),
    quantity_in_stock INTEGER DEFAULT 0 CHECK (quantity_in_stock >= 0),
    description TEXT,
    photo VARCHAR(255),
    CONSTRAINT fk_products_units
        FOREIGN KEY (unit_id)
        REFERENCES units(unit_id),
    CONSTRAINT fk_products_suppliers
        FOREIGN KEY (supplier_id)
        REFERENCES suppliers(supplier_id),
    CONSTRAINT fk_products_manufacturers
        FOREIGN KEY (manufacturer_id)
        REFERENCES manufacturers(manufacturer_id),
    CONSTRAINT fk_products_categories
        FOREIGN KEY (category_id)
        REFERENCES categories(category_id)
);

-- 6. Заказы
CREATE TABLE orders (
    order_id SERIAL PRIMARY KEY,
    order_date DATE NOT NULL DEFAULT CURRENT_DATE,
    delivery_date DATE NOT NULL,
    point_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    pickup_code VARCHAR(20),
    status_id INTEGER NOT NULL,
    CONSTRAINT fk_orders_pickup_points
        FOREIGN KEY (point_id)
        REFERENCES pickup_points(point_id),
    CONSTRAINT fk_orders_users
        FOREIGN KEY (user_id)
        REFERENCES users(user_id),
    CONSTRAINT fk_orders_statuses
        FOREIGN KEY (status_id)
        REFERENCES order_statuses(status_id)
);

-- 7. Состав заказов
CREATE TABLE order_items (
    order_item_id SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL,
    product_article VARCHAR(50) NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    CONSTRAINT fk_order_items_orders
        FOREIGN KEY (order_id)
        REFERENCES orders(order_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_order_items_products
        FOREIGN KEY (product_article)
        REFERENCES products(product_article),
    CONSTRAINT uq_order_items_order_product
        UNIQUE (order_id, product_article)
);

-- Индексы

CREATE INDEX idx_pickup_points_city ON pickup_points(city);
CREATE INDEX idx_pickup_points_street ON pickup_points(street);
CREATE INDEX idx_pickup_points_postal_code ON pickup_points(postal_code);
CREATE INDEX idx_pickup_points_city_street ON pickup_points(city, street);

CREATE INDEX idx_products_supplier_id ON products(supplier_id);
CREATE INDEX idx_products_category_id ON products(category_id);
CREATE INDEX idx_products_unit_id ON products(unit_id);
CREATE INDEX idx_products_manufacturer_id ON products(manufacturer_id);

CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_point_id ON orders(point_id);
CREATE INDEX idx_orders_status_id ON orders(status_id);

CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_product_article ON order_items(product_article);

CREATE INDEX idx_users_role_id ON users(role_id);
CREATE INDEX idx_users_login ON users(login);
