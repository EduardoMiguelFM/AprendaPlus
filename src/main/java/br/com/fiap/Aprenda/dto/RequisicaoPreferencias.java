package br.com.fiap.Aprenda.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO para requisição de preferências do usuário
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequisicaoPreferencias {

    @Valid
    @Size(max = 3, message = "Máximo de 3 áreas de interesse permitidas")
    @Builder.Default
    private List<AreaInteresseDTO> areasInteresse = new ArrayList<>();

    @Builder.Default
    private Boolean onboardingConcluido = false;
}
