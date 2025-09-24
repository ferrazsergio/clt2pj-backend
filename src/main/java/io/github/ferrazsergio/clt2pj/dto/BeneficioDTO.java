package io.github.ferrazsergio.clt2pj.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO que representa um benefício (padrão ou personalizado) para CLT ou PJ.
 */
@Data
@Schema(description = "Representa um benefício (padrão ou personalizado) para CLT ou PJ")
public class BeneficioDTO {

    @Schema(description = "Nome do benefício (ex: Vale Refeição, Plano de Saúde)", example = "Vale Refeição")
    private String nome;

    @Schema(description = "Descrição do benefício", example = "Cartão para compras em supermercado")
    private String descricao;

    @Schema(description = "Valor do benefício em reais, padrão BR", example = "500.00")
    private BigDecimal valor;
}