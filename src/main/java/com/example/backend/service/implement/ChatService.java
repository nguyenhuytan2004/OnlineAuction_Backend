package com.example.backend.service.implement;

import java.util.Collections;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
    log.info(
            "[SERVICE][GET][CONVERSATIONS] Input userId={}",
            userId
    );

    try {
      List<Conversation> conversations =
              _conversationRepository.findByUserIdAndIsActiveTrue(userId);

      log.info(
              "[SERVICE][GET][CONVERSATIONS] Output conversations={}",
              conversations
      );
      return conversations;

    } catch (Exception e) {
      log.error(
              "[SERVICE][GET][CONVERSATIONS] Error occurred (userId={}): {}",
              userId,
              e.getMessage(),
              e
      );
      throw e;
    }
  }

  @Override
  public List<Message> getMessages(Integer conversationId, Integer beforeId, Integer limit) {
    log.info(
            "[SERVICE][GET][MESSAGES] Input conversationId={}, beforeId={}, limit={}",
            conversationId,
            beforeId,
            limit
    );

    try {
      List<Message> messages;

      if (beforeId == null) {
        messages = _messageRepository
                .findLatestMessagesByConversation(conversationId, limit);
      } else {
        messages = _messageRepository
                .findBeforeMessagesByConversation(conversationId, beforeId, limit);
      }

      Collections.reverse(messages);

      log.info(
              "[SERVICE][GET][MESSAGES] Output messages={}",
              messages
      );
      return messages;

    } catch (Exception e) {
      log.error(
              "[SERVICE][GET][MESSAGES] Error occurred (conversationId={}): {}",
              conversationId,
              e.getMessage(),
              e
      );
      throw e;
    }
  }

  @Override
  public Message createMessage(
          Integer conversationId,
          CreateMessageRequest createMessageRequest) {

    log.info(
            "[SERVICE][POST][CREATE_MESSAGE] Input conversationId={}, request={}",
            conversationId,
            createMessageRequest
    );

    try {
      Message newMessage = Message.builder()
              .conversation(
                      _conversationRepository.findById(conversationId).orElse(null)
              )
              .sender(
                      _userRepository.findById(createMessageRequest.getSenderId()).orElse(null)
              )
              .messageText(createMessageRequest.getMessageText())
              .build();

      Message savedMessage = _messageRepository.save(newMessage);

      _messagingTemplate.convertAndSend(
              "/topic/chat/" + conversationId,
              savedMessage
      );

      log.info(
              "[SERVICE][POST][CREATE_MESSAGE] Success message={}",
              savedMessage
      );

      return savedMessage;

    } catch (Exception e) {
      log.error(
              "[SERVICE][POST][CREATE_MESSAGE] Error occurred (conversationId={}): {}",
              conversationId,
              e.getMessage(),
              e
      );
      throw e;
    }
  }
}
