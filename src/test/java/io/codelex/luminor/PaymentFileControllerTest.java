package io.codelex.luminor;

import io.codelex.luminor.controller.PaymentFileController;
import io.codelex.luminor.service.PaymentFileService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentFileController.class)
class PaymentFileControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PaymentFileService paymentFileService;

    @Test
    void testUploadValidCsv() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "payments.csv", "text/csv",
                ("amount,iban\n100,LT121000011101001000").getBytes());

        mockMvc.perform(multipart("/payment-files")
                        .file(file))
                .andExpect(status().isOk());
    }

    @Test
    void testUploadCsvWithExtraColumns() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "payments.csv", "text/csv",
                ("amount,iban,extra_column\n100,LT121000011101001000,extra").getBytes());

        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid CSV format")).when(paymentFileService).processPaymentFile(Mockito.any(), Mockito.any());

        mockMvc.perform(multipart("/payment-files")
                        .file(file))
                .andExpect(status().isBadRequest());
    }

}
