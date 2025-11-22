package br.com.fiap.Aprenda.controller.web;

import br.com.fiap.Aprenda.service.GamificacaoService;
import br.com.fiap.Aprenda.service.IAService;
import br.com.fiap.Aprenda.service.UsuarioService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para chat com IA
 */
@Controller
@RequestMapping("/api/ia")
@RequiredArgsConstructor
@Slf4j
public class IAChatController {

    private final ObjectProvider<IAService> iaServiceProvider;
    private final UsuarioService servicoUsuario;
    private final GamificacaoService servicoGamificacao;

    @PostMapping("/chat")
    @ResponseBody
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()
                    || "anonymousUser".equalsIgnoreCase(String.valueOf(authentication.getName()))) {
                return ResponseEntity.ok(new ChatResponse(
                        "Faça login para receber recomendações personalizadas da nossa IA."));
            }

            var usuario = servicoUsuario.obterUsuarioAtual();
            var stats = servicoGamificacao.obterEstatisticas(usuario.getId());

            String contexto = String.format(
                    "Usuário: %s. Pontos: %d. Cursos em andamento: %d. Cursos concluídos: %d.",
                    usuario.getNome(),
                    stats.getPontos(),
                    stats.getCursosEmAndamento(),
                    stats.getCursosConcluidos());

            IAService servicoIA = iaServiceProvider.getIfAvailable();
            if (servicoIA == null) {
                return ResponseEntity.ok(new ChatResponse(
                        "Nosso assistente IA está em manutenção agora. Continue estudando e tente novamente em breve!"));
            }

            String resposta = servicoIA.responderPergunta(request.getMensagem(), contexto);
            return ResponseEntity.ok(new ChatResponse(resposta));
        } catch (br.com.fiap.Aprenda.exception.RecursoNaoEncontradoException ex) {
            log.warn("Chat IA sem usuário autenticado", ex);
            return ResponseEntity.ok(new ChatResponse(
                    "Não conseguimos identificar sua conta. Faça login novamente para usar o assistente."));
        } catch (Exception e) {
            log.error("Erro no chat IA", e);
            return ResponseEntity.ok(new ChatResponse(
                    "Estou fora do ar por um instante. Continue explorando seus cursos e tente novamente em alguns minutos!"));
        }
    }

    @Data
    public static class ChatRequest {
        private String mensagem;
    }

    @Data
    public static class ChatResponse {
        private String resposta;

        public ChatResponse(String resposta) {
            this.resposta = resposta;
        }
    }
}
