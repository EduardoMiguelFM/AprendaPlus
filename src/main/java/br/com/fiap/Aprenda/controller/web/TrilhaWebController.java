package br.com.fiap.Aprenda.controller.web;

import br.com.fiap.Aprenda.model.Trilha;
import br.com.fiap.Aprenda.service.TrilhaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

/**
 * Controller Web para Trilhas
 */
@Controller
@RequestMapping("/trilhas")
@RequiredArgsConstructor
public class TrilhaWebController {

    private final TrilhaService servicoTrilha;

    @GetMapping
    public String listar(
            @RequestParam(required = false) String area,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "12") int tamanho,
            Model model) {
        Pageable pageable = PageRequest.of(pagina, tamanho);
        Page<Trilha> trilhas = servicoTrilha.listarTodos(area, pageable);

        model.addAttribute("trilhas", trilhas);
        model.addAttribute("paginaAtual", pagina);
        model.addAttribute("totalPaginas", trilhas.getTotalPages());
        model.addAttribute("areaFiltro", area);

        return "trilhas/lista";
    }

    @GetMapping("/{id}")
    public String detalhes(@PathVariable Long id, Model model) {
        try {
            Trilha trilha = servicoTrilha.obterPorId(id);
            model.addAttribute("trilha", trilha);

            // Tentar obter progresso apenas se usuário estiver autenticado
            try {
                Map<String, Object> progresso = servicoTrilha.obterProgresso(id);
                model.addAttribute("progresso", progresso);
            } catch (Exception e) {
                // Usuário não autenticado ou erro ao obter progresso - não é crítico
                model.addAttribute("progresso", null);
            }

            return "trilhas/detalhes";
        } catch (Exception e) {
            return "redirect:/trilhas?erro=nao_encontrado";
        }
    }

    @PostMapping("/{id}/inscrever")
    public String inscrever(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            servicoTrilha.inscrever(id);
            redirectAttributes.addFlashAttribute("sucesso", "Inscrição realizada com sucesso!");
            return "redirect:/trilhas/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao se inscrever: " + e.getMessage());
            return "redirect:/trilhas/" + id;
        }
    }
}
