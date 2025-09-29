package io.github.ferrazsergio.clt2pj.security;

import io.github.ferrazsergio.clt2pj.domain.Usuario;
import io.github.ferrazsergio.clt2pj.service.TokenService;
import io.github.ferrazsergio.clt2pj.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenService tokenService;
    private final UsuarioService usuarioService;
    private final OAuth2AuthorizedClientService authorizedClientService;

    public OAuth2LoginSuccessHandler(TokenService tokenService, UsuarioService usuarioService,
                                     OAuth2AuthorizedClientService authorizedClientService) {
        this.tokenService = tokenService;
        this.usuarioService = usuarioService;
        this.authorizedClientService = authorizedClientService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = (String) oAuth2User.getAttributes().get("email");
        String name = (String) oAuth2User.getAttributes().get("name");
        String provider = oAuth2User.getAttributes().containsKey("login") ? "github" : "google";

        // Busca o email do GitHub se não veio no principal
        if ("github".equals(provider) && email == null) {
            OAuth2AuthorizedClient client = authorizedClientService
                    .loadAuthorizedClient("github", authentication.getName());
            String tokenOAuth = client.getAccessToken().getTokenValue();

            RestTemplate restTemplate = new RestTemplate();
            String emailsUrl = "https://api.github.com/user/emails";
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + tokenOAuth);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<List<Map<String, Object>>> responseEmail = restTemplate.exchange(
                    emailsUrl,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {
                    }
            );

            List<Map<String, Object>> emails = responseEmail.getBody();
            if (emails != null) {
                email = emails.stream()
                        .filter(e -> Boolean.TRUE.equals(e.get("primary")) && Boolean.TRUE.equals(e.get("verified")))
                        .map(e -> (String) e.get("email"))
                        .findFirst()
                        .orElse(name);
            } else {
                email = name;
            }
        }

        Usuario usuario = usuarioService.findOrCreateByEmail(email, name != null ? name : email);
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