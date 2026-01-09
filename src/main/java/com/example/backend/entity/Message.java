package com.example.backend.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Schema(description = "Message entity representing a message in a conversation between users")
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
  @Schema(description = "Unique message identifier", example = "1", required = true)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer messageId;

  @Schema(description = "The conversation this message belongs to")
  @ManyToOne
  @JoinColumn(name = "conversation_id", nullable = false)
  private Conversation conversation;

  @Schema(description = "The user who sent this message")
  @ManyToOne
  @JoinColumn(name = "sender_id", nullable = false)
  private User sender;

  @Schema(description = "The content of the message", example = "Is this item still available?")
  @Column(name = "message_text", nullable = false)
  private String messageText;

  @Schema(description = "Timestamp when the message was sent", format = "date-time")
  @CreationTimestamp
  @Column(name = "sent_at", nullable = false)
  private String sentAt;
}
