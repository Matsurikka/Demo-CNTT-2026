-- 1. Tạo Database
CREATE DATABASE IF NOT EXISTS kms_system;
USE kms_system;

-- 2. Bảng Nhân sự (HR)
CREATE TABLE IF NOT EXISTS employees (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    experience INT DEFAULT 0,
    salary DOUBLE DEFAULT 0,
    absent_days INT DEFAULT 0,
    bonus DOUBLE DEFAULT 0,
    fine DOUBLE DEFAULT 0,
    `rank` VARCHAR(100)
);

-- 3. Bảng Tài chính (Finance)
CREATE TABLE IF NOT EXISTS finance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255),
    type VARCHAR(10), -- 'THU' hoặc 'CHI'
    amount DOUBLE DEFAULT 0,
    status VARCHAR(50),
    note TEXT
);

-- 4. Bảng Sản xuất (Production)
CREATE TABLE IF NOT EXISTS production (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(255),
    quantity INT DEFAULT 0,
    defects INT DEFAULT 0,
    error_rate DOUBLE DEFAULT 0,
    status VARCHAR(50)
);

-- 5. Dữ liệu mẫu (Optional - Để test giao diện)
INSERT INTO employees (name, experience, salary, rank) VALUES ('Nguyen Van A', 5, 1500, 'Senior');
INSERT INTO finance (description, type, amount, status) VALUES ('Mua thiết bị', 'CHI', 5000000, 'CHO DUYET');
INSERT INTO production (product_name, quantity, defects, status) VALUES ('Laptop Dell', 100, 2, 'DAT CHUAN');