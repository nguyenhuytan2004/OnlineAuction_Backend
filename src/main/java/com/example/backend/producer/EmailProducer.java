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

    /* ================= CORE ================= */

    public void publishEmailNotification(EmailNotificationRequest request) {
        try {
            log.debug(
                    "[EMAIL][PUBLISH] type={} recipient={} product={}",
                    request.getEmailType(),
                    request.getRecipientEmail(),
                    request.getProductName()
            );

            rabbitTemplate.convertAndSend(
                    emailExchangeName,
                    emailRoutingKeyName,
                    request
            );

            log.info(
                    "[EMAIL][PUBLISHED] type={} recipient={}",
                    request.getEmailType(),
                    request.getRecipientEmail()
            );

        } catch (AmqpException e) {
            log.error(
                    "[EMAIL][PRODUCER][ERROR] type={} recipient={} error={}",
                    request.getEmailType(),
                    request.getRecipientEmail(),
                    e.getMessage(),
                    e
            );
            throw new RuntimeException("Failed to publish email notification", e);
        }
    }

    private void publishWithType(
            EmailNotificationRequest request,
            EmailNotificationRequest.EmailType emailType
    ) {
        request.setEmailType(emailType);
        publishEmailNotification(request);
    }


    public void publishQuestionNotification(EmailNotificationRequest request) {
        publishWithType(request, EmailNotificationRequest.EmailType.QUESTION_ASKED);
    }

    public void publishAnswerNotification(EmailNotificationRequest request) {
        publishWithType(request, EmailNotificationRequest.EmailType.QUESTION_ANSWERED);
    }

    public void publishBidSuccessNotification(EmailNotificationRequest request) {
        publishWithType(request, EmailNotificationRequest.EmailType.BID_SUCCESS);
    }

    public void publishBidRejectedNotification(EmailNotificationRequest request) {
        publishWithType(request, EmailNotificationRequest.EmailType.BID_REJECTED);
    }

    public void publishAuctionEndedNoWinnerNotification(EmailNotificationRequest request) {
        publishWithType(request, EmailNotificationRequest.EmailType.AUCTION_ENDED_NO_WINNER);
    }

    public void publishAuctionEndedHasWinnerNotification(EmailNotificationRequest request) {
        publishWithType(request, EmailNotificationRequest.EmailType.AUCTION_ENDED_HAS_WINNER);
    }
}
