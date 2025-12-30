package com.example.backend.service.implement;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.entity.AuctionResult;
import com.example.backend.entity.Product;
import com.example.backend.entity.Rating;
import com.example.backend.entity.User;
import com.example.backend.model.Rating.CreateRatingRequest;
import com.example.backend.model.Rating.UpdateRatingRequest;
import com.example.backend.repository.IAuctionResultRepository;
import com.example.backend.repository.IProductRepository;
import com.example.backend.service.IAuctionResultService;
import com.example.backend.service.IRatingService;

import jakarta.transaction.Transactional;

@Service
@Slf4j
public class AuctionResultService implements IAuctionResultService {

  @Autowired
  private IAuctionResultRepository _auctionResultRepository;
  @Autowired
  private IRatingService _ratingService;
  @Autowired
  private IProductRepository _productRepository;

  @Override
  public AuctionResult getAuctionResult(Integer productId) {
    log.info(
            "[SERVICE][GET][AUCTION_RESULT] Input productId={}",
            productId
    );

    try {
      AuctionResult result =
              _auctionResultRepository.findByProductProductId(productId);

      log.info(
              "[SERVICE][GET][AUCTION_RESULT] Output result={}",
              result
      );
      return result;

    } catch (Exception e) {
      log.error(
              "[SERVICE][GET][AUCTION_RESULT] Error occurred (productId={}): {}",
              productId,
              e.getMessage(),
              e
      );
      throw e;
    }
  }

  @Override
  @Transactional
  public AuctionResult cancelAuction(Integer productId, Integer userId) {
    log.info(
            "[SERVICE][POST][CANCEL_AUCTION] Input productId={}, userId={}",
            productId,
            userId
    );

    try {
      Product product = _productRepository.findById(productId)
              .orElseThrow(() -> new IllegalArgumentException("Product not found"));

      User seller = product.getSeller();
      if (!seller.getUserId().equals(userId)) {
        throw new IllegalArgumentException("Only the seller can cancel the auction.");
      }

      AuctionResult auctionResult =
              _auctionResultRepository.findByProductProductId(productId);

      if (auctionResult == null) {
        throw new IllegalArgumentException("Auction result not found for this product");
      }

      auctionResult.setPaymentStatus(AuctionResult.PaymentStatus.CANCELED);

      Rating existingRating = _ratingService.getRating(
              productId,
              seller.getUserId(),
              auctionResult.getWinner().getUserId()
      );

      if (existingRating != null) {
        Rating updatedRating = _ratingService.updateRating(
                new UpdateRatingRequest(
                        productId,
                        auctionResult.getWinner().getUserId(),
                        -1,
                        "Người thắng không thanh toán"
                ),
                seller.getUserId()
        );

        if (updatedRating == null) {
          throw new IllegalArgumentException("Error updating rating upon auction cancellation.");
        }

      } else {
        CreateRatingRequest req = new CreateRatingRequest();
        req.setProductId(productId);
        req.setRatingValue(-1);
        req.setComment("Người thắng không thanh toán");

        Rating savedRating = _ratingService.rateBuyer(req, userId);
        if (savedRating == null) {
          throw new IllegalArgumentException("Error rating buyer upon auction cancellation.");
        }
      }

      AuctionResult saved = _auctionResultRepository.save(auctionResult);

      log.info(
              "[SERVICE][POST][CANCEL_AUCTION] Success productId={}, status={}",
              productId,
              saved.getPaymentStatus()
      );
      return saved;

    } catch (Exception e) {
      log.error(
              "[SERVICE][POST][CANCEL_AUCTION] Error occurred (productId={}, userId={}): {}",
              productId,
              userId,
              e.getMessage(),
              e
      );
      throw e;
    }
  }
}
