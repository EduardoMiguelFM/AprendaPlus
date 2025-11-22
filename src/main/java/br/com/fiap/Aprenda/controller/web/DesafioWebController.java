package br.com.fiap.Aprenda.controller.web;

import br.com.fiap.Aprenda.dto.RequisicaoCompletarDesafio;
import br.com.fiap.Aprenda.model.Desafio;
import br.com.fiap.Aprenda.model.PerguntaDesafio;
import br.com.fiap.Aprenda.service.DesafioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controller Web para Desafios
 */
@Controller
@RequestMapping("/desafios")
@RequiredArgsConstructor
public class DesafioWebController {

    private final DesafioService servicoDesafio;

    @GetMapping
    public String listar(
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String nivel,
            @RequestParam(required = false) String tipo,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "12") int tamanho,
            Model model) {
        Pageable pageable = PageRequest.of(pagina, tamanho);

        // Verificar se os filtros não estão vazios
        String areaFiltro = (area != null && !area.trim().isEmpty()) ? area : null;
        String nivelFiltro = (nivel != null && !nivel.trim().isEmpty()) ? nivel : null;
        String tipoFiltro = (tipo != null && !tipo.trim().isEmpty()) ? tipo : null;

        Page<Desafio> desafios = servicoDesafio.listarTodos(areaFiltro, nivelFiltro, tipoFiltro, pageable);
        Set<Long> desafiosConcluidos = servicoDesafio.obterCompletos()
                .stream()
                .map(usuarioDesafio -> usuarioDesafio.getDesafio().getId())
                .collect(Collectors.toSet());

        model.addAttribute("desafios", desafios);
        model.addAttribute("paginaAtual", pagina);
        model.addAttribute("totalPaginas", desafios.getTotalPages());
        model.addAttribute("areaFiltro", areaFiltro);
        model.addAttribute("nivelFiltro", nivelFiltro);
        model.addAttribute("tipoFiltro", tipoFiltro);
        model.addAttribute("desafiosConcluidos", desafiosConcluidos);

        return "desafios/lista";
    }

    @GetMapping("/{id}")
    public String detalhes(@PathVariable Long id, Model model) {
        try {
            Desafio desafio = servicoDesafio.obterPorId(id);
            List<PerguntaDesafio> perguntas = servicoDesafio.obterPerguntas(id);

            model.addAttribute("desafio", desafio);
            model.addAttribute("perguntas", perguntas);

            return "desafios/detalhes";
        } catch (Exception e) {
            return "redirect:/desafios?erro=nao_encontrado";
        }
    }

    @GetMapping("/{id}/quiz")
    public String quiz(@PathVariable Long id, Model model) {
        try {
            Desafio desafio = servicoDesafio.obterPorId(id);
            List<PerguntaDesafio> perguntas = servicoDesafio.obterPerguntas(id);

            if (perguntas == null || perguntas.isEmpty()) {
                model.addAttribute("erro", "Este desafio não possui perguntas disponíveis.");
                return "desafios/detalhes";
            }

            model.addAttribute("desafio", desafio);
            model.addAttribute("perguntas", perguntas);

            return "desafios/quiz";
        } catch (Exception e) {
            return "redirect:/desafios?erro=nao_encontrado";
        }
    }

    @PostMapping("/{id}/completar")
    public String completar(@PathVariable Long id,
            @RequestParam Integer pontuacao,
            @RequestParam Integer totalPerguntas,
            @RequestParam(required = false) Integer tempoGasto,
            RedirectAttributes redirectAttributes) {
        try {
            RequisicaoCompletarDesafio requisicao = RequisicaoCompletarDesafio.builder()
                    .pontuacao(pontuacao)
                    .totalPerguntas(totalPerguntas)
                    .tempoGasto(tempoGasto != null ? tempoGasto : 0)
                    .build();

            var resultado = servicoDesafio.completar(id, requisicao);
            redirectAttributes.addFlashAttribute("resultado", resultado);
            redirectAttributes.addFlashAttribute("sucesso", "Desafio completado com sucesso!");

            return "redirect:/desafios/" + id + "/resultado";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao completar desafio: " + e.getMessage());
            return "redirect:/desafios/" + id;
        }
    }

    @GetMapping("/{id}/resultado")
    public String resultado(@PathVariable Long id, Model model) {
        try {
            Desafio desafio = servicoDesafio.obterPorId(id);
            model.addAttribute("desafio", desafio);

            // Buscar troféus ganhos recentemente (últimos 5 minutos)
            // O troféu "Perfeito" é concedido automaticamente no service se 100% de acerto
            // Aqui apenas verificamos se foi ganho
            var status = servicoDesafio.obterStatus(id);
            if (status.isPresent()) {
                var usuarioDesafio = status.get();
                if (usuarioDesafio.getPontuacao() != null &&
                        usuarioDesafio.getTotalPerguntas() != null &&
                        usuarioDesafio.getTotalPerguntas() > 0) {
                    double percentual = (double) usuarioDesafio.getPontuacao() / usuarioDesafio.getTotalPerguntas()
                            * 100;
                    if (percentual >= 100) {
                        model.addAttribute("trofeuPerfeito", true);
                    }
                }
            }

            return "desafios/resultado";
        } catch (Exception e) {
            return "redirect:/desafios?erro=nao_encontrado";
        }
    }
}
