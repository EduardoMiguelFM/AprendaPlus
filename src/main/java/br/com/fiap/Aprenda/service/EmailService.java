package br.com.fiap.Aprenda.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Serviço para envio de emails assíncronos
 * Utiliza @Async para processar emails em background
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    @Async("taskExecutor")
    public void enviarEmailEsqueciSenha(String email, String token) {
        try {
            // Simular processamento assíncrono
            log.info("Processando envio de email de recuperação de senha para: {}", email);

            // TODO: Implementar envio real de email (JavaMail, SendGrid, etc.)
            // Por enquanto, apenas loga a ação
            log.info("Email de recuperação de senha enviado para: {} com token: {}", email, token);

            // Simular delay de processamento
            Thread.sleep(500);

            log.info("Email processado com sucesso para: {}", email);
        } catch (Exception e) {
            log.error("Erro ao processar email de recuperação de senha para: {}", email, e);
        }
    }

    @Async("taskExecutor")
    public void enviarEmailBoasVindas(String email, String nome) {
        try {
            // Simular processamento assíncrono
            log.info("Processando envio de email de boas-vindas para: {}", email);

            // TODO: Implementar envio real de email
            log.info("Email de boas-vindas enviado para: {} - Olá {}!", email, nome);

            // Simular delay de processamento
            Thread.sleep(500);

            log.info("Email de boas-vindas processado com sucesso para: {}", email);
        } catch (Exception e) {
            log.error("Erro ao processar email de boas-vindas para: {}", email, e);
        }
    }

    @Async("taskExecutor")
    public void enviarNotificacaoConquista(String email, String titulo, String descricao) {
        try {
            log.info("Processando notificação de conquista para: {} - {}", email, titulo);

            // TODO: Implementar envio real de email
            log.info("Notificação de conquista enviada: {} - {}", titulo, descricao);

            Thread.sleep(300);

            log.info("Notificação processada com sucesso para: {}", email);
        } catch (Exception e) {
            log.error("Erro ao processar notificação de conquista para: {}", email, e);
        }
    }
}









