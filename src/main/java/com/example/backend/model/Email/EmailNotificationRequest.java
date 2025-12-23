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

    public enum EmailType {
        BID_SUCCESS,
        BID_REJECTED,
        AUCTION_ENDED_NO_WINNER,
        AUCTION_ENDED_HAS_WINNER,
        QUESTION_ASKED,
        QUESTION_ANSWERED,
        EMAIL_OTP_VERIFY,
        EMAIL_OTP_RESET_PASSWORD
    }

    private EmailType emailType;

    // Thông tin Người nhận
    private Integer recipientUserId;
    private String recipientEmail;
    private String recipientName;

    // Thông tin Sản phẩm
    private Integer productId;
    private String productName;

    // Nội dung cần thiết
    private String subject;
    private String messageContent;

    // Đường dẫn URL (Link tới câu hỏi)
    private String deepLinkPath;
}