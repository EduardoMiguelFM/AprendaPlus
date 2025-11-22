package br.com.fiap.Aprenda.controller.api;

import br.com.fiap.Aprenda.dto.*;
import br.com.fiap.Aprenda.model.Curso;
import br.com.fiap.Aprenda.model.Desafio;
import br.com.fiap.Aprenda.model.UsuarioCurso;
import br.com.fiap.Aprenda.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller REST para dashboard do usuário
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Dashboard", description = "Endpoint para dashboard do usuário")
public class DashboardController {

    private final UsuarioService servicoUsuario;
    private final CursoService servicoCurso;
    private final DesafioService servicoDesafio;
    private final GamificacaoService servicoGamificacao;

    @GetMapping
    @Operation(summary = "Obter dados do dashboard do usuário")
    public ResponseEntity<RespostaApi<Map<String, Object>>> obterDashboard() {
        Long usuarioId = servicoUsuario.obterUsuarioAtual().getId();

        UsuarioDTO usuario = servicoUsuario.obterUsuarioAtual();
        GamificacaoService.EstatisticasDTO stats = servicoGamificacao.obterEstatisticas(usuarioId);
        List<Curso> cursosSugeridos = servicoCurso.obterSugeridos();
        List<UsuarioCurso> cursosEmAndamento = servicoCurso.obterCursosUsuario("em_andamento");

        Pageable pageable = PageRequest.of(0, 5);
        List<Desafio> desafiosDisponiveis = servicoDesafio.listarTodos(null, null, null, pageable)
                .getContent().stream().limit(5).toList();

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("usuario", usuario);
        dashboard.put("estatisticas", stats);
        dashboard.put("proximoCurso", cursosEmAndamento.isEmpty() ? null : cursosEmAndamento.get(0).getCurso());
        dashboard.put("cursosSugeridos", cursosSugeridos);
        dashboard.put("desafiosDisponiveis", desafiosDisponiveis);

        return ResponseEntity.ok(RespostaApi.sucesso(dashboard, "Dashboard recuperado com sucesso"));
    }
}
