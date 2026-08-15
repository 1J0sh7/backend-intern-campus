
-- SQL Basics Lab - PostgreSQL
-- Phase 8 - Database, SQL and Hibernate




-- 1. CREATE DATABASE

-- Run this first to create the database
-- psql -U postgres
-- CREATE DATABASE customer_db;
-- \q



-- 2. CREATE TABLE (Manual version - Hibernate does this automatically)

CREATE TABLE IF NOT EXISTS customers (
                                         id BIGSERIAL PRIMARY KEY,
                                         name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(255) UNIQUE NOT NULL
    );



-- 3. INSERT



INSERT INTO customers (name, email, phone)
VALUES ('John Doe', 'john@email.com', '1234567890');

INSERT INTO customers (name, email, phone)
VALUES ('Jane Smith', 'jane@email.com', '0987654321');

INSERT INTO customers (name, email, phone)
VALUES ('Bob Johnson', 'bob@email.com', '1122334455');

-- 4. SELECT (READ) - All customers


SELECT * FROM customers;

-- 5. SELECT - With WHERE clause (filtering)


SELECT * FROM customers WHERE name = 'John Doe';
SELECT * FROM customers WHERE email LIKE '%gmail.com%';
SELECT * FROM customers WHERE phone = '1234567890';


-- 6. SELECT - With ORDER BY (sorting)


SELECT * FROM customers ORDER BY name ASC;
SELECT * FROM customers ORDER BY name DESC;

-- 7. SELECT - With LIMIT and OFFSET (pagination)



SELECT * FROM customers ORDER BY id LIMIT 5 OFFSET 0; -- Page 1
SELECT * FROM customers ORDER BY id LIMIT 5 OFFSET 5; -- Page 2


-- 8. UPDATE


UPDATE customers SET name = 'John Updated' WHERE id = 1;
UPDATE customers SET email = 'john.updated@email.com' WHERE id = 1;


-- 9. DELETE


DELETE FROM customers WHERE id = 3;

-- 10. COUNT


SELECT COUNT(*) FROM customers;

-- 11. UNIQUE CONSTRAINT


-- If you try to insert a duplicate email, you get an error:
-- INSERT INTO customers (name, email, phone)
-- VALUES ('Duplicate', 'john@email.com', '9999999999');
-- ERROR: duplicate key value violates unique constraint "customers_email_key"

-- 12. NOT NULL CONSTRAINT


-- If you try to insert a null name, you get an error:
-- INSERT INTO customers (name, email, phone)
-- VALUES (NULL, 'test@email.com', '9999999999');
-- ERROR: null value in column "name" violates not-null constraint

-- 13. CREATE INDEX (Performance)

CREATE INDEX idx_customers_email ON customers(email);
CREATE INDEX idx_customers_name ON customers(name);

-- 14. EXPLAIN (Query tuning - Performance)


EXPLAIN SELECT * FROM customers WHERE email = 'john@email.com';
EXPLAIN ANALYZE SELECT * FROM customers WHERE email = 'john@email.com';

-- 15. DROP TABLE (Cleanup)


-- DROP TABLE customers;