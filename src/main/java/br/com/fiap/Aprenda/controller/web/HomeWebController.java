package br.com.fiap.Aprenda.controller.web;

import br.com.fiap.Aprenda.service.CursoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller Web para páginas principais
 */
@Controller
@RequiredArgsConstructor
public class HomeWebController {

    private final CursoService servicoCurso;

    /**
     * Redireciona `/` para login se não autenticado, ou para dashboard se
     * autenticado
     */
    @GetMapping("/")
    public String root() {
        try {
            // Verificar se usuário está autenticado
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                // Se autenticado, redireciona para dashboard
                return "redirect:/dashboard";
            }
        } catch (Exception e) {
            // Em caso de erro, redireciona para login
        }

        // Se não autenticado, redireciona para login
        return "redirect:/login";
    }

    /**
     * Home/Dashboard do usuário autenticado (/home)
     * Requer autenticação - redireciona para login se não autenticado
     */
    @GetMapping("/home")
    public String home(Model model) {
        try {
            // Verificar se usuário está autenticado
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
                // Se não autenticado, redireciona para login
                return "redirect:/login";
            }

            // Se autenticado, redireciona para dashboard (que tem o mesmo conteúdo)
            return "redirect:/dashboard";
        } catch (Exception e) {
            // Em caso de erro, redireciona para login
            return "redirect:/login";
        }
    }

    @GetMapping("/sobre")
    public String sobre() {
        return "sobre";
    }
}
