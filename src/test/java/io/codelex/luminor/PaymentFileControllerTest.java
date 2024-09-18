package io.codelex.luminor;

import io.codelex.luminor.controller.PaymentFileController;
import io.codelex.luminor.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentFileController.class)
class PaymentFileControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PaymentService paymentService;

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

        mockMvc.perform(multipart("/payment-files")
                        .file(file))
                .andExpect(status().isBadRequest());
    }

}
