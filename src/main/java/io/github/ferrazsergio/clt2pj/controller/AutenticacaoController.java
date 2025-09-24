package io.github.ferrazsergio.clt2pj.controller;

import io.github.ferrazsergio.clt2pj.dto.LoginDTO;
import io.github.ferrazsergio.clt2pj.dto.RegistroDTO;
import io.github.ferrazsergio.clt2pj.service.AutenticacaoService;
import io.github.ferrazsergio.clt2pj.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Autenticação", description = "Endpoints para registro, login e logout de usuários")
@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AutenticacaoController {

    private final AutenticacaoService autenticacaoService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "Registro de novo usuário")
    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody @Valid RegistroDTO dto) {
        var usuario = autenticacaoService.registrar(dto);
        String token = jwtUtil.gerarToken(usuario.getEmail(), usuario.getPapeis());
        return ResponseEntity.ok().body(token);
    }

    @Operation(summary = "Login de usuário")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDTO dto) {
        var usuario = autenticacaoService.autenticar(dto);
        String token = jwtUtil.gerarToken(usuario.getEmail(), usuario.getPapeis());
        return ResponseEntity.ok().body(token);
    }

    @Operation(summary = "Logout do usuário")
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok().body("Logout realizado com sucesso.");
    }
}