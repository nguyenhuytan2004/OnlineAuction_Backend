package com.example.backend.model.Admin;

import java.math.BigDecimal;
import java.util.List;

import com.example.backend.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response model for admin dashboard with overview and analytics")
public class AdminDashboardResponse {

  @Schema(description = "Dashboard overview metrics")
  private Overview overview;

  @Schema(description = "Dashboard chart data")
  private Charts charts;

  @Data
  @Schema(description = "Overview statistics for the dashboard")
  public static class Overview {
    @Schema(description = "Total number of auctions", example = "150")
    private long totalAuctions;

    @Schema(description = "Total number of registered users", example = "500")
    private long totalUsers;

    @Schema(description = "Total revenue generated", example = "50000.00")
    private BigDecimal totalRevenue;

    @Schema(description = "New auctions created this month", example = "25")
    private long newAuctionsThisMonth;

    @Schema(description = "New sellers registered this month", example = "10")
    private long newSellersThisMonth;

    @Schema(description = "Auction success rate percentage", example = "0.85")
    private double successRate;

    @Schema(description = "Top performing product")
    private Product topProduct;

    @Schema(description = "Payment success rate percentage", example = "95")
    private int paymentSuccessRate;
  }

  @Data
  @Schema(description = "Chart data for dashboard analytics")
  public static class Charts {
    @Schema(description = "Auctions trend by month")
    private List<MonthCount> auctionsTrend;

    @Schema(description = "Revenue trend by month")
    private List<MonthAmount> revenueTrend;

    @Schema(description = "Users trend by month")
    private List<MonthCount> usersTrend;

    @Schema(description = "Seller upgrade requests trend by month")
    private List<MonthCount> upgradesTrend;
  }

  @Data
  @AllArgsConstructor
  @Schema(description = "Monthly count data for trend charts")
  public static class MonthCount {
    @Schema(description = "Month identifier", example = "2024-01")
    private String month;

    @Schema(description = "Count for the month", example = "15")
    private long count;
  }

  @Data
  @AllArgsConstructor
  @Schema(description = "Monthly amount data for revenue trends")
  public static class MonthAmount {
    @Schema(description = "Month identifier", example = "2024-01")
    private String month;
    private BigDecimal revenue;
  }
}
