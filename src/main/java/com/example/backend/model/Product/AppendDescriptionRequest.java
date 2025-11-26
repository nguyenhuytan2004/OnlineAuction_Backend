package com.example.backend.model.Product;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppendDescriptionRequest {
    @NotBlank(message = "Additional description must not be blank")
    private String additionalDescription;
}
