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
    role ENUM('BIDDER', 'SELLER', 'ADMIN') DEFAULT 'BIDDER',
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
    allow_unrated_bidder BOOLEAN DEFAULT TRUE,
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

-- Bảng chứa kết quả đấu giá sau khi kết thúc
CREATE TABLE `AUCTION_RESULT` (
    result_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL UNIQUE, 
    winner_id INT NOT NULL, 
    final_price DECIMAL(18, 2) NOT NULL, 
    result_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 

    -- Trạng thái của giao dịch sau đấu giá (Ví dụ: PENDING, PAID, CANCELLED)
    payment_status VARCHAR(50) DEFAULT 'PENDING',
    
    FOREIGN KEY (product_id) REFERENCES `PRODUCT`(product_id) ON DELETE CASCADE,
    FOREIGN KEY (winner_id) REFERENCES `USER`(user_id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Bảng chứa đánh giá người bán
CREATE TABLE `RATING` (
    rating_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    reviewer_id INT NOT NULL, -- Người đánh giá
    reviewee_id INT NOT NULL, -- Người được đánh giá
    rating_value INT NOT NULL CHECK (rating_value IN (-1, 1)), -- +1 hoặc -1
    comment TEXT,
    rated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (product_id) REFERENCES `PRODUCT`(product_id) ON DELETE CASCADE,
    FOREIGN KEY (reviewer_id) REFERENCES `USER`(user_id) ON DELETE RESTRICT,
    FOREIGN KEY (reviewee_id) REFERENCES `USER`(user_id) ON DELETE RESTRICT,
    
    -- Mỗi bidder chỉ được đánh giá seller 1 lần cho 1 sản phẩm
    UNIQUE KEY unique_rating (product_id, reviewer_id, reviewee_id)
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
INSERT INTO `PRODUCT` (seller_id, category_id, main_image_url, product_name, current_price, buy_now_price, start_price, price_step, description, end_time, bid_count) VALUES
(3, 3, "https://www.dxomark.cn/wp-content/uploads/medias/post-95390/Apple-iPhone-13-Pro-Max-featured-image-packshot-review.jpg", 'iPhone 13 Pro Max', 20000000.00, 25000000.00, 18000000.00, 100000.00, 'Mẫu iPhone mới nhất với camera 48MP, chip A16 Bionic mạnh mẽ. Tình trạng như mới, còn bảo hành chính hãng', DATE_ADD(NOW(), INTERVAL 5 DAY), 8), -- Điện thoại di động
(2, 4, "https://didongmango.com/images/products/2022/11/07/large/2_1667844618.png", 'MacBook Pro M2', 30000000.00, NULL, 30000000.00, 200000.00, 'Máy tính xách tay MacBook Pro 14 inch M2, RAM 16GB, SSD 512GB. Hiệu suất vượt trội, pin chống cháy lâu dài', DATE_ADD(NOW(), INTERVAL 1 DAY), 0), -- Máy tính xách tay
(3, 5, "", 'Đồng hồ Casio G-Shock', 1500000.00, 2000000.00, 1000000.00, 50000.00, 'Đồng hồ Casio G-Shock chống sốc, chống nước. Thiết kế nam tính, bền bỉ, pin chạy 10 năm', DATE_ADD(NOW(), INTERVAL 7 DAY), 3), -- Đồng hồ
(3, 10, "", 'Samsung Galaxy Tab S9', 12000000.00, 15000000.00, 10000000.00, 50000.00, 'Máy tính bảng Samsung Galaxy Tab S9 11 inch, Snapdragon 8 Gen 2 Leader, màn hình AMOLED đẹp tuyệt vời', DATE_ADD(NOW(), INTERVAL 3 DAY), 5), -- Máy tính bảng
(3, 7, "", 'Kindle Paperwhite', 3000000.00, 4000000.00, 3000000.00, 20000.00, 'Máy đọc sách điện tử Kindle Paperwhite 11th gen, màn hình 6.8 inch, sạc USB-C, pin tới 10 tuần', DATE_ADD(NOW(), INTERVAL 2 DAY), 0), -- Sách
(3, 7,"", 'Bộ truyện Harry Potter', 800000.00, 1000000.00, 600000.00, 10000.00, 'Trọn bộ 7 cuốn truyện Harry Potter, bản dịch Việt Nam chính thức, bìa cứng chất lượng cao', DATE_ADD(NOW(), INTERVAL 10 DAY), 12), -- Sách
(3, 9,"", 'LEGO Thành phố - Đồn cảnh sát', 2000000.00, 2500000.00, 1800000.00, 50000.00, 'Bộ LEGO City Police Station, hơn 1200 mảnh, kèm minifigures đầy đủ, phù hợp trẻ từ 6 tuổi trở lên', DATE_ADD(NOW(), INTERVAL 6 DAY), 4), -- Đồ chơi
(3, 6, "", 'Giày Nike Air Max', 2500000.00, 3000000.00, 2000000.00, 50000.00, 'Giày Nike Air Max 90 chính hãng, màu trắng-đen, size 42, tình trạng 95% như mới', DATE_ADD(NOW(), INTERVAL 4 DAY), 7), -- Giày dép
(3, 5, "", 'Đồng hồ Rolex Submariner', 120000000.00, 150000000.00, 100000000.00, 1000000.00, 'Đồng hồ Rolex Submariner Stainless Steel, mặt đen, chống nước 300m, bảo hành quốc tế', DATE_ADD(NOW(), INTERVAL 15 DAY), 2), -- Đồng hồ
(4, 3, "", 'Samsung Galaxy S23 Ultra', 22000000.00, 26000000.00, 20000000.00, 150000.00, 'Điện thoại Samsung Galaxy S23 Ultra, camera 200MP, pin 5000mAh, chip Snapdragon 8 Gen 2', DATE_ADD(NOW(), INTERVAL 8 DAY), 6), -- Điện thoại di động
(4, 4, "", 'Dell XPS 13', 18000000.00, 22000000.00, 16000000.00, 100000.00, 'Laptop Dell XPS 13 Plus, Intel Core i7, RAM 16GB, SSD 512GB, màn hình OLED 13.4 inch', DATE_ADD(NOW(), INTERVAL 6 DAY), 3), -- Máy tính xách tay
(4, 5, "", 'Smartwatch Apple Watch Series 9', 8000000.00, 10000000.00, 7000000.00, 50000.00, 'Đồng hồ thông minh Apple Watch Series 9, chip S9, màn hình Always-On Retina, pin 18 giờ', DATE_ADD(NOW(), INTERVAL 5 DAY), 4), -- Đồng hồ
(4, 6, "", 'Giày Adidas Ultraboost 23', 3500000.00, 4200000.00, 3000000.00, 100000.00, 'Giày chạy bộ Adidas Ultraboost 23 chính hãng, đệm boost thoải mái, đế tổng hợp nhẹ', DATE_ADD(NOW(), INTERVAL 7 DAY), 5), -- Giày dép
(4, 7, "", 'Truyện Cỏ đỏ - Sơn Tùng', 250000.00, 350000.00, 200000.00, 5000.00, 'Quyển truyện Cỏ đỏ của nhà văn Sơn Tùng, bìa cứng, chất lượng giấy tốt', DATE_ADD(NOW(), INTERVAL 9 DAY), 2), -- Sách
(4, 9, "", 'Hot Wheels Collection 50 chiếc', 1500000.00, 1800000.00, 1200000.00, 30000.00, 'Bộ sưu tập 50 chiếc xe Hot Wheels đa chủng loại, có hộp đẹp, phù hợp cho những người sưu tập', DATE_ADD(NOW(), INTERVAL 8 DAY), 1), -- Đồ chơi
(5, 10, "", 'iPad Air 5', 15000000.00, 18000000.00, 13000000.00, 100000.00, 'Máy tính bảng iPad Air 5, chip M1, RAM 8GB, màn hình Liquid Retina 10.9 inch, hỗ trợ Apple Pencil', DATE_ADD(NOW(), INTERVAL 4 DAY), 3), -- Máy tính bảng
(5, 3, "", 'Google Pixel 8 Pro', 21000000.00, 25000000.00, 19000000.00, 100000.00, 'Điện thoại Google Pixel 8 Pro, camera Gemini AI, chip Tensor G3, màn hình OLED 120Hz', DATE_ADD(NOW(), INTERVAL 5 DAY), 4), -- Điện thoại di động
(5, 4, "", 'ASUS VivoBook 15', 12000000.00, 15000000.00, 10000000.00, 100000.00, 'Laptop ASUS VivoBook 15, chip AMD Ryzen 7, RAM 16GB, SSD 512GB, pin lên đến 10 tiếng', DATE_ADD(NOW(), INTERVAL 6 DAY), 2), -- Máy tính xách tay
(5, 7, "", 'Bộ sách Ngôn tình xuyên thời gian', 600000.00, 800000.00, 500000.00, 10000.00, 'Bộ 3 cuốn sách ngôn tình xuyên thời gian, bản dịch Việt, sắc nét, in đẹp', DATE_ADD(NOW(), INTERVAL 12 DAY), 8); -- Sách

-- Dữ liệu mẫu cho bảng PRODUCT_IMAGE (Đã sửa Image URL)
INSERT INTO `PRODUCT_IMAGE` (product_id, image_url) VALUES
-- Product 1: iPhone 13 Pro Max
(1, 'https://i.imgur.com/jG5dF7z.jpg'),
(1, 'https://i.imgur.com/GZ5lG5b.jpg'),
(1, 'https://i.imgur.com/4qD2l8W.jpg'),
-- Product 2: MacBook Pro M2
(2, 'https://i.imgur.com/YtH4bO8.jpg'),
(2, 'https://i.imgur.com/K1L7xTq.jpg'),
(2, 'https://i.imgur.com/j0xT5Zc.jpg'),
-- Product 3: Casio G-Shock
(3, 'https://i.imgur.com/vHqX6oW.jpg'),
(3, 'https://i.imgur.com/Z4c9wBf.jpg'),
-- Product 4: Samsung Galaxy Tab S9
(4, 'https://i.imgur.com/rS2Xg9N.jpg'),
(4, 'https://i.imgur.com/QjB9sUe.jpg'),
-- Product 5: Kindle Paperwhite
(5, 'https://i.imgur.com/3N4o9Z6.jpg'),
(5, 'https://i.imgur.com/6Xw8yUa.jpg'),
-- Product 6: Harry Potter Set
(6, 'https://i.imgur.com/yGfX9vQ.jpg'),
(6, 'https://i.imgur.com/uC5vM7X.jpg'),
-- Product 7: LEGO City Police
(7, 'https://i.imgur.com/u5E6yLq.jpg'),
(7, 'https://i.imgur.com/FjB8eO9.jpg'),
-- Product 8: Nike Air Max
(8, 'https://i.imgur.com/W7hGzH2.jpg'),
(8, 'https://i.imgur.com/L1M5qZp.jpg'),
-- Product 9: Rolex Submariner
(9, 'https://i.imgur.com/P4E2oV0.jpg'),
(9, 'https://i.imgur.com/8Kj4tWq.jpg'),
-- Product 10: Samsung Galaxy S23 Ultra
(10, 'https://i.imgur.com/3Yx4eW0.jpg'),
(10, 'https://i.imgur.com/n7D2gP5.jpg'),
-- Product 11: Dell XPS 13
(11, 'https://i.imgur.com/b9Jc1M7.jpg'),
(11, 'https://i.imgur.com/4S2xG0e.jpg'),
-- Product 12: Apple Watch Series 9
(12, 'https://i.imgur.com/1GZ6s1w.jpg'),
(12, 'https://i.imgur.com/Z3b8oN2.jpg'),
-- Product 13: Adidas Ultraboost 23
(13, 'https://i.imgur.com/J4Pj5nZ.jpg'),
(13, 'https://i.imgur.com/e9w2cQd.jpg'),
-- Product 14: Cỏ đỏ
(14, 'https://i.imgur.com/8Qp4w0K.jpg'),
-- Product 15: Hot Wheels Collection
(15, 'https://i.imgur.com/h5vYf4m.jpg'),
(15, 'https://i.imgur.com/G3t1oR8.jpg'),
-- Product 16: iPad Air 5
(16, 'https://i.imgur.com/0v5k6uS.jpg'),
(16, 'https://i.imgur.com/j4oR2eL.jpg'),
-- Product 17: Google Pixel 8 Pro
(17, 'https://i.imgur.com/9w2gH1U.jpg'),
(17, 'https://i.imgur.com/k6l4tYv.jpg'),
-- Product 18: ASUS VivoBook 15
(18, 'https://i.imgur.com/5l4hE7t.jpg'),
(18, 'https://i.imgur.com/a7x8pCj.jpg'),
-- Product 19: Bộ sách ngôn tình
(19, 'https://i.imgur.com/M6L2b9C.jpg'),
(19, 'https://i.imgur.com/N4O1P9e.jpg');

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

-- Dữ liệu mẫu cho bảng PRODUCT_ANSWER
INSERT INTO `PRODUCT_ANSWER` (question_id, answer_user_id, answer_text) VALUES
-- Q1: 'Sản phẩm này có còn bảo hành không?' - Seller_id=3 trả lời
(1, 3, 'Sản phẩm còn bảo hành chính hãng Apple đến tháng 11/2026.'),
(1, 3, 'Có tem bảo hành đầy đủ trên thân máy ạ.'),
-- Q2: 'Chiếc máy này màu gì?' (0 câu trả lời - Đang chờ seller trả lời)
-- Q3: 'Giao hàng đến Hà Nội mất bao lâu?' - Seller_id=3 trả lời
(3, 3, 'Thời gian giao hàng dự kiến 1-2 ngày làm việc.'),
-- Q4: 'MacBook M2 có RAM bao nhiêu?' - Seller_id=3 trả lời
(4, 3, 'Bản tôi đang bán là 16GB RAM, 512GB SSD.'),
(4, 3, 'Mình còn tùy chọn 8GB hoặc 16GB RAM để bạn chọn.'),
-- Q5: 'Có box, sạc đầy đủ không?' - Seller_id=3 trả lời
(5, 3, 'Sản phẩm full box, phụ kiện zin 100% kèm theo.'),
-- Q6: 'Đây là phiên bản nước ngoài hay hàng Việt Nam?' - Seller_id=3 trả lời
(6, 3, 'Đây là hàng chính hãng phân phối tại Việt Nam (mã VN/A).'),
(6, 3, 'Có tem BH chính hãng và hóa đơn mua hàng đầy đủ.'),
-- Q7: 'Đồng hồ pin tốt không?' - Seller_id=3 trả lời
(7, 3, 'Dòng G-Shock này nổi tiếng về độ bền, pin dùng khoảng 10 năm.'),
-- Q8: 'Galaxy Tab S9 có hỗ trợ stylus không?' (0 câu trả lời - Đang chờ seller)
-- Q9: 'Màn hình loại nào, độ phân giải như thế nào?' - Seller_id=3 trả lời
(9, 3, 'Màn hình Dynamic AMOLED 2X, hiển thị rất đẹp.'),
(9, 3, 'Độ phân giải cao 2560x1600px.');
-- Q10: 'Máy chạy Android phiên bản mấy?' (0 câu trả lời - Đang chờ seller)

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
