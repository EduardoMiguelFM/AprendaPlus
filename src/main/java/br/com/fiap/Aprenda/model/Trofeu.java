package br.com.fiap.Aprenda.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade Troféu
 * Representa os troféus disponíveis na plataforma
 */
@Entity
@Table(name = "trofeus")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trofeu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(length = 10)
    private String icone;

    @Column(name = "criado_em", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime criadoEm = LocalDateTime.now();

    @OneToMany(mappedBy = "trofeu", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UsuarioTrofeu> usuariosTrofeus = new ArrayList<>();
}
