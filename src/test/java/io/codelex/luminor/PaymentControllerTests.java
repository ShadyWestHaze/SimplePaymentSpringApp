package io.codelex.luminor;

import io.codelex.luminor.controller.PaymentController;
import io.codelex.luminor.model.Payment;
import io.codelex.luminor.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PaymentService paymentService;

    @Test
    void testCreatePayment() throws Exception {
        Payment payment = new Payment(new BigDecimal("150.00"), "LT123456789012345678", null, "LT");
        Mockito.when(paymentService.createPayment(Mockito.any(BigDecimal.class), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(payment);
        Mockito.when(paymentService.isValidIban("LT123456789012345678")).thenReturn(true);

        mockMvc.perform(post("/payments")
                        .param("amount", "150.00")
                        .param("debtorIban", "LT123456789012345678")
                        .header("X-Forwarded-For", "192.168.1.1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.debtorIban", is("LT123456789012345678")));
    }

    @Test
    void testCreatePaymentWithZero() throws Exception {
        Mockito.when(paymentService.createPayment(BigDecimal.ZERO, "LT121000011101001000", ""))
                .thenThrow(new IllegalArgumentException("Amount must be greater than zero"));

        mockMvc.perform(post("/payments")
                        .param("amount", "0")
                        .param("debtorIban", "LT121000011101001000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreatePaymentWithWrongIban() throws Exception {
        Mockito.when(paymentService.createPayment(new BigDecimal("100.00"), "Something", ""))
                .thenThrow(new IllegalArgumentException("Invalid IBAN"));

        mockMvc.perform(post("/payments")
                        .param("amount", "100.00")
                        .param("debtorIban", "Something")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreatePaymentWithNonNumericalAmount() throws Exception {
        mockMvc.perform(post("/payments")
                        .param("amount", "abc")
                        .param("debtorIban", "LT121000011101001000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}
