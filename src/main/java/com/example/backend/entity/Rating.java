package com.example.backend.entity;

import java.time.LocalDateTime;

import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import com.example.backend.validator.RatingValue;

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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "RATING")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {
        "product",
        "reviewer",
        "reviewee"
})
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rating_id")
    @Schema(description = "Unique identifier for the rating", example = "1")
    private Integer ratingId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    @NotNull(message = "Product is required")
    @Schema(description = "The product associated with the rating")
    private Product product;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reviewer_id", nullable = false)
    @NotNull(message = "Winner is required")
    @Schema(description = "The user who gave the rating")
    private User reviewer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reviewee_id", nullable = false)
    @NotNull(message = "Seller is required")
    @Schema(description = "The user who received the rating")
    private User reviewee;

    @Column(name = "rating_value", nullable = false)
    @NotNull(message = "Rating value is required")
    @RatingValue
    @Schema(description = "Rating value must be -1, 0, or 1", example = "1")
    private Integer ratingValue;

    @Column(name = "comment", columnDefinition = "TEXT")
    @Size(max = 1000, message = "Comment must not exceed 1000 characters")
    @Schema(description = "Optional comment for the rating", example = "Great seller, fast shipping!")
    private String comment;

    @CreationTimestamp
    @Column(name = "rated_at")
    @Schema(description = "Timestamp when the rating was created")
    private LocalDateTime ratedAt;
}
