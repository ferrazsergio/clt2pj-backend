package io.github.ferrazsergio.clt2pj.service;

import io.github.ferrazsergio.clt2pj.domain.Usuario;
import io.github.ferrazsergio.clt2pj.dto.LoginDTO;
import io.github.ferrazsergio.clt2pj.dto.RegistroDTO;
import io.github.ferrazsergio.clt2pj.exception.AuthException;
import io.github.ferrazsergio.clt2pj.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Serviço responsável pela autenticação e registro de usuários.
 */
@Service
@RequiredArgsConstructor
public class AutenticacaoService {

    private static final Logger logger = LogManager.getLogger(AutenticacaoService.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Realiza o registro de um novo usuário.
     *
     * @param dto dados do registro
     * @return usuário registrado
     * @throws AuthException se o e-mail já estiver cadastrado
     */
    public Usuario registrar(RegistroDTO dto) {
        logger.info("Tentando registrar usuário com e-mail: {}", dto.getEmail());
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            logger.warn("Tentativa de registro com e-mail já cadastrado: {}", dto.getEmail());
            throw new AuthException("E-mail já cadastrado.");
        }
        Usuario usuario = Usuario.builder()
                .email(dto.getEmail())
                .senha(passwordEncoder.encode(dto.getSenha()))
                .papeis(Set.of("USER"))
                .build();
        Usuario salvo = usuarioRepository.save(usuario);
        logger.info("Usuário registrado com sucesso: {}", salvo.getEmail());
        return salvo;
    }

    /**
     * Realiza a autenticação do usuário.
     *
     * @param dto dados de login
     * @return usuário autenticado
     * @throws AuthException se usuário não encontrado ou senha inválida
     */
    public Usuario autenticar(LoginDTO dto) {
        logger.info("Tentando autenticar usuário: {}", dto.getEmail());
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> {
                    logger.error("Usuário não encontrado: {}", dto.getEmail());
                    return new AuthException("Usuário não encontrado.");
                });
        if (!passwordEncoder.matches(dto.getSenha(), usuario.getSenha())) {
            logger.warn("Senha inválida para usuário: {}", dto.getEmail());
            throw new AuthException("Senha inválida.");
        }
        logger.info("Usuário autenticado com sucesso: {}", usuario.getEmail());
        return usuario;
    }
}