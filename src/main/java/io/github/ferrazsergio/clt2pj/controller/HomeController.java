package io.github.ferrazsergio.clt2pj.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "Home", description = "Endpoint de verificação da API")
@RestController
public class HomeController {

    @Operation(summary = "Informações da API", description = "Retorna informações básicas sobre a API")
    @GetMapping("/")
    public Map<String, Object> home() {
        return Map.of(
                "aplicacao", "CLT2PJ API",
                "status", "online",
                "mensagem", "API rodando com sucesso!",
                "documentacao", "/swagger-ui.html"
        );
    }
}