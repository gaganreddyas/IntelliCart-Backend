-- duplicate data for convenience
INSERT INTO users (email, password_hash, role, full_name, active)
VALUES
('admin@intellicart.com', '$2a$11$9j8r0dGkZg9Q1pQp3gY9ieuTQ9jK0r2e6x0X1x0yQ5sM3Zb4wGq7K', 'ADMIN', 'Admin User', true),
('customer@intellicart.com', '$2a$11$9j8r0dGkZg9Q1pQp3gY9ieuTQ9jK0r2e6x0X1x0yQ5sM3Zb4wGq7K', 'CUSTOMER', 'Customer User', true);

INSERT INTO products (name, category, price, stock_quantity)
VALUES
('Stellar T-Shirt', 'merch', 19.99, 100),
('Galaxy Mug', 'merch', 12.50, 150),
('Astronaut Poster', 'posters', 9.99, 80),
('Rocket Hoodie', 'merch', 49.99, 40),
('Space Sticker Pack', 'accessories', 4.99, 500);
