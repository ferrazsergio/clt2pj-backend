package io.github.ferrazsergio.clt2pj.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ferrazsergio.clt2pj.domain.Usuario;
import io.github.ferrazsergio.clt2pj.service.TokenService;
import io.github.ferrazsergio.clt2pj.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenService tokenService;
    private final UsuarioService usuarioService;

    public OAuth2LoginSuccessHandler(TokenService tokenService, UsuarioService usuarioService) {
        this.tokenService = tokenService;
        this.usuarioService = usuarioService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = (String) oAuth2User.getAttributes().get("email");
        String provider = authentication.getAuthorities().stream()
                .findFirst().map(Object::toString).orElse("google");
        // Gere usuário e token como necessário
        Usuario usuario = usuarioService.findOrCreateByEmail(email, "Nome");
        String token = tokenService.generateToken(usuario);

        String redirectUrl = UriComponentsBuilder
                .fromUriString("http://localhost:5173/auth/callback")
                .queryParam("token", token)
                .queryParam("usuario", email)
                .queryParam("provider", provider)
                .build()
                .toUriString();

        response.sendRedirect(redirectUrl);
    }
}