package io.github.ferrazsergio.clt2pj.controller;

import io.github.ferrazsergio.clt2pj.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

@Tag(name = "Autenticação Social", description = "Endpoints de autenticação via provedores OAuth2 (Google, GitHub)")
@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequiredArgsConstructor
public class OAuth2Controller {

    private final JwtUtil jwtUtil;

    @Operation(summary = "Sucesso na autenticação OAuth2", description = "Retorna o token JWT após login social")
    @GetMapping("/auth/oauth2/sucesso")
    public ResponseEntity<?> oauth2Sucesso(@AuthenticationPrincipal OAuth2User principal) {
        String email = principal.getAttribute("email");
        String nome = principal.getAttribute("name");
        String usuarioOuEmail = email != null ? email : nome;

        String token = jwtUtil.gerarToken(usuarioOuEmail, Set.of("USUARIO"));

        return ResponseEntity.ok(Map.of(
                "sucesso", true,
                "token", token,
                "tipo", "Bearer",
                "usuario", usuarioOuEmail,
                "provider", principal.getAttributes().containsKey("login") ? "github" : "google"
        ));
    }

    @GetMapping("/auth/oauth2/erro")
    public ResponseEntity<?> oauth2Erro() {
        return ResponseEntity.status(401).body(Map.of(
                "sucesso", false,
                "erro", "Falha na autenticação OAuth2"
        ));
    }
}