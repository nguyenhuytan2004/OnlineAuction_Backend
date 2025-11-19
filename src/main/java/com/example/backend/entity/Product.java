package com.example.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;

    @NotNull(message = "Seller must not be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @NotNull(message = "Category must not be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @NotBlank(message = "Product name must not be blank")
    @Size(max = 255, message = "Product name must not exceed 255 characters")
    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @NotNull(message = "Current price must not be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Current price must be greater than 0")
    @Digits(integer = 16, fraction = 2, message = "Invalid current price")
    @Column(name = "current_price", nullable = false, precision = 18, scale = 2)
    private BigDecimal currentPrice;

    @DecimalMin(value = "0.0", inclusive = false, message = "Buy now price must be greater than 0")
    @Digits(integer = 16, fraction = 2, message = "Invalid buy now price")
    @Column(name = "buy_now_price", precision = 18, scale = 2)
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
    private String description;

    @NotNull(message = "End time must not be null")
    @Future(message = "End time must be in the future")
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "is_auto_renew", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isAutoRenew = false;

    @Min(value = 0, message = "Bid count must be greater than or equal to 0")
    @Column(name = "bid_count", columnDefinition = "INT DEFAULT 0")
    private Integer bidCount = 0;

    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<WatchList> watchLists;
}
