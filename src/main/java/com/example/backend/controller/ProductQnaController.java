package com.example.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.entity.ProductQnA.ProductAnswer;
import com.example.backend.entity.ProductQnA.ProductQuestion;
import com.example.backend.model.ProductQna.ProductAnswer.CreateProductAnswerRequest;
import com.example.backend.model.ProductQna.ProductQuestion.CreateProductQuestionRequest;
import com.example.backend.service.IProductQnaService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api/product")
public class ProductQnaController {
    @Autowired
    private IProductQnaService _productQnaService;

    @GetMapping("{product_Id}/questions")
    public ResponseEntity<?> getProductQuestions(@PathVariable("product_Id") Integer productId) {
        try {
            List<ProductQuestion> productQuestions = _productQnaService.getProductQuestions(productId);
            if (productQuestions == null || productQuestions.isEmpty()) {
                log.info("[CONTROLLER][GET][WARN] /api/product/{}/questions - No question found",
                        productId);
                return new ResponseEntity<>("No question found for product with ID: " + productId,
                        HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(productQuestions, HttpStatus.OK);
        } catch (Exception e) {
            log.error("[CONTROLLER][GET][ERROR] /api/product/{}/questions - Error occurred: {}", productId,
                    e.getMessage(), e);
            return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("{product_id}/questions")
    public ResponseEntity<?> createProductQuestion(
            @Valid @RequestBody CreateProductQuestionRequest createProductQuestionRequest,
            @RequestParam Integer userId) {
        try {
            ProductQuestion createdQuestion = _productQnaService
                    .createProductQuestion(createProductQuestionRequest, userId);
            return new ResponseEntity<>(createdQuestion, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            log.info("[CONTROLLER][POST][WARN] /api/product/{}/questions - Invalid input data: {}",
                    createProductQuestionRequest.getProductId(), e.getMessage());
            return new ResponseEntity<>("Invalid input data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("[CONTROLLER][POST][ERROR] /api/product/{}/questions - Error occurred: {}",
                    createProductQuestionRequest.getProductId(), e.getMessage(), e);
            return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("{product_id}/answers")
    public ResponseEntity<?> createProductAnswer(
            @Valid @RequestBody CreateProductAnswerRequest createProductAnswerRequest,
            @RequestParam Integer userId) {
        try {
            ProductAnswer createdAnswer = _productQnaService
                    .createProductAnswer(createProductAnswerRequest, userId);
            return new ResponseEntity<>(createdAnswer, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Only the seller")) {
                log.info("[CONTROLLER][POST][WARN] /api/product/{}/answers - Permission denied: {}",
                        createProductAnswerRequest.getQuestionId(), e.getMessage());
                return new ResponseEntity<>("Permission denied: " + e.getMessage(), HttpStatus.FORBIDDEN);
            }

            log.info("[CONTROLLER][POST][WARN] /api/product/{}/answers - Invalid input data: {}",
                    createProductAnswerRequest.getQuestionId(), e.getMessage());
            return new ResponseEntity<>("Invalid input data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("[CONTROLLER][POST][ERROR] /api/product/{}/answers - Error occurred: {}",
                    createProductAnswerRequest.getQuestionId(), e.getMessage(), e);
            return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}