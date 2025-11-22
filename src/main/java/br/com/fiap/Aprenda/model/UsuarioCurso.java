package br.com.fiap.Aprenda.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidade Usuário-Curso
 * Relaciona usuários com cursos e armazena o progresso
 */
@Entity
@Table(name = "usuario_curso", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "usuario_id", "curso_id" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioCurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    @NotNull
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "curso_id", nullable = false)
    @NotNull
    private Curso curso;

    @Min(value = 0, message = "Progresso deve ser maior ou igual a 0")
    @Max(value = 100, message = "Progresso deve ser menor ou igual a 100")
    @Column(nullable = false)
    @Builder.Default
    private Integer progresso = 0;

    @Column(name = "status", length = 20)
    @Builder.Default
    private String status = "em_andamento"; // em_andamento, concluido

    @Column(name = "inscrito_em", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime inscritoEm = LocalDateTime.now();

    @Column(name = "concluido_em")
    private LocalDateTime concluidoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @PreUpdate
    public void preAtualizar() {
        this.atualizadoEm = LocalDateTime.now();
        if (this.progresso >= 100 && !"concluido".equals(this.status)) {
            this.status = "concluido";
            this.concluidoEm = LocalDateTime.now();
        }
    }
}
