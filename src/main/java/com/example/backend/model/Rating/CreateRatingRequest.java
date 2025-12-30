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
public class CreateRatingRequest {
  @NotNull(message = "Product ID is not null")
  private Integer productId;

  @NotNull(message = "Rating value is not null")
  @RatingValue
  private Integer ratingValue;

  @Size(max = 1000, message = "Comment must not exceed 1000 characters")
  private String comment;
}
