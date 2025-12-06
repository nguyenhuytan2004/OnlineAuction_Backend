package com.example.backend.model.ProductQna.ProductAnswer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductAnswerRequest {
    @NotNull(message = "User ID must not be null")
    private Integer userId;

    @NotNull(message = "Product ID must not be null")
    private Integer productId;

    @NotNull(message = "Question ID must not be null")
    private Integer questionId;

    @NotBlank(message = "Answer text must not be blank")
    @Size(max = 1000, message = "Answer text must not exceed 1000 characters")
    private String answerText;
}
