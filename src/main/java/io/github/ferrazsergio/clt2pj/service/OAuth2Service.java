package io.github.ferrazsergio.clt2pj.service;

import io.github.ferrazsergio.clt2pj.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class OAuth2Service {

    private final JwtUtil jwtUtil;

    public String gerarToken(OAuth2User principal) {
        String email = principal.getAttribute("email");
        String nome = principal.getAttribute("name");
        String usuarioOuEmail = email != null ? email : nome;
        return jwtUtil.gerarToken(usuarioOuEmail, Set.of("USUARIO"));
    }

    public String getProvider(OAuth2User principal) {
        return principal.getAttributes().containsKey("login") ? "github" : "google";
    }

    public String getUsuarioOuEmail(OAuth2User principal) {
        String email = principal.getAttribute("email");
        String nome = principal.getAttribute("name");
        return email != null ? email : nome;
    }
}