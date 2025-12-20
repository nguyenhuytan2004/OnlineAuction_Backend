DROP DATABASE IF EXISTS online_auction_db;
CREATE DATABASE online_auction_db;

USE online_auction_db;

-- Bảng chứa thông tin danh mục sản phẩm
CREATE TABLE `category` (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,
    parent_id INT,

    FOREIGN KEY (parent_id) REFERENCES `category`(category_id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- Bảng chứa thông tin người dùng (Bidder, Seller)
CREATE TABLE `user` (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    encrypted_password VARCHAR(255) NOT NULL,
    rating_score INT DEFAULT 0,
    rating_count INT DEFAULT 0,
    role ENUM('BIDDER', 'SELLER', 'ADMIN') NOT NULL DEFAULT 'BIDDER',
    seller_expires_at DATETIME DEFAULT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Bảng chứa thông tin sản phẩm đấu giá
CREATE TABLE `product` (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    seller_id INT NOT NULL,
    category_id INT NOT NULL,
    main_image_url VARCHAR(255) DEFAULT NULL,
    product_name VARCHAR(255) NOT NULL,
    current_price DECIMAL(18, 2) NOT NULL,
    highest_bidder_id INT DEFAULT NULL,
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

    FOREIGN KEY (seller_id) REFERENCES `user`(user_id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES `category`(category_id) ON DELETE RESTRICT,
    FOREIGN KEY (highest_bidder_id) REFERENCES `user`(user_id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- Bảng chứa thông tin yêu cầu nâng cấp seller
CREATE TABLE `seller_upgrade_request` (
    request_id INT AUTO_INCREMENT PRIMARY KEY,  
    user_id INT NOT NULL,
    request_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    reviewed_at TIMESTAMP DEFAULT NULL,
    comments TEXT DEFAULT NULL, -- Lý do từ chối nếu có

    FOREIGN KEY (user_id) REFERENCES `user`(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Bảng chứa thông tin ảnh phụ
CREATE TABLE `product_image` (
    image_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    image_url VARCHAR(255) NOT NULL,

    FOREIGN KEY (product_id) REFERENCES `product`(product_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Bảng chứa câu hỏi về sản phẩm
CREATE TABLE `product_question` (
    question_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    question_user_id INT NOT NULL,
    question_text TEXT NOT NULL,
    question_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (product_id) REFERENCES `product`(product_id) ON DELETE CASCADE,
    FOREIGN KEY (question_user_id) REFERENCES `user`(user_id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Bảng chứa câu trả lời cho câu hỏi sản phẩm
CREATE TABLE `product_answer` (
    answer_id INT AUTO_INCREMENT PRIMARY KEY,
    question_id INT NOT NULL,
    answer_user_id INT NOT NULL,
    answer_text TEXT NOT NULL,
    answer_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (question_id) REFERENCES `product_question`(question_id) ON DELETE CASCADE,
    FOREIGN KEY (answer_user_id) REFERENCES `user`(user_id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Bảng chứa thông tin Watchlist của người dùng
CREATE TABLE `watch_list` (
    watch_list_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES `user`(user_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES `product`(product_id) ON DELETE CASCADE,

    UNIQUE KEY unique_watchlist (user_id, product_id)
) ENGINE=InnoDB;

-- Bảng chứa thông tin lịch sử ra giá
CREATE TABLE `bid` (
    bid_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    bidder_id INT NOT NULL,
    bid_price DECIMAL(18, 2) NOT NULL,
    max_auto_price DECIMAL(18, 2) NOT NULL,
    bid_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (product_id) REFERENCES `product`(product_id) ON DELETE CASCADE,
    FOREIGN KEY (bidder_id) REFERENCES `user`(user_id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Bảng chứa kết quả đấu giá
CREATE TABLE `auction_result` (
    result_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL UNIQUE,
    winner_id INT NOT NULL,
    final_price DECIMAL(18, 2) NOT NULL,
    result_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_status ENUM('PENDING', 'PAID', 'CANCELED') DEFAULT 'PENDING',

    FOREIGN KEY (product_id) REFERENCES `product`(product_id) ON DELETE CASCADE,
    FOREIGN KEY (winner_id) REFERENCES `user`(user_id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Bảng chứa đánh giá người bán
CREATE TABLE `rating` (
    rating_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    reviewer_id INT NOT NULL,
    reviewee_id INT NOT NULL,
    rating_value INT NOT NULL CHECK (rating_value IN (-1, 1)),
    comment TEXT,
    rated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (product_id) REFERENCES `product`(product_id) ON DELETE CASCADE,
    FOREIGN KEY (reviewer_id) REFERENCES `user`(user_id) ON DELETE RESTRICT,
    FOREIGN KEY (reviewee_id) REFERENCES `user`(user_id) ON DELETE RESTRICT,

    UNIQUE KEY unique_rating (product_id, reviewer_id, reviewee_id)
) ENGINE=InnoDB;

-- Bảng chứa thông tin người bị chặn khỏi đấu giá một sản phẩm cụ thể
CREATE TABLE `blocked_bidder` (
    block_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    blocker_id INT NOT NULL, -- Người chặn
    blocked_id INT NOT NULL,  -- Người bị chặn
    blocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reason VARCHAR(255),

    UNIQUE KEY unique_block (product_id, blocked_id),
    
    FOREIGN KEY (product_id) REFERENCES `product`(product_id) ON DELETE CASCADE,
    FOREIGN KEY (blocked_id) REFERENCES `user`(user_id) ON DELETE CASCADE,
    FOREIGN KEY (blocker_id) REFERENCES `user`(user_id) ON DELETE RESTRICT
);

-- Bảng chứa thông tin các cuộc hội thoại giữa người mua và người bán
CREATE TABLE `conversation` (
    conversation_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    seller_id INT NOT NULL,
    buyer_id INT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (product_id) REFERENCES `product`(product_id) ON DELETE CASCADE,
    FOREIGN KEY (buyer_id) REFERENCES `user`(user_id) ON DELETE RESTRICT,
    FOREIGN KEY (seller_id) REFERENCES `user`(user_id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Bảng chứa tin nhắn trong cuộc hội thoại
CREATE TABLE `message` (
    message_id INT AUTO_INCREMENT PRIMARY KEY,
    conversation_id INT NOT NULL,
    sender_id INT NOT NULL,
    message_text TEXT NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (conversation_id) REFERENCES `conversation`(conversation_id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES `user`(user_id) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE auction_order (
   order_id INT AUTO_INCREMENT PRIMARY KEY,

   product_id INT NOT NULL,
   seller_id INT NOT NULL,
   buyer_id INT NOT NULL,

   final_price DECIMAL(15,2) NOT NULL,

   status ENUM(
        'WAIT_PAYMENT',
        'PAID',
        'ON_DELIVERING'
        'COMPLETED',
        'CANCELLED'
        ) NOT NULL DEFAULT 'WAIT_PAYMENT',

-- Payment
   paid_at TIMESTAMP NULL,

-- Shipping
   shipping_address TEXT,

-- Cancel
   cancelled_reason TEXT,
   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

   FOREIGN KEY (product_id) REFERENCES product(product_id),
   FOREIGN KEY (seller_id) REFERENCES user(user_id),
   FOREIGN KEY (buyer_id) REFERENCES user(user_id)
);

-- Dữ liệu mẫu cho bảng CATEGORY
INSERT INTO `category` (category_name, parent_id) VALUES
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

-- USER (Realistic names + realistic emails)
INSERT INTO `user` (full_name, email, encrypted_password, rating_score, rating_count) VALUES
('Nguyễn Hoàng Minh', 'minh.nguyen@gmail.com', '$2a$10$5K7irzpbIRu8CobNZJFVDempz9WDIf.LDvGLOB0wxM0ElH5eQodhC', 8, 10),
('Trần Khánh Linh', 'linh.tran@gmail.com', '$2a$10$savotgG5leGb/SwtvrRPguX76AwBumNxFZMJYegUUncRvegOucX4O', 10, 10),
('Lê Quốc Huy', 'huy.le@gmail.com', '$2a$10$BOtUAA6RN.v581tSt7vW4.vgDr8DdFrtXSkTKArazSZC.dWl.wLVm', 12, 15),
('Phạm Bảo Trân', 'tran.pham@gmail.com', '$2a$10$examplehashforsellerd', 14, 20),
('Hoàng Gia Bảo', 'bao.hoang@gmail.com', '$2a$10$examplehashforsellere', 16, 25),
('Nguyễn Thu Hà', 'ha.nguyen@gmail.com', '$2a$10$examplehashforbidderf', 9, 12),
('Nguyễn Huy Tấn', 'nhtan22@clc.fitus.edu.vn', '$2a$10$F4RGn9dExHFHf1kCcFSNa.BzlanrhH3wt0mdSbzAtdZ7cQSIQI/Ae', 15, 20),
('Đăng Văn Quang', 'dvquang22@clc.fitus.edu.vn', '$2a$10$MHmloXfvFV7ar1N/clY3getdUu2/rWND21KaWQbx5eAsp49RLpdOW', 38, 43);

-- INSERT PRODUCT
INSERT INTO `product`
(seller_id, category_id, main_image_url, product_name, current_price, highest_bidder_id, buy_now_price, start_price, price_step, description, end_time, bid_count) VALUES
 (3, 3, "https://www.dxomark.cn/wp-content/uploads/medias/post-95390/Apple-iPhone-13-Pro-Max-featured-image-packshot-review.jpg",
 'iPhone 13 Pro Max', 18000000.00, NULL, 25000000.00, 18000000.00, 100000.00,
 'Mẫu iPhone mới nhất với camera 48MP, chip A16 Bionic mạnh mẽ. Tình trạng như mới, còn bảo hành chính hãng',
 DATE_ADD(NOW(), INTERVAL 5 DAY), 0),
 (5, 4, "https://didongmango.com/images/products/2022/11/07/large/2_1667844618.png",
 'MacBook Pro M2', 10000000.00, NULL, NULL, 10000000.00, 100000.00,
 'Máy tính xách tay MacBook Pro 14 inch M2, RAM 16GB, SSD 512GB. Hiệu suất vượt trội, pin chống cháy lâu dài',
 DATE_ADD(NOW(), INTERVAL 1 DAY), 0),
 (3, 5, "", 'Đồng hồ Casio G-Shock', 1000000.00, NULL, 2000000.00, 1000000.00, 50000.00,
 'Đồng hồ Casio G-Shock chống sốc, chống nước. Thiết kế nam tính, bền bỉ, pin chạy 10 năm',
 DATE_ADD(NOW(), INTERVAL 7 DAY), 0),
 (3, 10, "", 'Samsung Galaxy Tab S9', 10000000.00, NULL, NULL, 10000000.00, 50000.00,
 'Máy tính bảng Samsung Galaxy Tab S9 11 inch, Snapdragon 8 Gen 2 Leader, màn hình AMOLED đẹp tuyệt vời',
 DATE_ADD(NOW(), INTERVAL 3 DAY), 0),
 (3, 7, "", 'Kindle Paperwhite', 3000000.00, NULL, 4000000.00, 3000000.00, 20000.00,
 'Máy đọc sách điện tử Kindle Paperwhite 11th gen, màn hình 6.8 inch, sạc USB-C, pin tới 10 tuần',
 DATE_ADD(NOW(), INTERVAL 2 DAY), 0),
 (3, 7, "", 'Bộ truyện Harry Potter', 600000.00, NULL, 1000000.00, 600000.00, 10000.00,
 'Trọn bộ 7 cuốn truyện Harry Potter, bản dịch Việt Nam chính thức, bìa cứng chất lượng cao',
 DATE_ADD(NOW(), INTERVAL 10 DAY), 0),
 (3, 9, "", 'LEGO Thành phố - Đồn cảnh sát', 1800000.00, NULL, 2500000.00, 1800000.00, 50000.00,
 'Bộ LEGO City Police Station, hơn 1200 mảnh, kèm minifigures đầy đủ, phù hợp trẻ từ 6 tuổi trở lên',
 DATE_ADD(NOW(), INTERVAL 6 DAY), 0),
 (3, 6, "", 'Giày Nike Air Max', 2000000.00, NULL, 3000000.00, 2000000.00, 50000.00,
 'Giày Nike Air Max 90 chính hãng, màu trắng-đen, size 42, tình trạng 95% như mới',
 DATE_ADD(NOW(), INTERVAL 4 DAY), 0),
 (3, 5, "", 'Đồng hồ Rolex Submariner', 100000000.00, NULL, NULL, 100000000.00, 1000000.00,
 'Đồng hồ Rolex Submariner Stainless Steel, mặt đen, chống nước 300m, bảo hành quốc tế',
 DATE_ADD(NOW(), INTERVAL 15 DAY), 0),
 (4, 3, "", 'Samsung Galaxy S23 Ultra', 20000000.00, NULL, 26000000.00, 20000000.00, 150000.00,
 'Điện thoại Samsung Galaxy S23 Ultra, camera 200MP, pin 5000mAh, chip Snapdragon 8 Gen 2',
 DATE_ADD(NOW(), INTERVAL 8 DAY), 0),
 (4, 4, "", 'Dell XPS 13', 16000000.00, NULL, NULL, 16000000.00, 100000.00,
 'Laptop Dell XPS 13 Plus, Intel Core i7, RAM 16GB, SSD 512GB, màn hình OLED 13.4 inch',
 DATE_ADD(NOW(), INTERVAL 6 DAY), 0),
 (4, 5, "", 'Smartwatch Apple Watch Series 9', 7000000.00, NULL, 10000000.00, 7000000.00, 50000.00,
 'Đồng hồ thông minh Apple Watch Series 9, chip S9, màn hình Always-On Retina, pin 18 giờ',
 DATE_ADD(NOW(), INTERVAL 5 DAY), 0),
 (4, 6, "", 'Giày Adidas Ultraboost 23', 3000000.00, NULL, 4200000.00, 3000000.00, 100000.00,
 'Giày chạy bộ Adidas Ultraboost 23 chính hãng, đệm boost thoải mái, đế tổng hợp nhẹ',
 DATE_ADD(NOW(), INTERVAL 7 DAY), 0),
 (4, 7, "", 'Truyện Cỏ đỏ - Sơn Tùng', 200000.00, NULL, 350000.00, 200000.00, 5000.00,
 'Quyển truyện Cỏ đỏ của nhà văn Sơn Tùng, bìa cứng, chất lượng giấy tốt',
 DATE_ADD(NOW(), INTERVAL 9 DAY), 0),
 (4, 9, "", 'Hot Wheels Collection 50 chiếc', 1200000.00, NULL, 1800000.00, 1200000.00, 30000.00,
 'Bộ sưu tập 50 chiếc xe Hot Wheels đa chủng loại, có hộp đẹp, phù hợp cho những người sưu tập',
 DATE_ADD(NOW(), INTERVAL 8 DAY), 0),
 (5, 10, "", 'iPad Air 5', 13000000.00, NULL, 18000000.00, 13000000.00, 100000.00,
 'Máy tính bảng iPad Air 5, chip M1, RAM 8GB, màn hình Liquid Retina 10.9 inch, hỗ trợ Apple Pencil',
 DATE_ADD(NOW(), INTERVAL 4 DAY), 0),
 (5, 3, "", 'Google Pixel 8 Pro', 19000000.00, NULL, 25000000.00, 19000000.00, 100000.00,
 'Điện thoại Google Pixel 8 Pro, camera Gemini AI, chip Tensor G3, màn hình OLED 120Hz',
 DATE_ADD(NOW(), INTERVAL 5 DAY), 0),
 (5, 4, "", 'ASUS VivoBook 15', 10000000.00, NULL, 15000000.00, 10000000.00, 100000.00,
 'Laptop ASUS VivoBook 15, chip AMD Ryzen 7, RAM 16GB, SSD 512GB, pin lên đến 10 tiếng',
 DATE_ADD(NOW(), INTERVAL 6 DAY), 0),
 (5, 7, "", 'Bộ sách Ngôn tình xuyên thời gian', 500000.00, NULL, 800000.00, 500000.00, 10000.00,
 'Bộ 3 cuốn sách ngôn tình xuyên thời gian, bản dịch Việt, sắc nét, in đẹp',
 DATE_ADD(NOW(), INTERVAL 12 DAY), 0);

-- Product images
INSERT INTO `product_image` (product_id, image_url) VALUES
(1, 'https://i.imgur.com/jG5dF7z.jpg'),
(1, 'https://i.imgur.com/GZ5lG5b.jpg'),
(1, 'https://i.imgur.com/4qD2l8W.jpg'),
(2, 'https://i.imgur.com/YtH4bO8.jpg'),
(2, 'https://i.imgur.com/K1L7xTq.jpg'),
(2, 'https://i.imgur.com/j0xT5Zc.jpg'),
(3, 'https://i.imgur.com/vHqX6oW.jpg'),
(3, 'https://i.imgur.com/Z4c9wBf.jpg'),
(4, 'https://i.imgur.com/rS2Xg9N.jpg'),
(4, 'https://i.imgur.com/QjB9sUe.jpg'),
(5, 'https://i.imgur.com/3N4o9Z6.jpg'),
(5, 'https://i.imgur.com/6Xw8yUa.jpg'),
(6, 'https://i.imgur.com/yGfX9vQ.jpg'),
(6, 'https://i.imgur.com/uC5vM7X.jpg'),
(7, 'https://i.imgur.com/u5E6yLq.jpg'),
(7, 'https://i.imgur.com/FjB8eO9.jpg'),
(8, 'https://i.imgur.com/W7hGzH2.jpg'),
(8, 'https://i.imgur.com/L1M5qZp.jpg'),
(9, 'https://i.imgur.com/P4E2oV0.jpg'),
(9, 'https://i.imgur.com/8Kj4tWq.jpg'),
(10, 'https://i.imgur.com/3Yx4eW0.jpg'),
(10, 'https://i.imgur.com/n7D2gP5.jpg'),
(11, 'https://i.imgur.com/b9Jc1M7.jpg'),
(11, 'https://i.imgur.com/4S2xG0e.jpg'),
(12, 'https://i.imgur.com/1GZ6s1w.jpg'),
(12, 'https://i.imgur.com/Z3b8oN2.jpg'),
(13, 'https://i.imgur.com/J4Pj5nZ.jpg'),
(13, 'https://i.imgur.com/e9w2cQd.jpg'),
(14, 'https://i.imgur.com/8Qp4w0K.jpg'),
(15, 'https://i.imgur.com/h5vYf4m.jpg'),
(15, 'https://i.imgur.com/G3t1oR8.jpg'),
(16, 'https://i.imgur.com/0v5k6uS.jpg'),
(16, 'https://i.imgur.com/j4oR2eL.jpg'),
(17, 'https://i.imgur.com/9w2gH1U.jpg'),
(17, 'https://i.imgur.com/k6l4tYv.jpg'),
(18, 'https://i.imgur.com/5l4hE7t.jpg'),
(18, 'https://i.imgur.com/a7x8pCj.jpg'),
(19, 'https://i.imgur.com/M6L2b9C.jpg'),
(19, 'https://i.imgur.com/N4O1P9e.jpg');

-- WATCHLIST
INSERT INTO `watch_list` (user_id, product_id) VALUES
(1, 1),
(1, 3),
(2, 1);

-- PRODUCT QUESTION
INSERT INTO `product_question` (product_id, question_user_id, question_text) VALUES
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

-- PRODUCT ANSWER
INSERT INTO `product_answer` (question_id, answer_user_id, answer_text) VALUES
(1, 3, 'Sản phẩm còn bảo hành chính hãng Apple đến tháng 11/2026.'),
(1, 3, 'Có tem bảo hành đầy đủ trên thân máy ạ.'),
(3, 3, 'Thời gian giao hàng dự kiến 1-2 ngày làm việc.'),
(4, 3, 'Bản tôi đang bán là 16GB RAM, 512GB SSD.'),
(4, 3, 'Mình còn tùy chọn 8GB hoặc 16GB RAM để bạn chọn.'),
(5, 3, 'Sản phẩm full box, phụ kiện zin 100% kèm theo.'),
(6, 3, 'Đây là hàng chính hãng phân phối tại Việt Nam (mã VN/A).'),
(6, 3, 'Có tem BH chính hãng và hóa đơn mua hàng đầy đủ.'),
(7, 3, 'Dòng G-Shock này nổi tiếng về độ bền, pin dùng khoảng 10 năm.'),
(9, 3, 'Màn hình Dynamic AMOLED 2X, hiển thị rất đẹp.'),
(9, 3, 'Độ phân giải cao 2560x1600px.');

-- RATING
INSERT INTO `rating` (product_id, reviewer_id, reviewee_id, rating_value, comment) VALUES
(2, 1, 7, 1, 'Sản phẩm tuyệt vời, giao hàng nhanh chóng!'),
(3, 2, 7, -1, 'Sản phẩm không đúng mô tả, thất vọng.'),
(4, 3, 7, 1, 'Rất hài lòng với chất lượng và dịch vụ bán hàng.'),
(5, 4, 7, -1, 'Phản hồi chậm và sản phẩm bị lỗi nhỏ.'),
(6, 5, 7, 1, 'Giá tốt, chất lượng ổn. Sẽ ủng hộ lần sau.');

INSERT INTO seller_upgrade_request
(user_id, request_at, status, reviewed_at, comments) VALUES
(4, NOW(), 'PENDING', NULL, NULL),
(5, DATE_SUB(NOW(), INTERVAL 2 DAY), 'APPROVED', NOW(), 'User meets all seller requirements'),
(6, DATE_SUB(NOW(), INTERVAL 1 DAY), 'REJECTED', NOW(), 'Insufficient transaction history');

