package io.github.ferrazsergio.clt2pj.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO que representa os dados necessários para realizar uma simulação financeira entre CLT e PJ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para simulação financeira entre CLT e PJ")
public class SimulacaoRequestDTO {

    @NotNull(message = "Salário bruto CLT é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Salário bruto CLT deve ser positivo")
    @Schema(description = "Salário bruto CLT", example = "5000.00")
    private BigDecimal salarioClt;

    @Size(min = 0, message = "Lista de benefícios CLT deve ser informada (pode ser vazia)")
    @Schema(description = "Lista de benefícios CLT selecionados (padrão ou personalizados)")
    private List<BeneficioDTO> beneficiosClt;

    @NotNull(message = "Salário bruto PJ é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Salário bruto PJ deve ser positivo")
    @Schema(description = "Salário bruto PJ", example = "7000.00")
    private BigDecimal salarioPj;

    @NotNull(message = "Tipo de tributação para PJ é obrigatório")
    @NotBlank(message = "Tipo de tributação para PJ não pode estar em branco")
    @Schema(description = "Tipo de tributação para PJ (MEI, Simples Nacional, Lucro Presumido, etc)", example = "Simples Nacional")
    private String tipoTributacao;

    @Size(min = 0, message = "Lista de benefícios PJ deve ser informada (pode ser vazia)")
    @Schema(description = "Lista de benefícios PJ selecionados")
    private List<BeneficioDTO> beneficiosPj;

    @DecimalMin(value = "0.0", inclusive = true, message = "Percentual de reserva de emergência deve ser igual ou maior que zero")
    @Schema(description = "Percentual (%) de reserva de emergência para PJ", example = "10")
    private BigDecimal reservaEmergencia;

    @Size(min = 0, message = "Lista de nomes dos benefícios escolhidos deve ser informada (pode ser vazia)")
    @Schema(description = "Nomes dos benefícios escolhidos para simular")
    private List<String> beneficiosSelecionados;
}