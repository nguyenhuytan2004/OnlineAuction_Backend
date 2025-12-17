package com.example.backend.service.implement;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.backend.entity.Conversation;
import com.example.backend.entity.Message;
import com.example.backend.model.Chat.CreateMessageRequest;
import com.example.backend.repository.IConversationRepository;
import com.example.backend.repository.IMessageRepository;
import com.example.backend.repository.IUserRepository;
import com.example.backend.service.IChatService;

@Service
public class ChatService implements IChatService {
  @Autowired
  private IMessageRepository _messageRepository;
  @Autowired
  private IConversationRepository _conversationRepository;
  @Autowired
  private IUserRepository _userRepository;

  @Autowired
  private SimpMessagingTemplate _messagingTemplate;

  @Override
  public List<Conversation> getAllConversations(Integer userId) {
    return _conversationRepository.findByUserIdAndIsActiveTrue(userId);
  }

  @Override
  public List<Message> getMessages(Integer conversationId, Integer beforeId, Integer limit) {
    List<Message> messages;

    if (beforeId == null) {
      messages = _messageRepository.findLatestMessagesByConversation(conversationId, limit);
    } else {
      messages = _messageRepository.findBeforeMessagesByConversation(conversationId, beforeId, limit);
    }

    Collections.reverse(messages);
    return messages;
  }

  @Override
  public Message createMessage(Integer conversationId, CreateMessageRequest createMessageRequest) {
    Message newMessage = Message.builder()
        .conversation(_conversationRepository.findById(conversationId).orElse(null))
        .sender(_userRepository.findById(createMessageRequest.getSenderId()).orElse(null))
        .messageText(createMessageRequest.getMessageText())
        .build();

    Message savedMessage = _messageRepository.save(newMessage);
    _messagingTemplate.convertAndSend("/topic/chat/" + conversationId, savedMessage);
    System.out.println("Sent message to /topic/chat/" + conversationId + ": " + savedMessage.getMessageText());

    return savedMessage;
  }
}
