package com.example.backend.model.ProductQna.ProductAnswer;

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
@Schema(description = "Request model for answering a product question")
public class CreateProductAnswerRequest {
  @NotNull(message = "User ID must not be null")
  @Schema(description = "ID of the user providing the answer", example = "789", required = true)
  private Integer userId;

  @NotNull(message = "Product ID must not be null")
  @Schema(description = "ID of the product", example = "456", required = true)
  private Integer productId;

  @NotNull(message = "Question ID must not be null")
  @Schema(description = "ID of the question being answered", example = "111", required = true)
  private Integer questionId;

  @NotBlank(message = "Answer text must not be blank")
  @Size(max = 1000, message = "Answer text must not exceed 1000 characters")
  @Schema(description = "The answer text", example = "Yes, it's still available and in perfect condition.", required = true, maxLength = 1000)
  private String answerText;
}
