package io.github.ferrazsergio.clt2pj.service;

import io.github.ferrazsergio.clt2pj.dto.BeneficioDTO;
import io.github.ferrazsergio.clt2pj.dto.SimulacaoRequestDTO;
import io.github.ferrazsergio.clt2pj.dto.SimulacaoResponseDTO;
import io.github.ferrazsergio.clt2pj.repository.SimulacaoRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários do serviço de simulação financeira CLT/PJ.
 */
class SimulacaoServiceTest {

    SimulacaoRepository repository = Mockito.mock(SimulacaoRepository.class);
    SimulacaoService service = new SimulacaoService(repository);

    @Test
    void testSimulacaoCltPjCompleta() {
        SimulacaoRequestDTO request = new SimulacaoRequestDTO();
        request.setSalarioClt(BigDecimal.valueOf(5000.00));
        request.setBeneficiosClt(Arrays.asList(
                beneficio("Vale Refeição", BigDecimal.valueOf(500.00)),
                beneficio("Plano de Saúde", BigDecimal.valueOf(300.00))
        ));
        request.setSalarioPj(BigDecimal.valueOf(7000.00));
        request.setBeneficiosPj(Arrays.asList(
                beneficio("Plano de Saúde", BigDecimal.valueOf(350.00))
        ));
        request.setTipoTributacao("Simples Nacional");
        request.setReservaEmergencia(BigDecimal.valueOf(10.0));

        SimulacaoResponseDTO response = service.simular(request);
        assertNotNull(response);

        assertTrue(response.getSalarioLiquidoClt().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(response.getSalarioLiquidoPj().compareTo(BigDecimal.ZERO) > 0);
        assertEquals(BigDecimal.valueOf(800.00).setScale(2), response.getProvisaoBeneficios());
        assertEquals(BigDecimal.valueOf(800.00).setScale(2), response.getValorReservaSugerido());
    }

    @Test
    void testCalculoInssFaixa1() {
        BigDecimal inss = service.calcularInss2025(BigDecimal.valueOf(1500.00));
        assertEquals(BigDecimal.valueOf(112.50).setScale(2), inss); // 7.5%
    }

    @Test
    void testCalculoIrrfIsento() {
        BigDecimal irrf = service.calcularIrrf2025(BigDecimal.valueOf(2000.00), BigDecimal.valueOf(150.00));
        assertEquals(BigDecimal.ZERO.setScale(2), irrf); // Isenção
    }

    private BeneficioDTO beneficio(String nome, BigDecimal valor) {
        BeneficioDTO b = new BeneficioDTO();
        b.setNome(nome);
        b.setValor(valor);
        return b;
    }
}