package br.com.fiap.Aprenda.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisição de conclusão de desafio
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequisicaoCompletarDesafio {

    @NotNull(message = "Pontuação é obrigatória")
    @Min(value = 0, message = "Pontuação deve ser maior ou igual a 0")
    private Integer pontuacao;

    @NotNull(message = "Total de perguntas é obrigatório")
    @Min(value = 1, message = "Total de perguntas deve ser maior que 0")
    private Integer totalPerguntas;

    @Min(value = 0, message = "Tempo gasto deve ser maior ou igual a 0")
    private Integer tempoGasto; // em segundos
}






