package io.github.ferrazsergio.clt2pj.controller;

import io.github.ferrazsergio.clt2pj.dto.SimulacaoRequestDTO;
import io.github.ferrazsergio.clt2pj.dto.SimulacaoResponseDTO;
import io.github.ferrazsergio.clt2pj.domain.Simulacao;
import io.github.ferrazsergio.clt2pj.service.SimulacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para simulações financeiras entre regimes CLT e PJ.
 */
@RestController
@RequestMapping("/simulacao")
@Tag(name = "Simulação CLT/PJ", description = "Endpoints para simulação e histórico de comparativos financeiros entre regimes CLT e PJ")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequiredArgsConstructor
public class SimulacaoController {

    private final SimulacaoService simulacaoService;

    /**
     * Realiza simulação CLT/PJ.
     * Calcula salários líquidos, impostos e benefícios comparando regimes CLT e PJ.
     */
    @Operation(
            summary = "Realizar simulação CLT/PJ",
            description = "Calcula salários líquidos, impostos e benefícios entre CLT e PJ, conforme regras vigentes de INSS e IRRF (2025), padrão Real brasileiro."
    )
    @PostMapping
    public ResponseEntity<SimulacaoResponseDTO> simular(
            @RequestBody @Valid SimulacaoRequestDTO request
    ) {
        SimulacaoResponseDTO resposta = simulacaoService.simular(request);
        return ResponseEntity.ok(resposta);
    }

    /**
     * Salva uma nova simulação no banco.
     */
    @Operation(summary = "Salvar simulação", description = "Persiste uma simulação realizada no histórico do usuário.")
    @PostMapping("/salvar")
    public ResponseEntity<Simulacao> salvarSimulacao(@RequestBody @Valid Simulacao simulacao) {
        Simulacao salva = simulacaoService.salvar(simulacao);
        return ResponseEntity.ok(salva);
    }

    /**
     * Lista o histórico de simulações do usuário.
     */
    @GetMapping("/historico/{usuarioId}")
    public ResponseEntity<List<SimulacaoResponseDTO>> getHistorico(@PathVariable String usuarioId) {
        return ResponseEntity.ok(simulacaoService.historicoDTO(usuarioId));
    }
}