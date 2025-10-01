package io.github.ferrazsergio.clt2pj.service;

import io.github.ferrazsergio.clt2pj.domain.Simulacao;
import io.github.ferrazsergio.clt2pj.dto.BeneficioDTO;
import io.github.ferrazsergio.clt2pj.dto.SimulacaoRequestDTO;
import io.github.ferrazsergio.clt2pj.dto.SimulacaoResponseDTO;
import io.github.ferrazsergio.clt2pj.repository.SimulacaoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Serviço responsável pela simulação financeira entre regimes CLT e PJ.
 */
@Service
@RequiredArgsConstructor
public class SimulacaoService {

    private static final Logger log = LoggerFactory.getLogger(SimulacaoService.class);

    private final SimulacaoRepository repository;

    /**
     * Realiza a simulação com base nos dados informados.
     */
    public SimulacaoResponseDTO simular(SimulacaoRequestDTO request) {
        log.info("Iniciando simulação CLT/PJ com request: {}", request);

        // Cálculo CLT
        BigDecimal inss = calcularInss2025(request.getSalarioClt());
        log.debug("INSS calculado: {}", SimulacaoResponseDTO.formatarReal(inss));

        BigDecimal irrf = calcularIrrf2025(request.getSalarioClt(), inss);
        log.debug("IRRF calculado: {}", SimulacaoResponseDTO.formatarReal(irrf));

        BigDecimal totalBeneficiosClt = somarBeneficios(request.getBeneficiosClt());
        log.debug("Total de benefícios CLT: {}", SimulacaoResponseDTO.formatarReal(totalBeneficiosClt));

        BigDecimal salarioLiquidoClt = request.getSalarioClt()
                .subtract(inss)
                .subtract(irrf)
                .add(totalBeneficiosClt)
                .setScale(2, RoundingMode.HALF_UP);
        log.info("Salário líquido CLT: {}", SimulacaoResponseDTO.formatarReal(salarioLiquidoClt));

        // Cálculo PJ
        BigDecimal salarioLiquidoPj = calcularLiquidoPj(request);
        log.info("Salário líquido PJ: {}", SimulacaoResponseDTO.formatarReal(salarioLiquidoPj));

        BigDecimal totalBeneficiosPj = somarBeneficios(request.getBeneficiosPj());
        log.debug("Total de benefícios PJ: {}", SimulacaoResponseDTO.formatarReal(totalBeneficiosPj));

        // Provisão de benefícios CLT (exemplo: soma dos benefícios)
        BigDecimal provisaoBeneficios = totalBeneficiosClt.setScale(2, RoundingMode.HALF_UP);
        log.debug("Provisão de benefícios CLT: {}", SimulacaoResponseDTO.formatarReal(provisaoBeneficios));

        // Valor sugerido de reserva PJ (exemplo: igual à soma dos benefícios CLT)
        BigDecimal valorReservaSugerido = provisaoBeneficios;
        log.debug("Valor de reserva sugerido para PJ: {}", SimulacaoResponseDTO.formatarReal(valorReservaSugerido));

        // Monta mapa detalhado, nunca passa null para Map.of
        List<String> cltBeneficiosSelecionados = request.getBeneficiosSelecionados() != null ? request.getBeneficiosSelecionados() : Collections.emptyList();
        List<String> pjBeneficiosSelecionados = request.getBeneficiosSelecionados() != null ? request.getBeneficiosSelecionados() : Collections.emptyList();

        Map<String, Object> comparativoDetalhado = montarComparativoDetalhado(
                salarioLiquidoClt, inss, irrf, totalBeneficiosClt, cltBeneficiosSelecionados,
                salarioLiquidoPj, request.getTipoTributacao(), request.getReservaEmergencia(), totalBeneficiosPj, pjBeneficiosSelecionados, // ← Já é BigDecimal
                valorReservaSugerido
        );

        log.info("Simulação concluída. Comparativo detalhado: {}", comparativoDetalhado);

        return SimulacaoResponseDTO.builder()
                .salarioLiquidoClt(salarioLiquidoClt)
                .salarioLiquidoPj(salarioLiquidoPj)
                .provisaoBeneficios(provisaoBeneficios)
                .valorReservaSugerido(valorReservaSugerido)
                .comparativoDetalhado(comparativoDetalhado)
                .build();
    }

    /**
     * Monta o comparativo detalhado para retorno e histórico.
     */
    public Map<String, Object> montarComparativoDetalhado(
            BigDecimal salarioLiquidoClt, BigDecimal inss, BigDecimal irrf, BigDecimal totalBeneficiosClt, List<String> cltBeneficiosSelecionados,
            BigDecimal salarioLiquidoPj, String tipoTributacao, BigDecimal reservaEmergencia, BigDecimal totalBeneficiosPj, List<String> pjBeneficiosSelecionados, // ← CORREÇÃO: BigDecimal reservaEmergencia
            BigDecimal valorReservaSugerido
    ) {
        Map<String, Object> comparativoDetalhado = new HashMap<>();

        // CLT
        comparativoDetalhado.put("clt", Map.of(
                "salarioLiquido", salarioLiquidoClt != null ? salarioLiquidoClt : BigDecimal.ZERO,
                "inss", inss != null ? inss : BigDecimal.ZERO,
                "irrf", irrf != null ? irrf : BigDecimal.ZERO,
                "totalBeneficios", totalBeneficiosClt != null ? totalBeneficiosClt : BigDecimal.ZERO,
                "beneficiosSelecionados", cltBeneficiosSelecionados != null ? cltBeneficiosSelecionados : Collections.emptyList()
        ));

        // PJ
        comparativoDetalhado.put("pj", Map.of(
                "salarioLiquido", salarioLiquidoPj != null ? salarioLiquidoPj : BigDecimal.ZERO,
                "tipoTributacao", tipoTributacao != null ? tipoTributacao : "NÃO INFORMADO",
                "reservaEmergencia", reservaEmergencia != null ? reservaEmergencia : BigDecimal.ZERO, // ← AGORA É BigDecimal
                "totalBeneficios", totalBeneficiosPj != null ? totalBeneficiosPj : BigDecimal.ZERO,
                "beneficiosSelecionados", pjBeneficiosSelecionados != null ? pjBeneficiosSelecionados : Collections.emptyList()
        ));

        comparativoDetalhado.put("valorReservaSugerido", valorReservaSugerido != null ? valorReservaSugerido : BigDecimal.ZERO);
        return comparativoDetalhado;
    }

    /**
     * Monta o comparativo detalhado a partir de uma entidade Simulacao (usado para histórico).
     */
    public Map<String, Object> montarComparativoDetalhadoFromEntity(Simulacao simulacao) {
        // CLT
        BigDecimal salarioClt = simulacao.getSalarioClt() != null ? simulacao.getSalarioClt() : BigDecimal.ZERO;
        BigDecimal inss = calcularInss2025(salarioClt);
        BigDecimal irrf = calcularIrrf2025(salarioClt, inss);

        BigDecimal totalBeneficiosClt = simulacao.getTotalBeneficiosClt() != null ? simulacao.getTotalBeneficiosClt() : BigDecimal.ZERO;
        List<String> cltBeneficiosSelecionados = simulacao.getBeneficiosSelecionadosClt() != null
                ? simulacao.getBeneficiosSelecionadosClt()
                : Collections.emptyList();

        // PJ
        BigDecimal salarioPj = simulacao.getSalarioPj() != null ? simulacao.getSalarioPj() : BigDecimal.ZERO;
        BigDecimal reservaEmergencia = simulacao.getReservaEmergencia() != null ? simulacao.getReservaEmergencia() : BigDecimal.ZERO;
        BigDecimal salarioLiquidoPj = salarioPj.subtract(salarioPj.multiply(reservaEmergencia).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalBeneficiosPj = simulacao.getTotalBeneficiosPj() != null ? simulacao.getTotalBeneficiosPj() : BigDecimal.ZERO;
        List<String> pjBeneficiosSelecionados = simulacao.getBeneficiosSelecionadosPj() != null
                ? simulacao.getBeneficiosSelecionadosPj()
                : Collections.emptyList();

        BigDecimal valorReservaSugerido = simulacao.getValorReservaSugerido() != null ? simulacao.getValorReservaSugerido() : totalBeneficiosClt;

        // --- ALTERAÇÃO: garantir que tipoTributacao nunca seja nulo ou vazio ---
        String tipoTributacao = simulacao.getTipoTributacao();
        if (tipoTributacao == null || tipoTributacao.trim().isEmpty() || tipoTributacao.trim().equalsIgnoreCase("null")) {
            tipoTributacao = "NÃO INFORMADO";
        }

        return montarComparativoDetalhado(
                salarioClt.subtract(inss).subtract(irrf).add(totalBeneficiosClt).setScale(2, RoundingMode.HALF_UP),
                inss, irrf, totalBeneficiosClt, cltBeneficiosSelecionados,
                salarioLiquidoPj, tipoTributacao, reservaEmergencia, totalBeneficiosPj, pjBeneficiosSelecionados,
                valorReservaSugerido
        );
    }

    /**
     * Calcula o valor do INSS conforme as faixas de 2025.
     */
    BigDecimal calcularInss2025(BigDecimal salarioBruto) {
        log.debug("Calculando INSS para salário bruto: {}", SimulacaoResponseDTO.formatarReal(salarioBruto));
        if (salarioBruto == null) return BigDecimal.ZERO;
        double[] faixas = {1518.00, 2793.88, 4190.83, 8157.41};
        double[] aliquotas = {0.075, 0.09, 0.12, 0.14};
        double[] limites = {1518.00, 1275.88, 1396.95, 3966.58};

        double salario = salarioBruto.doubleValue();
        double inss = 0.0;

        if (salario <= faixas[0]) {
            inss = salario * aliquotas[0];
        } else if (salario <= faixas[1]) {
            inss = limites[0] * aliquotas[0] + (salario - faixas[0]) * aliquotas[1];
        } else if (salario <= faixas[2]) {
            inss = limites[0] * aliquotas[0] + limites[1] * aliquotas[1] + (salario - faixas[1]) * aliquotas[2];
        } else {
            inss = limites[0] * aliquotas[0] + limites[1] * aliquotas[1] + limites[2] * aliquotas[2] + (Math.min(salario, faixas[3]) - faixas[2]) * aliquotas[3];
        }
        BigDecimal result = BigDecimal.valueOf(inss).setScale(2, RoundingMode.HALF_UP);
        log.debug("INSS resultado: {}", SimulacaoResponseDTO.formatarReal(result));
        return result;
    }

    /**
     * Calcula o valor do IRRF conforme as faixas de 2025.
     */
    BigDecimal calcularIrrf2025(BigDecimal salarioBruto, BigDecimal inss) {
        log.debug("Calculando IRRF para salário bruto: {}, INSS: {}", SimulacaoResponseDTO.formatarReal(salarioBruto), SimulacaoResponseDTO.formatarReal(inss));
        if (salarioBruto == null || inss == null) return BigDecimal.ZERO;
        double base = salarioBruto.subtract(inss).doubleValue();
        double irrf;

        if (base <= 2259.20) {
            irrf = 0.0;
        } else if (base <= 2826.65) {
            irrf = base * 0.075 - 169.44;
        } else if (base <= 3751.05) {
            irrf = base * 0.15 - 381.44;
        } else if (base <= 4664.68) {
            irrf = base * 0.225 - 662.77;
        } else {
            irrf = base * 0.275 - 896.00;
        }
        BigDecimal result = BigDecimal.valueOf(Math.max(0.0, irrf)).setScale(2, RoundingMode.HALF_UP);
        log.debug("IRRF resultado: {}", SimulacaoResponseDTO.formatarReal(result));
        return result;
    }

    /**
     * Calcula o salário líquido do PJ considerando reserva de emergência.
     */
    private BigDecimal calcularLiquidoPj(SimulacaoRequestDTO request) {
        BigDecimal salario = request.getSalarioPj() != null ? request.getSalarioPj() : BigDecimal.ZERO;
        BigDecimal reserva = request.getReservaEmergencia() != null ? request.getReservaEmergencia() : BigDecimal.ZERO;
        BigDecimal liquido = salario.subtract(salario.multiply(reserva).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP);
        return liquido;
    }

    /**
     * Soma todos os benefícios informados.
     */
    private BigDecimal somarBeneficios(List<BeneficioDTO> beneficios) {
        BigDecimal soma = BigDecimal.ZERO;
        if (beneficios != null) {
            for (BeneficioDTO b : beneficios) {
                if (b != null && b.getValor() != null) {
                    soma = soma.add(b.getValor());
                }
            }
        }
        soma = soma.setScale(2, RoundingMode.HALF_UP);
        return soma;
    }

    /**
     * Salva a simulação no banco de dados.
     */
    public Simulacao salvar(Simulacao simulacao) {
        log.info("Simulação recebida no service: {}", simulacao);

        return repository.save(simulacao);
    }

    /**
     * Consulta o histórico de simulações de um usuário.
     */
    public List<Simulacao> historico(String usuarioId) {
        return repository.findByUsuario_Id(usuarioId);
    }

    public List<SimulacaoResponseDTO> historicoDTO(String identificador) {
        List<Simulacao> historico;
        if (identificador != null && identificador.contains("@")) {
            historico = repository.findByUsuario_Email(identificador);
        } else {
            historico = repository.findByUsuario_Id(identificador);
        }

        List<SimulacaoResponseDTO> dtoList = new ArrayList<>();
        for (Simulacao simulacao : historico) {
            Map<String, Object> comparativoDetalhado = montarComparativoDetalhadoFromEntity(simulacao);
            dtoList.add(SimulacaoResponseDTO.builder()
                    .salarioLiquidoClt(simulacao.getSalarioClt() != null ? simulacao.getSalarioClt() : BigDecimal.ZERO)
                    .salarioLiquidoPj(simulacao.getSalarioPj() != null ? simulacao.getSalarioPj() : BigDecimal.ZERO)
                    .provisaoBeneficios(simulacao.getTotalBeneficiosClt() != null ? simulacao.getTotalBeneficiosClt() : BigDecimal.ZERO)
                    .valorReservaSugerido(simulacao.getValorReservaSugerido() != null ? simulacao.getValorReservaSugerido() : BigDecimal.ZERO)
                    .comparativoDetalhado(comparativoDetalhado)
                    .build());
        }
        return dtoList;
    }
}