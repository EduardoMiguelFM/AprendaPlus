package br.com.fiap.Aprenda.controller.api;

import br.com.fiap.Aprenda.dto.*;
import br.com.fiap.Aprenda.model.Trilha;
import br.com.fiap.Aprenda.model.UsuarioTrilha;
import br.com.fiap.Aprenda.service.TrilhaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller REST para gerenciamento de trilhas
 */
@RestController
@RequestMapping("/api/trilhas")
@RequiredArgsConstructor
@Tag(name = "Trilhas", description = "Endpoints para gerenciamento de trilhas de aprendizado")
public class TrilhaController {

    private final TrilhaService servicoTrilha;

    @GetMapping
    @Operation(summary = "Listar todas as trilhas")
    public ResponseEntity<RespostaApi<RespostaPaginada<Trilha>>> listarTodos(
            @RequestParam(required = false) String area,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho) {
        Pageable pageable = PageRequest.of(pagina, tamanho);
        Page<Trilha> trilhasPage = servicoTrilha.listarTodos(area, pageable);

        RespostaPaginada.InformacaoPaginacao paginacao = RespostaPaginada.InformacaoPaginacao.builder()
                .pagina(pagina)
                .tamanho(tamanho)
                .total(trilhasPage.getTotalElements())
                .totalPaginas(trilhasPage.getTotalPages())
                .build();

        RespostaPaginada<Trilha> resposta = RespostaPaginada.<Trilha>builder()
                .sucesso(true)
                .dados(trilhasPage.getContent())
                .paginacao(paginacao)
                .mensagem("Trilhas recuperadas com sucesso")
                .build();

        return ResponseEntity.ok(RespostaApi.sucesso(resposta, null));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter detalhes de uma trilha")
    public ResponseEntity<RespostaApi<Trilha>> obterPorId(@PathVariable Long id) {
        Trilha trilha = servicoTrilha.obterPorId(id);
        return ResponseEntity.ok(RespostaApi.sucesso(trilha, "Trilha recuperada com sucesso"));
    }

    @GetMapping("/minhas-trilhas")
    @Operation(summary = "Obter trilhas do usuário")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<RespostaApi<List<UsuarioTrilha>>> obterMinhasTrilhas() {
        List<UsuarioTrilha> trilhas = servicoTrilha.obterTrilhasUsuario();
        return ResponseEntity.ok(RespostaApi.sucesso(trilhas, "Trilhas do usuário recuperadas com sucesso"));
    }

    @PostMapping("/{trilhaId}/inscrever")
    @Operation(summary = "Inscrever-se em uma trilha")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<RespostaApi<UsuarioTrilha>> inscrever(@PathVariable Long trilhaId) {
        UsuarioTrilha usuarioTrilha = servicoTrilha.inscrever(trilhaId);
        return ResponseEntity.ok(RespostaApi.sucesso(usuarioTrilha, "Inscrição na trilha realizada com sucesso"));
    }

    @GetMapping("/{trilhaId}/progresso")
    @Operation(summary = "Obter progresso em uma trilha")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<RespostaApi<Map<String, Object>>> obterProgresso(@PathVariable Long trilhaId) {
        Map<String, Object> progresso = servicoTrilha.obterProgresso(trilhaId);
        return ResponseEntity.ok(RespostaApi.sucesso(progresso, "Progresso recuperado com sucesso"));
    }
}
