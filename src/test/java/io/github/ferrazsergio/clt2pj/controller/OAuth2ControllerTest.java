package io.github.ferrazsergio.clt2pj.controller;

import io.github.ferrazsergio.clt2pj.service.OAuth2Service;
import io.github.ferrazsergio.clt2pj.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Map;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OAuth2Controller.class)
class OAuth2ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private OAuth2Service oAuth2Service;

    /**
     * Simula o principal OAuth2User via OAuth2AuthenticationToken para testes de controller.
     */
    private RequestPostProcessor oauth2Principal(String email, String name, String provider) {
        return request -> {
            OAuth2User principal = Mockito.mock(OAuth2User.class);
            Mockito.when(principal.getAttribute("email")).thenReturn(email);
            Mockito.when(principal.getAttribute("name")).thenReturn(name);

            Map<String, Object> attributes = new java.util.HashMap<>();
            attributes.put("email", email);
            attributes.put("name", name);
            attributes.put("provider", provider);

            if ("github".equals(provider)) {
                attributes.put("login", "githubUser");
            } else {
                attributes.put("sub", "googleSub");
            }
            Mockito.when(principal.getAttributes()).thenReturn(attributes);

            OAuth2AuthenticationToken authToken = new OAuth2AuthenticationToken(
                    principal,
                    java.util.List.of(), // authorities
                    provider // registrationId
            );
            request.setUserPrincipal(authToken);
            return request;
        };
    }

    @Test
    @WithMockUser
    void testSucessoOauth2Redirect() throws Exception {
        String email = "usuario@exemplo.com";
        String name = "Nome Usuário";
        String provider = "google";
        String token = "token-jwt-oauth2-mockado";

        // Mock oAuth2Service para retornar valores esperados
        Mockito.when(oAuth2Service.gerarToken(Mockito.any())).thenReturn(token);
        Mockito.when(oAuth2Service.getUsuarioOuEmail(Mockito.any())).thenReturn(email);
        Mockito.when(oAuth2Service.getProvider(Mockito.any())).thenReturn(provider);

        mockMvc.perform(get("/auth/oauth2/sucesso")
                        .with(oauth2Principal(email, name, provider)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("http://localhost:5173/oauth2/sucesso?sucesso=true&token=*&tipo=Bearer&usuario=usuario@exemplo.com&provider=google"));
    }
}