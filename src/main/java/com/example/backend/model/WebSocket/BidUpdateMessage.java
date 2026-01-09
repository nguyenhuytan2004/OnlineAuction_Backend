package com.example.backend.model.WebSocket;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "WebSocket message model for bid updates")
public class BidUpdateMessage {
  @Schema(description = "Message type indicating the nature of bid update", enumAsRef = true)
  public enum MessageType {
    NEWBID, OUTBID, LEADING
  }

  // Product info
  @Schema(description = "Product ID", example = "123", required = true)
  private Integer productId;

  @Schema(description = "Product name", example = "iPhone 14 Pro", required = true)
  private String productName;

  // Price info
  @Schema(description = "Current highest bid price", example = "450.00")
  private BigDecimal currentPrice;

  @Schema(description = "Previous bid price", example = "400.00")
  private BigDecimal previousPrice;

  @Schema(description = "Minimum price increment", example = "10.00")
  private BigDecimal priceStep;

  // Highest bidder info
  @Schema(description = "ID of the highest bidder", example = "456")
  private Integer highestBidderId;

  @Schema(description = "Name of the highest bidder", example = "John Doe")
  private String highestBidderName;

  // New bid info
  @Schema(description = "New bid ID", example = "789")
  private Integer newBidId;

  @Schema(description = "Bidder ID who placed the new bid", example = "100")
  private Integer newBidderId;

  @Schema(description = "Name of the bidder who placed the new bid", example = "Jane Smith")
  private String newBidderName;

  @Schema(description = "The new bid amount", example = "500.00")
  private BigDecimal newBidPrice;

  @Schema(description = "Maximum automatic bid price if auto-bidding", example = "550.00")
  private BigDecimal newBidMaxPrice;

  @Schema(description = "Timestamp when the bid was placed", format = "date-time")
  private LocalDateTime bidAt;

  // Metadata
  @Schema(description = "Total number of bids on this product", example = "15")
  private Integer totalBids;

  @Schema(description = "Type of bid update message", example = "NEWBID", required = true, allowableValues = { "NEWBID",
      "OUTBID", "LEADING" })
  private MessageType messageType;

  @Schema(description = "Human-readable message about the bid", example = "You have been outbid")
  private String message;
}