package com.example.backend.producer;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.backend.model.Email.EmailNotificationRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EmailProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.email.exchange.name}")
    private String emailExchangeName;

    @Value("${rabbitmq.email.routing_key.name}")
    private String emailRoutingKeyName;

    public void publishEmailNotification(EmailNotificationRequest emailRequest) {
        try {
            log.debug("Publishing email notification to queue: recipient={}, subject={}, product={}",
                    emailRequest.getRecipientEmail(),
                    emailRequest.getSubject(),
                    emailRequest.getProductName());

            // Publish message to exchange with routing key
            rabbitTemplate.convertAndSend(emailExchangeName, emailRoutingKeyName, emailRequest);

            log.info("Email notification published successfully for recipient: {} about product: {}",
                    emailRequest.getRecipientEmail(),
                    emailRequest.getProductName());

        } catch (AmqpException e) {
            log.error("[PRODUCER][EMAIL][ERROR] Failed to publish email notification for recipient: {} - Error: {}",
                    emailRequest.getRecipientEmail(), e.getMessage(), e);
            throw new RuntimeException("Failed to publish email notification", e);
        }
    }

    public void publishQuestionNotification(EmailNotificationRequest emailRequest) {
        try {
            log.info("Publishing question notification for seller: {} about product: {}",
                    emailRequest.getRecipientEmail(), emailRequest.getProductName());

            publishEmailNotification(emailRequest);

        } catch (Exception e) {
            log.error("[PRODUCER][EMAIL][ERROR] Failed to publish question notification: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to publish question notification", e);
        }
    }

    public void publishAnswerNotification(EmailNotificationRequest emailRequest) {
        try {
            log.info("Publishing answer notification for buyer: {} about product: {}",
                    emailRequest.getRecipientEmail(), emailRequest.getProductName());

            publishEmailNotification(emailRequest);

        } catch (Exception e) {
            log.error("[PRODUCER][EMAIL][ERROR] Failed to publish answer notification: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to publish answer notification", e);
        }
    }
}