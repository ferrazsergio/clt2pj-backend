package io.github.ferrazsergio.clt2pj.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal; // <-- Adicione para usar BigDecimal

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
    private BigDecimal salarioClt; // <-- Troque Double para BigDecimal

    @Positive(message = "Salário PJ deve ser positivo")
    @Column(name = "salario_pj", nullable = false)
    private BigDecimal salarioPj; // <-- Troque Double para BigDecimal

    @Column(name = "beneficios")
    private String beneficios;

    @Column(name = "resultado_comparativo")
    private String resultadoComparativo;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    // Valor total de benefícios CLT
    @Column(name = "total_beneficios_clt")
    private BigDecimal totalBeneficiosClt;

    // Valor total de benefícios PJ
    @Column(name = "total_beneficios_pj")
    private BigDecimal totalBeneficiosPj;

    // Benefícios selecionados CLT
    @ElementCollection
    @CollectionTable(name = "simulacao_beneficios_clt", joinColumns = @JoinColumn(name = "simulacao_id"))
    @Column(name = "beneficio_clt")
    private List<String> beneficiosSelecionadosClt;

    // Benefícios selecionados PJ
    @ElementCollection
    @CollectionTable(name = "simulacao_beneficios_pj", joinColumns = @JoinColumn(name = "simulacao_id"))
    @Column(name = "beneficio_pj")
    private List<String> beneficiosSelecionadosPj;

    // Reserva de emergência
    @Column(name = "reserva_emergencia")
    private BigDecimal reservaEmergencia;

    // Valor sugerido de reserva
    @Column(name = "valor_reserva_sugerido")
    private BigDecimal valorReservaSugerido;

    // Tipo de tributação PJ
    @Column(name = "tipo_tributacao")
    private String tipoTributacao;
}