package io.github.ferrazsergio.clt2pj.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para registro de novo usuário.
 */
@Data
@Schema(description = "Dados necessários para registrar um novo usuário")
public class RegistroDTO {

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail deve ser válido")
    @Schema(description = "E-mail do usuário", example = "novo@exemplo.com")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 100, message = "Senha deve ter entre 6 e 100 caracteres")
    @Schema(description = "Senha do usuário", example = "minhaSenhaForte123")
    private String senha;
}