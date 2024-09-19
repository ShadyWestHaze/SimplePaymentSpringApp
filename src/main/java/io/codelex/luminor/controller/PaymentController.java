package io.codelex.luminor.controller;

import io.codelex.luminor.model.Payment;
import io.codelex.luminor.service.PaymentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Payment createPayment(@RequestParam BigDecimal amount, @RequestParam String debtorIban, @RequestHeader(value = "X-Forwarded-For", required = false) String xForwardedFor) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be bigger than zero");
        }
        if (StringUtils.isBlank(debtorIban) || !paymentService.isValidIban(debtorIban)) {
            throw new IllegalArgumentException("Invalid IBAN");
        }

        String ipAddress = StringUtils.isEmpty(xForwardedFor) ? "" : xForwardedFor;
        return paymentService.createPayment(amount, debtorIban, ipAddress);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleBadRequest(IllegalArgumentException e) {
        return e.getMessage();
    }

    @GetMapping
    public Iterable<Payment> getPayments(@RequestParam Optional<String> debtorIban) {
        return paymentService.getPayments(debtorIban);
    }
}
