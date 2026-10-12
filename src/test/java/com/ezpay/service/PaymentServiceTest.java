package com.ezpay.service;

import com.ezpay.entity.Payment;
import com.ezpay.entity.PaymentStatus;
import com.ezpay.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void processPaymentSetsSuccessAndSavesPayment() {
        Payment input = new Payment();
        input.setAmount(new BigDecimal("125.50"));
        input.setCurrency("USD");
        input.setDescription("Order payment");

        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Payment result = paymentService.processPayment(input);

        assertEquals(PaymentStatus.SUCCESS, result.getStatus());
        assertNotNull(result.getCreatedAt());
        assertEquals(new BigDecimal("125.50"), result.getAmount());
        verify(paymentRepository).save(input);
    }

    @Test
    void getAllPaymentsReturnsPaymentsFromRepository() {
        Payment payment = new Payment();
        payment.setAmount(new BigDecimal("50.00"));
        payment.setCurrency("USD");

        List<Payment> expectedPayments = List.of(payment);
        when(paymentRepository.findAll()).thenReturn(expectedPayments);

        List<Payment> result = paymentService.getAllPayments();

        assertEquals(1, result.size());
        assertSame(payment, result.get(0));
        verify(paymentRepository).findAll();
    }

    @Test
    void getPaymentByIdReturnsPaymentWhenFound() {
        Payment payment = new Payment();
        payment.setAmount(new BigDecimal("75.00"));
        payment.setCurrency("USD");

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        Payment result = paymentService.getPaymentById(1L);

        assertSame(payment, result);
        verify(paymentRepository).findById(1L);
    }

    @Test
    void getPaymentByIdThrowsWhenPaymentDoesNotExist() {
        when(paymentRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                PaymentService.PaymentNotFoundException.class,
                () -> paymentService.getPaymentById(99L)
        );

        verify(paymentRepository).findById(99L);
    }
}