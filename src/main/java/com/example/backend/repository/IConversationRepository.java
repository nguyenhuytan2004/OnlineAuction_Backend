package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend.entity.Conversation;

public interface IConversationRepository extends JpaRepository<Conversation, Integer> {
  @Query("SELECT c FROM Conversation c " +
      "WHERE (c.seller.userId = :userId OR c.buyer.userId = :userId) " +
      "AND c.isActive = true " +
      "ORDER BY c.conversationId DESC")
  List<Conversation> findByUserIdAndIsActiveTrue(@Param("userId") Integer userId);
}
