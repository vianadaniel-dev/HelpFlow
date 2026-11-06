package com.vianadanieldev.dispatch.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DispatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldDispatchNetworkTechnicianForRedCategory() throws Exception {
        String requestJson = """
                {
                    "ticketId": 125,
                    "categoria": "RED",
                    "prioridad": "ALTA"
                }
                """;

        mockMvc.perform(post("/technician-dispatch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketId").value(125))
                .andExpect(jsonPath("$.tecnicoId").value("33333333-3333-3333-3333-333333333333"))
                .andExpect(jsonPath("$.disponibilidad").value("ALTA"))
                .andExpect(jsonPath("$.tiempoEstimadoHoras").value(2));
    }

    @Test
    void shouldDispatchSoftwareTechnicianForSoftwareCategory() throws Exception {
        String requestJson = """
                {
                    "ticketId": "999",
                    "categoria": "SOFTWARE",
                    "prioridad": "CRITICA"
                }
                """;

        mockMvc.perform(post("/technician-dispatch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tecnicoId").value("22222222-2222-2222-2222-222222222222"))
                .andExpect(jsonPath("$.tiempoEstimadoHoras").value(1));
    }
}
