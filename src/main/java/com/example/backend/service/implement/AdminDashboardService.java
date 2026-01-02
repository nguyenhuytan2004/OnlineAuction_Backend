package com.example.backend.service.implement;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.backend.model.Admin.AdminDashboardResponse;
import com.example.backend.repository.IAuctionOrderRepository;
import com.example.backend.repository.IAuctionResultRepository;
import com.example.backend.repository.IProductRepository;
import com.example.backend.repository.ISellerUpgradeRequestRepository;
import com.example.backend.repository.IUserRepository;
import com.example.backend.repository.IWatchListRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminDashboardService {

  private final IProductRepository productRepo;
  private final IUserRepository userRepo;
  private final IAuctionResultRepository auctionResultRepo;
  private final ISellerUpgradeRequestRepository upgradeRepo;
  private final IWatchListRepository watchListRepo;
  private final IAuctionOrderRepository orderRepo;

  public AdminDashboardResponse getDashboard() {
    log.info("[SERVICE][GET][ADMIN_DASHBOARD] Start");

    try {
      AdminDashboardResponse res = new AdminDashboardResponse();

      AdminDashboardResponse.Overview ov = new AdminDashboardResponse.Overview();
      ov.setTotalAuctions(productRepo.countAll());
      ov.setTotalUsers(userRepo.countActiveUsers());
      ov.setTotalRevenue(auctionResultRepo.sumRevenue());
      ov.setNewAuctionsThisMonth(productRepo.countThisMonth());
      ov.setNewSellersThisMonth(userRepo.countNewSellersThisMonth());
      ov.setSuccessRate(
          Optional.ofNullable(auctionResultRepo.successRate()).orElse(0.0));
      ov.setTopProduct(
          productRepo.findTopByOrderByBidCountDesc());
      ov.setPaymentSuccessRate(
          Optional.ofNullable(orderRepo.paymentSuccessRate()).orElse(0));

      AdminDashboardResponse.Charts charts = new AdminDashboardResponse.Charts();
      charts.setAuctionsTrend(
          mapMonthCount(productRepo.countProductsByMonth()));
      charts.setUsersTrend(
          mapMonthCount(userRepo.countUsersByMonth()));
      charts.setUpgradesTrend(
          mapMonthCount(upgradeRepo.countUpgradeRequestsByMonth()));
      charts.setRevenueTrend(
          mapMonthAmount(auctionResultRepo.revenueByMonth()));

      res.setOverview(ov);
      res.setCharts(charts);

      log.info(
          "[SERVICE][GET][ADMIN_DASHBOARD] Success (overview={}, charts={})",
          ov, charts);
      return res;

    } catch (Exception e) {
      log.error(
          "[SERVICE][GET][ADMIN_DASHBOARD] Error occurred: {}",
          e.getMessage(),
          e);
      throw e;
    }
  }

  private List<AdminDashboardResponse.MonthCount> mapMonthCount(List<Object[]> rows) {
    log.info(
        "[SERVICE][MAP][MONTH_COUNT] Input rows={}",
        rows != null ? rows.size() : 0);

    try {
      List<AdminDashboardResponse.MonthCount> result = rows.stream()
          .limit(6)
          .map(r -> {
            int year = ((Number) r[0]).intValue();
            int month = ((Number) r[1]).intValue();
            long count = ((Number) r[2]).longValue();

            String label = YearMonth.of(year, month)
                .getMonth()
                .getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

            return new AdminDashboardResponse.MonthCount(label, count);
          })
          .toList();

      log.info(
          "[SERVICE][MAP][MONTH_COUNT] Output size={}",
          result.size());
      return result;

    } catch (Exception e) {
      log.error(
          "[SERVICE][MAP][MONTH_COUNT] Error occurred: {}",
          e.getMessage(),
          e);
      throw e;
    }
  }

  private List<AdminDashboardResponse.MonthAmount> mapMonthAmount(List<Object[]> rows) {
    log.info(
        "[SERVICE][MAP][MONTH_AMOUNT] Input rows={}",
        rows != null ? rows.size() : 0);

    try {
      List<AdminDashboardResponse.MonthAmount> result = rows.stream()
          .limit(6)
          .map(r -> {
            int year = ((Number) r[0]).intValue();
            int month = ((Number) r[1]).intValue();
            BigDecimal revenue = (BigDecimal) r[2];

            String label = YearMonth.of(year, month)
                .getMonth()
                .getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

            return new AdminDashboardResponse.MonthAmount(label, revenue);
          })
          .toList();

      log.info(
          "[SERVICE][MAP][MONTH_AMOUNT] Output size={}",
          result.size());
      return result;

    } catch (Exception e) {
      log.error(
          "[SERVICE][MAP][MONTH_AMOUNT] Error occurred: {}",
          e.getMessage(),
          e);
      throw e;
    }
  }
}
