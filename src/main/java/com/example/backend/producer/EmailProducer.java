package com.example.backend.producer;

import com.example.backend.entity.Product;
import com.example.backend.entity.User;
import com.example.backend.model.Email.EmailNotificationRequest;
import com.example.backend.service.implement.ProductService;
import com.example.backend.service.implement.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailProducer {

    private final RabbitTemplate rabbitTemplate;
    private final UserService userService;
    private final ProductService productService;

    @Value("${rabbitmq.email.exchange.name}")
    private String emailExchangeName;

    @Value("${rabbitmq.email.routing_key.name}")
    private String emailRoutingKeyName;

    private void publish(EmailNotificationRequest request) {
        try {
            log.info(
                    "[EMAIL][PUBLISH] type={} recipientId={} productId={}",
                    request.getEmailType(),
                    request.getRecipientUserId(),
                    request.getProductId()
            );

            rabbitTemplate.convertAndSend(
                    emailExchangeName,
                    emailRoutingKeyName,
                    request
            );

        } catch (AmqpException e) {
            log.error("[EMAIL][PRODUCER][ERROR]", e);
            throw new RuntimeException("Failed to publish email notification", e);
        }
    }

    private EmailNotificationRequest buildRequest(
            EmailNotificationRequest.EmailType type,
            Integer recipientUserId,
            Integer productId
    ) {
        User user = userService.getUser(recipientUserId);
        Product product = productService.getProduct(productId);

        return EmailNotificationRequest.builder()
                .emailType(type)

                .recipientUserId(user.getUserId())
                .recipientEmail(user.getEmail())
                .recipientName(user.getFullName())

                .productId(product.getProductId())
                .productName(product.getProductName())

                .subject(null)
                .messageContent(null)
                .deepLinkPath("/products/" + product.getProductId())
                .build();
    }

    public void sendEmailOtp(
            Integer userId,
            String otp,
            EmailNotificationRequest.EmailType emailType
    ) {

        User user = userService.getUser(userId);

        EmailNotificationRequest request = EmailNotificationRequest.builder()
                .emailType(emailType)

                .recipientUserId(user.getUserId())
                .recipientEmail(user.getEmail())
                .recipientName(user.getFullName())

                .productId(null)
                .productName(null)

                .subject(null)
                .messageContent(otp)
                .deepLinkPath(null)

                .build();

        publish(request);
    }

    public void sendProductEmail(
            EmailNotificationRequest.EmailType emailType,
            Integer recipientUserId,
            Integer productId
    ) {
        publish(buildRequest(emailType, recipientUserId, productId));
    }
}
