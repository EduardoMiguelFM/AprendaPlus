package br.com.fiap.Aprenda.config;

import br.com.fiap.Aprenda.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor para verificar se o usuário completou o onboarding
 * Redireciona para onboarding se necessário antes de acessar páginas protegidas
 */
@Component
@RequiredArgsConstructor
public class OnboardingInterceptor implements HandlerInterceptor {

    private final UsuarioService servicoUsuario;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // Verificar se é uma requisição para página web (não API)
        String path = request.getRequestURI();

        // Não interceptar endpoints de API, login, cadastro, onboarding ou recursos
        // estáticos
        if (path.startsWith("/api/") ||
                path.startsWith("/login") ||
                path.startsWith("/cadastro") ||
                path.startsWith("/onboarding") ||
                path.startsWith("/logout") ||
                path.equals("/") ||
                path.equals("/sobre") ||
                path.startsWith("/public/") ||
                path.startsWith("/css/") ||
                path.startsWith("/js/") ||
                path.startsWith("/images/") ||
                path.startsWith("/webjars/") ||
                path.startsWith("/swagger") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/actuator") ||
                path.startsWith("/error") ||
                path.equals("/favicon.ico") ||
                path.endsWith(".ico")) {
            return true;
        }

        // Verificar se usuário está autenticado
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getName())) {
            // Não autenticado - permitir acesso (SecurityConfig cuida disso)
            return true;
        }

        try {
            // Verificar se onboarding foi concluído
            var usuario = servicoUsuario.obterUsuarioAtual();
            if (usuario != null && !usuario.getOnboardingConcluido()) {
                // Onboarding não concluído - redirecionar
                response.sendRedirect("/onboarding/areas-interesse");
                return false;
            }
        } catch (Exception e) {
            // Se houver erro ao obter usuário (usuário não encontrado, etc)
            // Invalidar sessão e redirecionar para login
            request.getSession().invalidate();
            response.sendRedirect("/login?error=sessao_expirada");
            return false;
        }

        return true;
    }
}
