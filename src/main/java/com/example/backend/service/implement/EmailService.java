package com.example.backend.service.implement;

import org.springframework.stereotype.Service;

import com.example.backend.model.Email.EmailNotificationRequest;
import com.example.backend.service.IEmailService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailService implements IEmailService {
    @Override
    public void simulateEmailSending(EmailNotificationRequest emailRequest) {
        System.out.println("=== EMAIL SIMULATION ===");
        System.out.println("To: " + emailRequest.getRecipientEmail());
        System.out.println(
                "Recipient: " + emailRequest.getRecipientName() + " (ID: " + emailRequest.getRecipientUserId() + ")");
        System.out.println("Subject: " + emailRequest.getSubject());
        System.out.println("Product: " + emailRequest.getProductName() + " (ID: " + emailRequest.getProductId() + ")");
        System.out.println("Sender: " + emailRequest.getSenderName() + " (ID: " + emailRequest.getSenderUserId() + ")");
        System.out.println("Message: " + emailRequest.getMessageContent());
        System.out.println("Link: " + emailRequest.getDeepLinkPath());
        System.out.println("========================");
    }

    @Override
    public void sendEmail(EmailNotificationRequest emailRequest) {
        try {
            log.info("Sending actual email to: {}", emailRequest.getRecipientEmail());

            // Gửi email thực tế
            // Có thể sử dụng Spring Mail hoặc services khác như SendGrid, AWS SES

            // Mô phỏng gửi email
            log.info("Actual email sending is not yet implemented. Using simulation instead.");
            simulateEmailSending(emailRequest);

        } catch (Exception e) {
            log.error("[SERVICE][EMAIL][ERROR] Failed to send email: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

}