package br.com.fiap.Aprenda.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade Preferências do Usuário
 * Armazena áreas de interesse e níveis de conhecimento do usuário
 */
@Entity
@Table(name = "preferencias_usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreferenciasUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    @NotNull
    private Usuario usuario;

    @ElementCollection
    @CollectionTable(name = "areas_interesse", joinColumns = @JoinColumn(name = "preferencias_id"))
    @Column(name = "area")
    @Builder.Default
    private List<String> areasInteresse = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "niveis_interesse", joinColumns = @JoinColumn(name = "preferencias_id"))
    @Column(name = "nivel")
    @Builder.Default
    private List<String> niveisInteresse = new ArrayList<>();

    @Column(name = "onboarding_concluido")
    @Builder.Default
    private Boolean onboardingConcluido = false;

    @Column(name = "criado_em", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime criadoEm = LocalDateTime.now();

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @PreUpdate
    public void preAtualizar() {
        this.atualizadoEm = LocalDateTime.now();
    }
}
