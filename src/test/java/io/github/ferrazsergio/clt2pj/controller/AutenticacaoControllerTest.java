package io.github.ferrazsergio.clt2pj.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ferrazsergio.clt2pj.dto.RegistroDTO;
import io.github.ferrazsergio.clt2pj.dto.LoginDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de integração do controller de autenticação (registro e login).
 */
@SpringBootTest
@AutoConfigureMockMvc
class AutenticacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegistrarEAutenticar() throws Exception {
        // Registro - deve funcionar
        RegistroDTO registroDTO = new RegistroDTO();
        registroDTO.setEmail("novousuario@exemplo.com");
        registroDTO.setSenha("123456");

        mockMvc.perform(post("/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registroDTO)))
                .andExpect(status().isOk());

        // Registro duplicado - deve falhar
        mockMvc.perform(post("/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registroDTO)))
                .andExpect(status().isConflict())
                .andExpect(content().string("E-mail já cadastrado."));

        // Login - deve funcionar
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("novousuario@exemplo.com");
        loginDTO.setSenha("123456");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk());
    }
}