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

INSERT INTO products (name, brand, price, inventory, description, category_id, seller_id)
SELECT 'Portable Bluetooth Speaker',
       'WaveBox',
       59.99,
       70,
       'Compact speaker with deep bass, USB-C charging, and 12-hour playback.',
       c.id,
       u.id
FROM categories c
         JOIN users u ON u.user_name = 'seller1'
WHERE c.name = 'Electronics'
  AND NOT EXISTS (SELECT 1
                  FROM products p
                  WHERE p.seller_id = u.id
                    AND p.name = 'Portable Bluetooth Speaker');

INSERT INTO products (name, brand, price, inventory, description, category_id, seller_id)
SELECT '4K Streaming Stick',
       'ViewCast',
       39.99,
       90,
       'Plug-in streaming stick with voice remote and support for major apps.',
       c.id,
       u.id
FROM categories c
         JOIN users u ON u.user_name = 'seller1'
WHERE c.name = 'Electronics'
  AND NOT EXISTS (SELECT 1
                  FROM products p
                  WHERE p.seller_id = u.id
                    AND p.name = '4K Streaming Stick');

INSERT INTO products (name, brand, price, inventory, description, category_id, seller_id)
SELECT 'Mechanical Gaming Keyboard',
       'KeyForge',
       109.00,
       45,
       'RGB mechanical keyboard with tactile switches and detachable wrist rest.',
       c.id,
       u.id
FROM categories c
         JOIN users u ON u.user_name = 'seller1'
WHERE c.name = 'Electronics'
  AND NOT EXISTS (SELECT 1
                  FROM products p
                  WHERE p.seller_id = u.id
                    AND p.name = 'Mechanical Gaming Keyboard');

INSERT INTO products (name, brand, price, inventory, description, category_id, seller_id)
SELECT 'USB-C Fast Charger',
       'VoltEdge',
       19.99,
       150,
       '30W wall charger with foldable prongs for phones, tablets, and accessories.',
       c.id,
       u.id
FROM categories c
         JOIN users u ON u.user_name = 'seller2'
WHERE c.name = 'Electronics'
  AND NOT EXISTS (SELECT 1
                  FROM products p
                  WHERE p.seller_id = u.id
                    AND p.name = 'USB-C Fast Charger');

INSERT INTO products (name, brand, price, inventory, description, category_id, seller_id)
SELECT 'Slim Fit Chinos',
       'North Thread',
       44.50,
       65,
       'Stretch cotton chinos with tapered leg and all-day comfort fit.',
       c.id,
       u.id
FROM categories c
         JOIN users u ON u.user_name = 'seller1'
WHERE c.name = 'Fashion'
  AND NOT EXISTS (SELECT 1
                  FROM products p
                  WHERE p.seller_id = u.id
                    AND p.name = 'Slim Fit Chinos');

INSERT INTO products (name, brand, price, inventory, description, category_id, seller_id)
SELECT 'Leather Everyday Belt',
       'UrbanHide',
       29.99,
       85,
       'Genuine leather belt with matte buckle for casual and office wear.',
       c.id,
       u.id
FROM categories c
         JOIN users u ON u.user_name = 'seller1'
WHERE c.name = 'Fashion'
  AND NOT EXISTS (SELECT 1
                  FROM products p
                  WHERE p.seller_id = u.id
                    AND p.name = 'Leather Everyday Belt');

INSERT INTO products (name, brand, price, inventory, description, category_id, seller_id)
SELECT 'Everyday Denim Jacket',
       'BlueHarbor',
       74.99,
       40,
       'Classic denim jacket with structured fit and soft inner lining.',
       c.id,
       u.id
FROM categories c
         JOIN users u ON u.user_name = 'seller2'
WHERE c.name = 'Fashion'
  AND NOT EXISTS (SELECT 1
                  FROM products p
                  WHERE p.seller_id = u.id
                    AND p.name = 'Everyday Denim Jacket');

INSERT INTO products (name, brand, price, inventory, description, category_id, seller_id)
SELECT 'Canvas Travel Backpack',
       'TrailPort',
       64.00,
       55,
       'Multi-pocket travel backpack with padded straps and laptop sleeve.',
       c.id,
       u.id
FROM categories c
         JOIN users u ON u.user_name = 'seller2'
WHERE c.name = 'Fashion'
  AND NOT EXISTS (SELECT 1
                  FROM products p
                  WHERE p.seller_id = u.id
                    AND p.name = 'Canvas Travel Backpack');

INSERT INTO products (name, brand, price, inventory, description, category_id, seller_id)
SELECT 'Ceramic Dinner Set',
       'CasaBloom',
       79.99,
       30,
       'Twelve-piece ceramic dinnerware set suitable for daily use and entertaining.',
       c.id,
       u.id
FROM categories c
         JOIN users u ON u.user_name = 'seller1'
WHERE c.name = 'Home & Kitchen'
  AND NOT EXISTS (SELECT 1
                  FROM products p
                  WHERE p.seller_id = u.id
                    AND p.name = 'Ceramic Dinner Set');

INSERT INTO products (name, brand, price, inventory, description, category_id, seller_id)
SELECT 'Memory Foam Pillow Pair',
       'RestNest',
       54.99,
       75,
       'Set of two memory foam pillows designed for neck support and cooling comfort.',
       c.id,
       u.id
FROM categories c
         JOIN users u ON u.user_name = 'seller1'
WHERE c.name = 'Home & Kitchen'
  AND NOT EXISTS (SELECT 1
                  FROM products p
                  WHERE p.seller_id = u.id
                    AND p.name = 'Memory Foam Pillow Pair');

INSERT INTO products (name, brand, price, inventory, description, category_id, seller_id)
SELECT 'Air Fryer Oven',
       'ChefSphere',
       159.99,
       25,
       'Countertop air fryer oven with digital presets and large family-size basket.',
       c.id,
       u.id
FROM categories c
         JOIN users u ON u.user_name = 'seller2'
WHERE c.name = 'Home & Kitchen'
  AND NOT EXISTS (SELECT 1
                  FROM products p
                  WHERE p.seller_id = u.id
                    AND p.name = 'Air Fryer Oven');

INSERT INTO products (name, brand, price, inventory, description, category_id, seller_id)
SELECT 'Bamboo Cutting Board Set',
       'KitchenRoot',
       34.95,
       95,
       'Three-piece bamboo cutting board set with juice grooves and carrying handles.',
       c.id,
       u.id
FROM categories c
         JOIN users u ON u.user_name = 'seller2'
WHERE c.name = 'Home & Kitchen'
  AND NOT EXISTS (SELECT 1
                  FROM products p
                  WHERE p.seller_id = u.id
                    AND p.name = 'Bamboo Cutting Board Set');

INSERT INTO products (name, brand, price, inventory, description, category_id, seller_id)
SELECT 'Intro to Spring Boot',
       'TechReads',
       31.99,
       60,
       'Beginner-friendly introduction to building REST APIs and services with Spring Boot.',
       c.id,
       u.id
FROM categories c
         JOIN users u ON u.user_name = 'seller1'
WHERE c.name = 'Books'
  AND NOT EXISTS (SELECT 1
                  FROM products p
                  WHERE p.seller_id = u.id
                    AND p.name = 'Intro to Spring Boot');

INSERT INTO products (name, brand, price, inventory, description, category_id, seller_id)
SELECT 'Design Systems Handbook',
       'PixelPress',
       27.50,
       55,
       'Reference guide for building reusable UI systems across web products.',
       c.id,
       u.id
FROM categories c
         JOIN users u ON u.user_name = 'seller1'
WHERE c.name = 'Books'
  AND NOT EXISTS (SELECT 1
                  FROM products p
                  WHERE p.seller_id = u.id
                    AND p.name = 'Design Systems Handbook');

INSERT INTO products (name, brand, price, inventory, description, category_id, seller_id)
SELECT 'Productivity Notebook',
       'PaperTrail',
       14.99,
       130,
       'Undated productivity planner with task sections, notes, and weekly review pages.',
       c.id,
       u.id
FROM categories c
         JOIN users u ON u.user_name = 'seller2'
WHERE c.name = 'Books'
  AND NOT EXISTS (SELECT 1
                  FROM products p
                  WHERE p.seller_id = u.id
                    AND p.name = 'Productivity Notebook');

INSERT INTO products (name, brand, price, inventory, description, category_id, seller_id)
SELECT 'Mystery Thriller Collection',
       'PageTurner',
       22.00,
       70,
       'Boxed set of fast-paced mystery thrillers for weekend reading.',
       c.id,
       u.id
FROM categories c
         JOIN users u ON u.user_name = 'seller2'
WHERE c.name = 'Books'
  AND NOT EXISTS (SELECT 1
                  FROM products p
                  WHERE p.seller_id = u.id
                    AND p.name = 'Mystery Thriller Collection');

INSERT INTO products (name, brand, price, inventory, description, category_id, seller_id)
SELECT 'Adjustable Dumbbell Set',
       'IronPeak',
       219.99,
       18,
       'Space-saving adjustable dumbbell pair for home strength training.',
       c.id,
       u.id
FROM categories c
         JOIN users u ON u.user_name = 'seller1'
WHERE c.name = 'Sports'
  AND NOT EXISTS (SELECT 1
                  FROM products p
                  WHERE p.seller_id = u.id
                    AND p.name = 'Adjustable Dumbbell Set');

INSERT INTO products (name, brand, price, inventory, description, category_id, seller_id)
SELECT 'Insulated Water Bottle',
       'TrailPort',
       24.99,
       120,
       'Double-wall insulated bottle that keeps drinks cold for 24 hours.',
       c.id,
       u.id
FROM categories c
         JOIN users u ON u.user_name = 'seller1'
WHERE c.name = 'Sports'
  AND NOT EXISTS (SELECT 1
                  FROM products p
                  WHERE p.seller_id = u.id
                    AND p.name = 'Insulated Water Bottle');

INSERT INTO products (name, brand, price, inventory, description, category_id, seller_id)
SELECT 'Compact Yoga Mat',
       'FlexForm',
       32.99,
       88,
       'Non-slip yoga mat with carrying strap and joint-friendly cushioning.',
       c.id,
       u.id
FROM categories c
         JOIN users u ON u.user_name = 'seller2'
WHERE c.name = 'Sports'
  AND NOT EXISTS (SELECT 1
                  FROM products p
                  WHERE p.seller_id = u.id
                    AND p.name = 'Compact Yoga Mat');

INSERT INTO products (name, brand, price, inventory, description, category_id, seller_id)
SELECT 'Resistance Band Kit',
       'PulsePro',
       26.75,
       110,
       'Five-band resistance kit with handles, door anchor, and travel pouch.',
       c.id,
       u.id
FROM categories c
         JOIN users u ON u.user_name = 'seller2'
WHERE c.name = 'Sports'
  AND NOT EXISTS (SELECT 1
                  FROM products p
                  WHERE p.seller_id = u.id
                    AND p.name = 'Resistance Band Kit');

INSERT INTO products (name, brand, price, inventory, description, category_id, seller_id)
SELECT 'Stainless Steel Travel Mug',
       'CasaBloom',
       21.99,
       140,
       'Leak-resistant insulated mug sized for daily commuting and long drives.',
       c.id,
       u.id
FROM categories c
         JOIN users u ON u.user_name = 'seller1'
WHERE c.name = 'Home & Kitchen'
  AND NOT EXISTS (SELECT 1
                  FROM products p
                  WHERE p.seller_id = u.id
                    AND p.name = 'Stainless Steel Travel Mug');

INSERT INTO products (name, brand, price, inventory, description, category_id, seller_id)
SELECT 'Desk Lamp with Wireless Charging',
       'VoltEdge',
       68.00,
       42,
       'LED desk lamp with dimmable light modes and integrated wireless phone charger.',
       c.id,
       u.id
FROM categories c
         JOIN users u ON u.user_name = 'seller2'
WHERE c.name = 'Electronics'
  AND NOT EXISTS (SELECT 1
                  FROM products p
                  WHERE p.seller_id = u.id
                    AND p.name = 'Desk Lamp with Wireless Charging');

INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.role_id
FROM users u
         JOIN roles r ON r.name = 'ROLE_ADMIN'
WHERE u.user_name = 'admin1'
  AND NOT EXISTS (SELECT 1
                  FROM users_roles ur
                  WHERE ur.user_id = u.id
                    AND ur.role_id = r.role_id);
