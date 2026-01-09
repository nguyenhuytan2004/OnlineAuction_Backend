package com.example.backend.model.Email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request model for email notifications")
public class EmailNotificationRequest {

  @Schema(description = "Email notification type", enumAsRef = true)
  public enum EmailType {

    // Bid
    BID_SUCCESS_WINNER,
    BID_SUCCESS_PREVIOUS_BIDDER,
    BID_SUCCESS_SELLER,
    BID_BLOCKED,

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

  @Schema(description = "Type of email notification", example = "BID_SUCCESS_WINNER", required = true)
  private EmailType emailType;

  // Thông tin Người nhận
  @Schema(description = "Recipient user ID", example = "123", required = true)
  private Integer recipientUserId;

  @Schema(description = "Recipient email address", example = "user@example.com", required = true)
  private String recipientEmail;

  @Schema(description = "Recipient full name", example = "John Doe", required = true)
  private String recipientName;

  // Thông tin Sản phẩm
  @Schema(description = "Product ID for the notification context", example = "456")
  private Integer productId;

  @Schema(description = "Product name", example = "iPhone 14 Pro")
  private String productName;

  // Nội dung cần thiết
  @Schema(description = "Email subject", example = "Your bid was successful", required = true)
  private String subject;

  @Schema(description = "Email message content", example = "Congratulations! Your bid has been accepted.", required = true)
  private String messageContent;

  // Đường dẫn URL (Link tới câu hỏi)
  private String deepLinkPath;
}