package br.com.fiap.Aprenda.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidade Usuário-Desafio
 * Relaciona usuários com desafios concluídos e armazena a pontuação
 */
@Entity
@Table(name = "usuario_desafio", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "usuario_id", "desafio_id" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDesafio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    @NotNull
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "desafio_id", nullable = false)
    @NotNull
    private Desafio desafio;

    @Column(nullable = false)
    @Builder.Default
    private Integer pontuacao = 0;

    @Column(name = "total_perguntas", nullable = false)
    @Builder.Default
    private Integer totalPerguntas = 0;

    @Column(name = "tempo_gasto")
    private Integer tempoGasto; // em segundos

    @Column(name = "pontos_ganhos")
    @Builder.Default
    private Integer pontosGanhos = 0;

    @Column(name = "concluido_em", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime concluidoEm = LocalDateTime.now();
}
