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
import com.example.backend.service.IAuctionService;
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
    private IAuctionService _auctionService;

    @Autowired
    private EmailProducer emailProducer;

    @Override
    public List<ProductQuestion> getProductQuestions(Integer productId) {
        return _productQuestionRepository.findByProductProductIdOrderByQuestionAtDesc(productId);
    }

    @Override
    public ProductQuestion createProductQuestion(CreateProductQuestionRequest request) {
        // Thực hiện kiểm tra các điều kiện trước khi save
        User buyer = _userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        Product product = _productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getSeller().getUserId().equals(request.getUserId())) {
            throw new RuntimeException("Seller cannot ask questions on their own product");
        }

        ProductQuestion question = new ProductQuestion();
        question.setProduct(product);
        question.setQuestionUser(buyer);
        question.setQuestionText(HtmlSanitizerHelper.sanitize(request.getQuestionText()));
        ProductQuestion savedQuestion = _productQuestionRepository.save(question);

        _auctionService.broadcastQuestionAsked(savedQuestion);

        // Gửi mail thông báo (thực hiện sau)
        emailProducer.sendQuestionAsked(product.getSeller().getUserId(),product.getProductId());
        return savedQuestion;
    }

    @Override
    public ProductAnswer createProductAnswer(CreateProductAnswerRequest request) {
        // Thực hiện kiểm tra các điều kiện trước khi save
        User seller = _userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        ProductQuestion question = _productQuestionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found"));

        Product product = question.getProduct();
        if (!product.getSeller().getUserId().equals(request.getUserId())) {
            throw new RuntimeException("Only the seller of this product can answer questions");
        }

        ProductAnswer answer = new ProductAnswer();
        answer.setQuestion(question);
        answer.setAnswerUser(seller);
        answer.setAnswerText(HtmlSanitizerHelper.sanitize(request.getAnswerText()));
        ProductAnswer savedAnswer = _productAnswerRepository.save(answer);

        _auctionService.broadcastAnswerPosted(savedAnswer, product.getProductId());

        emailProducer.sendQuestionAnswered(question.getQuestionUser().getUserId(),product.getProductId());

        return savedAnswer;
    }
}