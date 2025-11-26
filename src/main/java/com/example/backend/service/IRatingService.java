package com.example.backend.service;

import com.example.backend.entity.Rating;
import com.example.backend.model.Rating.CreateRatingRequest;

public interface IRatingService {
    Rating rateSeller(CreateRatingRequest createRatingRequest, Integer userId);
}
