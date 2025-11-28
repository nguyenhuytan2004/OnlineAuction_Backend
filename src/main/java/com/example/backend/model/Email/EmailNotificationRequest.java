package com.example.backend.model.Email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailNotificationRequest {
    // Thông tin Người nhận
    private Integer recipientUserId;
    private String recipientEmail;
    private String recipientName;

    // Thông tin Sản phẩm
    private Integer productId;
    private String productName;

    // Thông tin người gửi
    private Integer senderUserId;
    private String senderName;

    // Nội dung cần thiết
    private String subject;
    private String messageContent;

    // Đường dẫn URL (Link tới câu hỏi)
    private String deepLinkPath;
}