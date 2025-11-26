package com.example.backend.model.ProductQna.ProductQuestion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductQuestionRequest {
    @NotNull(message = "Product ID must not be null")
    private Integer productId;

    @NotBlank(message = "Question text must not be blank")
    private String questionText;
}
