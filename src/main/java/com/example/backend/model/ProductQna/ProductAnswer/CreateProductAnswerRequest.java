package com.example.backend.model.ProductQna.ProductAnswer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductAnswerRequest {
    @NotNull(message = "Question ID must not be null")
    private Integer questionId;

    @NotBlank(message = "Answer text must not be blank")
    private String answerText;
}
