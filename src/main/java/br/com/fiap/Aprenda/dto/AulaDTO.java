package br.com.fiap.Aprenda.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa uma aula exibida na tela de detalhes do curso.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AulaDTO {

    private int numero;
    private String titulo;
    private String resumo;
    private String duracao;
    private boolean concluida;
}



