package com.example.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PRODUCT")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Indexed
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;

    @NotNull(message = "Seller must not be null")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seller_id", nullable = false)
    // @JsonBackReference("user-products")
    private User seller;

    @NotNull(message = "Category must not be null")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    // @JsonBackReference("category-products")
    @IndexedEmbedded
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    private Category category;

    @Column(name = "main_image_url")
    private String mainImageUrl;

    // Extra field
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonManagedReference("product-images")
    private List<ProductImage> auxiliaryImageUrls;

    @NotBlank(message = "Product name must not be blank")
    @Size(max = 255, message = "Product name must not exceed 255 characters")
    @Column(name = "product_name", nullable = false, length = 255)
    @FullTextField(analyzer = SearchAnalyzerConfig.VIETNAMESE_SEARCH)
    private String productName;

    @NotNull(message = "Current price must not be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Current price must be greater than 0")
    @Digits(integer = 16, fraction = 2, message = "Invalid current price")
    @Column(name = "current_price", nullable = false, precision = 18, scale = 2)
    // Dùng để sắp xếp
    @GenericField(sortable = Sortable.YES)
    private BigDecimal currentPrice;

    @DecimalMin(value = "0.0", inclusive = false, message = "Buy now price must be greater than 0")
    @Digits(integer = 16, fraction = 2, message = "Invalid buy now price")
    @Column(name = "buy_now_price", precision = 18, scale = 2)
    // Dùng để sắp xếp
    @GenericField(sortable = Sortable.YES)
    private BigDecimal buyNowPrice;

    @NotNull(message = "Start price must not be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Start price must be greater than 0")
    @Digits(integer = 16, fraction = 2, message = "Invalid start price")
    @Column(name = "start_price", nullable = false, precision = 18, scale = 2)
    private BigDecimal startPrice;

    @NotNull(message = "Price step must not be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price step must be greater than 0")
    @Digits(integer = 16, fraction = 2, message = "Invalid price step")
    @Column(name = "price_step", nullable = false, precision = 18, scale = 2)
    private BigDecimal priceStep;

    @Column(name = "description", columnDefinition = "TEXT")
    @FullTextField(analyzer = SearchAnalyzerConfig.VIETNAMESE_SEARCH)
    private String description;

    @NotNull(message = "End time must not be null")
    @Future(message = "End time must be in the future")
    @Column(name = "end_time", nullable = false)
    // Dùng để lọc(Is active?) và sắp xếp
    @GenericField(sortable = Sortable.YES)
    private LocalDateTime endTime;

    @Column(name = "is_auto_renew", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isAutoRenew = false;

    @Min(value = 0, message = "Bid count must be greater than or equal to 0")
    @Column(name = "bid_count", columnDefinition = "INT DEFAULT 0")
    private Integer bidCount = 0;

    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    // Dùng để lọc(Is active?)
    @GenericField(sortable = Sortable.YES)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    // // @JsonManagedReference("product-watchlists")
    // private List<WatchList> watchLists;
}