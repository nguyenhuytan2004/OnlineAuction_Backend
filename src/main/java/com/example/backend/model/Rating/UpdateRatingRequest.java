package com.example.backend.model.Rating;

import com.example.backend.validator.RatingValue;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request model for updating a rating")
public class UpdateRatingRequest {
  @NotNull(message = "Product ID cannot be null")
  @Schema(description = "ID of the product being rated", example = "123", required = true)
  private Integer productId;

  @NotNull(message = "Reviewee ID cannot be null")
  @Schema(description = "ID of the user being rated", example = "456", required = true)
  private Integer revieweeId;

  @RatingValue
  @NotNull(message = "Rating value cannot be null")
  @Schema(description = "Rating value: -1 (negative), 0 (neutral), 1 (positive)", example = "1", required = true, allowableValues = {
      "-1", "0", "1" })
  private Integer ratingValue;

  @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
  @Schema(description = "Comment about the rating", example = "Excellent seller", maxLength = 1000)
  private String comment;
}
