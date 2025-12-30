DROP DATABASE IF EXISTS online_auction_db;
CREATE DATABASE online_auction_db;

USE online_auction_db;

-- Bảng chứa thông tin danh mục sản phẩm
CREATE TABLE `category` (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,
    description TEXT,
    parent_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

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
    is_verified BOOLEAN DEFAULT FALSE,
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
        'ON_DELIVERING',
        'COMPLETED',
        'CANCELED'
        ) NOT NULL DEFAULT 'WAIT_PAYMENT',

-- Payment
   paid_at TIMESTAMP NULL,

-- Shipping
   shipping_address TEXT,

-- Cancel
   canceled_reason TEXT,
   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

   FOREIGN KEY (product_id) REFERENCES product(product_id),
   FOREIGN KEY (seller_id) REFERENCES user(user_id),
   FOREIGN KEY (buyer_id) REFERENCES user(user_id)
);

-- Dữ liệu mẫu cho bảng CATEGORY
INSERT INTO `category` (category_id, category_name, description, parent_id) VALUES
-- Danh mục cấp 1 (Parent là NULL)
(1, 'Điện tử', 'Các thiết bị điện tử, máy móc công nghệ cao', NULL),
(2, 'Thời trang', 'Quần áo, phụ kiện và xu hướng thời trang mới nhất', NULL),
(3, 'Sách', 'Kho tàng tri thức với đa dạng thể loại từ văn học đến khoa học', NULL),
(4, 'Đồ gia dụng', 'Thiết bị và vật dụng tiện ích cho gia đình', NULL),
(5, 'Đồ chơi', 'Đồ chơi giải trí và phát triển trí tuệ cho trẻ em', NULL),

-- Danh mục cấp 2 thuộc Điện tử (parent_id = 1)
(6, 'Điện thoại di động', 'Smartphone, điện thoại phổ thông và phụ kiện đi kèm', 1),
(7, 'Máy tính xách tay', 'Laptop văn phòng, gaming và workstation', 1),
(8, 'Máy tính bảng', 'Các dòng tablet phục vụ công việc và giải trí', 1),

-- Danh mục cấp 2 thuộc Thời trang (parent_id = 2)
(9, 'Đồng hồ', 'Đồng hồ đeo tay nam nữ, đồng hồ thông minh', 2),
(10, 'Giày dép', 'Giày thể thao, giày tây và dép thời trang', 2);

-- USER (Realistic names + realistic emails)
INSERT INTO `user` (full_name, email, encrypted_password, rating_score, rating_count, role, seller_expires_at, is_verified) VALUES
('Nguyễn Hoàng Minh', 'minh.nguyen@gmail.com', '$2a$10$biWJYj6QdRH/uQQ8ZjhFL.uzgzRQQKptyuffdgR7jKQSpCiakyg42', 8, 10, "ADMIN", NULL, 1),
('Trần Khánh Linh', 'linh.tran@gmail.com', '$2a$10$z848BvAPrkiyGesaKe4t.O.cl31Kbvi673w6cn3JAB6tTdhK9feoW', 10, 10, "BIDDER", NULL, 1),
('Lê Quốc Huy', 'huy.le@gmail.com', '$2a$10$ar7nlwDhBGR0zhkdx91/TuWOvdcQvNvZKykwq.gvzPLg8QlPD1CHi', 12, 15, "SELLER", DATE_ADD(NOW(), INTERVAL 7 DAY), 1),
('Phạm Bảo Trân', 'tran.pham@gmail.com', '$2a$10$nfeKkytJQVKgLf1oNPuqT./jaOU8VL2sfp1MCLQOhvlMEIOwgGkX6', 14, 20, "BIDDER", NULL, 1),
('Hoàng Gia Bảo', 'bao.hoang@gmail.com', '$2a$10$eORh7G2q68kXUQmcsU7E1.vN5WrFHu9Yl1FHxrnq0EfPyukTdllFG', 16, 25, "BIDDER", NULL, 1),
('Nguyễn Thu Hà', 'ha.nguyen@gmail.com', '$2a$10$ENoPzpks2zRYu1P/j8TELOHpGoF2KE4HUil7UbBGVZERsQh4qHXmi', 9, 12, "BIDDER", NULL, 1),
('Nguyễn Huy Tấn', 'nhtan22@clc.fitus.edu.vn', '$2a$10$ENoPzpks2zRYu1P/j8TELOHpGoF2KE4HUil7UbBGVZERsQh4qHXmi', 15, 20, "BIDDER", NULL, 1),
('Đăng Văn Quang', 'dvquang22@clc.fitus.edu.vn', '$2a$09$c/EAtme1ly8YmmeyaluffetqzxkivIyjRnlH9sqXJdlbwEyT7gEy.', 38, 43, "BIDDER", NULL, 1);

-- INSERT PRODUCT
INSERT INTO `product` 
(seller_id, category_id, main_image_url, product_name, current_price, highest_bidder_id, buy_now_price, start_price, price_step, description, end_time, is_auto_renew, allow_unrated_bidder) VALUES
(3, 6, "https://pos.nvncdn.com/a135ac-81120/ps/20231121_zPEcyW8KB6.jpeg?v=1700557390", 'iPhone 14 Pro Max', 18000000.00, NULL, 25000000.00, 18000000.00, 100000.00, 'Chip A16 Bionic, camera 48MP mạnh mẽ. Tình trạng như mới.', DATE_ADD(NOW(), INTERVAL 5 DAY), 1, 1),

(5, 7, "https://didongmango.com/images/products/2022/11/07/large/2_1667844618.png", 'MacBook Pro M2', 10000000.00, NULL, NULL, 10000000.00, 100000.00, 'Laptop MacBook Pro 14 inch M2, RAM 16GB, SSD 512GB.', DATE_ADD(NOW(), INTERVAL 1 DAY), 0, 1),

(3, 9, "https://product.hstatic.net/1000388227/product/ga-010-1a1_67845d1d7c2c4bc4b63a44878c278939_master.png", 'Đồng hồ Casio G-Shock', 1000000.00, NULL, 2000000.00, 1000000.00, 50000.00, 'Đồng hồ Casio G-Shock chống sốc, chống nước.', DATE_ADD(NOW(), INTERVAL 7 DAY), 0, 1),

(3, 8, "https://titaniummobile.net/cdn/shop/files/S9TABULTRA.png?v=1690249145", 'Samsung Galaxy Tab S9', 10000000.00, NULL, NULL, 10000000.00, 50000.00, 'Máy tính bảng Samsung Galaxy Tab S9 11 inch, AMOLED.', DATE_ADD(NOW(), INTERVAL 3 DAY), 1, 1),

(3, 8, "https://cdn2.cellphones.com.vn/x/media/catalog/product/m/a/may-doc-sach-kindle-paperwhite-5-16gb.png", 'Kindle Paperwhite 5', 3000000.00, NULL, 4000000.00, 3000000.00, 20000.00, 'Máy đọc sách Kindle Paperwhite Gen 11th, màn hình 6.8 inch.', DATE_ADD(NOW(), INTERVAL 2 DAY), 1, 1),

(3, 3, "", 'Bộ truyện Harry Potter', 600000.00, NULL, 1000000.00, 600000.00, 10000.00, 'Trọn bộ 7 cuốn truyện Harry Potter bản dịch Việt Nam.', DATE_ADD(NOW(), INTERVAL 10 DAY), 1, 1),

(3, 5, "", 'LEGO City - Đồn cảnh sát', 1800000.00, NULL, 2500000.00, 1800000.00, 50000.00, 'Bộ LEGO City Police Station, hơn 1200 mảnh ghép.', DATE_ADD(NOW(), INTERVAL 6 DAY), 0, 1),

(3, 10, "", 'Giày Nike Air Max 90', 2000000.00, NULL, 3000000.00, 2000000.00, 50000.00, 'Giày Nike Air Max 90 chính hãng, màu trắng-đen, size 42.', DATE_ADD(NOW(), INTERVAL 4 DAY), 0, 1),

(3, 9, "", 'Đồng hồ Rolex Submariner', 100000000.00, NULL, NULL, 100000000.00, 1000000.00, 'Đồng hồ Rolex Submariner chính hãng, chống nước 300m.', DATE_ADD(NOW(), INTERVAL 15 DAY), 1, 0),

(4, 6, "", 'Samsung Galaxy S23 Ultra', 20000000.00, NULL, 26000000.00, 20000000.00, 150000.00, 'Điện thoại Samsung Galaxy S23 Ultra, camera 200MP, Snap 8 Gen 2.', DATE_ADD(NOW(), INTERVAL 8 DAY), 0, 0),

(4, 7, "", 'Dell XPS 13 9315', 16000000.00, NULL, NULL, 16000000.00, 100000.00, 'Laptop Dell XPS 13, Intel Core i7, RAM 16GB, SSD 512GB.', DATE_ADD(NOW(), INTERVAL 6 DAY), 0, 1),

(4, 9, "", 'Apple Watch Series 9', 7000000.00, NULL, 10000000.00, 7000000.00, 50000.00, 'Đồng hồ thông minh Apple Watch Series 9 mới nhất.', DATE_ADD(NOW(), INTERVAL 5 DAY), 0, 1),

(4, 10, "", 'Giày Adidas Ultraboost 22', 3000000.00, NULL, 4200000.00, 3000000.00, 100000.00, 'Giày chạy bộ Adidas Ultraboost chính hãng.', DATE_ADD(NOW(), INTERVAL 7 DAY), 0, 1),

(4, 3, "", 'Tiểu thuyết "Búp sen xanh"', 200000.00, NULL, 350000.00, 200000.00, 5000.00, 'Tác phẩm nổi tiếng về Bác Hồ của nhà văn Sơn Tùng.', DATE_ADD(NOW(), INTERVAL 9 DAY), 1, 1),

(4, 5, "", 'Hot Wheels Collection', 1200000.00, NULL, 1800000.00, 1200000.00, 30000.00, 'Bộ sưu tập 50 chiếc xe Hot Wheels đa dạng.', DATE_ADD(NOW(), INTERVAL 8 DAY), 1, 1),

(5, 8, "", 'iPad Air 5 M1', 13000000.00, NULL, 18000000.00, 13000000.00, 100000.00, 'Máy tính bảng iPad Air 5, chip M1, hỗ trợ Apple Pencil.', DATE_ADD(NOW(), INTERVAL 4 DAY), 0, 1),

(5, 6, "", 'Google Pixel 8 Pro', 19000000.00, NULL, 25000000.00, 19000000.00, 100000.00, 'Điện thoại Google Pixel 8 Pro, chip Tensor G3.', DATE_ADD(NOW(), INTERVAL 5 DAY), 0, 1),

(5, 7, "", 'ASUS VivoBook 15', 10000000.00, NULL, 15000000.00, 10000000.00, 100000.00, 'Laptop ASUS VivoBook 15, chip AMD Ryzen 7, RAM 16GB.', DATE_ADD(NOW(), INTERVAL 6 DAY), 0, 1),

(5, 3, "", 'Bộ sách Chúa tể những chiếc nhẫn', 500000.00, NULL, 800000.00, 500000.00, 10000.00, 'Trọn bộ 3 cuốn tiểu thuyết kinh điển của J.R.R. Tolkien.', DATE_ADD(NOW(), INTERVAL 12 DAY), 1, 1),

(5, 9, "", 'Đồng hồ Omega Seamaster', 80000000.00, NULL, NULL, 80000000.00, 500000.00, 'Đồng hồ Omega Seamaster chính hãng, chống nước 300m.', DATE_ADD(NOW(), INTERVAL 20 DAY), 1, 0);

-- Product images
INSERT INTO `product_image` (product_id, image_url) VALUES
(1, 'https://descubraoquee.com.br/wp-content/uploads/2024/09/appleiphone15promax256gbtitniopreto-1.jpg.webp'),
(1, 'https://tpmobile.vn/wp-content/uploads/2022/07/ip12prm-blue-tpmobile.png'),
(1, 'https://www.imagineonline.store/cdn/shop/files/iPhone_14_Pro_Silver_PDP_Image_Position-1a__WWEN.jpg?v=1759733639'),
(2, 'https://bizweb.dktcdn.net/100/318/659/products/mbp-spacegray-gallery3-202206-jpeg-fbed5ccc-c085-4571-b58e-5ae6e27316c6.jpg?v=1660544588733'),
(2, 'https://bizweb.dktcdn.net/100/318/659/products/mbp-spacegray-gallery4-202206-jpeg-c6ff3063-62ae-4d9a-9740-2def28e410cc.jpg?v=1660544588733'),
(2, 'https://bizweb.dktcdn.net/100/318/659/products/mbp-spacegray-gallery6-202206-cb74d1d1-e0a3-45eb-8633-3c95aa2b8cc8.jpg?v=1660544588733'),
(3, 'https://binhminhdigital.com/storedata/images/product/dong-ho-casio-g-shock-ga-110gb-1adr.jpg'),
(3, 'https://cdn.tgdd.vn/Products/Images/7264/252159/g-shock-gba-900-1adr-nam-600x600.jpg'),
(3, 'https://casio.anhkhue.com/upload/images/2020_03/146_f2580962e9c74787bd551b5eaeb35dee_master.jpg'),
(4, 'https://otcer.ph/wp-content/uploads/2023/08/Samsung-Galaxy-Tab-S9-1.jpg'),
(4, 'https://promart.vteximg.com.br/arquivos/ids/7419322-700-700/image-5076cb6623fd4d5889bf75f830aafaf6.jpg?v=638272736658100000'),
(4, 'https://product.hstatic.net/1000379731/product/mul3dutway0g35ul8rlp_bc0427d5a0594b43820baccffe69c71b.png'),
(5, 'https://lagihitech.vn/wp-content/uploads/2023/09/may-doc-sach-Kindle-Paperwhite-5-Gen-11-16GB-hinh-2.jpg'),
(5, 'https://kindlehanoi.vn/wp-content/uploads/2022/02/z3196853413717_b707036feacdca5305fcabfa94885efe-scaled.jpg'),
(5, 'https://img.myipadbox.com/upload/store/detail_l/EDA002526901B_3.jpg'),
(6, 'https://i.imgur.com/yGfX9vQ.jpg'),
(6, 'https://i.imgur.com/yGfX9vQ.jpg'),
(6, 'https://i.imgur.com/uC5vM7X.jpg'),
(7, 'https://i.imgur.com/u5E6yLq.jpg'),
(7, 'https://i.imgur.com/FjB8eO9.jpg'),
(7, 'https://i.imgur.com/FjB8eO9.jpg'),
(8, 'https://i.imgur.com/W7hGzH2.jpg'),
(8, 'https://i.imgur.com/L1M5qZp.jpg'),
(8, 'https://i.imgur.com/L1M5qZp.jpg'),
(9, 'https://i.imgur.com/P4E2oV0.jpg'),
(9, 'https://i.imgur.com/8Kj4tWq.jpg'),
(9, 'https://i.imgur.com/8Kj4tWq.jpg'),
(10, 'https://i.imgur.com/3Yx4eW0.jpg'),
(10, 'https://i.imgur.com/3Yx4eW0.jpg'),
(10, 'https://i.imgur.com/n7D2gP5.jpg'),
(11, 'https://i.imgur.com/b9Jc1M7.jpg'),
(11, 'https://i.imgur.com/b9Jc1M7.jpg'),
(11, 'https://i.imgur.com/4S2xG0e.jpg'),
(12, 'https://i.imgur.com/1GZ6s1w.jpg'),
(12, 'https://i.imgur.com/Z3b8oN2.jpg'),
(12, 'https://i.imgur.com/Z3b8oN2.jpg'),
(13, 'https://i.imgur.com/J4Pj5nZ.jpg'),
(13, 'https://i.imgur.com/J4Pj5nZ.jpg'),
(13, 'https://i.imgur.com/e9w2cQd.jpg'),
(14, 'https://i.imgur.com/8Qp4w0K.jpg'),
(14, 'https://i.imgur.com/8Qp4w0K.jpg'),
(14, 'https://i.imgur.com/8Qp4w0K.jpg'),
(15, 'https://i.imgur.com/h5vYf4m.jpg'),
(15, 'https://i.imgur.com/h5vYf4m.jpg'),
(15, 'https://i.imgur.com/G3t1oR8.jpg'),
(16, 'https://i.imgur.com/0v5k6uS.jpg'),
(16, 'https://i.imgur.com/0v5k6uS.jpg'),
(16, 'https://i.imgur.com/j4oR2eL.jpg'),
(17, 'https://i.imgur.com/9w2gH1U.jpg'),
(17, 'https://i.imgur.com/k6l4tYv.jpg'),
(17, 'https://i.imgur.com/k6l4tYv.jpg'),
(18, 'https://i.imgur.com/5l4hE7t.jpg'),
(18, 'https://i.imgur.com/5l4hE7t.jpg'),
(18, 'https://i.imgur.com/a7x8pCj.jpg'),
(19, 'https://i.imgur.com/M6L2b9C.jpg'),
(19, 'https://i.imgur.com/N4O1P9e.jpg'),
(19, 'https://i.imgur.com/N4O1P9e.jpg'),
(20, 'https://i.imgur.com/N4O1P9e.jpg'),
(20, 'https://i.imgur.com/N4O1P9e.jpg'),
(20, 'https://i.imgur.com/N4O1P9e.jpg');

-- WATCHLIST
INSERT INTO `watch_list` (user_id, product_id) VALUES
(1, 1),
(1, 3),
(2, 1);

-- PRODUCT QUESTION
INSERT INTO `product_question` (product_id, question_user_id, question_text) VALUES
(1, 1, 'Máy có bị móp méo hay trầy xước màn hình không để mình biết đường đặt giá?'),
(1, 2, 'Pin còn bao nhiêu % và đã thay linh kiện gì chưa shop?'),
(1, 1, 'Nếu thắng đấu giá vào tối nay, sáng mai mình qua lấy máy trực tiếp được không?'),
(2, 2, 'Chu kỳ sạc của pin MacBook này là bao nhiêu lần rồi bạn?'),
(2, 1, 'Máy có iCloud ẩn hay bị MDM không, mình cần check kỹ trước khi bid?'),
(3, 2, 'Mặt kính G-Shock có vết xước nào không? Shop có ảnh chụp góc nghiêng không?'),
(3, 1, 'Nếu giá cuối thấp hơn kỳ vọng shop có hủy kèo không?'),
(4, 1, 'Bút S-Pen kèm theo máy là zin hay bút linh kiện vậy shop?'),
(4, 2, 'Góc máy có bị cấn không? Cho mình xin thêm ảnh các góc để tự tin đấu giá.'),
(4, 1, 'Sản phẩm này có áp dụng tính năng tự động gia hạn thêm 5 phút nếu có người bid ở phút cuối không?');

-- PRODUCT ANSWER
INSERT INTO `product_answer` (question_id, answer_user_id, answer_text) VALUES
(1, 3, 'Màn hình đẹp không vết xước, thân máy có một vết dăm nhỏ ở cạnh dưới, không ảnh hưởng thẩm mỹ.'),
(1, 3, 'Pin zin 92%, cam kết chưa qua sửa chữa, nếu sai shop đền gấp đôi số tiền đấu thắng.'),
(3, 3, 'Hoàn toàn được bạn nhé, bạn có thể thanh toán và nhận máy tại showroom sau khi phiên đấu kết thúc.'),
(4, 5, 'Pin mới sạc 45 lần, hiệu suất còn 100%, bạn yên tâm bid giá cao nhé.'),
(4, 5, 'Cam kết máy sạch, không MDM, không iCloud, bao test tại chỗ khi nhận hàng.'),
(5, 5, 'Full box sạc cáp theo máy, tặng kèm túi chống sốc cho người thắng đấu giá giá tốt.'),
(6, 3, 'Mặt kính không vết xước vì mình dùng rất kỹ, đã dán cường lực sẵn.'),
(6, 3, 'Mình đã đăng tải thêm 3 ảnh chụp macro các góc cạnh trong phần mô tả, bạn xem qua nhé.'),
(7, 3, 'Shop tuân thủ đúng luật sàn: Đã lên sàn đấu giá là sẽ bán với mọi mức giá thắng, không hủy kèo.'),
(9, 3, 'S-Pen theo máy là hàng zin, hỗ trợ đầy đủ các thao tác không chạm.'),
(9, 3, 'Góc máy hơi trầy nhẹ do dùng ốp, mình có mô tả kỹ trong ảnh số 4.');

-- RATING
INSERT INTO `rating` (product_id, reviewer_id, reviewee_id, rating_value, comment) VALUES
(2, 1, 7, 1, 'Sản phẩm tuyệt vời, giao hàng nhanh chóng!'),
(3, 2, 7, -1, 'Sản phẩm không đúng mô tả, thất vọng.'),
(4, 3, 7, 1, 'Rất hài lòng với chất lượng và dịch vụ bán hàng.'),
(5, 4, 7, -1, 'Phản hồi chậm và sản phẩm bị lỗi nhỏ.'),
(6, 5, 7, 1, 'Giá tốt, chất lượng ổn. Sẽ ủng hộ lần sau.');

INSERT INTO seller_upgrade_request
(user_id, request_at, status, reviewed_at, comments) VALUES
(4, DATE_SUB(NOW(), INTERVAL 3 DAY), 'PENDING', NULL, NULL),
(5, DATE_SUB(NOW(), INTERVAL 2 DAY), 'PENDING', NULL, NULL),
(6, DATE_SUB(NOW(), INTERVAL 1 DAY), 'PENDING', NULL, NULL);

-- INSERT INTO auction_result
-- (product_id, winner_id, final_price, payment_status)
-- VALUES
--     (1, 8, 1500000.00, 'PAID'),
--     (2, 8, 2750000.00, 'PENDING'),
--     (3, 8, 980000.00, 'CANCELED');

-- INSERT INTO auction_order (
--     product_id,
--     seller_id,
--     buyer_id,
--     final_price,
--     status,
--     paid_at,
--     shipping_address,
--     canceled_reason
-- ) VALUES
-- -- 1. Đơn vừa trúng đấu giá, chờ thanh toán
-- (1, 3, 8, 1500000.00, 'WAIT_PAYMENT', NULL,
--  '12 Nguyễn Trãi, Quận 1, TP.HCM', NULL),

-- -- 2. Đơn đã thanh toán, chuẩn bị giao hàng
-- (2, 3, 6, 2750000.00, 'PAID', NOW(),
--  '45 Lê Lợi, Quận Hải Châu, Đà Nẵng', NULL),

-- -- 3. Đơn đang giao hàng
-- (3, 4, 7, 3200000.00, 'ON_DELIVERING', NOW(),
--  '89 Trần Phú, Nha Trang, Khánh Hòa', NULL),

-- -- 4. Đơn đã hoàn tất
-- (4, 5, 8, 9800000.00, 'COMPLETED', NOW(),
--  '120 Phạm Văn Đồng, Cầu Giấy, Hà Nội', NULL),

-- -- 5. Đơn bị hủy (không thanh toán)
-- (5, 6, 8, 4500000.00, 'CANCELED', NULL,
--  '77 Nguyễn Huệ, Quận 1, TP.HCM',
--  'Buyer did not complete payment within allowed time')