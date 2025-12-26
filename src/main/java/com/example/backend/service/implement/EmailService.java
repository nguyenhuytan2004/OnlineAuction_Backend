package com.example.backend.service.implement;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.backend.model.Email.EmailNotificationRequest;
import com.example.backend.service.IEmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService implements IEmailService {

  private final JavaMailSender mailSender;

  @Value("${spring.mail.username}")
  private String fromEmail;

  @Override
  public void sendEmail(EmailNotificationRequest r) {

    try {
      String subject = buildSubject(r);
      String content = buildContent(r);

      SimpleMailMessage message = new SimpleMailMessage();
      message.setFrom(fromEmail);
      message.setTo(r.getRecipientEmail());
      message.setSubject(subject);
      message.setText(content);

      mailSender.send(message);

      log.info("[EMAIL][{}] Sent to {}", r.getEmailType(), r.getRecipientEmail());

    } catch (MailException e) {
      log.error("[EMAIL][ERROR] Failed to send email", e);
      throw new RuntimeException("Send email failed", e);
    }
  }

  private String buildSubject(EmailNotificationRequest r) {
    return switch (r.getEmailType()) {
      case BID_SUCCESS -> "Ra giá thành công";
      case BID_BLOCKED -> "Bị từ chối ra giá";
      case AUCTION_ENDED_HAS_WINNER -> "Đấu giá kết thúc – Có người thắng";
      case AUCTION_ENDED_NO_WINNER -> "Đấu giá kết thúc – Không có người mua";
      case QUESTION_ASKED -> "Có câu hỏi mới về sản phẩm";
      case QUESTION_ANSWERED -> "Câu hỏi về sản phẩm đã được trả lời";
      case EMAIL_OTP_VERIFY -> "Xác nhận email tài khoản Auction Online";
      case EMAIL_OTP_RESET_PASSWORD -> "Đặt lại mật khẩu tài khoản Auction Online";
    };
  }

  /* ================= CONTENT ================= */

  private String buildContent(EmailNotificationRequest r) {

    return switch (r.getEmailType()) {

      case BID_SUCCESS -> """
          Xin chào %s,

          Bạn đã ra giá thành công cho sản phẩm "%s".
          Giá hiện tại của sản phẩm đã được cập nhật.

          Hãy theo dõi để không bỏ lỡ diễn biến tiếp theo.

          Xem chi tiết: %s
          """.formatted(
          r.getRecipientName(),
          r.getProductName(),
          r.getDeepLinkPath());

      case BID_BLOCKED -> """
          Xin chào %s,

          Rất tiếc, bạn đã bị từ chối ra giá cho sản phẩm "%s". Người bán đã chặn bạn khỏi việc tham gia đấu giá này.

          Vui lòng kiểm tra lại và thử lại nếu còn thời gian.

          Xem chi tiết: %s
          """.formatted(
          r.getRecipientName(),
          r.getProductName(),
          r.getDeepLinkPath());

      case AUCTION_ENDED_HAS_WINNER -> """
          Xin chào %s,

          Đấu giá cho sản phẩm "%s" đã kết thúc thành công.
          Người thắng đấu giá sẽ sớm liên hệ để tiến hành giao dịch.

          Cảm ơn bạn đã sử dụng hệ thống.

          Xem chi tiết: %s
          """.formatted(
          r.getRecipientName(),
          r.getProductName(),
          r.getDeepLinkPath());

      case AUCTION_ENDED_NO_WINNER -> """
          Xin chào %s,

          Đấu giá cho sản phẩm "%s" đã kết thúc nhưng chưa có người mua.
          Bạn có thể cân nhắc đăng lại sản phẩm vào thời điểm khác.

          Xem chi tiết: %s
          """.formatted(
          r.getRecipientName(),
          r.getProductName(),
          r.getDeepLinkPath());

      case QUESTION_ASKED -> """
          Xin chào %s,

          Có người mua vừa đặt câu hỏi về sản phẩm "%s":

          "%s"

          Vui lòng truy cập hệ thống để trả lời câu hỏi.

          Trả lời tại: %s
          """.formatted(
          r.getRecipientName(),
          r.getProductName(),
          r.getMessageContent(),
          r.getDeepLinkPath());

      case QUESTION_ANSWERED -> """
          Xin chào %s,

          Người bán đã trả lời câu hỏi về sản phẩm "%s":

          "%s"

          Bạn có thể xem chi tiết tại liên kết bên dưới.

          Xem tại: %s
          """.formatted(
          r.getRecipientName(),
          r.getProductName(),
          r.getMessageContent(),
          r.getDeepLinkPath());

      case EMAIL_OTP_VERIFY -> """
          Xin chào %s,

          Mã OTP xác nhận email của bạn là: %s

          Mã có hiệu lực trong 5 phút.

          Nếu bạn không thực hiện hành động này, vui lòng bỏ qua email.
          """
          .formatted(
              r.getRecipientName(),
              r.getMessageContent());

      case EMAIL_OTP_RESET_PASSWORD -> """
          Xin chào %s,

          Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn.

          Mã OTP đặt lại mật khẩu là: %s

          Mã có hiệu lực trong 5 phút.

          Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng đổi mật khẩu ngay hoặc liên hệ hỗ trợ.
          """
          .formatted(
              r.getRecipientName(),
              r.getMessageContent());
    };
  }
}
