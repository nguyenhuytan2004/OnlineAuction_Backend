package com.example.backend.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "message")
@ToString(exclude = {
        "conversation",
        "sender"
})
public class Message {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer messageId;

  @ManyToOne
  @JoinColumn(name = "conversation_id", nullable = false)
  private Conversation conversation;

  @ManyToOne
  @JoinColumn(name = "sender_id", nullable = false)
  private User sender;

  @Column(name = "message_text", nullable = false)
  private String messageText;

  @CreationTimestamp
  @Column(name = "sent_at", nullable = false)
  private String sentAt;
}
