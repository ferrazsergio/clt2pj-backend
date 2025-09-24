package io.github.ferrazsergio.clt2pj.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para autenticação de usuário (login).
 */
@Data
@Schema(description = "Dados para login do usuário")
public class LoginDTO {

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail deve ser válido")
    @Schema(description = "E-mail do usuário", example = "usuario@exemplo.com")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Schema(description = "Senha do usuário", example = "suaSenhaForteAqui")
    private String senha;
}