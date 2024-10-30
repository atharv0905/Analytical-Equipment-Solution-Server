CREATE DATABASE analytical_equipment_solutions;
USE analytical_equipment_solutions;

CREATE TABLE analytics (
	id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, 
	page_reach BIGINT
);

CREATE TABLE email_verification (
	email VARCHAR(255) PRIMARY KEY NOT NULL,
    verified BOOLEAN
);

CREATE TABLE users (
    id VARCHAR(500) PRIMARY KEY NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone BIGINT NOT NULL UNIQUE,
    roles JSON NOT NULL
);

CREATE TABLE user_address (
	id VARCHAR(500) PRIMARY KEY NOT NULL,
    customer_id VARCHAR(500) NOT NULL,
    address VARCHAR(700) NOT NULL,
    FOREIGN KEY(customer_id) REFERENCES users(id)
);

CREATE TABLE products (
    product_id VARCHAR(500) PRIMARY KEY NOT NULL UNIQUE,
    product_name VARCHAR(255) NOT NULL,
    product_desc TEXT NOT NULL,
    product_category VARCHAR(100) NOT NULL,
    estimated_delivery_time VARCHAR(255) NOT NULL,
    product_price BIGINT NOT NULL,
    product_profit BIGINT NOT NULL,
    product_status BOOLEAN,
    product_images JSON NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cartitems (
	item_id VARCHAR(500) PRIMARY KEY NOT NULL UNIQUE,
    product_id VARCHAR(255) NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    quantity BIGINT NOT NULL
);

CREATE TABLE cart (
	cart_id VARCHAR(500) PRIMARY KEY NOT NULL UNIQUE,
    customer_id VARCHAR(500) NOT NULL UNIQUE,
    FOREIGN KEY (customer_id) REFERENCES users(id),
    item_ids JSON NOT NULL
);

CREATE TABLE orders (
	order_id VARCHAR(500) PRIMARY KEY NOT NULL UNIQUE,
    sale_id VARCHAR(500) NOT NULL,
    product_id VARCHAR(500) NOT NULL,
    quantity BIGINT NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    FOREIGN KEY (sale_id) REFERENCES sales(sale_id),
    order_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sales (
	sale_id VARCHAR(500) PRIMARY KEY NOT NULL UNIQUE,
    customer_id VARCHAR(500) NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES users(id),
    order_confirmation_status VARCHAR(100) NOT NULL,
	order_status VARCHAR(100) NOT NULL,
    shipping_address VARCHAR(500) NOT NULL,
    contact_phone BIGINT NOT NULL,
    transaction_id VARCHAR(500) NOT NULL UNIQUE,
    payment_status VARCHAR(100) NOT NULL,
    invoice_number VARCHAR(500) NOT NULL UNIQUE,
    sale_mode VARCHAR(100) NOT NULL,
    order_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
 );

CREATE VIEW online_order_summary AS
SELECT 
    o.order_id,
    o.sale_id,
    o.product_id,
    o.quantity,
    o.order_date,
    s.sale_mode,
    p.product_price,
    p.product_profit
FROM 
    orders o
JOIN 
    sales s ON o.sale_id = s.sale_id
JOIN 
    products p ON o.product_id = p.product_id
WHERE 
    s.sale_mode = 'ONLINE';

CREATE VIEW offline_order_summary AS
SELECT 
    o.order_id,
    o.sale_id,
    o.product_id,
    o.quantity,
    o.order_date,
    s.sale_mode,
    p.product_price,
    p.product_profit
FROM 
    orders o
JOIN 
    sales s ON o.sale_id = s.sale_id
JOIN 
    products p ON o.product_id = p.product_id
WHERE 
    s.sale_mode = 'OFFLINE';