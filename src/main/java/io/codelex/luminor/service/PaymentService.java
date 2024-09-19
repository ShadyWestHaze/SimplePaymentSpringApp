package io.codelex.luminor.service;

import io.codelex.luminor.model.Payment;
import io.codelex.luminor.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final GeoLocationService geoLocationService;

    public PaymentService(PaymentRepository paymentRepository, GeoLocationService geoLocationService) {
        this.paymentRepository = paymentRepository;
        this.geoLocationService = geoLocationService;
    }

    public Payment createPayment(BigDecimal amount, String debtorIban, String ipAddress) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        if (debtorIban == null || debtorIban.trim().isEmpty() || !isValidIban(debtorIban)) {
            throw new IllegalArgumentException("Invalid IBAN");
        }
        String countryCode = geoLocationService.resolveCountryCode(ipAddress);
        Payment payment = new Payment(amount, debtorIban, LocalDateTime.now(), countryCode);
        return paymentRepository.save(payment);
    }

    public boolean isValidIban(String iban) {
        String ltRegex = "^LT\\d{18}$";
        String lvRegex = "^LV\\d{2}[A-Z0-9]{4}\\d{13}$";
        String eeRegex = "^EE\\d{18}$";
        return Pattern.matches(ltRegex, iban) ||
                Pattern.matches(lvRegex, iban) ||
                Pattern.matches(eeRegex, iban);
    }

    public Iterable<Payment> getPayments(Optional<String> debtorIban) {
        return debtorIban.isPresent() ? paymentRepository.findByDebtorIban(debtorIban.get()) : paymentRepository.findAll();
    }
}
