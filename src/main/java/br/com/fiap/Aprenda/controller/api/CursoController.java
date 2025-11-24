package br.com.fiap.Aprenda.controller.api;

import br.com.fiap.Aprenda.dto.*;
import br.com.fiap.Aprenda.model.Curso;
import br.com.fiap.Aprenda.model.UsuarioCurso;
import br.com.fiap.Aprenda.service.CursoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller REST para gerenciamento de cursos
 */
@RestController
@RequestMapping("/api/cursos")
@RequiredArgsConstructor
@Tag(name = "Cursos", description = "Endpoints para gerenciamento de cursos")
public class CursoController {

    private final CursoService servicoCurso;

    @GetMapping
    @Operation(summary = "Listar todos os cursos")
    public ResponseEntity<RespostaApi<RespostaPaginada<Curso>>> listarTodos(
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String nivel,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho) {
        Pageable pageable = PageRequest.of(pagina, tamanho);
        Page<Curso> cursosPage = servicoCurso.listarTodos(area, nivel, pageable);

        RespostaPaginada.InformacaoPaginacao paginacao = RespostaPaginada.InformacaoPaginacao.builder()
                .pagina(pagina)
                .tamanho(tamanho)
                .total(cursosPage.getTotalElements())
                .totalPaginas(cursosPage.getTotalPages())
                .build();

        RespostaPaginada<Curso> resposta = RespostaPaginada.<Curso>builder()
                .sucesso(true)
                .dados(cursosPage.getContent())
                .paginacao(paginacao)
                .mensagem("Cursos recuperados com sucesso")
                .build();

        return ResponseEntity.ok(RespostaApi.sucesso(resposta, null));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter detalhes de um curso")
    public ResponseEntity<RespostaApi<Curso>> obterPorId(@PathVariable Long id) {
        Curso curso = servicoCurso.obterPorId(id);
        return ResponseEntity.ok(RespostaApi.sucesso(curso, "Curso recuperado com sucesso"));
    }

    @GetMapping("/sugeridos")
    @Operation(summary = "Obter cursos sugeridos para o usuário")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<RespostaApi<List<Curso>>> obterSugeridos() {
        List<Curso> cursos = servicoCurso.obterSugeridos();
        return ResponseEntity.ok(RespostaApi.sucesso(cursos, "Cursos sugeridos recuperados com sucesso"));
    }

    @GetMapping("/por-area/{area}")
    @Operation(summary = "Obter cursos de uma área específica")
    public ResponseEntity<RespostaApi<List<Curso>>> obterPorArea(
            @PathVariable String area,
            @RequestParam(required = false) String nivel,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho) {
        Pageable pageable = PageRequest.of(pagina, tamanho);
        List<Curso> cursos = servicoCurso.obterPorArea(area, nivel, pageable);
        return ResponseEntity.ok(RespostaApi.sucesso(cursos, "Cursos recuperados com sucesso"));
    }

    @PostMapping("/{cursoId}/inscrever")
    @Operation(summary = "Inscrever-se em um curso")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<RespostaApi<UsuarioCurso>> inscrever(@PathVariable Long cursoId) {
        UsuarioCurso inscricao = servicoCurso.inscrever(cursoId);
        return ResponseEntity.ok(RespostaApi.sucesso(inscricao, "Inscrição realizada com sucesso"));
    }

    @GetMapping("/meus-cursos")
    @Operation(summary = "Obter cursos do usuário")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<RespostaApi<Map<String, List<UsuarioCurso>>>> obterMeusCursos(
            @RequestParam(required = false) String status) {
        List<UsuarioCurso> todosCursos = servicoCurso.obterCursosUsuario(status);

        Map<String, List<UsuarioCurso>> resultado = new HashMap<>();
        resultado.put("cursosEmAndamento", todosCursos.stream()
                .filter(c -> "em_andamento".equals(c.getStatus()))
                .toList());
        resultado.put("cursosConcluidos", todosCursos.stream()
                .filter(c -> "concluido".equals(c.getStatus()))
                .toList());

        return ResponseEntity.ok(RespostaApi.sucesso(resultado, "Cursos do usuário recuperados com sucesso"));
    }

    @PutMapping("/{cursoId}/progresso")
    @Operation(summary = "Atualizar progresso do curso")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<RespostaApi<UsuarioCurso>> atualizarProgresso(
            @PathVariable Long cursoId,
            @RequestParam Integer progresso) {
        UsuarioCurso usuarioCurso = servicoCurso.atualizarProgresso(cursoId, progresso);
        return ResponseEntity.ok(RespostaApi.sucesso(usuarioCurso, "Progresso atualizado com sucesso"));
    }

    @GetMapping("/{cursoId}/progresso")
    @Operation(summary = "Obter progresso do curso")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<RespostaApi<UsuarioCurso>> obterProgresso(@PathVariable Long cursoId) {
        UsuarioCurso usuarioCurso = servicoCurso.obterProgresso(cursoId);
        return ResponseEntity.ok(RespostaApi.sucesso(usuarioCurso, "Progresso recuperado com sucesso"));
    }

    @PostMapping
    @Operation(summary = "Criar novo curso")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<RespostaApi<Curso>> criar(@Valid @RequestBody Curso curso) {
        Curso cursoCriado = servicoCurso.criar(curso);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RespostaApi.sucesso(cursoCriado, "Curso criado com sucesso"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar curso")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<RespostaApi<Curso>> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Curso curso) {
        Curso cursoAtualizado = servicoCurso.atualizar(id, curso);
        return ResponseEntity.ok(RespostaApi.sucesso(cursoAtualizado, "Curso atualizado com sucesso"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar curso")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<RespostaApi<Void>> deletar(@PathVariable Long id) {
        servicoCurso.deletar(id);
        return ResponseEntity.ok(RespostaApi.sucesso(null, "Curso deletado com sucesso"));
    }
}
