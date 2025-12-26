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

    // Bid
    BID_SUCCESS_WINNER,
    BID_SUCCESS_PREVIOUS_BIDDER,
    BID_SUCCESS_SELLER,
    BID_REJECTED,

    // Auction end
    AUCTION_ENDED_WINNER,
    AUCTION_ENDED_SELLER,
    AUCTION_ENDED_NO_WINNER_SELLER,

    // Question
    QUESTION_ASKED,
    QUESTION_ANSWERED,

    // OTP
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