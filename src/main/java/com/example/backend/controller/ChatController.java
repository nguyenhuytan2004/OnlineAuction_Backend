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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/chat")
public class ChatController {
  @Autowired
  private IChatService _chatService;

  @Operation(summary = "Get all conversations", description = "Retrieve all conversations for the authenticated user. Requires authentication.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved conversations", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Conversation.class)))),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
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

  @Operation(summary = "Get messages from a conversation", description = "Retrieve messages from a specific conversation with pagination support.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved messages", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Message.class)))),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
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
