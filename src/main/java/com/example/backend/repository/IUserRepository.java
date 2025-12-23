package com.example.backend.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.User;
import com.example.backend.entity.User.Role;

public interface IUserRepository extends JpaRepository<User, Integer> {
  Page<User> findByRole(Role role, Pageable pageable);

  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);
}
