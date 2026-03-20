package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.PaymentTransaction;

public interface IPaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
  PaymentTransaction findByOrderCode(long orderCode);
}
