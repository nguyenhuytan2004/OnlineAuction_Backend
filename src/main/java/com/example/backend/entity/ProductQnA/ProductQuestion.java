package com.example.backend.entity.ProductQnA;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.example.backend.entity.Product;
import com.example.backend.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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

@Entity
@Table(name = "PRODUCT_QUESTION")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Integer questionId;

    @NotNull(message = "Product must not be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull(message = "Question user must not be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_user_id", nullable = false)
    private User questionUser;

    @NotBlank(message = "Question text must not be blank")
    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @CreationTimestamp
    @Column(name = "question_at", nullable = false, updatable = false)
    private LocalDateTime questionAt;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @JsonManagedReference("question-answers")
    private List<ProductAnswer> answers;
}
