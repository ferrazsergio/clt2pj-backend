package io.github.ferrazsergio.clt2pj.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para confirmação de e-mail do usuário.
 */
@Data
@Schema(description = "Dados para confirmação de e-mail")
public class ConfirmacaoEmailDTO {

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail deve ser válido")
    @Schema(description = "E-mail do usuário", example = "novo@exemplo.com")
    private String email;

    @NotBlank(message = "Código é obrigatório")
    @Size(min = 6, max = 6, message = "O código deve ter 6 dígitos")
    @Schema(description = "Código de 6 dígitos enviado por e-mail", example = "123456")
    private String codigo;
}