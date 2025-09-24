package io.github.ferrazsergio.clt2pj.controller;

import io.github.ferrazsergio.clt2pj.dto.BeneficioDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Arrays;

/**
 * Controller responsável pela consulta dos benefícios oferecidos.
 */
@Tag(name = "Benefícios", description = "Consulta de benefícios disponíveis")
@RestController
public class BeneficioController {

    @Operation(summary = "Lista todos os benefícios disponíveis")
    @ApiResponse(responseCode = "200", description = "Lista de benefícios retornada com sucesso")
    @GetMapping("/beneficios")
    public ResponseEntity<List<BeneficioDTO>> listar() {
        List<BeneficioDTO> beneficios = Arrays.asList(
                criarBeneficio("Vale Alimentação", "Cartão para compras em supermercado"),
                criarBeneficio("Vale Transporte", "Auxílio para deslocamento casa-trabalho"),
                criarBeneficio("Plano de Saúde", "Cobertura médica para colaborador")
        );
        return ResponseEntity.ok(beneficios);
    }

    private BeneficioDTO criarBeneficio(String nome, String descricao) {
        BeneficioDTO dto = new BeneficioDTO();
        dto.setNome(nome);
        dto.setDescricao(descricao);
        return dto;
    }
}