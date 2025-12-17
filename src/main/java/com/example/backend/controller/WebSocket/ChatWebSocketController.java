package com.example.backend.controller.WebSocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import com.example.backend.model.Chat.CreateMessageRequest;
import com.example.backend.service.IChatService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ChatWebSocketController {
  @Autowired
  private IChatService _chatService;

  @MessageMapping("chat/{conversation_id}")
  public void sendMessage(
      @DestinationVariable("conversation_id") Integer conversationId,
      @Valid @Payload CreateMessageRequest createMessageRequest) {

    System.out
        .println("Received message for conversation " + conversationId + ": " + createMessageRequest.getMessageText());
    _chatService.createMessage(conversationId, createMessageRequest);
  }
}
