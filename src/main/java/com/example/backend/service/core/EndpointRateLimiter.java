package com.example.backend.service.core;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class EndpointRateLimiter {

  public Bandwidth getLimitFor(String path) {

    // AUTH login – hạn chế brute force
    if (path.startsWith("/api/auth/login")) {
      return Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1))); // 5 req/min
    }

    // Auction results – thường load real-time nhưng nhẹ
    if (path.startsWith("/api/auction-results")) {
      return Bandwidth.classic(120, Refill.intervally(120, Duration.ofMinutes(1))); // 120 req/min
    }

    // Bidding – nhiều thao tác, nhưng tránh spam bot
    if (path.startsWith("/api/bids")) {
      return Bandwidth.classic(60, Refill.intervally(60, Duration.ofMinutes(1))); // 60 req/min
    }

    // User profile – thường xem/đổi thông tin
    if (path.startsWith("/api/user-profile")) {
      return Bandwidth.classic(40, Refill.intervally(40, Duration.ofMinutes(1))); // 40 req/min
    }

    // Categories – thường load danh mục
    if (path.startsWith("/api/categories")) {
      return Bandwidth.classic(200, Refill.intervally(200, Duration.ofMinutes(1))); // 200 req/min
    }

    // Chat – thao tác liên tục nhưng tránh flood
    if (path.startsWith("/api/chat")) {
      return Bandwidth.classic(80, Refill.intervally(80, Duration.ofMinutes(1))); // 80 req/min
    }

    // Payments – thao tác quan trọng → hạn chế
    if (path.startsWith("/api/payments/momo")) {
      return Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1))); // 10 req/min
    }

    // Orders – tạo / xem đơn hàng
    if (path.startsWith("/api/orders")) {
      return Bandwidth.classic(40, Refill.intervally(40, Duration.ofMinutes(1))); // 40 req/min
    }

    // Products – xem sản phẩm (traffic nhiều)
    if (path.startsWith("/api/products")) {
      return Bandwidth.classic(200, Refill.intervally(200, Duration.ofMinutes(1))); // 200 req/min
    }

    // Ratings – đánh giá sản phẩm
    if (path.startsWith("/api/ratings")) {
      return Bandwidth.classic(30, Refill.intervally(30, Duration.ofMinutes(1))); // 30 req/min
    }

    // Users – quản lý user
    if (path.startsWith("/api/users")) {
      return Bandwidth.classic(50, Refill.intervally(50, Duration.ofMinutes(1))); // 50 req/min
    }

    // Watch list – thêm / xoá / lấy danh sách
    if (path.startsWith("/api/watch-list")) {
      return Bandwidth.classic(60, Refill.intervally(60, Duration.ofMinutes(1))); // 60 req/min
    }

    // Default cho các endpoint khác
    return Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1)));
  }
}

