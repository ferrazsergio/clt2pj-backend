package io.github.ferrazsergio.clt2pj.service;

import io.github.ferrazsergio.clt2pj.domain.Usuario;
import io.github.ferrazsergio.clt2pj.dto.LoginDTO;
import io.github.ferrazsergio.clt2pj.dto.RegistroDTO;
import io.github.ferrazsergio.clt2pj.exception.AuthException;
import io.github.ferrazsergio.clt2pj.repository.UsuarioRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.Set;

/**
 * Testes unitários para o serviço de autenticação (registro e login).
 */
class AutenticacaoServiceTest {

    private final UsuarioRepository usuarioRepository = Mockito.mock(UsuarioRepository.class);
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final AutenticacaoService autenticacaoService = new AutenticacaoService(usuarioRepository, passwordEncoder);

    @Test
    void testRegistrarNovoUsuario() {
        RegistroDTO dto = new RegistroDTO();
        dto.setEmail("teste@exemplo.com");
        dto.setSenha("123456");

        Mockito.when(usuarioRepository.findByEmail("teste@exemplo.com")).thenReturn(Optional.empty());
        Mockito.when(usuarioRepository.save(Mockito.any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Usuario usuario = autenticacaoService.registrar(dto);

        Assertions.assertEquals("teste@exemplo.com", usuario.getEmail());
        Assertions.assertTrue(passwordEncoder.matches("123456", usuario.getSenha()));
        Assertions.assertEquals(Set.of("USER"), usuario.getPapeis());
    }

    @Test
    void testRegistrarEmailDuplicado() {
        RegistroDTO dto = new RegistroDTO();
        dto.setEmail("teste@exemplo.com");
        dto.setSenha("123456");

        Mockito.when(usuarioRepository.findByEmail("teste@exemplo.com"))
                .thenReturn(Optional.of(Usuario.builder().email("teste@exemplo.com").senha("encoded").papeis(Set.of("USER")).build()));

        Assertions.assertThrows(AuthException.class, () -> autenticacaoService.registrar(dto));
    }

    @Test
    void testLoginSucesso() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("teste@exemplo.com");
        dto.setSenha("123456");

        Usuario usuario = Usuario.builder()
                .email("teste@exemplo.com")
                .senha(passwordEncoder.encode("123456"))
                .papeis(Set.of("USER"))
                .build();

        Mockito.when(usuarioRepository.findByEmail("teste@exemplo.com")).thenReturn(Optional.of(usuario));

        Usuario logado = autenticacaoService.autenticar(dto);

        Assertions.assertEquals("teste@exemplo.com", logado.getEmail());
    }

    @Test
    void testLoginSenhaErrada() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("teste@exemplo.com");
        dto.setSenha("senhaincorreta");

        Usuario usuario = Usuario.builder()
                .email("teste@exemplo.com")
                .senha(passwordEncoder.encode("123456"))
                .papeis(Set.of("USER"))
                .build();

        Mockito.when(usuarioRepository.findByEmail("teste@exemplo.com")).thenReturn(Optional.of(usuario));

        Assertions.assertThrows(AuthException.class, () -> autenticacaoService.autenticar(dto));
    }

    @Test
    void testLoginUsuarioNaoEncontrado() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("naoencontrado@exemplo.com");
        dto.setSenha("123456");

        Mockito.when(usuarioRepository.findByEmail("naoencontrado@exemplo.com")).thenReturn(Optional.empty());

        Assertions.assertThrows(AuthException.class, () -> autenticacaoService.autenticar(dto));
    }
}