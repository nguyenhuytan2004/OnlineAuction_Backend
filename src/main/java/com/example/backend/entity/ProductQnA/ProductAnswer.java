package com.example.backend.entity.ProductQnA;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.example.backend.entity.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Schema(description = "Product Answer entity")
@Entity
@Table(name = "PRODUCT_ANSWER")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {
    "question",
    "answerUser"
})
public class ProductAnswer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "answer_id")
  @Schema(description = "Answer ID")
  private Integer answerId;

  @NotNull(message = "Question must not be null")
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "question_id", nullable = false)
  @JsonBackReference("question-answers")
  @Schema(description = "Related product question")
  private ProductQuestion question;

  @NotNull(message = "Answer user must not be null")
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "answer_user_id", nullable = false)
  @Schema(description = "User who answered the question")
  private User answerUser;

  @NotBlank(message = "Answer text must not be blank")
  @Column(name = "answer_text", nullable = false, columnDefinition = "TEXT")
  @Schema(description = "Answer content")
  private String answerText;

  @CreationTimestamp
  @Column(name = "answer_at", nullable = false, updatable = false)
  @Schema(description = "Timestamp when answer was created")
  private LocalDateTime answerAt;
}
