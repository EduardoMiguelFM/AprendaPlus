package br.com.fiap.Aprenda.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para área de interesse
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AreaInteresseDTO {

    @NotBlank(message = "Área é obrigatória")
    @Pattern(regexp = "ia|dados|programacao|sustentabilidade|design|marketing|gestao|vendas|rh|financas|saude|educacao", message = "Área inválida")
    private String area;

    @NotBlank(message = "Nível é obrigatório")
    @Pattern(regexp = "Iniciante|Intermediário|Avançado", message = "Nível deve ser: Iniciante, Intermediário ou Avançado")
    private String nivel;
}
