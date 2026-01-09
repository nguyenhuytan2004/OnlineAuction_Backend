package com.example.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import com.example.backend.config.SearchAnalyzerConfig;
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
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Product entity in the online auction system with bidding functionality")
@Entity
@Table(name = "PRODUCT")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Indexed
@ToString(exclude = {
    "seller",
    "category",
    "auxiliaryImages",
    "highestBidder"
})
public class Product {

  @Schema(description = "Unique product identifier", example = "456", required = true)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "product_id")
  private Integer productId;

  @Schema(description = "The seller offering this product")
  @NotNull(message = "Seller must not be null")
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "seller_id", nullable = false)
  private User seller;

  @Schema(description = "The category this product belongs to")
  @NotNull(message = "Category must not be null")
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "category_id", nullable = false)
  @IndexedEmbedded
  @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
  private Category category;

  @Schema(description = "URL of the main product image", example = "https://example.com/product.jpg")
  @Column(name = "main_image_url")
  private String mainImageUrl;

  @Schema(description = "List of auxiliary/additional product images")
  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JsonManagedReference("product-images")
  private List<ProductImage> auxiliaryImages;

  @Schema(description = "Product name/title", example = "Vintage Leather Watch", required = true)
  @NotBlank(message = "Product name must not be blank")
  @Size(max = 255, message = "Product name must not exceed 255 characters")
  @Column(name = "product_name", nullable = false, length = 255)
  @FullTextField(analyzer = SearchAnalyzerConfig.VIETNAMESE_SEARCH)
  private String productName;

  @Schema(description = "Current highest bid price", example = "150.50", minimum = "0")
  @NotNull(message = "Current price must not be null")
  @DecimalMin(value = "0.0", inclusive = false, message = "Current price must be greater than 0")
  @Digits(integer = 16, fraction = 2, message = "Invalid current price")
  @Column(name = "current_price", nullable = false, precision = 18, scale = 2)
  @GenericField(sortable = Sortable.YES)
  private BigDecimal currentPrice;

  @Schema(description = "User who currently has the highest bid")
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "highest_bidder_id")
  private User highestBidder;

  @Schema(description = "Buy now price to purchase product immediately", example = "200.00", minimum = "0")
  @DecimalMin(value = "0.0", inclusive = false, message = "Buy now price must be greater than 0")
  @Digits(integer = 16, fraction = 2, message = "Invalid buy now price")
  @Column(name = "buy_now_price", precision = 18, scale = 2)
  @GenericField(sortable = Sortable.YES)
  private BigDecimal buyNowPrice;

  @Schema(description = "Starting price for the auction", example = "100.00", minimum = "0", required = true)
  @NotNull(message = "Start price must not be null")
  @DecimalMin(value = "0.0", inclusive = false, message = "Start price must be greater than 0")
  @Digits(integer = 16, fraction = 2, message = "Invalid start price")
  @Column(name = "start_price", nullable = false, precision = 18, scale = 2)
  private BigDecimal startPrice;

  @Schema(description = "Minimum increment for each bid", example = "5.00", minimum = "0", required = true)
  @NotNull(message = "Price step must not be null")
  @DecimalMin(value = "0.0", inclusive = false, message = "Price step must be greater than 0")
  @Digits(integer = 16, fraction = 2, message = "Invalid price step")
  @Column(name = "price_step", nullable = false, precision = 18, scale = 2)
  private BigDecimal priceStep;

  @Schema(description = "Detailed product description")
  @Column(name = "description", columnDefinition = "TEXT")
  @FullTextField(analyzer = SearchAnalyzerConfig.VIETNAMESE_SEARCH)
  private String description;

  @Schema(description = "Auction end date and time", format = "date-time", required = true)
  @NotNull(message = "End time must not be null")
  @Column(name = "end_time", nullable = false)
  @GenericField(sortable = Sortable.YES)
  private LocalDateTime endTime;

  @Schema(description = "Whether to automatically renew the auction when it ends", example = "false")
  @Column(name = "is_auto_renew")
  private Boolean isAutoRenew = false;

  @Schema(description = "Total number of bids placed on this product", example = "5", minimum = "0")
  @Min(value = 0, message = "Bid count must be greater than or equal to 0")
  @Column(name = "bid_count", columnDefinition = "INT DEFAULT 0")
  private Integer bidCount = 0;

  @Schema(description = "Whether the product is currently active/available for bidding", example = "true")
  @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
  @GenericField(sortable = Sortable.YES)
  private Boolean isActive = true;

  @Schema(description = "Whether unrated bidders are allowed to bid on this product", example = "true")
  @Column(name = "allow_unrated_bidder")
  private Boolean allowUnratedBidder = true;

  @Schema(description = "Product creation timestamp", format = "date-time")
  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  @GenericField(sortable = Sortable.YES)
  private LocalDateTime createdAt;

  // @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
  // // @JsonManagedReference("product-watchlists")
  // private List<WatchList> watchLists;
}
