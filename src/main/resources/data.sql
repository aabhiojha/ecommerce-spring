INSERT INTO roles (name)
VALUES ('ROLE_CUSTOMER'),
       ('ROLE_SELLER'),
       ('ROLE_ADMIN') ON DUPLICATE KEY
UPDATE name =
VALUES (name);

-- bcrypt hash below is for password: password
INSERT INTO users (user_name, password, email, enabled)
VALUES ('customer1', '$2a$10$kZCyMLLHZ3nOk/RXsXNvsOJzU1jOqab4/Et0SY/T6uUVwI9llFivW', 'customer1@example.com', 1),
        ('customer2', '$2a$10$kZCyMLLHZ3nOk/RXsXNvsOJzU1jOqab4/Et0SY/T6uUVwI9llFivW', 'customer2@example.com', 1),
       ('seller1', '$2a$10$kZCyMLLHZ3nOk/RXsXNvsOJzU1jOqab4/Et0SY/T6uUVwI9llFivW', 'seller1@example.com', 1),
       ('seller2', '$2a$10$kZCyMLLHZ3nOk/RXsXNvsOJzU1jOqab4/Et0SY/T6uUVwI9llFivW', 'seller2@example.com', 1),
       ('admin1', '$2a$10$kZCyMLLHZ3nOk/RXsXNvsOJzU1jOqab4/Et0SY/T6uUVwI9llFivW', 'admin1@example.com',
        1) ON DUPLICATE KEY
UPDATE
    password =
VALUES (password), enabled =
VALUES (enabled);

INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.role_id
FROM users u
         JOIN roles r ON r.name = 'ROLE_CUSTOMER'
WHERE u.user_name = 'customer1'
  AND NOT EXISTS (SELECT 1
                  FROM users_roles ur
                  WHERE ur.user_id = u.id
                    AND ur.role_id = r.role_id);

INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.role_id
FROM users u
         JOIN roles r ON r.name = 'ROLE_CUSTOMER'
WHERE u.user_name = 'customer2'
  AND NOT EXISTS (SELECT 1
                  FROM users_roles ur
                  WHERE ur.user_id = u.id
                    AND ur.role_id = r.role_id);

INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.role_id
FROM users u
         JOIN roles r ON r.name = 'ROLE_SELLER'
WHERE u.user_name = 'seller1'
  AND NOT EXISTS (SELECT 1
                  FROM users_roles ur
                  WHERE ur.user_id = u.id
                    AND ur.role_id = r.role_id);

INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.role_id
FROM users u
         JOIN roles r ON r.name = 'ROLE_SELLER'
WHERE u.user_name = 'seller2'
  AND NOT EXISTS (SELECT 1
                  FROM users_roles ur
                  WHERE ur.user_id = u.id
                    AND ur.role_id = r.role_id);

INSERT INTO categories (name)
VALUES ('Electronics'),
       ('Fashion'),
       ('Home & Kitchen'),
       ('Books'),
       ('Sports') ON DUPLICATE KEY
UPDATE name =
VALUES (name);

INSERT INTO products (name, brand, price, inventory, description, category_id, seller_id)
SELECT 'Wireless Noise Cancelling Headphones',
       'SoundMax',
       199.99,
       35,
       'Over-ear Bluetooth headphones with active noise cancellation and 30-hour battery life.',
       c.id,
       u.id
FROM categories c
         JOIN users u ON u.user_name = 'seller1'
WHERE c.name = 'Electronics'
  AND NOT EXISTS (SELECT 1
                  FROM products p
                  WHERE p.seller_id = u.id
                    AND p.name = 'Wireless Noise Cancelling Headphones');

INSERT INTO products (name, brand, price, inventory, description, category_id, seller_id)
SELECT 'Smart Fitness Watch',
       'PulsePro',
       149.50,
       50,
       'Water-resistant fitness smartwatch with heart-rate tracking and sleep monitoring.',
       c.id,
       u.id
FROM categories c
         JOIN users u ON u.user_name = 'seller1'
WHERE c.name = 'Electronics'
  AND NOT EXISTS (SELECT 1
                  FROM products p
                  WHERE p.seller_id = u.id
                    AND p.name = 'Smart Fitness Watch');

INSERT INTO products (name, brand, price, inventory, description, category_id, seller_id)
SELECT 'Classic Cotton Hoodie',
       'North Thread',
       49.99,
       80,
       'Midweight cotton hoodie with brushed fleece interior for everyday wear.',
       c.id,
       u.id
FROM categories c
         JOIN users u ON u.user_name = 'seller1'
WHERE c.name = 'Fashion'
  AND NOT EXISTS (SELECT 1
                  FROM products p
                  WHERE p.seller_id = u.id
                    AND p.name = 'Classic Cotton Hoodie');

INSERT INTO products (name, brand, price, inventory, description, category_id, seller_id)
SELECT 'Non-Stick Cookware Set',
       'ChefSphere',
       129.00,
       20,
       'Ten-piece non-stick cookware set suitable for gas, electric, and induction stovetops.',
       c.id,
       u.id
FROM categories c
         JOIN users u ON u.user_name = 'seller2'
WHERE c.name = 'Home & Kitchen'
  AND NOT EXISTS (SELECT 1
                  FROM products p
                  WHERE p.seller_id = u.id
                    AND p.name = 'Non-Stick Cookware Set');

INSERT INTO products (name, brand, price, inventory, description, category_id, seller_id)
SELECT 'Everyday Running Shoes',
       'StrideLab',
       89.95,
       60,
       'Lightweight running shoes with breathable mesh upper and cushioned sole.',
       c.id,
       u.id
FROM categories c
         JOIN users u ON u.user_name = 'seller2'
WHERE c.name = 'Sports'
  AND NOT EXISTS (SELECT 1
                  FROM products p
                  WHERE p.seller_id = u.id
                    AND p.name = 'Everyday Running Shoes');

INSERT INTO products (name, brand, price, inventory, description, category_id, seller_id)
SELECT 'Clean Code Companion',
       'TechReads',
       24.99,
       100,
       'Practical guide to writing maintainable backend code with real-world refactoring examples.',
       c.id,
       u.id
FROM categories c
         JOIN users u ON u.user_name = 'seller2'
WHERE c.name = 'Books'
  AND NOT EXISTS (SELECT 1
                  FROM products p
                  WHERE p.seller_id = u.id
                    AND p.name = 'Clean Code Companion');

INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.role_id
FROM users u
         JOIN roles r ON r.name = 'ROLE_ADMIN'
WHERE u.user_name = 'admin1'
  AND NOT EXISTS (SELECT 1
                  FROM users_roles ur
                  WHERE ur.user_id = u.id
                    AND ur.role_id = r.role_id);
