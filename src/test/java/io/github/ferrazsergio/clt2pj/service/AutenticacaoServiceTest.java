package io.github.ferrazsergio.clt2pj.service;

import io.github.ferrazsergio.clt2pj.domain.Usuario;
import io.github.ferrazsergio.clt2pj.dto.LoginDTO;
import io.github.ferrazsergio.clt2pj.dto.RegistroDTO;
import io.github.ferrazsergio.clt2pj.exception.AuthException;
import io.github.ferrazsergio.clt2pj.repository.UsuarioRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AutenticacaoServiceTest {

    private final UsuarioRepository usuarioRepository = Mockito.mock(UsuarioRepository.class);
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final AutenticacaoService autenticacaoService = new AutenticacaoService(
            usuarioRepository,
            passwordEncoder
    );

    @Test
    void testRegistrarNovoUsuario() {
        RegistroDTO dto = new RegistroDTO();
        dto.setEmail("teste@exemplo.com");
        dto.setSenha("123456");

        when(usuarioRepository.findByEmail("teste@exemplo.com")).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Usuario usuarioSalvo = autenticacaoService.registrar(dto);

        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(usuarioCaptor.capture());
        Usuario usuario = usuarioCaptor.getValue();

        Assertions.assertEquals("teste@exemplo.com", usuario.getEmail());
        Assertions.assertTrue(passwordEncoder.matches("123456", usuario.getSenha()));
        Assertions.assertEquals(Set.of("USER"), usuario.getPapeis());
        // Outros campos: depende do seu builder, adicione asserts se quiser
    }

    @Test
    void testRegistrarEmailDuplicado() {
        RegistroDTO dto = new RegistroDTO();
        dto.setEmail("teste@exemplo.com");
        dto.setSenha("123456");

        when(usuarioRepository.findByEmail("teste@exemplo.com"))
                .thenReturn(Optional.of(Usuario.builder()
                        .email("teste@exemplo.com")
                        .senha("encoded")
                        .papeis(Set.of("USER"))
                        .build()));

        AuthException exception = Assertions.assertThrows(
                AuthException.class,
                () -> autenticacaoService.registrar(dto)
        );

        Assertions.assertEquals("E-mail já cadastrado.", exception.getMessage());
        verify(usuarioRepository, never()).save(any());
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

        when(usuarioRepository.findByEmail("teste@exemplo.com")).thenReturn(Optional.of(usuario));

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

        when(usuarioRepository.findByEmail("teste@exemplo.com")).thenReturn(Optional.of(usuario));

        AuthException exception = Assertions.assertThrows(
                AuthException.class,
                () -> autenticacaoService.autenticar(dto)
        );

        Assertions.assertEquals("Senha inválida.", exception.getMessage());
    }

    @Test
    void testLoginUsuarioNaoEncontrado() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("naoencontrado@exemplo.com");
        dto.setSenha("123456");

        when(usuarioRepository.findByEmail("naoencontrado@exemplo.com")).thenReturn(Optional.empty());

        AuthException exception = Assertions.assertThrows(
                AuthException.class,
                () -> autenticacaoService.autenticar(dto)
        );

        Assertions.assertEquals("Usuário não encontrado.", exception.getMessage());
    }
}