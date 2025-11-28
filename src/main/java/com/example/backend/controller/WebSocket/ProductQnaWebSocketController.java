package com.example.backend.controller.WebSocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.example.backend.entity.ProductQnA.ProductAnswer;
import com.example.backend.entity.ProductQnA.ProductQuestion;
import com.example.backend.model.ProductQna.ProductAnswer.CreateProductAnswerRequest;
import com.example.backend.model.ProductQna.ProductQuestion.CreateProductQuestionRequest;
import com.example.backend.service.IProductQnaService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ProductQnaWebSocketController {
    @Autowired
    private IProductQnaService _productQnaService;

    @MessageMapping("product/{productId}/questions")
    @SendTo("topic/product/{productId}/questions")
    public ProductQuestion askQuestion(
            @DestinationVariable Integer productId,
            @Payload CreateProductQuestionRequest request) {
        try {
            // Tạm thời hardcode, sau này sẽ lấy từ Security Context
            Integer buyerId = 1;

            ProductQuestion productQuestion = _productQnaService.createProductQuestion(request, buyerId);

            return productQuestion;
        } catch (Exception e) {
            log.error(
                    "[CONTROLLER][WEBSOCKET][PRODUCT-QNA][ERROR] /product/{}/questions - Error occurred: {}",
                    productId, e.getMessage(), e);

            return null;
        }
    }

    @MessageMapping("product/{productId}/questions/{questionId}/answers")
    @SendTo("topic/product/{productId}/questions/{questionId}/answers")
    public ProductAnswer answerQuestion(
            @DestinationVariable Integer productId,
            @DestinationVariable Integer questionId,
            @Payload CreateProductAnswerRequest answerRequest) {
        try {
            // Tạm thời hardcode, sau này sẽ lấy từ Security Context
            Integer sellerId = 1;

            ProductAnswer productAnswer = _productQnaService.createProductAnswer(answerRequest, sellerId);

            return productAnswer;
        } catch (Exception e) {
            log.error(
                    "[CONTROLLER][WEBSOCKET][PRODUCT-QNA][ERROR] /product/{}/questions/{}/answers - Error occurred: {}",
                    productId, questionId, e.getMessage(), e);

            return null;
        }
    }
}
