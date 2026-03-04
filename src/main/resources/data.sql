INSERT INTO roles (name)
VALUES ('ROLE_CUSTOMER'),
       ('ROLE_SELLER'),
       ('ROLE_ADMIN') ON DUPLICATE KEY
UPDATE name =
VALUES (name);

-- bcrypt hash below is for password: password
INSERT INTO users (user_name, password, email, enabled)
VALUES ('customer1', '$2a$10$7EqJtq98hPqEX7fNZaFWoOePaWxn96p36nA3i4s1ihQ6p0QxP4mGa', 'customer1@example.com', 1),
       ('seller1', '$2a$10$7EqJtq98hPqEX7fNZaFWoOePaWxn96p36nA3i4s1ihQ6p0QxP4mGa', 'seller1@example.com', 1),
       ('admin1', '$2a$10$7EqJtq98hPqEX7fNZaFWoOePaWxn96p36nA3i4s1ihQ6p0QxP4mGa', 'admin1@example.com',
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
         JOIN roles r ON r.name = 'ROLE_SELLER'
WHERE u.user_name = 'seller1'
  AND NOT EXISTS (SELECT 1
                  FROM users_roles ur
                  WHERE ur.user_id = u.id
                    AND ur.role_id = r.role_id);

INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.role_id
FROM users u
         JOIN roles r ON r.name = 'ROLE_ADMIN'
WHERE u.user_name = 'admin1'
  AND NOT EXISTS (SELECT 1
                  FROM users_roles ur
                  WHERE ur.user_id = u.id
                    AND ur.role_id = r.role_id);
