package br.com.fiap.Aprenda.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para resposta padrão da API
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RespostaApi<T> {

    private Boolean sucesso;
    private String mensagem;
    private T dados;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    public static <T> RespostaApi<T> sucesso(T dados, String mensagem) {
        return RespostaApi.<T>builder()
                .sucesso(true)
                .mensagem(mensagem)
                .dados(dados)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> RespostaApi<T> sucesso(String mensagem) {
        return RespostaApi.<T>builder()
                .sucesso(true)
                .mensagem(mensagem)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> RespostaApi<T> erro(String mensagem) {
        return RespostaApi.<T>builder()
                .sucesso(false)
                .mensagem(mensagem)
                .timestamp(LocalDateTime.now())
                .build();
    }
}









