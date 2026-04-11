package com.example.backend.exception;

import java.math.BigDecimal;

public class ConcurrentBidException extends RuntimeException {
  private final Integer productId;
  private final BigDecimal currentPrice;
  private final BigDecimal priceStep;
  private final Integer highestBidderId;

  public ConcurrentBidException(
      String message,
      Integer productId,
      BigDecimal currentPrice,
      BigDecimal priceStep,
      Integer highestBidderId,
      Throwable cause) {
    super(message, cause);
    this.productId = productId;
    this.currentPrice = currentPrice;
    this.priceStep = priceStep;
    this.highestBidderId = highestBidderId;
  }

  public Integer getProductId() {
    return productId;
  }

  public BigDecimal getCurrentPrice() {
    return currentPrice;
  }

  public BigDecimal getPriceStep() {
    return priceStep;
  }

  public Integer getHighestBidderId() {
    return highestBidderId;
  }
}
