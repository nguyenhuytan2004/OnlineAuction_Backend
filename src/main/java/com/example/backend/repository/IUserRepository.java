package com.example.backend.repository;

import com.example.backend.entity.User;
import com.example.backend.entity.User.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IUserRepository extends JpaRepository<User, Integer> {
    Page<User> findByRole(Role role, Pageable pageable);

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
