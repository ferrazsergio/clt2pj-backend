package io.github.ferrazsergio.clt2pj.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidade que representa uma simulação financeira entre regimes CLT e PJ.
 */
@Entity
@Table(name = "simulacoes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Simulacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @NotNull(message = "Usuário é obrigatório")
    private Usuario usuario;

    @Positive(message = "Salário CLT deve ser positivo")
    @Column(name = "salario_clt", nullable = false)
    private Double salarioClt;

    @Positive(message = "Salário PJ deve ser positivo")
    @Column(name = "salario_pj", nullable = false)
    private Double salarioPj;

    @Column(name = "beneficios")
    private String beneficios;

    @Column(name = "resultado_comparativo")
    private String resultadoComparativo;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;
}