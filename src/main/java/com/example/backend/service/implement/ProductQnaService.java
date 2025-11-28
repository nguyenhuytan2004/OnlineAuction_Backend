package com.example.backend.service.implement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.entity.Product;
import com.example.backend.entity.ProductQnA.ProductAnswer;
import com.example.backend.entity.ProductQnA.ProductQuestion;
import com.example.backend.entity.User;
import com.example.backend.helper.HtmlSanitizerHelper;
import com.example.backend.model.Email.EmailNotificationRequest;
import com.example.backend.model.ProductQna.ProductAnswer.CreateProductAnswerRequest;
import com.example.backend.model.ProductQna.ProductQuestion.CreateProductQuestionRequest;
import com.example.backend.producer.EmailProducer;
import com.example.backend.repository.IProductAnswerRepository;
import com.example.backend.repository.IProductQuestionRepository;
import com.example.backend.repository.IProductRepository;
import com.example.backend.repository.IUserRepository;
import com.example.backend.service.IProductQnaService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProductQnaService implements IProductQnaService {
    @Autowired
    private IProductQuestionRepository _productQuestionRepository;
    @Autowired
    private IProductAnswerRepository _productAnswerRepository;
    @Autowired
    private IUserRepository _userRepository;
    @Autowired
    private IProductRepository _productRepository;

    @Autowired
    private EmailProducer emailProducer;

    @Override
    public List<ProductQuestion> getProductQuestions(Integer productId) {
        return _productQuestionRepository.findByProductProductId(productId);
    }

    @Override
    public ProductQuestion createProductQuestion(CreateProductQuestionRequest createProductQuestionRequest,
            Integer userId) {
        // Thực hiện kiểm tra các điều kiện trước khi save
        User buyer = _userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        Product product = _productRepository.findById(createProductQuestionRequest.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        User seller = product.getSeller();
        

        if (product.getSeller().getUserId().equals(userId)) {
            throw new RuntimeException("Seller cannot ask questions on their own product");
        }

        ProductQuestion question = new ProductQuestion();
        question.setProduct(product);
        question.setQuestionUser(buyer);
        question.setQuestionText(HtmlSanitizerHelper.sanitize(createProductQuestionRequest.getQuestionText()));

        // Gửi mail thông báo (thực hiện sau)
        EmailNotificationRequest emailRequest = new EmailNotificationRequest();

        return _productQuestionRepository.save(question);
    }

    @Override
    public void sendQuestionNotificationToSeller(EmailNotificationRequest request) {
        try {
            log.info("Preparing question notification email for seller: {} about product: {}",
                    request.getRecipientEmail(), request.getProductName());

            emailProducer.publishQuestionNotification(request);

            log.info("Question notification email queued successfully for seller: {}", request.getRecipientEmail());

        } catch (Exception e) {
            log.error("Failed to queue question notification email for seller {}: {}",
                    request.getRecipientEmail(), e.getMessage(), e);
            throw new RuntimeException("Failed to send email notification", e);
        }
    }

    @Override
    public ProductAnswer createProductAnswer(CreateProductAnswerRequest createProductAnswerRequest, Integer userId) {
        // Thực hiện kiểm tra các điều kiện trước khi save
        User user = _userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        ProductQuestion question = _productQuestionRepository.findById(createProductAnswerRequest.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found"));

        Product product = question.getProduct();
        if (!product.getSeller().getUserId().equals(userId)) {
            throw new RuntimeException("Only the seller of this product can answer questions");
        }

        ProductAnswer answer = new ProductAnswer();
        answer.setQuestion(question);
        answer.setAnswerUser(user);
        answer.setAnswerText(createProductAnswerRequest.getAnswerText());

        // Gửi mail thông báo (thực hiện sau)

        return _productAnswerRepository.save(answer);
    }

    @Override
    public void sendAnswerNotificationToBuyer(EmailNotificationRequest request) {
        try {
            log.info("Preparing answer notification email for buyer: {} about product: {}",
                    request.getRecipientEmail(), request.getProductName());

            // Use EmailProducer to publish message
            emailProducer.publishAnswerNotification(request);

            log.info("Answer notification email queued successfully for buyer: {}", request.getRecipientEmail());

        } catch (Exception e) {
            log.error("Failed to queue answer notification email for buyer {}: {}",
                    request.getRecipientEmail(), e.getMessage(), e);
            throw new RuntimeException("Failed to send email notification", e);
        }
    }
}