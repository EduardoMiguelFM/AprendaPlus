package br.com.fiap.Aprenda.controller.web;

import br.com.fiap.Aprenda.dto.*;
import br.com.fiap.Aprenda.service.AutenticacaoService;
import br.com.fiap.Aprenda.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller Web para autenticação (login, cadastro, onboarding)
 */
@Controller
@RequiredArgsConstructor
public class AutenticacaoWebController {

    private final AutenticacaoService servicoAutenticacao;
    private final UsuarioService servicoUsuario;

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error, Model model) {
        // Se já estiver autenticado, verificar onboarding
        if (SecurityContextHolder.getContext().getAuthentication() != null
                && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                && !"anonymousUser".equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
            try {
                var usuario = servicoUsuario.obterUsuarioAtual();

                if (usuario != null && !usuario.getOnboardingConcluido()) {
                    return "redirect:/onboarding/areas-interesse";
                }
                return "redirect:/dashboard";
            } catch (Exception e) {
                // Se houver erro, mostrar página de login
            }
        }

        // Tratar mensagens de erro da URL
        if (error != null) {
            switch (error) {
                case "sessao_expirada":
                    model.addAttribute("erro", "Sua sessão expirou. Por favor, faça login novamente.");
                    break;
                case "acesso_negado":
                    model.addAttribute("erro",
                            "Acesso negado. Você precisa estar autenticado para acessar esta página.");
                    break;
                case "true":
                    // Erro de login (credenciais inválidas)
                    model.addAttribute("erro", "Email ou senha incorretos. Por favor, tente novamente.");
                    break;
                default:
                    model.addAttribute("erro", "Erro ao acessar a página. Por favor, faça login novamente.");
            }
        }

        if (!model.containsAttribute("requisicao")) {
            model.addAttribute("requisicao", new RequisicaoLogin());
        }
        return "login";
    }

    // Login é processado automaticamente pelo Spring Security via formLogin()
    // Não precisa de @PostMapping("/login") - Spring Security gerencia isso

    @GetMapping("/cadastro")
    public String cadastroPage(Model model) {
        if (!model.containsAttribute("requisicao")) {
            model.addAttribute("requisicao", new RequisicaoRegistro());
        }
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastro(@Valid @ModelAttribute("requisicao") RequisicaoRegistro requisicao,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("erro", "Por favor, preencha todos os campos corretamente");
            redirectAttributes.addFlashAttribute("requisicao", requisicao);
            return "redirect:/cadastro";
        }

        // Validar se senhas coincidem
        if (!requisicao.getSenha().equals(requisicao.getConfirmarSenha())) {
            redirectAttributes.addFlashAttribute("erro", "As senhas não coincidem");
            redirectAttributes.addFlashAttribute("requisicao", requisicao);
            return "redirect:/cadastro";
        }

        try {
            servicoAutenticacao.registrar(requisicao);
            redirectAttributes.addFlashAttribute("sucesso",
                    "Cadastro realizado com sucesso! Faça login para continuar.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro",
                    e.getMessage() != null ? e.getMessage() : "Erro ao realizar cadastro");
            redirectAttributes.addFlashAttribute("requisicao", requisicao);
            return "redirect:/cadastro";
        }
    }

    // Logout é gerenciado completamente pelo Spring Security
    // Não precisamos de métodos aqui - o SecurityConfig cuida disso
}
