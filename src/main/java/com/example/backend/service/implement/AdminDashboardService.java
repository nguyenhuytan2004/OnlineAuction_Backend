package com.example.backend.service.implement;

import com.example.backend.model.Admin.AdminDashboardResponse;
import com.example.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final IProductRepository productRepo;
    private final IUserRepository userRepo;
    private final IAuctionResultRepository auctionResultRepo;
    private final ISellerUpgradeRequestRepository upgradeRepo;
    private final IWatchListRepository watchListRepo;
    private final IAuctionOrderRepository orderRepo;

    public AdminDashboardResponse getDashboard() {

        AdminDashboardResponse res = new AdminDashboardResponse();

        // OVERVIEW
        AdminDashboardResponse.Overview ov = new AdminDashboardResponse.Overview();
        ov.setTotalAuctions(productRepo.countAll());
        ov.setTotalUsers(userRepo.countActiveUsers());
        ov.setTotalRevenue(auctionResultRepo.sumRevenue());
        ov.setNewAuctionsThisMonth(productRepo.countThisMonth());
        ov.setNewSellersThisMonth(userRepo.countNewSellersThisMonth());
        ov.setSuccessRate(
                Optional.ofNullable(auctionResultRepo.successRate()).orElse(0.0)
        );
        ov.setTopProduct(
                watchListRepo.findTopProduct().stream().findFirst().orElse("N/A")
        );
        ov.setPaymentSuccessRate(
                Optional.ofNullable(orderRepo.paymentSuccessRate()).orElse(0)
        );

        // CHARTS
        AdminDashboardResponse.Charts charts = new AdminDashboardResponse.Charts();

        charts.setAuctionsTrend(mapMonthCount(productRepo.countProductsByMonth()));
        charts.setUsersTrend(mapMonthCount(userRepo.countUsersByMonth()));
        charts.setUpgradesTrend(mapMonthCount(upgradeRepo.countUpgradeRequestsByMonth()));
        charts.setRevenueTrend(mapMonthAmount(auctionResultRepo.revenueByMonth()));

        res.setOverview(ov);
        res.setCharts(charts);

        return res;
    }

    private List<AdminDashboardResponse.MonthCount> mapMonthCount(List<Object[]> rows) {
        return rows.stream()
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
    }


    private List<AdminDashboardResponse.MonthAmount> mapMonthAmount(List<Object[]> rows) {
        return rows.stream()
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
    }

}
