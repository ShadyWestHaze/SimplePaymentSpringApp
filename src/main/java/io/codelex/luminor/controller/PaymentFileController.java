package io.codelex.luminor.controller;

import io.codelex.luminor.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/payment-files")
public class PaymentFileController {

    private final PaymentService paymentService;

    public PaymentFileController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> uploadPaymentFile(@RequestParam("file") MultipartFile file, @RequestHeader(value = "X-Forwarded-For", required = false) String xForwardedFor) {

        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File empty");
        }

        String ipAddress = (xForwardedFor != null && !xForwardedFor.isEmpty()) ? xForwardedFor : "";

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            List<String[]> lines = reader.lines().skip(1).map(line -> line.split(",")).toList();

            for (String[] line : lines) {
                if (line.length != 2) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid CSV format");
                }
                BigDecimal amount = new BigDecimal(line[0].trim());
                String debtorIban = line[1].trim();

                try {
                    paymentService.createPayment(amount, debtorIban, ipAddress);
                } catch (IllegalArgumentException e) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Validation failed: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "File processing error");
        }
        return ResponseEntity.ok("Payments created successfully");
    }
}
