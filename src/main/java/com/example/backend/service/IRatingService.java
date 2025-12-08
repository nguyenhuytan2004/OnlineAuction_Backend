package com.example.backend.service;

import java.util.List;

import com.example.backend.entity.Rating;
import com.example.backend.model.Rating.CreateRatingRequest;
import com.example.backend.model.Rating.UpdateRatingRequest;

public interface IRatingService {
    Rating rateSeller(CreateRatingRequest createRatingRequest, Integer userId);

    Rating rateBuyer(CreateRatingRequest createRatingRequest, Integer userId);

    Rating updateRating(UpdateRatingRequest updateRatingRequest, Integer userId);

    List<Rating> geRatingsByRevieweeId(Integer revieweeId);
}
