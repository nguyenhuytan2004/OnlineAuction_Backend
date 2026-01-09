package com.example.backend.model.Chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request model for creating a new message")
public class CreateMessageRequest {
  @NotNull(message = "Sender ID cannot be null")
  @Schema(description = "ID of the message sender", example = "123", required = true)
  private Integer senderId;

  @NotBlank(message = "Message text cannot be blank")
  @Schema(description = "Content of the message", example = "Hello, I'm interested in this item!", required = true)
  private String messageText;
}
