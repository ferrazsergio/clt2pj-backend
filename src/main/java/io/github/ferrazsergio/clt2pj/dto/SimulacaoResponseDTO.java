package io.github.ferrazsergio.clt2pj.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * DTO de resposta detalhada da simulação financeira CLT vs PJ.
 */
@Data
@Builder
@Schema(description = "Resposta detalhada da simulação financeira CLT vs PJ")
public class SimulacaoResponseDTO {

    @Schema(description = "Salário líquido calculado para CLT (já descontados INSS e IRRF, somados benefícios)", example = "4800.00")
    private BigDecimal salarioLiquidoClt;

    @Schema(description = "Salário líquido calculado para PJ (já descontada reserva de emergência)", example = "6300.00")
    private BigDecimal salarioLiquidoPj;

    @Schema(description = "Total de benefícios CLT considerados", example = "700.00")
    private BigDecimal provisaoBeneficios;

    @Schema(description = "Valor sugerido de reserva de emergência para PJ", example = "700.00")
    private BigDecimal valorReservaSugerido;

    @Schema(description = "Comparativo detalhado entre CLT e PJ")
    private Map<String, Object> comparativoDetalhado;

    /**
     * Serializa o valor do salário líquido CLT para string no padrão BR (R$ 6.300,00).
     */
    public String getSalarioLiquidoCltBR() {
        return formatarReal(salarioLiquidoClt);
    }

    /**
     * Serializa o valor do salário líquido PJ para string no padrão BR (R$ 6.300,00).
     */
    public String getSalarioLiquidoPjBR() {
        return formatarReal(salarioLiquidoPj);
    }

    /**
     * Serializa o valor da provisão de benefícios para string no padrão BR.
     */
    public String getProvisaoBeneficiosBR() {
        return formatarReal(provisaoBeneficios);
    }

    /**
     * Serializa o valor da reserva sugerida para string no padrão BR.
     */
    public String getValorReservaSugeridoBR() {
        return formatarReal(valorReservaSugerido);
    }

    /**
     * Formata um valor BigDecimal para moeda Real brasileiro (R$).
     */
    public static String formatarReal(BigDecimal valor) {
        if (valor == null) return null;
        java.text.NumberFormat nf = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("pt", "BR"));
        return nf.format(valor);
    }
}