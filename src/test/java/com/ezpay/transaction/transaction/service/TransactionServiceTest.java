package com.ezpay.transaction.transaction.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ezpay.transaction.transaction.dto.CreateTransactionRequest;
import com.ezpay.transaction.transaction.entity.PaymentTransaction;
import com.ezpay.transaction.transaction.entity.TransactionStatus;
import com.ezpay.transaction.transaction.exception.TransactionNotFoundException;
import com.ezpay.transaction.transaction.repository.TransactionRepository;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository repository;

    @InjectMocks
    private TransactionService transactionService;

    private CreateTransactionRequest request;

    @BeforeEach
    void setUp() {
        request = new CreateTransactionRequest();
        request.setCustomerId(42L);
        request.setAmount(new BigDecimal("25.50"));
        request.setCurrency("usd");
        request.setDescription("Test payment");
    }

    @Test
    void testTransactionService() {
        assertNotNull(transactionService);
    }

    @Test
    void testCreate() {
        when(repository.save(any(PaymentTransaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PaymentTransaction result = transactionService.create(request);

        ArgumentCaptor<PaymentTransaction> captor =
                ArgumentCaptor.forClass(PaymentTransaction.class);
        verify(repository).save(captor.capture());

        PaymentTransaction saved = captor.getValue();
        assertSame(saved, result);
        assertEquals(42L, saved.getCustomerId());
        assertEquals(new BigDecimal("25.50"), saved.getAmount());
        assertEquals("USD", saved.getCurrency());
        assertEquals("Test payment", saved.getDescription());
        assertEquals(TransactionStatus.PENDING, saved.getStatus());
    }

    @Test
    void testGetById() {
        PaymentTransaction transaction = new PaymentTransaction();
        when(repository.findById(1L)).thenReturn(Optional.of(transaction));

        PaymentTransaction result = transactionService.getById(1L);

        assertSame(transaction, result);
        verify(repository).findById(1L);
    }

    @Test
    void testGetByIdThrowsWhenTransactionDoesNotExist() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                TransactionNotFoundException.class,
                () -> transactionService.getById(99L));

        verify(repository).findById(99L);
    }

    @Test
    void testGetByCustomer() {
        List<PaymentTransaction> transactions = Arrays.asList(
                new PaymentTransaction(),
                new PaymentTransaction());
        when(repository.findByCustomerIdOrderByCreatedAtDesc(42L))
                .thenReturn(transactions);

        List<PaymentTransaction> result =
                transactionService.getByCustomer(42L);

        assertSame(transactions, result);
        verify(repository).findByCustomerIdOrderByCreatedAtDesc(42L);
    }

    @Test
    void testUpdateStatus() {
        TransactionStatus newStatus = Arrays.stream(TransactionStatus.values())
                .filter(status -> status != TransactionStatus.PENDING)
                .findFirst()
                .orElse(TransactionStatus.PENDING);

        PaymentTransaction transaction = new PaymentTransaction();
        when(repository.findById(1L)).thenReturn(Optional.of(transaction));
        when(repository.save(transaction)).thenReturn(transaction);

        PaymentTransaction result =
                transactionService.updateStatus(1L, newStatus);

        assertSame(transaction, result);
        assertEquals(newStatus, transaction.getStatus());
        verify(repository).findById(1L);
        verify(repository).save(transaction);
    }

    @Test
    void testUpdateStatusThrowsWhenTransactionDoesNotExist() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                TransactionNotFoundException.class,
                () -> transactionService.updateStatus(
                        99L, TransactionStatus.PENDING));

        verify(repository).findById(99L);
    }
}