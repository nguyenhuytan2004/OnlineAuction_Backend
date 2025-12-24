package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.User;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IUserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true")
    long countActiveUsers();

    @Query("""
        SELECT COUNT(u)
        FROM User u
        WHERE u.role = 'SELLER'
          AND MONTH(u.createdAt) = MONTH(CURRENT_DATE)
          AND YEAR(u.createdAt) = YEAR(CURRENT_DATE)
    """)
    long countNewSellersThisMonth();

    @Query("""
        SELECT YEAR(u.createdAt), MONTH(u.createdAt), COUNT(u)
        FROM User u
        GROUP BY YEAR(u.createdAt), MONTH(u.createdAt)
        ORDER BY YEAR(u.createdAt) DESC, MONTH(u.createdAt) DESC
    """)
    List<Object[]> countUsersByMonth();
}
