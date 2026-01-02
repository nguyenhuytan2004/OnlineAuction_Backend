package com.example.backend.model.Admin;

import java.math.BigDecimal;
import java.util.List;

import com.example.backend.entity.Product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminDashboardResponse {

  private Overview overview;
  private Charts charts;

  @Data
  public static class Overview {
    private long totalAuctions;
    private long totalUsers;
    private BigDecimal totalRevenue;
    private long newAuctionsThisMonth;
    private long newSellersThisMonth;
    private double successRate;
    private Product topProduct;
    private int paymentSuccessRate;
  }

  @Data
  public static class Charts {
    private List<MonthCount> auctionsTrend;
    private List<MonthAmount> revenueTrend;
    private List<MonthCount> usersTrend;
    private List<MonthCount> upgradesTrend;
  }

  @Data
  @AllArgsConstructor
  public static class MonthCount {
    private String month;
    private long count;
  }

  @Data
  @AllArgsConstructor
  public static class MonthAmount {
    private String month;
    private BigDecimal revenue;
  }
}
