package com.example.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Schema(description = "Product image entity for storing auxiliary product images")
@Entity
@Table(name = "PRODUCT_IMAGE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {
    "product"
})
public class ProductImage {

  @Schema(description = "Unique image identifier", example = "1", required = true)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long imageId;

  @Schema(description = "The product this image belongs to")
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "product_id", nullable = false)
  @JsonBackReference("product-images")
  private Product product;

  @Schema(description = "URL of the product image", example = "https://example.com/product-image.jpg", required = true)
  @Column(name = "image_url", nullable = false)
  private String imageUrl;
}
