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
-- DROP TABLE `PRODUCT`; -- Dùng khi cần tạo lại
CREATE TABLE `PRODUCT` (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    seller_id INT NOT NULL,
    category_id INT NOT NULL,
    main_image_url VARCHAR(255) DEFAULT NULL, 
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
    FOREIGN KEY (category_id) REFERENCES `CATEGORY`(category_id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Bảng chứa thông tin ảnh phụ
CREATE TABLE `PRODUCT_IMAGE` (
    image_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    
    FOREIGN KEY (product_id) REFERENCES `PRODUCT`(product_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Bảng chứa câu hỏi về sản phẩm
CREATE TABLE `PRODUCT_QUESTION` (
    question_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    question_user_id INT NOT NULL,
    question_text TEXT NOT NULL,
    question_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (product_id) REFERENCES `PRODUCT`(product_id) ON DELETE CASCADE,
    FOREIGN KEY (question_user_id) REFERENCES `USER`(user_id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Bảng chứa câu trả lời cho câu hỏi sản phẩm
CREATE TABLE `PRODUCT_ANSWER` (
    answer_id INT AUTO_INCREMENT PRIMARY KEY,
    question_id INT NOT NULL,
    answer_user_id INT NOT NULL,
    answer_text TEXT NOT NULL,
    answer_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (question_id) REFERENCES `PRODUCT_QUESTION`(question_id) ON DELETE CASCADE,
    FOREIGN KEY (answer_user_id) REFERENCES `USER`(user_id) ON DELETE RESTRICT
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

-- Bảng chứa thông tin lịch sử ra giá
CREATE TABLE `BID` (
    bid_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    bidder_id INT NOT NULL, -- Người đặt giá (bidder)
    bid_price DECIMAL(18, 2) NOT NULL, -- Mức giá đặt
    
    -- Cờ cho biết đây có phải là bid TỰ ĐỘNG hay không
    is_auto_bid BOOLEAN DEFAULT FALSE, 
    
    -- Chỉ lưu giá tối đa của bidder nếu là Auto Bid (giá thực tế có thể thấp hơn)
    max_auto_price DECIMAL(18, 2) DEFAULT NULL, 
    
    bid_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (product_id) REFERENCES `PRODUCT`(product_id) ON DELETE CASCADE,
    FOREIGN KEY (bidder_id) REFERENCES `USER`(user_id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Dữ liệu mẫu cho bảng CATEGORY
INSERT INTO `CATEGORY` (category_name, parent_id) VALUES
('Điện tử', NULL),
('Thời trang', NULL),
('Điện thoại di động', 1),
('Máy tính xách tay', 1),
('Đồng hồ', 2),
('Giày dép', 2),
('Sách', NULL),
('Đồ gia dụng', NULL),
('Đồ chơi', NULL),
('Máy tính bảng', 1);

-- Dữ liệu mẫu cho bảng USER
INSERT INTO `USER` (full_name, email, encrypted_password, is_seller, rating_score, rating_count) VALUES
('Nguyễn Văn A (Người đấu giá)', 'bidder_a@example.com', '$2a$10$5K7irzpbIRu8CobNZJFVDempz9WDIf.LDvGLOB0wxM0ElH5eQodhC', 0, 8, 10),
('Trần Thị B (Người đấu giá)', 'bidder_b@example.com', '$2a$10$savotgG5leGb/SwtvrRPguX76AwBumNxFZMJYegUUncRvegOucX4O', 0, 10, 10),
('Lê Văn C (Người bán)', 'seller_c@example.com', '$2a$10$BOtUAA6RN.v581tSt7vW4.vgDr8DdFrtXSkTKArazSZC.dWl.wLVm', 1, 12, 15),
('Phạm Thị D (Người bán)', 'seller_d@example.com', '$2a$10$examplehashforsellerd', 1, 14, 20),
('Hoàng Văn E (Người bán)', 'seller_e@example.com', '$2a$10$examplehashforsellere', 1, 16, 25),
('Nguyễn Thị F (Người đấu giá)', 'bidder_f@example.com', '$2a$10$examplehashforbidderf', 0, 9, 12);

-- Dữ liệu mẫu cho bảng PRODUCT
-- Dữ liệu mẫu cho bảng PRODUCT
INSERT INTO `PRODUCT` (seller_id, category_id, product_name, current_price, buy_now_price, start_price, price_step, description, end_time, bid_count) VALUES
(3, 3, 'iPhone 13 Pro Max', 20000000.00, 25000000.00, 18000000.00, 100000.00, 'Mẫu iPhone mới nhất', DATE_ADD(NOW(), INTERVAL 5 DAY), 8), -- Điện thoại di động
(3, 4, 'MacBook Pro M2', 30000000.00, NULL, 30000000.00, 200000.00, 'Máy tính xách tay', DATE_ADD(NOW(), INTERVAL 1 DAY), 0), -- Máy tính xách tay
(3, 5, 'Đồng hồ Casio G-Shock', 1500000.00, 2000000.00, 1000000.00, 50000.00, 'Đồng hồ thời trang', DATE_ADD(NOW(), INTERVAL 7 DAY), 3), -- Đồng hồ
(3, 10, 'Samsung Galaxy Tab S9', 12000000.00, 15000000.00, 10000000.00, 50000.00, 'Máy tính bảng Android cao cấp', DATE_ADD(NOW(), INTERVAL 3 DAY), 5), -- Máy tính bảng
(3, 7, 'Kindle Paperwhite', 3000000.00, 4000000.00, 3000000.00, 20000.00, 'Máy đọc sách điện tử', DATE_ADD(NOW(), INTERVAL 2 DAY), 0), -- Sách
(3, 7, 'Bộ truyện Harry Potter', 800000.00, 1000000.00, 600000.00, 10000.00, 'Trọn bộ truyện Harry Potter', DATE_ADD(NOW(), INTERVAL 10 DAY), 12), -- Sách
(3, 9, 'LEGO Thành phố - Đồn cảnh sát', 2000000.00, 2500000.00, 1800000.00, 50000.00, 'Bộ đồ chơi LEGO', DATE_ADD(NOW(), INTERVAL 6 DAY), 4), -- Đồ chơi
(3, 6, 'Giày Nike Air Max', 2500000.00, 3000000.00, 2000000.00, 50000.00, 'Giày chạy bộ nổi tiếng', DATE_ADD(NOW(), INTERVAL 4 DAY), 7), -- Giày dép
(3, 5, 'Đồng hồ Rolex Submariner', 120000000.00, 150000000.00, 100000000.00, 1000000.00, 'Đồng hồ cao cấp', DATE_ADD(NOW(), INTERVAL 15 DAY), 2); -- Đồng hồ

-- Dữ liệu mẫu cho bảng PRODUCT_IMAGE
INSERT INTO `PRODUCT_IMAGE` (product_id, image_url) VALUES
(1, 'https://example.com/images/iphone13promax_1.jpg'),
(1, 'https://example.com/images/iphone13promax_2.jpg'),
(2, 'https://example.com/images/macbookpro_m2_1.jpg'),
(2, 'https://example.com/images/macbookpro_m2_2.jpg'),
(2, 'https://example.com/images/casio_gshock_1.jpg'),
(2, 'https://example.com/images/galaxy_tab_s9_1.jpg'),
(2, 'https://example.com/images/kindle_paperwhite_1.jpg'),
(3, 'https://example.com/images/harry_potter_set_1.jpg'),
(3, 'https://example.com/images/lego_city_police_1.jpg'),
(3, 'https://example.com/images/nike_air_max_1.jpg'),
(3, 'https://example.com/images/rolex_submariner_1.jpg');

-- Dữ liệu mẫu cho bảng WATCH_LIST
INSERT INTO `WATCH_LIST` (user_id, product_id) VALUES
(1, 1),
(1, 3);
INSERT INTO `WATCH_LIST` (user_id, product_id) VALUES
(2, 1);

-- Dữ liệu mẫu cho bảng PRODUCT_QUESTION
INSERT INTO `PRODUCT_QUESTION` (product_id, question_user_id, question_text) VALUES
(1, 1, 'Sản phẩm này có còn bảo hành không?'),
(1, 2, 'Chiếc máy này màu gì?'),
(1, 1, 'Giao hàng đến Hà Nội mất bao lâu?'),
(2, 2, 'MacBook M2 có RAM bao nhiêu?'),
(2, 1, 'Có box, sạc đầy đủ không?'),
(3, 2, 'Đây là phiên bản nước ngoài hay hàng Việt Nam?'),
(3, 1, 'Đồng hồ pin tốt không?'),
(4, 1, 'Galaxy Tab S9 có hỗ trợ stylus không?'),
(4, 2, 'Màn hình loại nào, độ phân giải như thế nào?'),
(4, 1, 'Máy chạy Android phiên bản mấy?');

-- Dữ liệu mẫu đa dạng cho bảng PRODUCT_ANSWER
INSERT INTO `PRODUCT_ANSWER` (question_id, answer_user_id, answer_text) VALUES
-- Q1: 'Sản phẩm này có còn bảo hành không?' (3 câu trả lời)
(1, 3, 'Sản phẩm còn bảo hành chính hãng Apple đến tháng 11/2026.'),
(1, 4, 'Tôi mua máy này từ shop 2 tháng trước, check ra vẫn bảo hành dài nhé bạn.'),
(1, 3, 'Có tem bảo hành đầy đủ trên thân máy ạ.'),
-- Q2: 'Chiếc máy này màu gì?' (0 câu trả lời - Đang chờ người bán trả lời)
-- Q3: 'Giao hàng đến Hà Nội mất bao lâu?' (1 câu trả lời)
(3, 3, 'Thời gian giao hàng dự kiến 1-2 ngày làm việc.'),
-- Q4: 'MacBook M2 có RAM bao nhiêu?' (2 câu trả lời)
(4, 3, 'Bản tôi đang bán là 16GB RAM, 512GB SSD.'),
(4, 4, 'Mình thấy dòng M2 này có tùy chọn 8GB hoặc 16GB RAM.'),
-- Q5: 'Có box, sạc đầy đủ không?' (1 câu trả lời)
(5, 3, 'Sản phẩm full box, phụ kiện zin 100% kèm theo.'),
-- Q6: 'Đây là phiên bản nước ngoài hay hàng Việt Nam?' (4 câu trả lời)
(6, 3, 'Đây là hàng chính hãng phân phối tại Việt Nam (mã VN/A).'),
(6, 4, 'Hàng VN/A check imei ra đúng model bạn nhé.'),
(6, 3, 'Có tem BH chính hãng và hóa đơn mua hàng đầy đủ.'),
(6, 4, 'Tôi đã mua và xác nhận là hàng Việt Nam chính hãng.'),
-- Q7: 'Đồng hồ pin tốt không?' (1 câu trả lời)
(7, 4, 'Dòng G-Shock này nổi tiếng về độ bền, pin dùng khoảng 10 năm.'),
-- Q8: 'Galaxy Tab S9 có hỗ trợ stylus không?' (0 câu trả lời - Đang chờ)
-- Q9: 'Màn hình loại nào, độ phân giải như thế nào?' (2 câu trả lời)
(9, 3, 'Màn hình Dynamic AMOLED 2X, hiển thị rất đẹp.'),
(9, 3, 'Độ phân giải cao 2560x1600px.');
-- Q10: 'Máy chạy Android phiên bản mấy?' (0 câu trả lời - Đang chờ)

-- Dữ liệu mẫu cho bảng BID
INSERT INTO `BID` (product_id, bidder_id, bid_price, is_auto_bid, max_auto_price) VALUES
-- Product 1: iPhone 13 Pro Max
(1, 1, 18100000.00, FALSE, NULL),
(1, 2, 18500000.00, FALSE, NULL),
(1, 1, 19000000.00, TRUE, 20000000.00),
(1, 2, 19100000.00, FALSE, NULL),
(1, 1, 19500000.00, TRUE, 20000000.00),
-- Product 2: MacBook Pro M2
(2, 2, 30200000.00, FALSE, NULL),
(2, 1, 31000000.00, FALSE, NULL),
(2, 2, 32000000.00, TRUE, 35000000.00),
-- Product 3: Casio G-Shock Watch
(3, 1, 1050000.00, FALSE, NULL),
(3, 2, 1200000.00, FALSE, NULL),
(3, 1, 1350000.00, FALSE, NULL),
-- Product 4: Samsung Galaxy Tab S9
(4, 2, 10100000.00, FALSE, NULL),
(4, 1, 10500000.00, TRUE, 12000000.00);
