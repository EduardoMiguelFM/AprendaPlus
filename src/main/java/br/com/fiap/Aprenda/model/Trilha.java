package br.com.fiap.Aprenda.model;

import br.com.fiap.Aprenda.model.Desafio;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Entidade Trilha de Aprendizado
 * Representa uma trilha que agrupa cursos relacionados
 */
@Entity
@Table(name = "trilhas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trilha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Título é obrigatório")
    @Size(max = 200, message = "Título deve ter no máximo 200 caracteres")
    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @ManyToMany
    @JoinTable(name = "trilha_curso", joinColumns = @JoinColumn(name = "trilha_id"), inverseJoinColumns = @JoinColumn(name = "curso_id"))
    @Builder.Default
    private List<Curso> cursos = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "trilha_desafio", joinColumns = @JoinColumn(name = "trilha_id"), inverseJoinColumns = @JoinColumn(name = "desafio_id"))
    @Builder.Default
    private Set<Desafio> desafios = new HashSet<>();

    @Column(length = 10)
    private String icone;

    @Column(length = 7)
    private String cor; // hex color

    @NotBlank(message = "Área é obrigatória")
    @Column(nullable = false, length = 50)
    private String area;

    @NotBlank(message = "Nível mínimo é obrigatório")
    @Pattern(regexp = "Iniciante|Intermediário|Avançado", message = "Nível deve ser: Iniciante, Intermediário ou Avançado")
    @Column(name = "nivel_minimo", nullable = false, length = 20)
    private String nivelMinimo;

    @Column(name = "duracao_total", length = 50)
    private String duracaoTotal;

    @Column(name = "criado_em", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime criadoEm = LocalDateTime.now();

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @OneToMany(mappedBy = "trilha", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UsuarioTrilha> usuariosTrilhas = new ArrayList<>();

    @PreUpdate
    public void preAtualizar() {
        this.atualizadoEm = LocalDateTime.now();
    }
}
