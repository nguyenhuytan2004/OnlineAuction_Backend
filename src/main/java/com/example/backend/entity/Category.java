package com.example.backend.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import com.example.backend.config.SearchAnalyzerConfig;
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
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Schema(description = "Product category entity for organizing products in the auction system")
@Entity
@Table(name = "CATEGORY")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Indexed
@ToString(exclude = {
    "parent"
})
public class Category {

  @Schema(description = "Unique category identifier", example = "1", required = true)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "category_id")
  @GenericField(sortable = Sortable.YES)
  private Integer categoryId;

  @Schema(description = "Category name/title", example = "Electronics", required = true)
  @NotBlank(message = "Category name must not be blank")
  @Size(max = 100, message = "Category name must not exceed 100 characters")
  @Column(name = "category_name", nullable = false, length = 100)
  @FullTextField(analyzer = SearchAnalyzerConfig.VIETNAMESE_SEARCH)
  private String categoryName;

  @Schema(description = "Detailed description of the category")
  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Schema(description = "Parent category if this is a subcategory")
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "parent_id")
  private Category parent;

  @Schema(description = "Category creation timestamp", format = "date-time")
  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  // @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
  // // @JsonManagedReference("category-subcategories")
  // private List<Category> subCategories;
  // @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
  // // @JsonManagedReference("category-products")
  // private List<Product> products;
}
