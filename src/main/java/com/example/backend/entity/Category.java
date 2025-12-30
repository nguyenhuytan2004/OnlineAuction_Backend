package com.example.backend.entity;

import java.time.LocalDateTime;

import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import com.example.backend.config.SearchAnalyzerConfig;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "category_id")
  @GenericField(sortable = Sortable.YES)
  private Integer categoryId;

  @NotBlank(message = "Category name must not be blank")
  @Size(max = 100, message = "Category name must not exceed 100 characters")
  @Column(name = "category_name", nullable = false, length = 100)
  @FullTextField(analyzer = SearchAnalyzerConfig.VIETNAMESE_SEARCH)
  private String categoryName;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "parent_id")
  // @JsonBackReference("category-subcategories")
  private Category parent;

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
