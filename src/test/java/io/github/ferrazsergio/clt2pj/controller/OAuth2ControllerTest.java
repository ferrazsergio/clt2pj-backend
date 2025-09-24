package io.github.ferrazsergio.clt2pj.controller;

import io.github.ferrazsergio.clt2pj.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

/**
 * Teste do controller de autenticação OAuth2.
 */
@WebMvcTest(OAuth2Controller.class)
class OAuth2ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void testSucessoOauth2RetornaToken() throws Exception {
        // Simula um principal OAuth2User
        OAuth2User principal = Mockito.mock(OAuth2User.class);
        Mockito.when(principal.getAttribute("email")).thenReturn("usuario@exemplo.com");
        Mockito.when(principal.getAttribute("name")).thenReturn("Nome Usuário");

        Mockito.when(jwtUtil.gerarToken("usuario@exemplo.com", Set.of("USER"))).thenReturn("token-jwt-oauth2-mockado");

        // Testa diretamente o método do controller (sem MockMvc, pois @AuthenticationPrincipal não é populado por default no teste)
        OAuth2Controller controller = new OAuth2Controller(jwtUtil);
        String token = controller.oauth2Sucesso(principal);

        // Validação direta
        org.junit.jupiter.api.Assertions.assertEquals("token-jwt-oauth2-mockado", token);
    }
}