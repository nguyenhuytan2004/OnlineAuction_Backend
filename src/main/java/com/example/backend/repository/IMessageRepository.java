package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend.entity.Message;

public interface IMessageRepository extends JpaRepository<Message, Integer> {

  @Query("SELECT m FROM Message m WHERE m.conversation.conversationId = :conversationId " +
      "ORDER BY m.messageId DESC LIMIT :limit")
  List<Message> findLatestMessagesByConversation(
      @Param("conversationId") Integer conversationId,
      @Param("limit") Integer limit);

  @Query("SELECT m FROM Message m WHERE m.conversation.conversationId = :conversationId " +
      "AND m.messageId < :beforeId " +
      "ORDER BY m.messageId DESC LIMIT :limit")
  List<Message> findBeforeMessagesByConversation(
      @Param("conversationId") Integer conversationId,
      @Param("beforeId") Integer beforeId,
      @Param("limit") Integer limit);
}
