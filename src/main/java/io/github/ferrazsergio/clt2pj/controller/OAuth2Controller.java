package io.github.ferrazsergio.clt2pj.controller;

import io.github.ferrazsergio.clt2pj.service.OAuth2Service;
import io.github.ferrazsergio.clt2pj.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Tag(name = "Autenticação Social", description = "Endpoints de autenticação via provedores OAuth2 (Google, GitHub)")
@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequiredArgsConstructor
public class OAuth2Controller {

    @Autowired
    private OAuth2Service oAuth2Service;

    @Operation(summary = "Sucesso na autenticação OAuth2", description = "Redireciona para o frontend com token JWT após login social")
    @GetMapping("/auth/oauth2/sucesso")
    public void oauth2Sucesso(@AuthenticationPrincipal OAuth2User principal, HttpServletResponse response) throws IOException {
        String token = oAuth2Service.gerarToken(principal);
        String usuarioOuEmail = oAuth2Service.getUsuarioOuEmail(principal);
        String provider = oAuth2Service.getProvider(principal);

        String redirectUrl = String.format(
                "http://localhost:5173/oauth2/sucesso?sucesso=true&token=%s&tipo=Bearer&usuario=%s&provider=%s",
                token,
                java.net.URLEncoder.encode(usuarioOuEmail, "UTF-8"),
                provider
        );
        response.sendRedirect(redirectUrl);
    }


    @GetMapping("/auth/oauth2/erro")
    public void oauth2Erro(HttpServletResponse response) throws IOException {
        response.sendRedirect("http://localhost:5173/login?oauth2=erro");
    }
}