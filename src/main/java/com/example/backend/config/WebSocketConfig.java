package com.example.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Broker sẽ chuyển tiếp tin nhắn đến các client subscribe
        // với prefix /topic
        config.enableSimpleBroker("/topic");

        // Các tin nhắn từ client gửi đến server sẽ có prefix "/app"
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket endpoint
        registry.addEndpoint("/ws/product-qna")
                .setAllowedOrigins("*") // Chỉ dùng ở development
                .withSockJS(); // Fallback cho trình duyệt không hỗ trợ WebSocket
    }
}