package com.example.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.entity.ProductQnA.ProductQuestion;
import com.example.backend.service.IProductQnaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api/products")
public class ProductQnaController {
  @Autowired
  private IProductQnaService _productQnaService;

  @Operation(summary = "Get product questions", description = "Retrieve all questions asked about a specific product.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved product questions", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProductQuestion.class)))),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping("{product_Id}/questions")
  public ResponseEntity<?> getProductQuestions(@PathVariable("product_Id") Integer productId) {
    try {
      List<ProductQuestion> productQuestions = _productQnaService.getProductQuestions(productId);

      return new ResponseEntity<>(productQuestions, HttpStatus.OK);
    } catch (Exception e) {
      log.error("[CONTROLLER][GET][ERROR] /api/products/{}/questions - Error occurred: {}", productId,
          e.getMessage(), e);
      return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // @PostMapping("{product_id}/questions")
  // public ResponseEntity<?> createProductQuestion(
  // @Valid @RequestBody CreateProductQuestionRequest
  // createProductQuestionRequest,
  // (@AuthenticationPrincipal CustomUserDetails userDetails) {
  // try {
  // ProductQuestion createdQuestion = _productQnaService
  // .createProductQuestion(createProductQuestionRequest, userId);
  // return new ResponseEntity<>(createdQuestion, HttpStatus.CREATED);
  // } catch (RuntimeException e) {
  // log.info("[CONTROLLER][POST][WARN] /api/products/{}/questions - Invalid input
  // data: {}",
  // createProductQuestionRequest.getProductId(), e.getMessage());
  // return new ResponseEntity<>("Invalid input data: " + e.getMessage(),
  // HttpStatus.BAD_REQUEST);
  // } catch (Exception e) {
  // log.error("[CONTROLLER][POST][ERROR] /api/products/{}/questions - Error
  // occurred: {}",
  // createProductQuestionRequest.getProductId(), e.getMessage(), e);
  // return new ResponseEntity<>("Error occurred: " + e.getMessage(),
  // HttpStatus.INTERNAL_SERVER_ERROR);
  // }
  // }

  // @PostMapping("{product_id}/answers")
  // public ResponseEntity<?> createProductAnswer(
  // @Valid @RequestBody CreateProductAnswerRequest createProductAnswerRequest,
  // (@AuthenticationPrincipal CustomUserDetails userDetails) {
  // try {
  // ProductAnswer createdAnswer = _productQnaService
  // .createProductAnswer(createProductAnswerRequest, userId);
  // return new ResponseEntity<>(createdAnswer, HttpStatus.CREATED);
  // } catch (RuntimeException e) {
  // if (e.getMessage().contains("Only the seller")) {
  // log.info("[CONTROLLER][POST][WARN] /api/products/{}/answers - Permission
  // denied: {}",
  // createProductAnswerRequest.getQuestionId(), e.getMessage());
  // return new ResponseEntity<>("Permission denied: " + e.getMessage(),
  // HttpStatus.FORBIDDEN);
  // }

  // log.info("[CONTROLLER][POST][WARN] /api/products/{}/answers - Invalid input
  // data: {}",
  // createProductAnswerRequest.getQuestionId(), e.getMessage());
  // return new ResponseEntity<>("Invalid input data: " + e.getMessage(),
  // HttpStatus.BAD_REQUEST);
  // } catch (Exception e) {
  // log.error("[CONTROLLER][POST][ERROR] /api/products/{}/answers - Error
  // occurred: {}",
  // createProductAnswerRequest.getQuestionId(), e.getMessage(), e);
  // return new ResponseEntity<>("Error occurred: " + e.getMessage(),
  // HttpStatus.INTERNAL_SERVER_ERROR);
  // }
  // }
}