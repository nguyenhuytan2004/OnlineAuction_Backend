package com.example.backend.service.implement;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.entity.AuctionResult;
import com.example.backend.entity.Product;
import com.example.backend.entity.Rating;
import com.example.backend.entity.User;
import com.example.backend.model.Rating.CreateRatingRequest;
import com.example.backend.model.Rating.UpdateRatingRequest;
import com.example.backend.repository.IAuctionResultRepository;
import com.example.backend.repository.IProductRepository;
import com.example.backend.repository.IRatingRepository;
import com.example.backend.repository.IUserRepository;
import com.example.backend.service.IRatingService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
  public Boolean checkIfRated(Integer productId, Integer reviewerId, Integer revieweeId) {

    log.info(
        "[SERVICE][GET][CHECK_IF_RATED] Input productId={}, reviewerId={}, revieweeId={}",
        productId,
        reviewerId,
        revieweeId);

    try {
      Boolean rated = _ratingRepository
          .existsByReviewerAndRevieweeAndProduct(
              reviewerId,
              revieweeId,
              productId);

      log.info(
          "[SERVICE][GET][CHECK_IF_RATED] Output rated={}",
          rated);

      return rated;

    } catch (Exception e) {
      log.error(
          "[SERVICE][GET][CHECK_IF_RATED] Error occurred (productId={}, reviewerId={}, revieweeId={}): {}",
          productId,
          reviewerId,
          revieweeId,
          e.getMessage(),
          e);
      throw e;
    }
  }

  @Override
  public Rating getRating(Integer productId, Integer reviewerId, Integer revieweeId) {

    log.info(
        "[SERVICE][GET][GET_RATING] Input productId={}, reviewerId={}, revieweeId={}",
        productId,
        reviewerId,
        revieweeId);

    try {
      Rating rating = _ratingRepository
          .findByProductProductIdAndReviewerUserIdAndRevieweeUserId(
              productId,
              reviewerId,
              revieweeId);

      log.info(
          "[SERVICE][GET][GET_RATING] Output rating={}",
          rating);

      return rating;

    } catch (Exception e) {
      log.error(
          "[SERVICE][GET][GET_RATING] Error occurred (productId={}, reviewerId={}, revieweeId={}): {}",
          productId,
          reviewerId,
          revieweeId,
          e.getMessage(),
          e);
      throw e;
    }
  }

  @Override
  public List<Boolean> checkIfSellerRatedBuyer(Integer sellerId, Integer buyerId) {

    log.info(
        "[SERVICE][GET][CHECK_SELLER_RATED_BUYER] Input sellerId={}, buyerId={}",
        sellerId,
        buyerId);

    try {
      List<Product> soldProducts = _auctionResultRepository.findSoldProductsBySellerUserId(sellerId);

      List<Boolean> result = soldProducts.stream()
          .map(product -> _ratingRepository.existsByReviewerAndRevieweeAndProduct(
              sellerId,
              buyerId,
              product.getProductId()))
          .toList();

      log.info(
          "[SERVICE][GET][CHECK_SELLER_RATED_BUYER] Output result={}",
          result);

      return result;

    } catch (Exception e) {
      log.error(
          "[SERVICE][GET][CHECK_SELLER_RATED_BUYER] Error occurred (sellerId={}, buyerId={}): {}",
          sellerId,
          buyerId,
          e.getMessage(),
          e);
      throw e;
    }
  }

  @Override
  @Transactional
  public Rating rateSeller(CreateRatingRequest createRatingRequest, Integer userId) {

    log.info(
        "[SERVICE][POST][RATE_SELLER] Input request={}, userId={}",
        createRatingRequest,
        userId);

    try {
      Product product = _productRepository.findById(createRatingRequest.getProductId())
          .orElseThrow(() -> new IllegalArgumentException("Product not found"));

      AuctionResult auctionResult = _auctionResultRepository.findByProductProductId(
          createRatingRequest.getProductId());
      if (auctionResult == null) {
        throw new IllegalArgumentException(
            "Auction result not found for this product");
      }

      Integer buyerId = auctionResult.getWinner().getUserId();
      if (!Objects.equals(buyerId, userId)) {
        throw new IllegalArgumentException(
            "Only the winner can rate the seller.");
      }

      User winner = _userRepository.findById(buyerId)
          .orElseThrow(() -> new IllegalArgumentException("Winner not found"));

      User seller = product.getSeller();
      if (seller == null) {
        throw new IllegalArgumentException("Seller not found");
      }

      if (_ratingRepository.existsByReviewerAndRevieweeAndProduct(
          buyerId,
          seller.getUserId(),
          auctionResult.getProduct().getProductId())) {
        throw new IllegalArgumentException(
            "Per winner only rate seller once for a product");
      }

      Rating newRating = new Rating();
      newRating.setProduct(product);
      newRating.setReviewer(winner);
      newRating.setReviewee(seller);
      newRating.setRatingValue(createRatingRequest.getRatingValue());
      newRating.setComment(createRatingRequest.getComment());

      Rating savedRating = _ratingRepository.save(newRating);

      // Update seller' rating score
      seller.setRatingScore(
          seller.getRatingScore() + createRatingRequest.getRatingValue());
      seller.setRatingCount(seller.getRatingCount() + 1);
      _userRepository.save(seller);

      log.info(
          "[SERVICE][POST][RATE_SELLER] Success productId={}, sellerId={}, ratingValue={}",
          product.getProductId(),
          seller.getUserId(),
          createRatingRequest.getRatingValue());

      return savedRating;

    } catch (Exception e) {
      log.error(
          "[SERVICE][POST][RATE_SELLER] Error occurred (request={}, userId={}): {}",
          createRatingRequest,
          userId,
          e.getMessage(),
          e);
      throw e;
    }
  }

  @Override
  @Transactional
  public Rating rateBuyer(CreateRatingRequest createRatingRequest, Integer userId) {

    log.info(
        "[SERVICE][POST][RATE_BUYER] Input request={}, userId={}",
        createRatingRequest,
        userId);

    try {
      Product product = _productRepository.findById(createRatingRequest.getProductId())
          .orElseThrow(() -> new IllegalArgumentException("Product not found"));

      AuctionResult auctionResult = _auctionResultRepository.findByProductProductId(
          createRatingRequest.getProductId());
      if (auctionResult == null) {
        throw new IllegalArgumentException(
            "Auction result not found for this product");
      }

      Integer sellerId = product.getSeller().getUserId();
      if (!Objects.equals(sellerId, userId)) {
        throw new IllegalArgumentException(
            "Only the seller can rate the buyer.");
      }

      User seller = _userRepository.findById(sellerId)
          .orElseThrow(() -> new IllegalArgumentException("Seller not found"));

      User buyer = auctionResult.getWinner();
      if (buyer == null) {
        throw new IllegalArgumentException("Buyer not found");
      }

      if (_ratingRepository.existsByReviewerAndRevieweeAndProduct(
          sellerId,
          buyer.getUserId(),
          auctionResult.getProduct().getProductId())) {
        throw new IllegalArgumentException(
            "Per seller only rate buyer once for a product");
      }

      Rating newRating = new Rating();
      newRating.setProduct(product);
      newRating.setReviewer(seller);
      newRating.setReviewee(buyer);
      newRating.setRatingValue(createRatingRequest.getRatingValue());
      newRating.setComment(createRatingRequest.getComment());

      Rating savedRating = _ratingRepository.save(newRating);

      // Update buyer' rating score
      buyer.setRatingScore(
          buyer.getRatingScore() + createRatingRequest.getRatingValue());
      buyer.setRatingCount(buyer.getRatingCount() + 1);
      _userRepository.save(buyer);

      log.info(
          "[SERVICE][POST][RATE_BUYER] Success productId={}, buyerId={}, ratingValue={}",
          product.getProductId(),
          buyer.getUserId(),
          createRatingRequest.getRatingValue());

      return savedRating;

    } catch (Exception e) {
      log.error(
          "[SERVICE][POST][RATE_BUYER] Error occurred (request={}, userId={}): {}",
          createRatingRequest,
          userId,
          e.getMessage(),
          e);
      throw e;
    }
  }

  @Override
  @Transactional
  public Rating updateRating(UpdateRatingRequest updateRatingRequest, Integer reviewerId) {

    log.info(
        "[SERVICE][PUT][UPDATE_RATING] Input request={}, reviewerId={}",
        updateRatingRequest,
        reviewerId);

    try {
      Rating existingRating = _ratingRepository
          .findByProductProductIdAndReviewerUserIdAndRevieweeUserId(
              updateRatingRequest.getProductId(),
              reviewerId,
              updateRatingRequest.getRevieweeId());

      if (existingRating == null) {
        throw new IllegalArgumentException(
            "Rating not found for the given product and users");
      }

      Integer oldRatingValue = existingRating.getRatingValue();

      existingRating.setRatingValue(updateRatingRequest.getRatingValue());
      existingRating.setComment(updateRatingRequest.getComment());

      Rating updatedRating = _ratingRepository.save(existingRating);

      // Update reviewee's rating score
      User reviewee = existingRating.getReviewee();
      reviewee.setRatingScore(
          reviewee.getRatingScore()
              - oldRatingValue
              + updateRatingRequest.getRatingValue());
      _userRepository.save(reviewee);

      log.info(
          "[SERVICE][PUT][UPDATE_RATING] Success productId={}, revieweeId={}, oldValue={}, newValue={}",
          updateRatingRequest.getProductId(),
          updateRatingRequest.getRevieweeId(),
          oldRatingValue,
          updateRatingRequest.getRatingValue());

      return updatedRating;

    } catch (Exception e) {
      log.error(
          "[SERVICE][PUT][UPDATE_RATING] Error occurred (request={}, reviewerId={}): {}",
          updateRatingRequest,
          reviewerId,
          e.getMessage(),
          e);
      throw e;
    }
  }

  @Override
  public List<Rating> getRatingsByRevieweeId(Integer revieweeId) {

    log.info(
        "[SERVICE][GET][GET_RATINGS_BY_REVIEWEE] Input revieweeId={}",
        revieweeId);

    try {
      List<Rating> ratings = _ratingRepository.findByRevieweeUserId(revieweeId);

      log.info(
          "[SERVICE][GET][GET_RATINGS_BY_REVIEWEE] Output ratings={}",
          ratings);

      return ratings;

    } catch (Exception e) {
      log.error(
          "[SERVICE][GET][GET_RATINGS_BY_REVIEWEE] Error occurred (revieweeId={}): {}",
          revieweeId,
          e.getMessage(),
          e);
      throw e;
    }
  }
}
