package io.codelex.luminor.repository;

import io.codelex.luminor.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByDebtorIban(String debtorIban);
}
