package com.example.backend.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.backend.model.Email.EmailNotificationRequest;
import com.example.backend.service.IEmailService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EmailConsumer {
    @Autowired
    private IEmailService _emailService;

    @RabbitListener(queues = "${rabbitmq.email.queue.name}")
    public void handleEmailNotification(EmailNotificationRequest emailRequest) {
        try {
            log.info("Received email notification request: recipient={}, subject={}, product={}",
                    emailRequest.getRecipientEmail(),
                    emailRequest.getSubject(),
                    emailRequest.getProductName());

            _emailService.sendEmail(emailRequest);

            log.info("Email notification processed successfully for recipient: {}",
                    emailRequest.getRecipientEmail());

        } catch (Exception e) {
            log.error("[CONSUMER][EMAIL][ERROR] Failed to process email notification for recipient {}: {}",
                    emailRequest.getRecipientEmail(), e.getMessage(), e);
            throw e; // Re-throw để RabbitMQ có thể retry hoặc gửi tới DLQ
        }
    }
}