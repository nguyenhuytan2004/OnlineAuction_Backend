package com.example.backend.model.Rating;

import com.example.backend.validator.RatingValue;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request model for rating a seller or product")
public class CreateRatingRequest {
  @NotNull(message = "Product ID is not null")
  @Schema(description = "ID of the product being rated", example = "123", required = true)
  private Integer productId;

  @NotNull(message = "Rating value is not null")
  @RatingValue
  @Schema(description = "Rating value: -1 (negative), 0 (neutral), 1 (positive)", example = "1", required = true, allowableValues = {
      "-1", "0", "1" })
  private Integer ratingValue;

  @Size(max = 1000, message = "Comment must not exceed 1000 characters")
  @Schema(description = "Optional comment about the rating", example = "Great seller, fast shipping!", maxLength = 1000)
  private String comment;
}
