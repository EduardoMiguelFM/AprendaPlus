package br.com.fiap.Aprenda.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade Desafio
 * Representa os desafios/jogos disponíveis na plataforma
 */
@Entity
@Table(name = "desafios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Desafio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tipo é obrigatório")
    @Pattern(regexp = "quiz|memoria|logica|codigo", message = "Tipo deve ser: quiz, memoria, logica ou codigo")
    @Column(nullable = false, length = 20)
    private String tipo;

    @NotBlank(message = "Área é obrigatória")
    @Column(nullable = false, length = 50)
    private String area;

    @NotBlank(message = "Nível é obrigatório")
    @Pattern(regexp = "Iniciante|Intermediário|Avançado", message = "Nível deve ser: Iniciante, Intermediário ou Avançado")
    @Column(nullable = false, length = 20)
    private String nivel;

    @NotBlank(message = "Título é obrigatório")
    @Size(max = 200, message = "Título deve ter no máximo 200 caracteres")
    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Min(value = 1, message = "Pontos deve ser maior que 0")
    @Column(nullable = false)
    private Integer pontos;

    @Column(length = 10)
    private String icone;

    @Size(max = 50, message = "Dificuldade deve ter no máximo 50 caracteres")
    @Column(length = 50)
    private String dificuldade;

    @Column(name = "criado_em", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime criadoEm = LocalDateTime.now();

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @OneToMany(mappedBy = "desafio", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private List<PerguntaDesafio> perguntas = new ArrayList<>();

    @OneToMany(mappedBy = "desafio", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private List<UsuarioDesafio> usuariosDesafios = new ArrayList<>();

    @PreUpdate
    public void preAtualizar() {
        this.atualizadoEm = LocalDateTime.now();
    }
}
