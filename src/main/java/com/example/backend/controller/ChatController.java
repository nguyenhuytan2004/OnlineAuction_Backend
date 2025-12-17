package com.example.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.entity.Conversation;
import com.example.backend.entity.Message;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.service.IChatService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/chat")
public class ChatController {
  @Autowired
  private IChatService _chatService;

  @GetMapping("/conversations")
  public ResponseEntity<?> getAllConversations(@AuthenticationPrincipal CustomUserDetails userDetails) {
    try {
      List<Conversation> conversations = _chatService.getAllConversations(userDetails.getUser().getUserId());

      return new ResponseEntity<>(conversations, HttpStatus.OK);
    } catch (Exception e) {
      log.error("[CONTROLLER][GET][ERROR] /api/chat/conversations - Error occured: {}", e.getMessage(), e);

      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/conversations/{conversation_id}/messages")
  public ResponseEntity<?> getMessages(
      @PathVariable("conversation_id") Integer conversationId,
      @RequestParam(name = "beforeId", required = false) Integer beforeId,
      @RequestParam(name = "limit", defaultValue = "20") Integer limit) {
    try {
      List<Message> messages = _chatService.getMessages(conversationId, beforeId, limit);

      return new ResponseEntity<>(messages, HttpStatus.OK);
    } catch (Exception e) {
      log.error("[CONTROLLER][GET][ERROR] /api/chat/conversations/{}/messages - Error occured: {}",
          conversationId, e.getMessage(), e);

      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
