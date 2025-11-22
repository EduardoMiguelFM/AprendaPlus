package br.com.fiap.Aprenda.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidade Usuário-Trilha
 * Relaciona usuários com trilhas inscritas
 */
@Entity
@Table(name = "usuario_trilha", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "usuario_id", "trilha_id" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioTrilha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    @NotNull
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "trilha_id", nullable = false)
    @NotNull
    private Trilha trilha;

    @Column(name = "inscrito_em", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime inscritoEm = LocalDateTime.now();
}
