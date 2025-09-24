package io.github.ferrazsergio.clt2pj.controller;

import io.github.ferrazsergio.clt2pj.dto.BeneficioDTO;
import io.github.ferrazsergio.clt2pj.dto.SimulacaoRequestDTO;
import io.github.ferrazsergio.clt2pj.dto.SimulacaoResponseDTO;
import io.github.ferrazsergio.clt2pj.service.SimulacaoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Teste para o controller de simulação financeira CLT/PJ.
 */
@WebMvcTest(SimulacaoController.class)
@AutoConfigureMockMvc(addFilters = false)
class SimulacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SimulacaoService simulacaoService;

    @TestConfiguration
    static class Config {
        @Bean
        public SimulacaoService simulacaoService() {
            return org.mockito.Mockito.mock(SimulacaoService.class);
        }
    }

    @Test
    void testSimularEndpoint() throws Exception {
        SimulacaoRequestDTO request = new SimulacaoRequestDTO();
        request.setSalarioClt(BigDecimal.valueOf(5000.00));
        request.setBeneficiosClt(Arrays.asList(beneficio("VR", BigDecimal.valueOf(400.00))));
        request.setSalarioPj(BigDecimal.valueOf(7000.00));
        request.setTipoTributacao("Simples Nacional");
        request.setReservaEmergencia(BigDecimal.valueOf(10.0));

        when(simulacaoService.simular(any(SimulacaoRequestDTO.class))).thenReturn(
                SimulacaoResponseDTO.builder()
                        .salarioLiquidoClt(BigDecimal.valueOf(4500.00).setScale(2))
                        .salarioLiquidoPj(BigDecimal.valueOf(6300.00).setScale(2))
                        .provisaoBeneficios(BigDecimal.valueOf(400.00).setScale(2))
                        .valorReservaSugerido(BigDecimal.valueOf(400.00).setScale(2))
                        .build()
        );

        mockMvc.perform(post("/simulacao")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.salarioLiquidoClt").value(4500.00))
                .andExpect(jsonPath("$.salarioLiquidoPj").value(6300.00))
                .andExpect(jsonPath("$.provisaoBeneficios").value(400.00))
                .andExpect(jsonPath("$.valorReservaSugerido").value(400.00));
    }

    private BeneficioDTO beneficio(String nome, BigDecimal valor) {
        BeneficioDTO b = new BeneficioDTO();
        b.setNome(nome);
        b.setValor(valor);
        return b;
    }
}