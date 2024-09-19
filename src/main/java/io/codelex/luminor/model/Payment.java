package io.codelex.luminor.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private BigDecimal amount;
    private String debtorIban;
    private LocalDateTime createdAt;
    private String countryCode;

    public Payment() {}
    public Payment(BigDecimal amount, String debtorIban, LocalDateTime createdAt, String countryCode) {
        this.amount = amount;
        this.debtorIban = debtorIban;
        this.createdAt = createdAt;
        this.countryCode = countryCode;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setDebtorIban(String debtorIban) {
        this.debtorIban = debtorIban;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getDebtorIban() {
        return debtorIban;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
