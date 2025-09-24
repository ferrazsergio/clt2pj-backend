package io.github.ferrazsergio.clt2pj.repository;

import io.github.ferrazsergio.clt2pj.domain.Simulacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repositório para operações de persistência da entidade Simulacao.
 */
public interface SimulacaoRepository extends JpaRepository<Simulacao, String> {
    List<Simulacao> findByUsuarioId(String usuarioId);
}