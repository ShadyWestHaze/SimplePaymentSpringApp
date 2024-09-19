package io.codelex.luminor.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;

@Service
public class PaymentFileService {

    private final PaymentService paymentService;

    public PaymentFileService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public void processPaymentFile(MultipartFile file, String ipAddress) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File empty");
        }

        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            csvReader.readNext();
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                if (line.length != 2) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid CSV format");
                }
                BigDecimal amount = new BigDecimal(line[0].trim());
                String debtorIban = line[1].trim();

                if (StringUtils.isBlank(debtorIban)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debtor IBAN must not be blank");
                }
                try {
                    paymentService.createPayment(amount, debtorIban, ipAddress);
                } catch (IllegalArgumentException e) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Validation failed: " + e.getMessage());
                }
            }
        } catch (IOException | CsvValidationException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "File processing error");
        }
    }
}
