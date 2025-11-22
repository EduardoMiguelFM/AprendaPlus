package br.com.fiap.Aprenda.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resposta de autenticação
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RespostaAutenticacao {

    private Boolean sucesso;
    private String mensagem;
    private UsuarioDTO usuario;
}
