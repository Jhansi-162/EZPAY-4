package com.ezpay.transaction.transaction.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ezpay.transaction.transaction.entity.PaymentTransaction;

public interface TransactionRepository
        extends JpaRepository<PaymentTransaction, Long> {

    List<PaymentTransaction> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
}