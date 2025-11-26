package com.example.backend.service.implement;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.entity.AuctionResult;
import com.example.backend.entity.Product;
import com.example.backend.entity.Rating;
import com.example.backend.entity.User;
import com.example.backend.model.Rating.CreateRatingRequest;
import com.example.backend.repository.IAuctionResultRepository;
import com.example.backend.repository.IProductRepository;
import com.example.backend.repository.IRatingRepository;
import com.example.backend.repository.IUserRepository;
import com.example.backend.service.IRatingService;

@Service
public class RatingService implements IRatingService {
    @Autowired
    private IAuctionResultRepository _auctionResultRepository;

    @Autowired
    private IRatingRepository _ratingRepository;

    @Autowired
    private IProductRepository _productRepository;

    @Autowired
    private IUserRepository _userRepository;

    @Override
    @Transactional
    public Rating rateSeller(CreateRatingRequest createRatingRequest, Integer userId) {
        Product product = _productRepository.findById(createRatingRequest.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        AuctionResult auctionResult = _auctionResultRepository
                .findByProductProductId(createRatingRequest.getProductId());
        if (auctionResult == null) {
            throw new IllegalArgumentException("Auction result not found for this product");
        }

        // Kiểm tra xem userId có phải là người thắng không
        Integer winnerId = auctionResult.getWinner().getUserId();
        if (!Objects.equals(winnerId, userId)) {
            throw new IllegalArgumentException("Only the winner can rate the seller.");
        }

        User winner = _userRepository.findById(winnerId)
                .orElseThrow(() -> new IllegalArgumentException("Winner not found"));

        User seller = product.getSeller();
        if (seller == null) {
            throw new IllegalArgumentException("Seller not found");
        }

        // Kiểm tra xem winner đã rating cho seller trên sản phẩm này chưa
        if (_ratingRepository.existsByBidderAndSellerAndProduct(winnerId, seller.getUserId(),
                auctionResult.getProduct().getProductId())) {
            throw new IllegalArgumentException("Per winner only rate seller once for a product");
        }

        Rating newRating = new Rating();
        newRating.setProduct(product);
        newRating.setWinner(winner);
        newRating.setSeller(seller);
        newRating.setRatingValue(createRatingRequest.getRatingValue());
        newRating.setComment(createRatingRequest.getComment());

        Rating savedRating = _ratingRepository.save(newRating);

        // Update seller' rating score
        seller.setRatingScore(seller.getRatingScore() + createRatingRequest.getRatingValue());
        seller.setRatingCount(seller.getRatingCount() + 1);
        _userRepository.save(seller);

        return savedRating;
    }
}
