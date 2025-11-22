package br.com.fiap.Aprenda.controller.api;

import br.com.fiap.Aprenda.dto.*;
import br.com.fiap.Aprenda.model.TransacaoPontos;
import br.com.fiap.Aprenda.service.GamificacaoService;
import br.com.fiap.Aprenda.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gerenciamento de usuários
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Usuários", description = "Endpoints para gerenciamento de usuários")
public class UsuarioController {

    private final UsuarioService servicoUsuario;
    private final GamificacaoService servicoGamificacao;

    @GetMapping("/me")
    @Operation(summary = "Obter usuário autenticado")
    public ResponseEntity<RespostaApi<UsuarioDTO>> obterUsuarioAtual() {
        UsuarioDTO usuario = servicoUsuario.obterUsuarioAtual();
        return ResponseEntity.ok(RespostaApi.sucesso(usuario, "Usuário recuperado com sucesso"));
    }

    @PutMapping("/perfil")
    @Operation(summary = "Atualizar perfil")
    public ResponseEntity<RespostaApi<UsuarioDTO>> atualizarPerfil(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String senha) {
        UsuarioDTO usuario = servicoUsuario.atualizarPerfil(nome, email, senha);
        return ResponseEntity.ok(RespostaApi.sucesso(usuario, "Perfil atualizado com sucesso"));
    }

    @DeleteMapping("/perfil")
    @Operation(summary = "Deletar conta")
    public ResponseEntity<RespostaApi<String>> deletarPerfil() {
        servicoUsuario.deletarPerfil();
        return ResponseEntity.ok(RespostaApi.sucesso("Conta deletada com sucesso"));
    }

    @GetMapping("/preferencias")
    @Operation(summary = "Obter preferências do usuário")
    public ResponseEntity<RespostaApi<RequisicaoPreferencias>> obterPreferencias() {
        RequisicaoPreferencias preferencias = servicoUsuario.obterPreferencias();
        return ResponseEntity.ok(RespostaApi.sucesso(preferencias, "Preferências recuperadas com sucesso"));
    }

    @PostMapping("/preferencias")
    @Operation(summary = "Salvar preferências do usuário")
    public ResponseEntity<RespostaApi<RequisicaoPreferencias>> salvarPreferencias(
            @Valid @RequestBody RequisicaoPreferencias requisicao) {
        RequisicaoPreferencias preferencias = servicoUsuario.salvarPreferencias(requisicao);
        return ResponseEntity.ok(RespostaApi.sucesso(preferencias, "Preferências salvas com sucesso"));
    }

    @GetMapping("/estatisticas")
    @Operation(summary = "Obter estatísticas do usuário")
    public ResponseEntity<RespostaApi<GamificacaoService.EstatisticasDTO>> obterEstatisticas() {
        Long usuarioId = servicoUsuario.obterUsuarioAtual().getId();
        GamificacaoService.EstatisticasDTO stats = servicoGamificacao.obterEstatisticas(usuarioId);
        return ResponseEntity.ok(RespostaApi.sucesso(stats, "Estatísticas recuperadas com sucesso"));
    }

    @GetMapping("/pontos")
    @Operation(summary = "Obter total de pontos")
    public ResponseEntity<RespostaApi<Integer>> obterPontos() {
        Long usuarioId = servicoUsuario.obterUsuarioAtual().getId();
        Integer pontos = servicoGamificacao.obterPontosTotais(usuarioId);
        return ResponseEntity.ok(RespostaApi.sucesso(pontos, "Pontos recuperados com sucesso"));
    }

    @GetMapping("/pontos/historico")
    @Operation(summary = "Histórico de pontos")
    public ResponseEntity<RespostaApi<RespostaPaginada<TransacaoPontos>>> obterHistoricoPontos(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho) {
        Long usuarioId = servicoUsuario.obterUsuarioAtual().getId();
        Pageable pageable = PageRequest.of(pagina, tamanho);
        List<TransacaoPontos> historico = servicoGamificacao.obterHistoricoPontos(usuarioId, pageable);

        RespostaPaginada.InformacaoPaginacao paginacao = RespostaPaginada.InformacaoPaginacao.builder()
                .pagina(pagina)
                .tamanho(tamanho)
                .total((long) historico.size())
                .totalPaginas((int) Math.ceil((double) historico.size() / tamanho))
                .build();

        RespostaPaginada<TransacaoPontos> resposta = RespostaPaginada.<TransacaoPontos>builder()
                .sucesso(true)
                .dados(historico)
                .paginacao(paginacao)
                .mensagem("Histórico recuperado com sucesso")
                .build();

        return ResponseEntity.ok(RespostaApi.sucesso(resposta, null));
    }

    @GetMapping("/trofeus")
    @Operation(summary = "Obter troféus do usuário")
    public ResponseEntity<RespostaApi<Object>> obterTrofeus() {
        Long usuarioId = servicoUsuario.obterUsuarioAtual().getId();
        var trofeus = servicoGamificacao.obterTrofeusUsuario(usuarioId);
        return ResponseEntity.ok(RespostaApi.sucesso(trofeus, "Troféus recuperados com sucesso"));
    }
}
