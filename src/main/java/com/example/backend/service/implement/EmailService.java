package com.example.backend.service.implement;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.backend.model.Email.EmailNotificationRequest;
import com.example.backend.service.IEmailService;

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
            String content = """
                Xin chào %s,

                Bạn đã ra giá thành công cho sản phẩm "%s".
                Giá hiện tại của sản phẩm đã được cập nhật.

                Hãy theo dõi để không bỏ lỡ diễn biến tiếp theo.

                Xem chi tiết: %s
                """.formatted(
                    r.getRecipientName(),
                    r.getProductName(),
                    r.getDeepLinkPath()
            );

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(r.getRecipientEmail());
            message.setSubject(subject);
            message.setText(content);

            mailSender.send(message);

            log.info("[EMAIL][{}] Sent to {}", r.getEmailType(), r.getRecipientEmail());

        } catch (Exception e) {
            log.error("[EMAIL][ERROR] Failed to send email", e);
            throw new RuntimeException("Send email failed", e);
        }
    }

    private String buildSubject(EmailNotificationRequest r) {
        return switch (r.getEmailType()) {
            case BID_SUCCESS -> "Ra giá thành công";
            case BID_REJECTED -> "Ra giá không thành công";
            case AUCTION_ENDED_HAS_WINNER -> "Đấu giá kết thúc – Có người thắng";
            case AUCTION_ENDED_NO_WINNER -> "Đấu giá kết thúc – Không có người mua";
            case QUESTION_ASKED -> "Có câu hỏi mới về sản phẩm";
            case QUESTION_ANSWERED -> "Câu hỏi về sản phẩm đã được trả lời";
        };
    }
}
