package com.example.backend.service;

import java.util.List;

import com.example.backend.entity.ProductQnA.ProductAnswer;
import com.example.backend.entity.ProductQnA.ProductQuestion;
import com.example.backend.model.Email.EmailNotificationRequest;
import com.example.backend.model.ProductQna.ProductAnswer.CreateProductAnswerRequest;
import com.example.backend.model.ProductQna.ProductQuestion.CreateProductQuestionRequest;

public interface IProductQnaService {
    public List<ProductQuestion> getProductQuestions(Integer productId);

    public ProductQuestion createProductQuestion(CreateProductQuestionRequest createQuestionRequest);

    public void sendQuestionNotificationToSeller(EmailNotificationRequest request);

    public ProductAnswer createProductAnswer(CreateProductAnswerRequest createAnswerRequest);

    public void sendAnswerNotificationToBuyer(EmailNotificationRequest request);
}
