package br.com.fiap.Aprenda.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidade Usuário-Troféu
 * Relaciona usuários com troféus conquistados
 */
@Entity
@Table(name = "usuario_trofeu", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "usuario_id", "trofeu_id" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioTrofeu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    @NotNull
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "trofeu_id", nullable = false)
    @NotNull
    private Trofeu trofeu;

    @Column(name = "conquistado_em", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime conquistadoEm = LocalDateTime.now();
}
