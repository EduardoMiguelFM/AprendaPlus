package br.com.fiap.Aprenda.exception;

/**
 * Exceção para recursos não encontrados
 */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }

    public RecursoNaoEncontradoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}









