-- 1. Tạo Database (nếu chưa có)
CREATE DATABASE IF NOT EXISTS hr_expert_system;
USE hr_expert_system;

-- 2. Xóa bảng cũ nếu muốn làm mới 
-- DROP TABLE IF EXISTS employees;

-- 3. Tạo bảng lưu trữ thông tin nhân viên (Fact Base)
CREATE TABLE IF NOT EXISTS employees (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    experience INT NOT NULL,
    salary DOUBLE NOT NULL,
    bonus DOUBLE DEFAULT 0,
    `rank` VARCHAR(100) DEFAULT 'Chưa xếp hạng'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Chèn thử một dòng dữ liệu mẫu (Optional)
INSERT INTO employees (name, experience, salary, bonus, `rank`) 
VALUES ('Quốc Huy Sample', 5, 20000000, 4000000, 'VIP');