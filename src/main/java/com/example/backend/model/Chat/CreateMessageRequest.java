package com.example.backend.model.Chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMessageRequest {
  @NotNull(message = "Sender ID cannot be null")
  private Integer senderId;

  @NotBlank(message = "Message text cannot be blank")
  private String messageText;
}
