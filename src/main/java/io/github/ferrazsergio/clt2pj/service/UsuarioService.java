package io.github.ferrazsergio.clt2pj.service;

import io.github.ferrazsergio.clt2pj.domain.Usuario;
import io.github.ferrazsergio.clt2pj.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario findOrCreateByEmail(String email, String name) {
        return usuarioRepository.findByEmail(email)
                .orElseGet(() -> {
                    Usuario novo = Usuario.builder()
                            .email(email)
                            .senha("OAUTH2")
                            .papeis(Collections.singleton("ROLE_USER"))
                            .build();
                    return usuarioRepository.save(novo);
                });
    }
}