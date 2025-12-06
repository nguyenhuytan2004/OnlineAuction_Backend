package com.example.backend.controller.WebSocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import com.example.backend.model.ProductQna.ProductAnswer.CreateProductAnswerRequest;
import com.example.backend.model.ProductQna.ProductQuestion.CreateProductQuestionRequest;
import com.example.backend.service.IProductQnaService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ProductQnaWebSocketController {
    @Autowired
    private IProductQnaService _productQnaService;

    @MessageMapping("products/{productId}/questions")
    public void askQuestion(@Payload CreateProductQuestionRequest createQuestionRequest) {
        try {
            _productQnaService.createProductQuestion(createQuestionRequest);

        } catch (Exception e) {
            log.error(
                    "[WEBSOCKET][QUESTION][ERROR] /product/{}/questions - Error occurred: {}",
                    createQuestionRequest.getProductId(), e.getMessage(), e);

        }
    }

    @MessageMapping("products/{productId}/questions/{questionId}/answers")
    public void answerQuestion(@Payload CreateProductAnswerRequest createAnswerRequest) {
        try {
            _productQnaService.createProductAnswer(createAnswerRequest);

        } catch (Exception e) {
            log.error(
                    "[WEBSOCKET][ANSWER][ERROR] /product/{}/questions/{}/answers - Error occurred: {}",
                    createAnswerRequest.getProductId(), createAnswerRequest.getQuestionId(), e.getMessage(), e);

        }
    }
}
