package io.codelex.luminor.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.codelex.luminor.model.Payment;
import io.codelex.luminor.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${geoapify.api.key}")
    private String apiKey;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public String resolveCountryCode(String ip) {
        if (ip == null || ip.isEmpty()) {
            return "unknown";
        }

        String url = UriComponentsBuilder.fromHttpUrl("https://api.geoapify.com/v1/ipinfo")
                .queryParam("ip", ip)
                .queryParam("apiKey", apiKey)
                .toUriString();
        try {
            String response = restTemplate.getForObject(url, String.class);
            return parseCountryCode(response);
        } catch (Exception e) {
            return "unknown";
        }
    }

    private String parseCountryCode(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            return jsonNode.path("country").path("iso_code").asText("unknown");
        } catch (Exception e) {
            return "unknown";
        }
    }
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Payment createPayment(BigDecimal amount, String debtorIban, String ipAddress) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cant null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        if (debtorIban == null || debtorIban.trim().isEmpty()) {
            throw new IllegalArgumentException("IBAN cant be null");
        }
        if (!isValidIban(debtorIban)) {
            throw new IllegalArgumentException("Invalid IBAN");
        }
        String countryCode = resolveCountryCode(ipAddress);
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
        if (debtorIban.isPresent()) {
            return paymentRepository.findByDebtorIban(debtorIban.get());
        } else {
            return paymentRepository.findAll();
        }
    }
}
