package com.ezpay.transaction.transaction.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ezpay.transaction.transaction.dto.CreateTransactionRequest;
import com.ezpay.transaction.transaction.entity.PaymentTransaction;
import com.ezpay.transaction.transaction.entity.TransactionStatus;
import com.ezpay.transaction.transaction.exception.TransactionNotFoundException;
import com.ezpay.transaction.transaction.repository.TransactionRepository;

@Service
public class TransactionService {

    private static final Logger logger =
            LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public PaymentTransaction create(CreateTransactionRequest request) {
        logger.info("Creating transaction for customerId={}", request.getCustomerId());

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setCustomerId(request.getCustomerId());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency().toUpperCase());
        transaction.setDescription(request.getDescription());
        transaction.setStatus(TransactionStatus.PENDING);

        PaymentTransaction saved = repository.save(transaction);
        logger.info("Transaction created successfully with id={}", saved.getId());

        return saved;
    }

    @Transactional(readOnly = true)
    public PaymentTransaction getById(Long id) {
        logger.info("Looking up transaction id={}", id);

        return repository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Transaction not found id={}", id);
                    return new TransactionNotFoundException(id);
                });
    }

    @Transactional(readOnly = true)
    public List<PaymentTransaction> getByCustomer(Long customerId) {
        logger.info("Getting transactions for customerId={}", customerId);
        return repository.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    @Transactional
    public PaymentTransaction updateStatus(Long id, TransactionStatus status) {
        logger.info("Updating transaction id={} to status={}", id, status);

        PaymentTransaction transaction = getById(id);
        transaction.setStatus(status);

        PaymentTransaction updated = repository.save(transaction);
        logger.info("Transaction status updated id={}", updated.getId());

        return updated;
    }
}