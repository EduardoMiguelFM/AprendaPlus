package br.com.fiap.Aprenda.controller.api;

import br.com.fiap.Aprenda.dto.*;
import br.com.fiap.Aprenda.model.Desafio;
import br.com.fiap.Aprenda.model.PerguntaDesafio;
import br.com.fiap.Aprenda.model.UsuarioDesafio;
import br.com.fiap.Aprenda.service.DesafioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller REST para gerenciamento de desafios
 */
@RestController
@RequestMapping("/api/desafios")
@RequiredArgsConstructor
@Tag(name = "Desafios", description = "Endpoints para gerenciamento de desafios")
public class DesafioController {

    private final DesafioService servicoDesafio;

    @GetMapping
    @Operation(summary = "Listar desafios disponíveis")
    public ResponseEntity<RespostaApi<RespostaPaginada<Desafio>>> listarTodos(
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String nivel,
            @RequestParam(required = false) String tipo,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho) {
        Pageable pageable = PageRequest.of(pagina, tamanho);
        Page<Desafio> desafiosPage = servicoDesafio.listarTodos(area, nivel, tipo, pageable);

        RespostaPaginada.InformacaoPaginacao paginacao = RespostaPaginada.InformacaoPaginacao.builder()
                .pagina(pagina)
                .tamanho(tamanho)
                .total(desafiosPage.getTotalElements())
                .totalPaginas(desafiosPage.getTotalPages())
                .build();

        RespostaPaginada<Desafio> resposta = RespostaPaginada.<Desafio>builder()
                .sucesso(true)
                .dados(desafiosPage.getContent())
                .paginacao(paginacao)
                .mensagem("Desafios recuperados com sucesso")
                .build();

        return ResponseEntity.ok(RespostaApi.sucesso(resposta, null));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter detalhes de um desafio")
    public ResponseEntity<RespostaApi<Desafio>> obterPorId(@PathVariable Long id) {
        Desafio desafio = servicoDesafio.obterPorId(id);
        return ResponseEntity.ok(RespostaApi.sucesso(desafio, "Desafio recuperado com sucesso"));
    }

    @GetMapping("/{id}/perguntas")
    @Operation(summary = "Obter perguntas de um desafio quiz")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<RespostaApi<List<PerguntaDesafio>>> obterPerguntas(@PathVariable Long id) {
        List<PerguntaDesafio> perguntas = servicoDesafio.obterPerguntas(id);
        return ResponseEntity.ok(RespostaApi.sucesso(perguntas, "Perguntas recuperadas com sucesso"));
    }

    @PostMapping("/{id}/completar")
    @Operation(summary = "Marcar desafio como concluído")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<RespostaApi<Map<String, Object>>> completar(
            @PathVariable Long id,
            @Valid @RequestBody RequisicaoCompletarDesafio requisicao) {
        UsuarioDesafio usuarioDesafio = servicoDesafio.completar(id, requisicao);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("pontosGanhos", usuarioDesafio.getPontosGanhos());
        resultado.put("pontuacao", usuarioDesafio.getPontuacao());
        resultado.put("totalPerguntas", usuarioDesafio.getTotalPerguntas());

        return ResponseEntity.ok(RespostaApi.sucesso(resultado, "Desafio concluído com sucesso"));
    }

    @GetMapping("/meus-desafios/completos")
    @Operation(summary = "Obter desafios concluídos do usuário")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<RespostaApi<List<UsuarioDesafio>>> obterCompletos() {
        List<UsuarioDesafio> desafios = servicoDesafio.obterCompletos();
        return ResponseEntity.ok(RespostaApi.sucesso(desafios, "Desafios concluídos recuperados com sucesso"));
    }

    @GetMapping("/{desafioId}/status")
    @Operation(summary = "Verificar status de um desafio")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<RespostaApi<Map<String, Object>>> obterStatus(@PathVariable Long desafioId) {
        Optional<UsuarioDesafio> usuarioDesafio = servicoDesafio.obterStatus(desafioId);

        Map<String, Object> status = new HashMap<>();
        if (usuarioDesafio.isPresent()) {
            UsuarioDesafio ud = usuarioDesafio.get();
            status.put("completo", true);
            status.put("concluidoEm", ud.getConcluidoEm());
            status.put("pontuacao", ud.getPontuacao());
            status.put("pontosGanhos", ud.getPontosGanhos());
        } else {
            status.put("completo", false);
        }

        return ResponseEntity.ok(RespostaApi.sucesso(status, "Status recuperado com sucesso"));
    }
}
