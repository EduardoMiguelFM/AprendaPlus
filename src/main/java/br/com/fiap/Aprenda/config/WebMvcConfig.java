package br.com.fiap.Aprenda.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração do Spring MVC
 * Registra interceptors personalizados
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final OnboardingInterceptor onboardingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(onboardingInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/api/**",
                        "/login",
                        "/cadastro",
                        "/onboarding/**",
                        "/logout",
                        "/",
                        "/sobre",
                        "/error",
                        "/public/**",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/webjars/**",
                        "/swagger/**",
                        "/v3/api-docs/**",
                        "/actuator/**",
                        "/favicon.ico",
                        "/*.ico");
    }
}
