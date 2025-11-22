package br.com.fiap.Aprenda.controller.web;

import br.com.fiap.Aprenda.dto.AreaInteresseDTO;
import br.com.fiap.Aprenda.dto.AulaDTO;
import br.com.fiap.Aprenda.dto.RequisicaoPreferencias;
import br.com.fiap.Aprenda.model.Curso;
import br.com.fiap.Aprenda.model.Usuario;
import br.com.fiap.Aprenda.model.UsuarioCurso;
import br.com.fiap.Aprenda.repository.CursoRepository;
import br.com.fiap.Aprenda.repository.UsuarioRepository;
import br.com.fiap.Aprenda.service.CursoService;
import br.com.fiap.Aprenda.service.CursoService.CursoConclusaoResultado;
import br.com.fiap.Aprenda.service.IAService;
import br.com.fiap.Aprenda.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * Controller Web para páginas de Cursos com Thymeleaf
 * Segue padrão similar ao MotoVision
 */
@Controller
@RequestMapping("/cursos")
@RequiredArgsConstructor
public class CursoWebController {

    private final CursoService servicoCurso;
    private final CursoRepository cursoRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService servicoUsuario;
    private final ObjectProvider<IAService> iaServiceProvider;

    @GetMapping
    public String listar(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "12") int tamanho,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String nivel,
            Model model) {

        Pageable pageable = PageRequest.of(pagina, tamanho);
        Page<Curso> cursos;

        // Verificar se os filtros não estão vazios
        boolean temArea = area != null && !area.trim().isEmpty();
        boolean temNivel = nivel != null && !nivel.trim().isEmpty();

        if (temArea && temNivel) {
            cursos = cursoRepository.findByAreaAndNivel(area, nivel, pageable);
        } else if (temArea) {
            cursos = cursoRepository.findByArea(area, pageable);
        } else if (temNivel) {
            cursos = cursoRepository.findByNivel(nivel, pageable);
        } else {
            cursos = cursoRepository.findAll(pageable);
        }

        model.addAttribute("cursos", cursos);
        model.addAttribute("paginaAtual", pagina);
        model.addAttribute("totalPaginas", cursos.getTotalPages());
        model.addAttribute("areaFiltro", area);
        model.addAttribute("nivelFiltro", nivel);

        return "cursos/lista";
    }

    @GetMapping("/{id}")
    public String detalhes(@PathVariable Long id, Model model) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso não encontrado"));

        int pontosCurso = servicoCurso.estimarPontosTotais(curso);
        List<AulaDTO> aulas = servicoCurso.gerarPlanoAulas(curso);

        // Verificar se usuário está inscrito
        boolean inscrito = false;
        UsuarioCurso usuarioCurso = null;
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(auth.getName());
                if (usuarioOpt.isPresent()) {
                    try {
                        usuarioCurso = servicoCurso.obterProgresso(id);
                        inscrito = true;
                    } catch (Exception ex) {
                        // Não está inscrito
                        inscrito = false;
                    }
                }
            }
        } catch (Exception e) {
            // Usuário não autenticado ou não inscrito
        }

        if (usuarioCurso != null) {
            int totalAulas = curso.getTotalAulas() != null ? curso.getTotalAulas() : aulas.size();
            for (AulaDTO aula : aulas) {
                int percentualAula = (int) Math.round((aula.getNumero() / (double) totalAulas) * 100);
                aula.setConcluida(usuarioCurso.getProgresso() >= percentualAula);
            }
        }

        model.addAttribute("curso", curso);
        model.addAttribute("inscrito", inscrito);
        model.addAttribute("usuarioCurso", usuarioCurso);
        model.addAttribute("aulas", aulas);
        model.addAttribute("pontosCurso", pontosCurso);

        return "cursos/detalhes";
    }

    @GetMapping("/sugeridos")
    public String sugeridos(Model model) {
        var cursosSugeridos = servicoCurso.obterSugeridos();
        var usuario = servicoUsuario.obterUsuarioAtual();

        String mensagemIA;
        IAService servicoIA = iaServiceProvider.getIfAvailable();
        if (servicoIA != null) {
            try {
                RequisicaoPreferencias preferencias = servicoUsuario.obterPreferencias();
                var areas = preferencias.getAreasInteresse().stream()
                        .map(AreaInteresseDTO::getArea)
                        .toList();
                var niveis = preferencias.getAreasInteresse().stream()
                        .map(AreaInteresseDTO::getNivel)
                        .toList();

                mensagemIA = servicoIA.gerarRecomendacoesCursos(areas, niveis, usuario.getPontosTotais());
            } catch (Exception e) {
                mensagemIA = "Não consegui identificar suas preferências. Atualize suas áreas de interesse para recomendações personalizadas.";
            }
        } else {
            mensagemIA = "A IA está temporariamente indisponível. Explore os cursos sugeridos manualmente por enquanto.";
        }

        model.addAttribute("cursos", cursosSugeridos);
        model.addAttribute("mensagemIA", mensagemIA);
        model.addAttribute("usuarioNome", usuario.getNome());
        return "cursos/sugeridos";
    }

    @PostMapping("/{id}/inscrever")
    public String inscrever(@PathVariable Long id) {
        try {
            servicoCurso.inscrever(id);
            return "redirect:/cursos/" + id + "?sucesso=inscrito";
        } catch (Exception e) {
            return "redirect:/cursos/" + id + "?erro=inscricao";
        }
    }

    @PostMapping("/{id}/aulas/{numero}/concluir")
    public String concluirAula(@PathVariable Long id,
            @PathVariable Integer numero,
            RedirectAttributes redirectAttributes) {
        try {
            CursoConclusaoResultado resultado = servicoCurso.concluirAula(id, numero);
            if (resultado.cursoConcluido()) {
                redirectAttributes.addFlashAttribute("mensagem",
                        "Curso concluído! Você ganhou +" + resultado.pontosGanhos() + " pontos.");
            } else {
                redirectAttributes.addFlashAttribute("mensagem", "Aula concluída! Pontuação atualizada.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/cursos/" + id;
    }

    @GetMapping("/meus-cursos")
    public String meusCursos(
            @RequestParam(required = false) String status,
            Model model) {

        var meusCursos = servicoCurso.obterCursosUsuario(status);

        model.addAttribute("cursosEmAndamento",
                meusCursos.stream().filter(c -> "em_andamento".equals(c.getStatus())).toList());
        model.addAttribute("cursosConcluidos",
                meusCursos.stream().filter(c -> "concluido".equals(c.getStatus())).toList());

        return "cursos/meus-cursos";
    }
}
