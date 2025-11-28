package com.example.backend.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    // Main Queue
    @Value("${rabbitmq.email.queue.name}")
    private String emailQueueName;

    @Value("${rabbitmq.email.exchange.name}")
    private String emailExchangeName;

    @Value("${rabbitmq.email.routing_key.name}")
    private String emailRoutingKeyName;

    // Dead Letter Queue
    @Value("${rabbitmq.dlq.email.queue.name}")
    private String dlqEmailQueueName;

    @Value("${rabbitmq.dlq.email.exchange.name}")
    private String dlqEmailExchangeName;

    @Value("${rabbitmq.dlq.email.routing_key.name}")
    private String dlqEmailRoutingKeyName;

    // Main Queue Bean
    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(emailQueueName)
                .withArgument("x-dead-letter-exchange", dlqEmailExchangeName)
                .withArgument("x-dead-letter-routing-key", dlqEmailRoutingKeyName)
                .build();
    }

    @Bean
    public DirectExchange emailExchange() {
        return new DirectExchange(emailExchangeName);
    }

    @Bean
    public Binding emailBinding(Queue emailQueue, DirectExchange emailExchange) {
        return BindingBuilder.bind(emailQueue).to(emailExchange).with(emailRoutingKeyName);
    }

    // Dead Letter Queue Bean
    @Bean
    public Queue dlqEmailQueue() {
        return QueueBuilder.durable(dlqEmailQueueName).build();
    }

    @Bean
    public DirectExchange dlqEmailExchange() {
        return new DirectExchange(dlqEmailExchangeName);
    }

    @Bean
    public Binding dlqEmailBinding(Queue dlqEmailQueue, DirectExchange dlqEmailExchange) {
        return BindingBuilder.bind(dlqEmailQueue).to(dlqEmailExchange).with(dlqEmailRoutingKeyName);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
