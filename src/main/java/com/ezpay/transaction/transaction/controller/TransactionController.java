package com.ezpay.transaction.transaction.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ezpay.transaction.transaction.dto.CreateTransactionRequest;
import com.ezpay.transaction.transaction.entity.PaymentTransaction;
import com.ezpay.transaction.transaction.entity.TransactionStatus;
import com.ezpay.transaction.transaction.service.TransactionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private static final Logger logger =
            LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentTransaction create(
            @Valid @RequestBody CreateTransactionRequest request) {
        logger.info("Received create transaction request");
        return service.create(request);
    }

    @GetMapping("/{id}")
    public PaymentTransaction getById(@PathVariable Long id) {
        logger.info("Received request to get transaction id={}", id);
        return service.getById(id);
    }

    @GetMapping
    public List<PaymentTransaction> getByCustomer(
            @RequestParam Long customerId) {
        logger.info("Received request for customerId={}", customerId);
        return service.getByCustomer(customerId);
    }

    @PatchMapping("/{id}/status")
    public PaymentTransaction updateStatus(
            @PathVariable Long id,
            @RequestParam TransactionStatus status) {
        logger.info("Received status update request for transaction id={}", id);
        return service.updateStatus(id, status);
    }
}