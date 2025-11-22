package br.com.fiap.Aprenda.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade Curso
 * Representa os cursos disponíveis na plataforma
 */
@Entity
@Table(name = "cursos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Título é obrigatório")
    @Size(max = 200, message = "Título deve ter no máximo 200 caracteres")
    @Column(nullable = false, length = 200)
    private String titulo;

    @NotBlank(message = "Descrição é obrigatória")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @NotBlank(message = "Área é obrigatória")
    @Column(nullable = false, length = 50)
    private String area;

    @NotBlank(message = "Duração é obrigatória")
    @Column(nullable = false, length = 50)
    private String duracao;

    @NotBlank(message = "Nível é obrigatório")
    @Pattern(regexp = "Iniciante|Intermediário|Avançado", message = "Nível deve ser: Iniciante, Intermediário ou Avançado")
    @Column(nullable = false, length = 20)
    private String nivel;

    @Column(length = 10)
    private String icone;

    @Column(columnDefinition = "TEXT")
    private String conteudo;

    @Size(max = 100, message = "Nome do instrutor deve ter no máximo 100 caracteres")
    @Column(length = 100)
    private String instrutor;

    @DecimalMin(value = "0.0", message = "Avaliação deve ser maior ou igual a 0")
    @DecimalMax(value = "5.0", message = "Avaliação deve ser menor ou igual a 5")
    @Column
    @Builder.Default
    private Double avaliacao = 0.0;

    @Min(value = 1, message = "Total de aulas deve ser maior que 0")
    @Column(name = "total_aulas")
    @Builder.Default
    private Integer totalAulas = 1;

    @Column(name = "criado_em", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime criadoEm = LocalDateTime.now();

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UsuarioCurso> usuariosCursos = new ArrayList<>();

    @ManyToMany(mappedBy = "cursos")
    @Builder.Default
    private List<Trilha> trilhas = new ArrayList<>();

    @PreUpdate
    public void preAtualizar() {
        this.atualizadoEm = LocalDateTime.now();
    }
}
