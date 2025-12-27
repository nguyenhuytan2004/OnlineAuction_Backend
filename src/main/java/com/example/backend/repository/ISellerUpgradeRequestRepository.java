package com.example.backend.repository;

import com.example.backend.entity.SellerUpgradeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ISellerUpgradeRequestRepository extends JpaRepository<SellerUpgradeRequest, Integer> {

    List<SellerUpgradeRequest> findByStatus(SellerUpgradeRequest.Status status);

    boolean existsByUserUserIdAndStatus(
            Integer userId,
            SellerUpgradeRequest.Status status
    );

    Optional<SellerUpgradeRequest>
    findTopByUser_UserIdOrderByRequestAtDesc(Integer userId);


    @Query("""
    SELECT YEAR(s.requestAt), MONTH(s.requestAt), COUNT(s)
    FROM SellerUpgradeRequest s
    GROUP BY YEAR(s.requestAt), MONTH(s.requestAt)
    ORDER BY YEAR(s.requestAt) DESC, MONTH(s.requestAt) DESC
""")
    List<Object[]> countUpgradeRequestsByMonth();

}
