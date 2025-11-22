package br.com.fiap.Aprenda.controller.web;

import br.com.fiap.Aprenda.service.CursoService;
import br.com.fiap.Aprenda.service.GamificacaoService;
import br.com.fiap.Aprenda.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller Web para Dashboard do usuário
 */
@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardWebController {

    private final UsuarioService servicoUsuario;
    private final CursoService servicoCurso;
    private final GamificacaoService servicoGamificacao;

    @GetMapping
    public String dashboard(Model model) {
        try {
            var usuario = servicoUsuario.obterUsuarioAtual();
            Long usuarioId = usuario.getId();

            var stats = servicoGamificacao.obterEstatisticas(usuarioId);
            var cursosSugeridos = servicoCurso.obterSugeridos();
            var meusCursos = servicoCurso.obterCursosUsuario(null);

            model.addAttribute("usuario", usuario);
            model.addAttribute("stats", stats);
            model.addAttribute("cursosSugeridos", cursosSugeridos);
            model.addAttribute("cursosEmAndamento",
                    meusCursos.stream().filter(c -> "em_andamento".equals(c.getStatus())).toList());

            return "dashboard";
        } catch (Exception e) {
            return "redirect:/login";
        }
    }
}
