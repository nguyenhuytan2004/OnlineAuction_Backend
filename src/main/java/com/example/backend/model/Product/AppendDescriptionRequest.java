package com.example.backend.model.Product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request model for appending additional description to a product")
public class AppendDescriptionRequest {
  @NotBlank(message = "Additional description must not be blank")
  @Schema(description = "Additional product description to append", example = "Condition: Like new, Minor cosmetic scratches", required = true)
  private String additionalDescription;
}
