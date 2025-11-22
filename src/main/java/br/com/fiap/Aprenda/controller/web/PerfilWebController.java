package br.com.fiap.Aprenda.controller.web;

import br.com.fiap.Aprenda.dto.AreaInteresseDTO;
import br.com.fiap.Aprenda.dto.RequisicaoPreferencias;
import br.com.fiap.Aprenda.service.CursoService;
import br.com.fiap.Aprenda.service.DesafioService;
import br.com.fiap.Aprenda.service.GamificacaoService;
import br.com.fiap.Aprenda.service.TrilhaService;
import br.com.fiap.Aprenda.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller Web para Perfil do Usuário
 */
@Controller
@RequestMapping("/perfil")
@RequiredArgsConstructor
public class PerfilWebController {

    private final UsuarioService servicoUsuario;
    private final GamificacaoService servicoGamificacao;
    private final CursoService servicoCurso;
    private final TrilhaService servicoTrilha;
    private final DesafioService servicoDesafio;

    @GetMapping
    public String perfil(Model model) {
        try {
            var usuario = servicoUsuario.obterUsuarioAtual();
            var preferencias = servicoUsuario.obterPreferencias();
            var stats = servicoGamificacao.obterEstatisticas(usuario.getId());
            var trofeus = servicoGamificacao.obterTrofeusUsuario(usuario.getId());
            var cursosAtivos = servicoCurso.obterCursosUsuario("em_andamento");
            var cursosConcluidos = servicoCurso.obterCursosUsuario("concluido");
            var trilhasUsuario = servicoTrilha.obterTrilhasUsuario();
            var desafiosConcluidos = servicoDesafio.obterCompletos();
            Map<String, String> areasSelecionadas = preferencias != null && preferencias.getAreasInteresse() != null
                    ? preferencias.getAreasInteresse().stream()
                            .collect(Collectors.toMap(AreaInteresseDTO::getArea,
                                    AreaInteresseDTO::getNivel,
                                    (valorExistente, novoValor) -> valorExistente,
                                    LinkedHashMap::new))
                    : Map.of();

            // Mapa de áreas para o template
            Map<String, String> todasAreas = Map.ofEntries(
                    Map.entry("ia", "Inteligência Artificial"),
                    Map.entry("dados", "Ciência de Dados"),
                    Map.entry("programacao", "Programação"),
                    Map.entry("sustentabilidade", "Sustentabilidade"),
                    Map.entry("design", "Design"),
                    Map.entry("marketing", "Marketing Digital"),
                    Map.entry("gestao", "Gestão"),
                    Map.entry("vendas", "Vendas"),
                    Map.entry("rh", "Recursos Humanos"),
                    Map.entry("financas", "Finanças"),
                    Map.entry("saude", "Saúde"),
                    Map.entry("educacao", "Educação"));

            model.addAttribute("usuario", usuario);
            model.addAttribute("preferencias", preferencias);
            model.addAttribute("stats", stats);
            model.addAttribute("trofeus", trofeus);
            model.addAttribute("cursosAtivos", cursosAtivos);
            model.addAttribute("cursosConcluidos", cursosConcluidos);
            model.addAttribute("trilhasUsuario", trilhasUsuario);
            model.addAttribute("desafiosConcluidos", desafiosConcluidos);
            model.addAttribute("todasAreas", todasAreas);
            model.addAttribute("areasSelecionadas", areasSelecionadas);

            return "perfil/index";
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

    @PostMapping("/atualizar")
    public String atualizarPerfil(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String senha,
            RedirectAttributes redirectAttributes) {
        try {
            servicoUsuario.atualizarPerfil(nome, email, senha);
            redirectAttributes.addFlashAttribute("sucesso", "Perfil atualizado com sucesso!");
            return "redirect:/perfil";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao atualizar perfil: " + e.getMessage());
            return "redirect:/perfil";
        }
    }

    @PostMapping("/preferencias")
    public String atualizarPreferencias(HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        try {
            String[] areasSelecionadas = request.getParameterValues("areasSelecionadas");
            if (areasSelecionadas == null || areasSelecionadas.length == 0) {
                redirectAttributes.addFlashAttribute("erro", "Selecione pelo menos uma área de interesse.");
                return "redirect:/perfil";
            }

            if (areasSelecionadas.length > 3) {
                redirectAttributes.addFlashAttribute("erro", "Selecione no máximo 3 áreas de interesse.");
                return "redirect:/perfil";
            }

            List<AreaInteresseDTO> areas = new ArrayList<>();
            for (String area : areasSelecionadas) {
                String nivel = request.getParameter("nivel_" + area);
                if (nivel == null || nivel.isBlank()) {
                    nivel = "Iniciante";
                }
                areas.add(AreaInteresseDTO.builder()
                        .area(area)
                        .nivel(nivel)
                        .build());
            }

            RequisicaoPreferencias requisicao = RequisicaoPreferencias.builder()
                    .areasInteresse(areas)
                    .onboardingConcluido(true)
                    .build();

            servicoUsuario.salvarPreferencias(requisicao);
            redirectAttributes.addFlashAttribute("sucesso", "Preferências atualizadas com sucesso!");
            return "redirect:/perfil";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao atualizar preferências: " + e.getMessage());
            return "redirect:/perfil";
        }
    }
}
