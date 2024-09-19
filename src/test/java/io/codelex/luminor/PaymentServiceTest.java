package io.codelex.luminor;

import io.codelex.luminor.model.Payment;
import io.codelex.luminor.repository.PaymentRepository;
import io.codelex.luminor.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {
    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreatePayment() {
        BigDecimal amount = new BigDecimal("100.00");
        String debtorIban = "LT121000011101001000";
        String ipAddress = "192.168.1.1";
        Payment payment = new Payment(amount, debtorIban, LocalDateTime.now(), "LT");

        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        Payment result = paymentService.createPayment(amount, debtorIban, ipAddress);

        assertNotNull(result);
        assertEquals(amount, result.getAmount());
        assertEquals(debtorIban, result.getDebtorIban());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testCreatePaymentWithZeroAmount() {
        BigDecimal amount = BigDecimal.ZERO;
        String debtorIban = "LT121000011101001000";
        String ipAddress = "192.168.1.1";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            paymentService.createPayment(amount, debtorIban, ipAddress);
        });
        assertEquals("Amount must be greater than zero", exception.getMessage());
    }

    @Test
    void testCreatePaymentWithInvalidIban() {
        BigDecimal amount = new BigDecimal("100.00");
        String invalidDebtorIban = "INVALID_IBAN";
        String ipAddress = "192.168.1.1";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            paymentService.createPayment(amount, invalidDebtorIban, ipAddress);
        });
        assertEquals("Invalid IBAN", exception.getMessage());
    }

    @Test
    void testFilterPaymentsWithIban() {
        String debtorIban = "LT121000011101001000";
        when(paymentRepository.findByDebtorIban(debtorIban)).thenReturn(List.of(new Payment()));

        Iterable<Payment> payments = paymentService.getPayments(Optional.of(debtorIban));

        assertNotNull(payments);
        verify(paymentRepository, times(1)).findByDebtorIban(debtorIban);
    }

    @Test
    void testDontFilterPaymentsIban() {
        when(paymentRepository.findAll()).thenReturn(List.of(new Payment()));

        Iterable<Payment> payments = paymentService.getPayments(Optional.empty());

        assertNotNull(payments);
        verify(paymentRepository, times(1)).findAll();
    }
}
