package com.example.backend.service;

import java.util.List;

import com.example.backend.entity.Conversation;
import com.example.backend.entity.Message;
import com.example.backend.model.Chat.CreateMessageRequest;

public interface IChatService {
  List<Conversation> getAllConversations(Integer userId);

  List<Message> getMessages(Integer conversationId, Integer beforeId, Integer limit);

  Message createMessage(Integer conversationId, CreateMessageRequest message);

}
