package com.example.backend.service;

import java.util.List;

import com.example.backend.entity.Rating;
import com.example.backend.model.Rating.CreateRatingRequest;
import com.example.backend.model.Rating.UpdateRatingRequest;

public interface IRatingService {
  Boolean checkIfRated(Integer productId, Integer reviewerId, Integer revieweeId);

  Rating getRating(Integer productId, Integer reviewerId, Integer revieweeId);

  List<Boolean> checkIfSellerRatedBuyer(Integer sellerId, Integer buyerId);

  Rating rateSeller(CreateRatingRequest createRatingRequest, Integer buyerId);

  Rating rateBuyer(CreateRatingRequest createRatingRequest, Integer sellerId);

  Rating updateRating(UpdateRatingRequest updateRatingRequest, Integer reviewerId);

  List<Rating> geRatingsByRevieweeId(Integer revieweeId);
}
