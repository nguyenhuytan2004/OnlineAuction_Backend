package com.example.backend.repository;

import com.example.backend.entity.SellerUpgradeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ISellerUpgradeRequestRepository extends JpaRepository<SellerUpgradeRequest, Integer> {

    List<SellerUpgradeRequest> findByStatus(SellerUpgradeRequest.Status status);

    boolean existsByUser_UserIdAndStatus(
            Integer userId,
            SellerUpgradeRequest.Status status);


    @Query("""
    SELECT YEAR(s.requestAt), MONTH(s.requestAt), COUNT(s)
    FROM SellerUpgradeRequest s
    GROUP BY YEAR(s.requestAt), MONTH(s.requestAt)
    ORDER BY YEAR(s.requestAt) DESC, MONTH(s.requestAt) DESC
""")
    List<Object[]> countUpgradeRequestsByMonth();

}
