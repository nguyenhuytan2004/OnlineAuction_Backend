package com.example.backend.entity.ProductQnA;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.example.backend.entity.Product;
import com.example.backend.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Schema(description = "Product Question Entity")
@Entity
@Table(name = "PRODUCT_QUESTION")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {
    "product",
    "questionUser",
    "answers"
})
public class ProductQuestion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "question_id")
  @Schema(description = "Question ID")
  private Integer questionId;

  @NotNull(message = "Product must not be null")
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "product_id", nullable = false)
  @Schema(description = "Product associated with question")
  private Product product;

  @NotNull(message = "Question user must not be null")
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "question_user_id", nullable = false)
  @Schema(description = "User who asked the question")
  private User questionUser;

  @NotBlank(message = "Question text must not be blank")
  @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
  @Schema(description = "Question text content")
  private String questionText;

  @CreationTimestamp
  @Column(name = "question_at", nullable = false, updatable = false)
  @Schema(description = "Timestamp when question was created")
  private LocalDateTime questionAt;

  @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
  @JsonManagedReference("question-answers")
  @Schema(description = "Answers to this question")
  private List<ProductAnswer> answers;
}
