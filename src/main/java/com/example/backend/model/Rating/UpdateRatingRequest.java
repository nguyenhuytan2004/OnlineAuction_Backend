package com.example.backend.model.Rating;

import com.example.backend.validator.RatingValue;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRatingRequest {
  @NotNull(message = "Product ID cannot be null")
  private Integer productId;

  @NotNull(message = "Reviewee ID cannot be null")
  private Integer revieweeId;

  @RatingValue
  @NotNull(message = "Rating value cannot be null")
  private Integer ratingValue;

  @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
  private String comment;
}
