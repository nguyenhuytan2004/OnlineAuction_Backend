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
        if (seller == null) {
            throw new RuntimeException("Seller not found");
        }

        if (product.getSeller().getUserId().equals(userId)) {
            throw new RuntimeException("Seller cannot ask questions on their own product");
        }

        ProductQuestion question = new ProductQuestion();
        question.setProduct(product);
        question.setQuestionUser(buyer);
        question.setQuestionText(HtmlSanitizerHelper.sanitize(createProductQuestionRequest.getQuestionText()));

        // Gửi mail thông báo
        EmailNotificationRequest emailRequest = EmailNotificationRequest
                .builder()
                .recipientUserId(seller.getUserId())
                .recipientEmail(seller.getEmail())
                .recipientName(seller.getFullName())
                .productId(product.getProductId())
                .productName(product.getProductName())
                .senderUserId(buyer.getUserId())
                .senderName(buyer.getFullName())
                .subject("Có câu hỏi mới về sản phẩm: " + product.getProductName())
                .messageContent(
                        "Bạn đã nhận được một câu hỏi mới từ: " + buyer.getFullName() + "\n Nội dung câu hỏi: "
                                + createProductQuestionRequest.getQuestionText())
                .deepLinkPath("/products/" + product.getProductId())
                .build();
        sendQuestionNotificationToSeller(emailRequest);

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
            log.error("[SERVICE][PRODUCT-QNA][ERROR] Failed to queue question notification email for seller {}: {}",
                    request.getRecipientEmail(), e.getMessage(), e);
            throw new RuntimeException("Failed to queue question notification email for seller", e);
        }
    }

    @Override
    public ProductAnswer createProductAnswer(CreateProductAnswerRequest createProductAnswerRequest, Integer userId) {
        // Thực hiện kiểm tra các điều kiện trước khi save
        User seller = _userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        ProductQuestion question = _productQuestionRepository.findById(createProductAnswerRequest.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found"));

        User buyer = question.getQuestionUser();
        if (buyer == null) {
            throw new RuntimeException("Buyer not found");
        }

        Product product = question.getProduct();
        if (!product.getSeller().getUserId().equals(userId)) {
            throw new RuntimeException("Only the seller of this product can answer questions");
        }

        ProductAnswer answer = new ProductAnswer();
        answer.setQuestion(question);
        answer.setAnswerUser(seller);
        answer.setAnswerText(createProductAnswerRequest.getAnswerText());

        // Gửi mail thông báo

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
            log.error("[SERVICE][PRODUCT-QNA][ERROR] Failed to queue answer notification email for buyer {}: {}",
                    request.getRecipientEmail(), e.getMessage(), e);
            throw new RuntimeException("Failed to queue answer notification email for buyer", e);
        }
    }
}