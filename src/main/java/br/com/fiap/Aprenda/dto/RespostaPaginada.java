package br.com.fiap.Aprenda.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para resposta paginada
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RespostaPaginada<T> {

    private Boolean sucesso;
    private List<T> dados;
    private InformacaoPaginacao paginacao;
    private String mensagem;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InformacaoPaginacao {
        private Integer pagina;
        private Integer tamanho;
        private Long total;
        private Integer totalPaginas;
    }
}







