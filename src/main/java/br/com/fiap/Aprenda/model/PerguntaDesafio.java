package br.com.fiap.Aprenda.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidade Pergunta de Desafio
 * Representa as perguntas de um desafio do tipo quiz
 */
@Entity
@Table(name = "perguntas_desafio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerguntaDesafio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "desafio_id", nullable = false)
    @NotNull
    private Desafio desafio;

    @NotBlank(message = "Pergunta é obrigatória")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String pergunta;

    @ElementCollection
    @CollectionTable(name = "opcoes_pergunta", joinColumns = @JoinColumn(name = "pergunta_id"))
    @Column(name = "opcao", length = 500)
    @Size(min = 2, max = 4, message = "Deve haver entre 2 e 4 opções")
    @Builder.Default
    private List<String> opcoes = new ArrayList<>();

    @Min(value = 0, message = "Índice correto deve ser maior ou igual a 0")
    @Max(value = 3, message = "Índice correto deve ser menor ou igual a 3")
    @Column(name = "indice_correto", nullable = false)
    private Integer indiceCorreto;

    @Column(columnDefinition = "TEXT")
    private String explicacao;
}
