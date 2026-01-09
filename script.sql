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
(seller_id, category_id, main_image_url, product_name, current_price, highest_bidder_id, buy_now_price, start_price, price_step, description, end_time, is_auto_renew, bid_count, allow_unrated_bidder) VALUES
(3, 6, "https://pos.nvncdn.com/a135ac-81120/ps/20231121_zPEcyW8KB6.jpeg?v=1700557390", 'iPhone 14 Pro Max', 21100000.00, 1, 25000000.00, 18000000.00, 100000.00, 'Chip A16 Bionic, camera 48MP mạnh mẽ. Tình trạng như mới.', DATE_ADD(NOW(), INTERVAL 5 DAY), 1, 6, 1),

(5, 7, "https://didongmango.com/images/products/2022/11/07/large/2_1667844618.png", 'MacBook Pro M2', 16100000.00, 2, NULL, 10000000.00, 100000.00, 'Laptop MacBook Pro 14 inch M2, RAM 16GB, SSD 512GB.', DATE_ADD(NOW(), INTERVAL 1 DAY), 0, 6, 1),

(3, 9, "https://product.hstatic.net/1000388227/product/ga-010-1a1_67845d1d7c2c4bc4b63a44878c278939_master.png", 'Đồng hồ Casio G-Shock', 2000000.00, 6, 2000000.00, 1000000.00, 50000.00, 'Đồng hồ Casio G-Shock chống sốc, chống nước.', DATE_ADD(NOW(), INTERVAL 7 DAY), 0, 6, 1),

(3, 8, "https://titaniummobile.net/cdn/shop/files/S9TABULTRA.png?v=1690249145", 'Samsung Galaxy Tab S9', 13050000.00, 8, NULL, 10000000.00, 50000.00, 'Máy tính bảng Samsung Galaxy Tab S9 11 inch, AMOLED.', DATE_ADD(NOW(), INTERVAL 3 DAY), 1, 5, 1),

(3, 8, "https://cdn2.cellphones.com.vn/x/media/catalog/product/m/a/may-doc-sach-kindle-paperwhite-5-16gb.png", 'Kindle Paperwhite 5', 3720000.00, 7, 4000000.00, 3000000.00, 20000.00, 'Máy đọc sách Kindle Paperwhite Gen 11th, màn hình 6.8 inch.', DATE_ADD(NOW(), INTERVAL 2 DAY), 1, 5, 1),

(3, 3, "https://file.hstatic.net/200000122283/file/sach-harry-potter-hay_101245eb626b436c96dfc452a362c653_1024x1024.jpg", 'Bộ truyện Harry Potter', 960000.00, 1, 1000000.00, 600000.00, 10000.00, 'Trọn bộ 7 cuốn truyện Harry Potter bản dịch Việt Nam.', DATE_ADD(NOW(), INTERVAL 10 DAY), 1, 5, 1),

(3, 5, "https://media.shoptretho.com.vn/upload/image/product/20170213/lego-city-60127-canh-sat-bien-khoi-bo-khoi-dau-1.png?mode=max&width=900&height=900", 'LEGO City - Đồn cảnh sát', 2250000.00, 1, 2500000.00, 1800000.00, 50000.00, 'Bộ LEGO City Police Station, hơn 1200 mảnh ghép.', DATE_ADD(NOW(), INTERVAL 6 DAY), 0, 5, 1),

(3, 10, "https://cdn.vuahanghieu.com/unsafe/0x900/left/top/smart/filters:quality(90)/https://admin.vuahanghieu.com/upload/product/2023/04/giay-the-thao-nu-nike-air-max-90-gs-pink-foam-white-pink-rise-cv9648-600-mau-hong-size-37-5-642e93a29d3e8-06042023164050.jpg", 'Giày Nike Air Max 90', 2850000.00, 2, 3000000.00, 2000000.00, 50000.00, 'Giày Nike Air Max 90 chính hãng, màu trắng-đen, size 42.', DATE_ADD(NOW(), INTERVAL 4 DAY), 0, 5, 1),

(3, 9, "https://frodos.com.vn/wp-content/uploads/2021/03/Capture-86.png.webp", 'Đồng hồ Rolex Submariner', 141000000.00, 7, NULL, 100000000.00, 1000000.00, 'Đồng hồ Rolex Submariner chính hãng, chống nước 300m.', DATE_ADD(NOW(), INTERVAL 15 DAY), 1, 5, 0),

(4, 6, "https://entel.cdn.modyo.com/uploads/6d1f6b25-500d-4aa4-9d2f-557741432ca5/original/smsung-S23-ultra-black-1.png", 'Samsung Galaxy S23 Ultra', 24150000.00, 1, 26000000.00, 20000000.00, 150000.00, 'Điện thoại Samsung Galaxy S23 Ultra, camera 200MP, Snap 8 Gen 2.', DATE_ADD(NOW(), INTERVAL 8 DAY), 0, 5, 0),

(4, 7, "https://mayxaugiacao.com/wp-content/uploads/2022/09/dell-xps-13-9315-new-mayxaugiacao.jpg", 'Dell XPS 13 9315', 20100000.00, 8, NULL, 16000000.00, 100000.00, 'Laptop Dell XPS 13, Intel Core i7, RAM 16GB, SSD 512GB.', DATE_ADD(NOW(), INTERVAL 6 DAY), 0, 5, 1),

(4, 9, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT_7AaNNNMdVtNXMCCRxQZ0f8vVlD9JJtjT3g&s", 'Apple Watch Series 9', 9550000.00, 8, 10000000.00, 7000000.00, 50000.00, 'Đồng hồ thông minh Apple Watch Series 9 mới nhất.', DATE_ADD(NOW(), INTERVAL 5 DAY), 0, 6, 1),

(4, 10, "https://i5.walmartimages.com/asr/38f5961e-35a8-47e3-90b4-f3ba970ff48a.c05dc8966f3ea968b7a9519e7916dad3.webp?odnHeight=2000&odnWidth=2000&odnBg=FFFFFF", 'Giày Adidas Ultraboost 22', 4100000.00, 2, 4200000.00, 3000000.00, 100000.00, 'Giày chạy bộ Adidas Ultraboost chính hãng.', DATE_ADD(NOW(), INTERVAL 7 DAY), 0, 5, 1),

(4, 3, "https://pbp.vn/wp-content/uploads/2024/12/z6102548207591_9e6ff6ce0114bcca62ebdd022f48ea16.jpg", 'Tiểu thuyết "Búp sen xanh"', 325000.00, 7, 350000.00, 200000.00, 5000.00, 'Tác phẩm nổi tiếng về Bác Hồ của nhà văn Sơn Tùng.', DATE_ADD(NOW(), INTERVAL 9 DAY), 1, 5, 1),

(4, 5, "https://preview.redd.it/my-small-hot-wheels-collection-so-far-what-do-you-guys-v0-gy4gq71x1cic1.jpeg?width=1080&crop=smart&auto=webp&s=fdb0e6b1676d693ca4b9841a0ad409dbd01a0423", 'Hot Wheels Collection', 1730000.00, 4, 1800000.00, 1200000.00, 30000.00, 'Bộ sưu tập 50 chiếc xe Hot Wheels đa dạng.', DATE_ADD(NOW(), INTERVAL 8 DAY), 1, 5, 1),

(5, 8, "https://bachlongstore.vn/vnt_upload/product/11_2023/43543.jpg", 'iPad Air 5 M1', 16600000.00, 1, 18000000.00, 13000000.00, 100000.00, 'Máy tính bảng iPad Air 5, chip M1, hỗ trợ Apple Pencil.', DATE_ADD(NOW(), INTERVAL 4 DAY), 0, 5, 1),

(5, 6, "https://hanhtech.com/wp-content/uploads/2025/02/google-pixel-8-pro-5g-128gb-cu-99-dep-nhu-moi-gia-re-hcm-image.jpg", 'Google Pixel 8 Pro', 23600000.00, 4, 25000000.00, 19000000.00, 100000.00, 'Điện thoại Google Pixel 8 Pro, chip Tensor G3.', DATE_ADD(NOW(), INTERVAL 5 DAY), 0, 5, 1),

(5, 7, "https://dlcdnwebimgs.asus.com/gain/5d3b0e30-c888-481c-8c1c-f45b65c5b560/", 'ASUS VivoBook 15', 14600000.00, 4, 15000000.00, 10000000.00, 100000.00, 'Laptop ASUS VivoBook 15, chip AMD Ryzen 7, RAM 16GB.', DATE_ADD(NOW(), INTERVAL 6 DAY), 0, 5, 1),

(5, 3, "https://bizweb.dktcdn.net/100/413/485/products/z2767223936862-67c037ad49da30dabe0f00ed18faedd1.jpg?v=1631783718543", 'Bộ sách Chúa tể những chiếc nhẫn', 760000.00, 2, 800000.00, 500000.00, 10000.00, 'Trọn bộ 3 cuốn tiểu thuyết kinh điển của J.R.R. Tolkien.', DATE_ADD(NOW(), INTERVAL 12 DAY), 1, 5, 1),

(5, 9, "https://luxewatch.vn/wp-content/uploads/2022/11/1275bb10562b9075c93a19.jpg", 'Đồng hồ Omega Seamaster', 100500000.00, 2, NULL, 80000000.00, 500000.00, 'Đồng hồ Omega Seamaster chính hãng, chống nước 300m.', DATE_ADD(NOW(), INTERVAL 20 DAY), 1, 5, 0);

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
(6, 'https://heomephim.wordpress.com/wp-content/uploads/2013/10/images653383_10.jpg'),
(6, 'https://w0.peakpx.com/wallpaper/573/674/HD-wallpaper-harry-potter-harry-potter-and-the-philosopher-s-stone.jpg'),
(6, 'https://product.hstatic.net/200000654445/product/8934974203223-2_fc054f71e9f646d5b816942807e66dae_master.jpg'),
(7, 'https://product.hstatic.net/200000504579/product/5702016912197_f419205f8f354fbf8f9503bca3d5afcf_master.jpg'),
(7, 'https://cdn.tgdd.vn/Products/Images/10421/258924/do-choi-xe-canh-sat-van-chuyen-toi-pham-lego-city-60276-2_3_11zon.jpg'),
(7, 'https://bizweb.dktcdn.net/100/405/289/products/lego-60319-1.jpg?v=1653667253573'),
(8, 'https://ash.vn/cdn/shop/files/9956114d8211358541a40d1aa6538f25_1318x.jpg?v=1731917675'),
(8, 'https://i5.walmartimages.com/seo/NIKE-Male-Adult-10-Men-CZ5594-100-White-White_a7245a3e-399b-4ee9-9aaa-e8755d62c946.3552041b84c8836d6526ba084f2cfc89.jpeg'),
(8, 'https://sizeer.hu/media/cache/gallery/rc/wqoazpvx/nike-air-max-90-ltr-ferfi-sportcipo-fekete-cz5594-001.jpg'),
(9, 'https://frodos.com.vn/wp-content/uploads/2021/03/Capture-85.jpg.webp'),
(9, 'https://luxewatch.vn/wp-content/uploads/2024/01/Rolex-124060-0001.4.jpg'),
(9, 'https://donghorep.com.vn/wp-content/uploads/2023/07/Dong-ho-Rolex-Submariner-nam-mat-den-Replica-11-EW-Factory-40mm-4-300x300.jpg'),
(10, 'https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/s/a/samsung-s23-ulatra_1__1.png'),
(10, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT0Un0PzmKxKYJdMsyzmT2rukXZoaP8Z9WLbw&s'),
(10, 'https://mobileworld.com.vn/uploads/news/03_2024/so-sanh-samsung-s23-ultra-my-va-samsung-s23-ultra-viet-nam-co-gi-khac-9.pngg'),
(11, 'https://bizweb.dktcdn.net/thumb/grande/100/501/863/products/ab52f36b4079fe976178ca4e93fec358-d24a71ff-fe78-42e1-9bf8-189a45f32b56-5644e902-be5d-4643-95f7-34671b848cb8.jpg?v=1699797833880'),
(11, 'https://no1computer.vn/images/products/2023/11/04/large/dell-xps-9315-i7-ram-32gb-5-_1699087050.jpg'),
(11, 'https://laptop360.net/wp-content/uploads/2022/12/Dell-xps-9315-1.jpg'),
(12, 'https://pcoutlet.com/wp-content/uploads/apple-watch-series-9.jpg'),
(12, 'https://ddfndelma2gpn.cloudfront.net/color/220/apple_watch_series_9_silver.webp'),
(12, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ0SlKtQv_sik_3Un8omlG29BPplnaCL87noQ&s'),
(13, 'https://bizweb.dktcdn.net/thumb/1024x1024/100/347/092/products/ultraboost-22-djen-gz0127-01-standard.jpg'),
(13, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRGauAvQidQY1lxNp1hfV4-bmb3NWiCjXM9uA&s'),
(13, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRZtR82Qhw1r8sMD4h9Gm-YZSWsHJKBY1hMGw&s'),
(14, 'https://voiz-prod.s3-wewe.cloud.cmctelecom.vn/uploads/avatar/filename/435120/e3b54a7f2c44ff46.jpg'),
(14, 'https://c1vietnamcuba.badinh.edu.vn/UploadFinder/images/2025/5/12/12052025181331_3.png'),
(14, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTKc__Cu9LPKt2WE0URr-cVatw9JuLUp0tKDQ&s'),
(15, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRPRyUoUmJboGtutztztzZGrDNaYJiDuiRgag&s'),
(15, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRr_b3jcrY0Lwcneg7oelEDLqU48FX9lGpuKA&s'),
(15, 'https://i.ebayimg.com/images/g/2AIAAOSwWkVksviY/s-l1200.jpg'),
(16, 'https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/i/p/ipad-air-5.png'),
(16, 'https://cdn.tgdd.vn/Products/Images/522/248096/ipad-air-5-wifi-pink-thumb-600x600.jpg'),
(16, 'https://cdn.viettablet.com/images/detailed/62/ipad-air-5.png'),
(17, 'https://cdn.tgdd.vn/Products/Images/42/307188/google-pixel-8-pro-600x600.jpg'),
(17, 'https://www.didongmy.com/vnt_upload/product/10_2023/pixel8/thumbs/600_crop_google-pixel-8-pro-blue-thumb-didongmy-600x600.jpg'),
(17, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRECUq0rGqvlUY7OvyNE2Lfv6vhSFo_JXqDmA&s'),
(18, 'https://m.media-amazon.com/images/I/71C-Yi26fZL.jpg'),
(18, 'https://i5.walmartimages.com/seo/ASUS-VivoBook-15-Laptop-15-6-Display-AMD-Ryzen-5-4600H-CPU-AMD-Radeon-GPU-8GB-RAM-256GB-SSD-Windows-11-Home-Quiet-Blue-M1502IA-AS51_c7503058-f613-47f0-8df1-95067909547b.7a015102bb13ae5d19def83f30fa9460.jpeg'),
(18, 'https://banlaptopcu.vn/Uploads/images/product/item/asus-vivobook-15-a512fl-core-i5-8265u-ram-8gb-ssd/fc43a58f-6047-4bbb-967e-dbbb150536b9.jpg'),
(19, 'https://thcskhesanhhh.quangtri.edu.vn/upload/32308/20231212/chua-te-cua-nhung-chiec-nhan-2_19f2a.jpg'),
(19, 'https://static.oreka.vn/800-800_6350e83c-55fa-4dbc-82a8-9ecee70788fd'),
(19, 'https://static.oreka.vn/800-800_8923bcd9-e44a-4171-b193-6224f79ebfe6'),
(20, 'https://www.coveted.com/_next/image?url=https%3A%2F%2Fassets.coveted.com%2Fwatches%2Fimages%2Funmapped%2Fv1%2Fgenerated%2Fu2net_final_1729243155_8a86d54bff_omega-seamaster-diver-300m-co-axial-master-chronometer-42-mm-21022422003002-c1e612.png&w=3840&q=60'),
(20, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQS9xFIUuPQSPcJZKgIfz_xNFujJn42hB-mQA&s'),
(20, 'https://idwx.co/cdn/shop/files/Omega-Seamaster-Diver-300-Blue-_1.jpg?v=1732260853');


-- WATCHLIST (dữ liệu đa dạng, seller không thích sản phẩm của chính mình)
INSERT INTO `watch_list` (user_id, product_id) VALUES
(1, 2),
(1, 4),
(1, 5),
(1, 10),
(2, 1),
(2, 3),
(2, 6),
(2, 8),
(3, 11),
(3, 12),
(3, 13),
(4, 2),
(4, 5),
(4, 7),
(4, 9),
(5, 1),
(5, 4),
(5, 8),
(5, 14),
(6, 2),
(6, 3),
(6, 10),
(6, 15),
(7, 5),
(7, 6),
(7, 12),
(7, 16),
(8, 7),
(8, 9),
(8, 13),
(8, 17);

-- PRODUCT QUESTION
INSERT INTO `product_question` (product_id, question_user_id, question_text) VALUES
-- Product 1 (iPhone 14 Pro Max) - seller_id = 3
(1, 1, 'Máy có bị móp méo hay trầy xước màn hình không để mình biết đường đặt giá?'),
(1, 2, 'Pin còn bao nhiêu % và đã thay linh kiện gì chưa shop?'),
(1, 4, 'Nếu thắng đấu giá vào tối nay, sáng mai mình qua lấy máy trực tiếp được không?'),
-- Product 2 (MacBook Pro M2) - seller_id = 5
(2, 2, 'Chu kỳ sạc của pin MacBook này là bao nhiêu lần rồi bạn?'),
(2, 1, 'Máy có iCloud ẩn hay bị MDM không, mình cần check kỹ trước khi bid?'),
(2, 4, 'Bàn phím có bị mòn hoặc bấm nhạt ở keys nào không?'),
-- Product 3 (Đồng hồ Casio G-Shock) - seller_id = 3
(3, 2, 'Mặt kính G-Shock có vết xước nào không? Shop có ảnh chụp góc nghiêng không?'),
(3, 1, 'Nếu giá cuối thấp hơn kỳ vọng shop có hủy kèo không?'),
(3, 6, 'Dây đeo có còn tốt không, có bị ỉu hay hỏng không?'),
-- Product 4 (Samsung Galaxy Tab S9) - seller_id = 3
(4, 1, 'Bút S-Pen kèm theo máy là zin hay bút linh kiện vậy shop?'),
(4, 2, 'Góc máy có bị cấn không? Cho mình xin thêm ảnh các góc để tự tin đấu giá.'),
(4, 5, 'Màn hình AMOLED có burn-in nào không, mình rất quan tâm điểm này?'),
-- Product 5 (Kindle Paperwhite 5) - seller_id = 3
(5, 2, 'Màn hình e-ink có vết trầy hay nấm mốc nào không?'),
(5, 7, 'Thời lượng pin được khoảng bao nhiêu ngày khi dùng bình thường?'),
(5, 8, 'Máy còn full warranty hay đã hết hạn?'),
-- Product 6 (Bộ truyện Harry Potter) - seller_id = 3
(6, 1, 'Cuốn sách nào trong bộ đã có trang bị rách hoặc bị cắt?'),
(6, 8, 'Các cuốn có tông màu giống nhau không hay khác nhau?'),
(6, 4, 'Shop có thể gửi trước được không để mình kiểm tra?'),
-- Product 7 (LEGO City - Đồn cảnh sát) - seller_id = 3
(7, 2, 'Tất cả các mảnh ghép có đầy đủ không, có thiếu gì không?'),
(7, 4, 'Hộp có hoàn nguyên vẹn không hay đã bị vỡ, in ấn mờ?'),
(7, 6, 'Có tất cả hướng dẫn lắp ráp không?'),
-- Product 8 (Giày Nike Air Max 90) - seller_id = 3
(8, 1, 'Size này chạy chuẩn không hay chạy nhỏ/lớn?'),
(8, 7, 'Đế có bị mòn chỗ nào không?'),
(8, 5, 'Thẻ tag nguyên bản hay là fake?'),
-- Product 9 (Đồng hồ Rolex Submariner) - seller_id = 3
(9, 2, 'Đây là fake hay authentic, có chứng chỉ không?'),
(9, 6, 'Chạy cơ hay quartz, bảo hành bao lâu?'),
-- Product 10 (Samsung Galaxy S23 Ultra) - seller_id = 4
(10, 1, 'Máy có bị vặn ốc lên xung quanh không, tồn tại dấu hiệu sửa chữa?'),
(10, 2, 'Loa speaker nghe có bị méo không?'),
(10, 5, 'Camera có dust hoặc water spots không?'),
(10, 7, 'Có kèm hộp zin và cáp sạc không?'),
-- Product 11 (Dell XPS 13 9315) - seller_id = 4
(11, 1, 'Màn hình Oled có burn-in không?'),
(11, 6, 'Bàn phím bao nhiêu lần gõ rồi, còn bền không?'),
(11, 8, 'Core i7 thế hệ mấy, Iris Xe graphics có mạnh không?'),
-- Product 12 (Apple Watch Series 9) - seller_id = 4
(12, 2, 'Dây đeo có kèm theo mấy dây, color nào?'),
(12, 7, 'Pin còn bao nhiêu health, dùng bao lâu?'),
(12, 1, 'Có lưu GPS + Cellular hay chỉ GPS?'),
(12, 8, 'Mặt kính sapphire có xước không?'),
-- Product 13 (Giày Adidas Ultraboost 22) - seller_id = 4
(13, 1, 'Boost midsole còn bao nhiêu độ độc lập mềm mại?'),
(13, 6, 'Lưỡi size tag còn rõ không hay bị mờ?'),
(13, 2, 'Đã từng giặt hoặc dùng nhiều lần chưa?'),
-- Product 14 (Tiểu thuyết "Búp sen xanh") - seller_id = 4
(14, 1, 'Trang sách có bị vàng hoặc ẩm mốc không?'),
(14, 7, 'Bìa cứng hay mềm, in sắc nét không?'),
(14, 5, 'Đây là lần thứ mấy phát hành, rare không?'),
-- Product 15 (Hot Wheels Collection) - seller_id = 4
(15, 2, 'Tất cả xe còn trong vỏ đựng hoặc đã mở?'),
(15, 8, 'Có model hiếm hoặc limited edition nào không?'),
(15, 1, 'Xe nào bị rust hoặc trầy xước?'),
-- Product 16 (iPad Air 5 M1) - seller_id = 5
(16, 1, 'Màn hình Liquid Retina có dead pixels không?'),
(16, 6, 'Đã dùng bao lâu, pin giảm bao nhiêu % rồi?'),
(16, 2, 'Face ID hoạt động bình thường không hay bị lỗi?'),
(16, 7, 'Có Air 2 kèm theo không?'),
-- Product 17 (Google Pixel 8 Pro) - seller_id = 5
(17, 1, 'Camera có scratches hoặc dust không?'),
(17, 4, 'Pin health còn lại bao nhiêu?'),
(17, 6, 'Tensor G3 chạy mượt mà không, chơi game có lag?'),
-- Product 18 (ASUS VivoBook 15) - seller_id = 5
(18, 2, 'SSD 512GB hay 256GB, là SSD hay HDD?'),
(18, 7, 'Ryzen 7 thế hệ mấy, hiệu năng như thế nào?'),
(18, 1, 'Màn hình IPS 120Hz có bị vàng không?'),
-- Product 19 (Bộ sách Chúa tể những chiếc nhẫn) - seller_id = 5
(19, 4, 'Tất cả 3 cuốn đều bìa cứng nguyên bản không?'),
(19, 8, 'Có tặng bookmark hoặc phụ kiện nào không?'),
(19, 1, 'In sắc nét không, không có trang bị lỗi in?'),
-- Product 20 (Đồng hồ Omega Seamaster) - seller_id = 5
(20, 2, 'Có Full Set box, card bảo hành không?'),
(20, 6, 'Serial number khớp với authentication card không?'),
(20, 7, 'Dây kim loại có bị kỳ hạn lỏng lẻo không?');

-- PRODUCT ANSWER
INSERT INTO `product_answer` (question_id, answer_user_id, answer_text) VALUES
(1, 3, 'Màn hình đẹp không vết xước, thân máy có một vết dăm nhỏ ở cạnh dưới, không ảnh hưởng thẩm mỹ.'),
(2, 3, 'Pin zin 92%, cam kết chưa qua sửa chữa, nếu sai shop đền gấp đôi số tiền đấu thắng.'),
(3, 3, 'Hoàn toàn được bạn nhé, bạn có thể thanh toán và nhận máy tại showroom sau khi phiên đấu kết thúc.'),
(4, 5, 'Pin mới sạc 45 lần, hiệu suất còn 100%, bạn yên tâm bid giá cao nhé.'),
(5, 5, 'Cam kết máy sạch, không MDM, không iCloud, bao test tại chỗ khi nhận hàng.'),
(6, 5, 'Full box sạc cáp theo máy, tặng kèm túi chống sốc cho người thắng đấu giá giá tốt.'),
(7, 3, 'Mặt kính không vết xước vì mình dùng rất kỹ, đã dán cường lực sẵn.'),
(8, 3, 'Mình đã đăng tải thêm 3 ảnh chụp macro các góc cạnh trong phần mô tả, bạn xem qua nhé.'),
(9, 3, 'S-Pen theo máy là hàng zin, hỗ trợ đầy đủ các thao tác không chạm.'),
(10, 3, 'Màn hình AMOLED hoàn toàn không burn-in, shop bảo hành 6 tháng cho điểm này.'),
(11, 3, 'Dây đeo đàn hồi tốt, không bị ỉu hoặc hỏng.'),
(12, 5, 'Dây được sạch sẽ không bị ỉu, dây zin không thay thế.'),
(13, 5, 'Màn hình e-ink sạch sẽ hoàn toàn không vết trầy, đã dán film bảo vệ.'),
(14, 5, 'Pin dùng bình thường được 3-4 tuần, rất bền với lối sử dụng vừa phải.'),
(15, 5, 'Máy còn warranty 2 năm từ ngày mua, shop có giấy tờ bảo hành.'),
(16, 5, 'Cuốn 1 và 2 nguyên vẹn, cuốn 3 cạnh trên có 1-2 vết nhỏ không ảnh hưởng đọc.'),
(17, 5, 'Ngoài là trang bị đẹp, các cuốn sách in rất sắc nét và đẹp mắt.'),
(18, 3, 'Tất cả mảnh ghép đầy đủ 100%, mình đã kiểm kỹ trước khi bán.'),
(19, 3, 'Hộp nguyên vẹn không hỏng, in ấn sắc nét, còn rất đẹp.'),
(20, 3, 'Có tất cả hướng dẫn lắp ráp bằng tiếng Anh, rất chi tiết từng bước.'),
(21, 3, 'Đế Nike Air Max này vẫn còn tốt, mình đã bảo quản tốt.'),
(22, 3, 'Thẻ tag hoàn toàn là asli Nike, không fake. Shop cam kết 100%.'),
(23, 5, 'Đây là authentic 100%, có certificate từ cửa hàng chính hãng chứng thực.'),
(24, 5, 'Đó là cơ tự động, bảo hành 5 năm từ chính hãng.'),
(25, 4, 'Máy được bảo quản tốt, không dấu hiệu sửa chữa hay tháo vỏ.'),
(26, 4, 'Loa nghe rất to và rõ ràng, chắc chắn không méo.'),
(27, 4, 'Camera sạch sẽ hoàn toàn, không một chút dust hay water spots.'),
(28, 4, 'Có kèm hộp zin chính hãng và cáp sạc USB-C original.'),
(29, 4, 'Màn hình OLED không burn-in, bảo hành 2 năm.'),
(30, 4, 'Bàn phím được dùng đến 80%, còn rất bền, phím nhạy bình thường.'),
(31, 4, 'Core i7-1355U với Iris Xe Graphics, hiệu năng rất tốt cho công việc văn phòng.'),
(32, 4, 'Có kèm 2 dây: 1 dây silicone đen và 1 dây vải thể thao xanh.'),
(33, 4, 'Pin health còn 89%, dùng 1 ngày được 1 ngày rưỡi bình thường.'),
(34, 4, 'Là bản GPS + Cellular, có eSIM sẵn.'),
(35, 4, 'Mặt kính sapphire không xước, cực kỳ sạch sẽ.'),
(36, 4, 'Boost midsole còn rất mềm mại và spongy, không cứng chút nào.'),
(37, 4, 'Lưỡi size tag còn rõ nét, không bị mờ, đây là hàng zin 100%.'),
(38, 4, 'Giày mới, chưa giặt lần nào, dùng bình thường 5-6 lần.'),
(39, 4, 'Trang sách hoàn toàn sạch sẽ, không bị vàng hay ẩm mốc.'),
(40, 4, 'Bìa cứng bởi Nhà xuất bản Hội Nhà văn, in rất sắc nét và đẹp.'),
(41, 4, 'Đây là tái bản lần 5, không phải limited nhưng chất lượng tuyệt vời.'),
(42, 4, 'Khoảng 70% xe còn trong vỏ đựng original, 30% đã mở để xem.'),
(43, 4, 'Có 3 model rare: Fast & Furious Skyline, Zamac Gold X, và Custom Chevy.'),
(44, 4, 'Một vài xe có trầy nhẹ từ việc lưu trữ, không ảnh hưởng đến giá trị sưu tập.'),
(45, 5, 'Màn hình Liquid Retina hoàn toàn không dead pixels, kiểm tra kỹ rồi.'),
(46, 5, 'Dùng 3 tháng, pin health còn 96%, dùng bình thường được 8-10 tiếng.'),
(47, 5, 'Face ID hoạt động hoàn hảo, nhận diện rất nhanh.'),
(48, 5, 'Có kèm Apple Pencil Gen 2, pin còn 85%, vừa sạc đầy.'),
(49, 5, 'Camera không có scratches hoặc dust, sạch sẽ hoàn toàn.'),
(50, 5, 'Pin health còn 94%, dùng bình thường được 1 ngày rưỡi.'),
(51, 5, 'Tensor G3 chạy cực mượt, chơi game AAA không lag.'),
(52, 5, 'Là SSD 512GB M.2 NVMe, tốc độ read/write rất nhanh.'),
(53, 5, 'Ryzen 7 5700U, 8 cores 16 threads, hiệu năng rất mạnh.'),
(54, 5, 'Màn hình IPS 120Hz hoàn toàn không bị vàng, góc nhìn rất tốt.'),
(55, 5, 'Tất cả 3 cuốn đều bìa cứng nguyên bản, không phải soft cover.'),
(56, 5, 'Tặng 3 cái bookmark da và 1 bộ hẳn chân đứng để sách, rất hữu ích.'),
(57, 5, 'In rất sắc nét, không trang nào bị lỗi in, chất lượng xuất sắc.'),
(58, 5, 'Có Full Set box, card bảo hành chính hãng và cả phiếu mua hàng.'),
(59, 5, 'Serial number khớp 100% với authentication card, hoàn toàn chính hãng.'),
(60, 5, 'Dây kim loại vẫn chắc chắn, không lỏng lẻo hay bị kỳ hạn, còn như mới.');

-- RATING
INSERT INTO `rating` (product_id, reviewer_id, reviewee_id, rating_value, comment) VALUES
(1, 1, 3, 1, 'Sản phẩm đúng như mô tả, rất hài lòng với chất lượng.'),
(2, 1, 7, 1, 'Sản phẩm tuyệt vời, giao hàng nhanh chóng!'),
(3, 2, 7, -1, 'Sản phẩm không đúng mô tả, thất vọng.'),
(4, 3, 7, 1, 'Rất hài lòng với chất lượng và dịch vụ bán hàng.'),
(5, 4, 7, -1, 'Phản hồi chậm và sản phẩm bị lỗi nhỏ.'),
(6, 5, 7, 1, 'Giá tốt, chất lượng ổn. Sẽ ủng hộ lần sau.');

-- BID HISTORY (Đấu giá tự động với logic mức giá tự động)
-- Product 1 (iPhone 14 Pro Max) - start_price: 18,000,000, price_step: 100,000
INSERT INTO `bid` (product_id, bidder_id, bid_price, max_auto_price, bid_at) VALUES
-- Bidder 2 đặt tối đa 19tr, giá khởi điểm 18tr
(1, 2, 18000000, 19000000, '2026-01-02 09:00:00'),
-- Bidder 4 đặt tối đa 20tr, hệ thống nâng Bidder 2 lên 19.1tr, rồi ghi bid cho Bidder 4 ở 19.1tr
(1, 4, 19100000, 20000000, '2026-01-02 09:05:00'),
-- Bidder 6 đặt tối đa 19.5tr, thấp hơn 20tr của Bidder 4, hệ thống nâng Bidder 4 lên 19.6tr
(1, 4, 19600000, 20000000, '2026-01-02 09:10:00'),
-- Bidder 8 đặt tối đa 21tr, vượt 20tr, hệ thống nâng Bidder 8 lên 20.1tr
(1, 8, 20100000, 21000000, '2026-01-02 09:15:00'),
-- Bidder 7 đặt tối đa 21tr không vượt được giá tối đa của Bidder 8 là 21tr, hệ thống nâng Bidder 8 lên 21tr
(1, 8, 21000000, 21000000, '2026-01-02 09:18:00'),
-- Bidder 1 đặt tối đa 22tr, vượt 21tr của Bidder 8, hệ thống nâng Bidder 1 lên 21.1tr
(1, 1, 21100000, 22000000, '2026-01-02 09:20:00');

-- Product 2: MacBook Pro M2 (Start: 10tr, Step: 100k, Buy Now: NULL)
INSERT INTO `bid` (product_id, bidder_id, bid_price, max_auto_price, bid_at) VALUES
(2, 4, 10000000, 12000000, '2026-01-02 10:00:00'),
(2, 6, 12100000, 13000000, '2026-01-02 10:05:00'),
(2, 4, 13100000, 15000000, '2026-01-02 10:10:00'),
(2, 4, 14000000, 15000000, '2026-01-02 10:15:00'), -- Chặn người đặt 14tr
(2, 1, 15100000, 16000000, '2026-01-02 10:20:00'),
(2, 2, 16100000, 17000000, '2026-01-02 10:25:00');

-- Product 3: Casio G-Shock (Start: 1tr, Step: 50k, Buy Now: 2tr)
INSERT INTO `bid` (product_id, bidder_id, bid_price, max_auto_price, bid_at) VALUES
(3, 1, 1000000, 1200000, '2026-01-02 11:00:00'),
(3, 2, 1250000, 1500000, '2026-01-02 11:05:00'),
(3, 2, 1400000, 1500000, '2026-01-02 11:10:00'), -- Chặn người đặt 1.4tr
(3, 4, 1550000, 1800000, '2026-01-02 11:15:00'),
(3, 6, 1850000, 2000000, '2026-01-02 11:20:00'),
(3, 6, 2000000, 2000000, '2026-01-02 11:25:00'); -- Chạm giá mua ngay

-- Product 4: Samsung Galaxy Tab S9 (Start: 10tr, Step: 50k, Buy Now: NULL)
INSERT INTO `bid` (product_id, bidder_id, bid_price, max_auto_price, bid_at) VALUES
(4, 2, 10000000, 11000000, '2026-01-02 12:00:00'),
(4, 5, 11050000, 12000000, '2026-01-02 12:05:00'),
(4, 5, 11800000, 12000000, '2026-01-02 12:10:00'), -- Chặn người đặt 11.8tr
(4, 7, 12050000, 13000000, '2026-01-02 12:15:00'),
(4, 8, 13050000, 14000000, '2026-01-02 12:20:00');

-- Product 5: Kindle Paperwhite 5 (Start: 3tr, Step: 20k, Buy Now: 4tr)
INSERT INTO `bid` (product_id, bidder_id, bid_price, max_auto_price, bid_at) VALUES
(5, 4, 3000000, 3200000, '2026-01-02 13:00:00'),
(5, 6, 3220000, 3300000, '2026-01-02 13:05:00'),
(5, 4, 3320000, 3500000, '2026-01-02 13:10:00'),
(5, 1, 3520000, 3700000, '2026-01-02 13:15:00'),
(5, 7, 3720000, 3800000, '2026-01-02 13:20:00');

-- Product 6: Bộ truyện Harry Potter (Start: 600k, Step: 10k, Buy Now: 1tr)
INSERT INTO `bid` (product_id, bidder_id, bid_price, max_auto_price, bid_at) VALUES
(6, 8, 600000, 700000, '2026-01-02 14:00:00'),
(6, 7, 710000, 800000, '2026-01-02 14:05:00'),
(6, 4, 810000, 900000, '2026-01-02 14:10:00'),
(6, 2, 910000, 950000, '2026-01-02 14:15:00'),
(6, 1, 960000, 1000000, '2026-01-02 14:20:00');

-- Product 7: LEGO City (Start: 1.8tr, Step: 50k, Buy Now: 2.5tr)
INSERT INTO `bid` (product_id, bidder_id, bid_price, max_auto_price, bid_at) VALUES
(7, 2, 1800000, 2000000, '2026-01-02 15:00:00'),
(7, 4, 2050000, 2100000, '2026-01-02 15:05:00'),
(7, 6, 2150000, 2200000, '2026-01-02 15:10:00'),
(7, 6, 2200000, 2200000, '2026-01-02 15:15:00'), -- Chạm trần tự động
(7, 1, 2250000, 2400000, '2026-01-02 15:20:00');

-- Product 8: Giày Nike Air Max 90 (Start: 2tr, Step: 50k, Buy Now: 3tr)
INSERT INTO `bid` (product_id, bidder_id, bid_price, max_auto_price, bid_at) VALUES
(8, 5, 2000000, 2200000, '2026-01-02 16:00:00'),
(8, 7, 2250000, 2400000, '2026-01-02 16:05:00'),
(8, 8, 2450000, 2600000, '2026-01-02 16:10:00'),
(8, 1, 2650000, 2800000, '2026-01-02 16:15:00'),
(8, 2, 2850000, 3000000, '2026-01-02 16:20:00');

-- Product 9: Rolex Submariner (Start: 100tr, Step: 1tr, Buy Now: NULL)
INSERT INTO `bid` (product_id, bidder_id, bid_price, max_auto_price, bid_at) VALUES
(9, 1, 100000000, 110000000, '2026-01-02 17:00:00'),
(9, 2, 111000000, 120000000, '2026-01-02 17:05:00'),
(9, 4, 121000000, 130000000, '2026-01-02 17:10:00'),
(9, 6, 131000000, 140000000, '2026-01-02 17:15:00'),
(9, 7, 141000000, 150000000, '2026-01-02 17:20:00');

-- Product 10: Samsung Galaxy S23 Ultra (Start: 20tr, Step: 150k, Buy Now: 26tr)
INSERT INTO `bid` (product_id, bidder_id, bid_price, max_auto_price, bid_at) VALUES
(10, 2, 20000000, 21000000, '2026-01-02 18:00:00'),
(10, 5, 21150000, 22000000, '2026-01-02 18:05:00'),
(10, 7, 22150000, 23000000, '2026-01-02 18:10:00'),
(10, 8, 23150000, 24000000, '2026-01-02 18:15:00'),
(10, 1, 24150000, 25500000, '2026-01-02 18:20:00');

-- Product 11: Dell XPS 13 9315 (Start: 16tr, Step: 100k, Buy Now: NULL)
INSERT INTO `bid` (product_id, bidder_id, bid_price, max_auto_price, bid_at) VALUES
(11, 1, 16000000, 18000000, '2026-01-02 19:00:00'),
(11, 2, 18100000, 19000000, '2026-01-02 19:05:00'),
(11, 6, 19100000, 20000000, '2026-01-02 19:10:00'),
(11, 6, 19500000, 20000000, '2026-01-02 19:15:00'), -- Chặn người đặt 19.5tr
(11, 8, 20100000, 21000000, '2026-01-02 19:20:00');

-- Product 12: Apple Watch Series 9 (Start: 7tr, Step: 50k, Buy Now: 10tr)
INSERT INTO `bid` (product_id, bidder_id, bid_price, max_auto_price, bid_at) VALUES
(12, 5, 7000000, 8000000, '2026-01-02 19:30:00'),
(12, 7, 8050000, 8500000, '2026-01-02 19:35:00'),
(12, 1, 8550000, 9000000, '2026-01-02 19:40:00'),
(12, 2, 9050000, 9500000, '2026-01-02 19:45:00'),
(12, 2, 9500000, 9500000, '2026-01-02 19:50:00'), -- Chạm trần tự động của B2
(12, 8, 9550000, 10000000, '2026-01-02 19:55:00');

-- Product 13: Giày Adidas Ultraboost 22 (Start: 3tr, Step: 100k, Buy Now: 4.2tr)
INSERT INTO `bid` (product_id, bidder_id, bid_price, max_auto_price, bid_at) VALUES
(13, 6, 3000000, 3500000, '2026-01-02 20:00:00'),
(13, 4, 3600000, 3800000, '2026-01-02 20:05:00'),
(13, 4, 3700000, 3800000, '2026-01-02 20:10:00'),
(13, 1, 3900000, 4000000, '2026-01-02 20:15:00'),
(13, 2, 4100000, 4200000, '2026-01-02 20:20:00');

-- Product 14: Tiểu thuyết "Búp sen xanh" (Start: 200k, Step: 5k, Buy Now: 350k)
INSERT INTO `bid` (product_id, bidder_id, bid_price, max_auto_price, bid_at) VALUES
(14, 8, 200000, 250000, '2026-01-02 20:30:00'),
(14, 5, 255000, 280000, '2026-01-02 20:35:00'),
(14, 1, 285000, 300000, '2026-01-02 20:40:00'),
(14, 2, 305000, 320000, '2026-01-02 20:45:00'),
(14, 7, 325000, 350000, '2026-01-02 20:50:00');

-- Product 15: Hot Wheels Collection (Start: 1.2tr, Step: 30k, Buy Now: 1.8tr)
INSERT INTO `bid` (product_id, bidder_id, bid_price, max_auto_price, bid_at) VALUES
(15, 2, 1200000, 1400000, '2026-01-02 21:00:00'),
(15, 6, 1430000, 1500000, '2026-01-02 21:05:00'),
(15, 8, 1530000, 1600000, '2026-01-02 21:10:00'),
(15, 1, 1630000, 1700000, '2026-01-02 21:15:00'),
(15, 4, 1730000, 1800000, '2026-01-02 21:20:00');

-- Product 16: iPad Air 5 M1 (Start: 13tr, Step: 100k, Buy Now: 18tr)
INSERT INTO `bid` (product_id, bidder_id, bid_price, max_auto_price, bid_at) VALUES
(16, 7, 13000000, 14500000, '2026-01-02 21:30:00'),
(16, 2, 14600000, 15500000, '2026-01-02 21:35:00'),
(16, 4, 15600000, 16500000, '2026-01-02 21:40:00'),
(16, 4, 16000000, 16500000, '2026-01-02 21:45:00'),
(16, 1, 16600000, 17500000, '2026-01-02 21:50:00');

-- Product 17: Google Pixel 8 Pro (Start: 19tr, Step: 100k, Buy Now: 25tr)
INSERT INTO `bid` (product_id, bidder_id, bid_price, max_auto_price, bid_at) VALUES
(17, 8, 19000000, 20500000, '2026-01-02 22:00:00'),
(17, 6, 20600000, 21500000, '2026-01-02 22:05:00'),
(17, 2, 21600000, 22500000, '2026-01-02 22:10:00'),
(17, 1, 22600000, 23500000, '2026-01-02 22:15:00'),
(17, 4, 23600000, 24500000, '2026-01-02 22:20:00');

-- Product 18: ASUS VivoBook 15 (Start: 10tr, Step: 100k, Buy Now: 15tr)
INSERT INTO `bid` (product_id, bidder_id, bid_price, max_auto_price, bid_at) VALUES
(18, 5, 10000000, 11500000, '2026-01-02 22:30:00'),
(18, 7, 11600000, 12500000, '2026-01-02 22:35:00'),
(18, 1, 12600000, 13500000, '2026-01-02 22:40:00'),
(18, 2, 13600000, 14500000, '2026-01-02 22:45:00'),
(18, 4, 14600000, 15000000, '2026-01-02 22:50:00');

-- Product 19: Bộ sách Chúa tể những chiếc nhẫn (Start: 500k, Step: 10k, Buy Now: 800k)
INSERT INTO `bid` (product_id, bidder_id, bid_price, max_auto_price, bid_at) VALUES
(19, 1, 500000, 600000, '2026-01-02 23:00:00'),
(19, 4, 610000, 650000, '2026-01-02 23:05:00'),
(19, 1, 660000, 700000, '2026-01-02 23:10:00'),
(19, 8, 710000, 750000, '2026-01-02 23:15:00'),
(19, 2, 760000, 800000, '2026-01-02 23:20:00');

-- Product 20: Omega Seamaster (Start: 80tr, Step: 500k, Buy Now: NULL)
INSERT INTO `bid` (product_id, bidder_id, bid_price, max_auto_price, bid_at) VALUES
(20, 6, 80000000, 85000000, '2026-01-02 23:30:00'),
(20, 4, 85500000, 90000000, '2026-01-02 23:35:00'),
(20, 8, 90500000, 95000000, '2026-01-02 23:40:00'),
(20, 1, 95500000, 100000000, '2026-01-02 23:45:00'),
(20, 2, 100500000, 110000000, '2026-01-02 23:50:00');

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