DROP DATABASE IF EXISTS online_auction_db;
CREATE DATABASE online_auction_db;

USE online_auction_db;

-- Bảng chứa thông tin danh mục sản phẩm
CREATE TABLE `CATEGORY` (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,
    parent_id INT, -- (dùng cho danh mục cấp 1)
    
    FOREIGN KEY (parent_id) REFERENCES `CATEGORY`(category_id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- Bảng chứa thông tin người dùng (Bidder, Seller)
CREATE TABLE `USER` (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    encrypted_password VARCHAR(255) NOT NULL, -- Mật khẩu đã mã hoá bằng bcrypt/scrypt
    is_seller BOOLEAN DEFAULT FALSE,
    rating_score INT DEFAULT 0,
    rating_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Bảng chứa thông tin sản phẩm đấu giá
CREATE TABLE `PRODUCT` (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    seller_id INT NOT NULL,
    category_id INT NOT NULL,  -- TRƯỜNG MỚI ĐƯỢC THÊM
    product_name VARCHAR(255) NOT NULL,
    current_price DECIMAL(18, 2) NOT NULL,
    buy_now_price DECIMAL(18, 2),
    start_price DECIMAL(18, 2) NOT NULL,
    price_step DECIMAL(18, 2) NOT NULL,
    description TEXT,
    end_time DATETIME NOT NULL,
    is_auto_renew BOOLEAN DEFAULT FALSE,
    bid_count INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (seller_id) REFERENCES `USER`(user_id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES `CATEGORY`(category_id) ON DELETE RESTRICT -- Không được xóa danh mục nếu còn sản phẩm
) ENGINE=InnoDB;

-- Bảng chứa thông tin Watchlist của người dùng
CREATE TABLE `WATCH_LIST` (
    watch_list_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES `USER`(user_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES `PRODUCT`(product_id) ON DELETE CASCADE,
    
    UNIQUE KEY unique_watchlist (user_id, product_id)
) ENGINE=InnoDB;

-- Dữ liệu mẫu cho bảng CATEGORY
INSERT INTO `CATEGORY` (category_name, parent_id) VALUES
('Electronics', NULL),
('Fashion', NULL),
('Mobile Phones', 1),
('Laptops', 1),
('Watches', 2),
('Shoes', 2),
('Books', NULL),
('Home Appliances', NULL),
('Toys', NULL),
('Tablets', 1);

-- Dữ liệu mẫu cho bảng USER
INSERT INTO `USER` (full_name, email, encrypted_password, is_seller, rating_score, rating_count) VALUES
('Nguyen Van A (Bidder)', 'bidder_a@example.com', '$2a$10$5K7irzpbIRu8CobNZJFVDempz9WDIf.LDvGLOB0wxM0ElH5eQodhC', 0, 8, 10),
('Tran Thi B (Bidder)', 'bidder_b@example.com', '$2a$10$savotgG5leGb/SwtvrRPguX76AwBumNxFZMJYegUUncRvegOucX4O', 0, 10, 10),
('Le Van C (Seller)', 'seller_c@example.com', '$2a$10$BOtUAA6RN.v581tSt7vW4.vgDr8DdFrtXSkTKArazSZC.dWl.wLVm', 1, 12, 15);

-- Dữ liệu mẫu cho bảng PRODUCT
INSERT INTO `PRODUCT` (seller_id, category_id, product_name, current_price, buy_now_price, start_price, price_step, description, end_time) VALUES
(3, 3, 'iPhone 13 Pro Max', 20000000.00, 25000000.00, 18000000.00, 100000.00, 'The latest iPhone model', DATE_ADD(NOW(), INTERVAL 5 DAY)),
(3, 4, 'MacBook Pro M2', 35000000.00, NULL, 30000000.00, 200000.00, 'Laptop computer', DATE_ADD(NOW(), INTERVAL 1 DAY)),
(3, 5, 'Casio G-Shock Watch', 1500000.00, 2000000.00, 1000000.00, 50000.00, 'Fashion watch', DATE_ADD(NOW(), INTERVAL 7 DAY)),(3, 8, 'Samsung Galaxy Tab S9', 12000000.00, 15000000.00, 10000000.00, 50000.00, 'High-end Android tablet', DATE_ADD(NOW(), INTERVAL 3 DAY)),
(3, 7, 'Kindle Paperwhite', 3500000.00, 4000000.00, 3000000.00, 20000.00, 'E-book reader', DATE_ADD(NOW(), INTERVAL 2 DAY)),
(3, 7, 'Harry Potter Book Set', 800000.00, 1000000.00, 600000.00, 10000.00, 'Full set of Harry Potter books', DATE_ADD(NOW(), INTERVAL 10 DAY)),
(3, 9, 'LEGO City Police Station', 2000000.00, 2500000.00, 1800000.00, 50000.00, 'LEGO toy set', DATE_ADD(NOW(), INTERVAL 6 DAY)),
(3, 6, 'Nike Air Max Shoes', 2500000.00, 3000000.00, 2000000.00, 50000.00, 'Popular running shoes', DATE_ADD(NOW(), INTERVAL 4 DAY)),
(3, 5, 'Rolex Submariner Watch', 120000000.00, 150000000.00, 100000000.00, 1000000.00, 'Luxury watch', DATE_ADD(NOW(), INTERVAL 15 DAY));

-- Dữ liệu mẫu cho bảng WATCH_LIST
INSERT INTO `WATCH_LIST` (user_id, product_id) VALUES
(1, 1),
(1, 3);
INSERT INTO `WATCH_LIST` (user_id, product_id) VALUES
(2, 1);

