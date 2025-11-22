package br.com.fiap.Aprenda.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidade Transação de Pontos
 * Armazena o histórico de ganhos e gastos de pontos
 */
@Entity
@Table(name = "transacoes_pontos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransacaoPontos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    @NotNull
    private Usuario usuario;

    @Column(nullable = false)
    @NotNull
    private Integer pontos; // positivo para ganhos, negativo para gastos

    @Column(name = "motivo", length = 200)
    private String motivo; // motivo da transação (ex: "Desafio concluído", "Curso completado")

    @Column(name = "tipo_transacao", length = 20)
    @Builder.Default
    private String tipoTransacao = "ganho"; // ganho, gasto

    @Column(name = "criado_em", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime criadoEm = LocalDateTime.now();
}
