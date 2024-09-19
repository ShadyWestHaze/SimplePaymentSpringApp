package io.codelex.luminor.controller;

import io.codelex.luminor.service.PaymentFileService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/payment-files")
public class PaymentFileController {

    private final PaymentFileService paymentFileService;

    public PaymentFileController(PaymentFileService paymentFileService) {
        this.paymentFileService = paymentFileService;
    }

    @PostMapping
    public String uploadPaymentFile(@RequestParam("file") MultipartFile file, @RequestHeader(value = "X-Forwarded-For", required = false) String xForwardedFor) {
        String ipAddress = StringUtils.isEmpty(xForwardedFor) ? "" : xForwardedFor;
        paymentFileService.processPaymentFile(file, ipAddress);
        return "Payments created successfully";
    }
}
