package com.example.backend.model.ProductQna.ProductQuestion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request model for creating a product question")
public class CreateProductQuestionRequest {
  @NotNull(message = "User ID must not be null")
  @Schema(description = "ID of the user asking the question", example = "123", required = true)
  private Integer userId;

  @NotNull(message = "Product ID must not be null")
  @Schema(description = "ID of the product being asked about", example = "456", required = true)
  private Integer productId;

  @NotBlank(message = "Question text must not be blank")
  @Size(max = 1000, message = "Question text must not exceed 1000 characters")
  @Schema(description = "The question text", example = "Is this item still available?", required = true, maxLength = 1000)
  private String questionText;
}
