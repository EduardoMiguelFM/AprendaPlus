package br.com.fiap.Aprenda.config;

import br.com.fiap.Aprenda.service.DetalhesUsuarioService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.Customizer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Configuração de Spring Security
 * Controla autenticação e autorização da aplicação
 * Segue padrão do MotoVision: HTTP Basic para APIs, Sessão para Web
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        private final DetalhesUsuarioService detalhesUsuarioService;
        private final CorsConfigurationSource corsConfigurationSource;

        public SecurityConfig(
                        DetalhesUsuarioService detalhesUsuarioService,
                        @Qualifier("corsConfigurationSource") CorsConfigurationSource corsConfigurationSource) {
                this.detalhesUsuarioService = detalhesUsuarioService;
                this.corsConfigurationSource = corsConfigurationSource;
        }

        @Bean
        public DaoAuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
                DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(passwordEncoder);
                authProvider.setUserDetailsService(detalhesUsuarioService);
                return authProvider;
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }

        private static final String[] PUBLIC_API_PATHS = {
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/api/auth/**",
                        "/api/ia/**" // Permitir acesso ao chat IA
        };

        @Bean
        @Order(1)
        public SecurityFilterChain apiFilterChain(HttpSecurity http, PasswordEncoder passwordEncoder) throws Exception {
                http
                                .securityMatcher("/api/**")
                                .authorizeHttpRequests(authz -> authz
                                                .requestMatchers(PUBLIC_API_PATHS).permitAll()
                                                .anyRequest().authenticated())
                                .httpBasic(Customizer.withDefaults())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                                .csrf(csrf -> csrf.disable())
                                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                                .authenticationProvider(authenticationProvider(passwordEncoder));

                return http.build();
        }

        @Bean
        @Order(2)
        public SecurityFilterChain webFilterChain(HttpSecurity http, PasswordEncoder passwordEncoder) throws Exception {
                http
                                .authorizeHttpRequests(authz -> authz
                                                // Páginas públicas
                                                .requestMatchers("/", "/login", "/cadastro", "/sobre", "/css/**",
                                                                "/js/**", "/images/**", "/webjars/**", "/favicon.ico",
                                                                "/*.ico", "/*.css", "/*.js", "/*.png",
                                                                "/*.jpg", "/*.jpeg", "/*.gif", "/*.svg")
                                                .permitAll()

                                                // Swagger/OpenAPI
                                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**",
                                                                "/swagger-resources/**")
                                                .permitAll()

                                                // Actuator
                                                .requestMatchers("/actuator/**").permitAll()

                                                // Rotas de onboarding
                                                .requestMatchers("/onboarding/**").authenticated()

                                                // Rotas de cursos, desafios, trilhas
                                                .requestMatchers("/cursos/**", "/desafios/**", "/trilhas/**")
                                                .authenticated()

                                                // Dashboard e perfil
                                                .requestMatchers("/home", "/dashboard", "/perfil/**",
                                                                "/cursos/meus-cursos")
                                                .authenticated()

                                                // Todas as outras requisições precisam de autenticação
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .loginProcessingUrl("/login")
                                                .usernameParameter("email")
                                                .passwordParameter("senha")
                                                .defaultSuccessUrl("/dashboard", true)
                                                .failureUrl("/login?error=true")
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login?logout=true")
                                                .invalidateHttpSession(true)
                                                .clearAuthentication(true)
                                                .deleteCookies("JSESSIONID")
                                                .permitAll())
                                .sessionManagement(session -> session
                                                .invalidSessionUrl("/login?error=sessao_expirada")
                                                .maximumSessions(1)
                                                .maxSessionsPreventsLogin(false)
                                                .expiredUrl("/login?error=sessao_expirada"))
                                .csrf(csrf -> csrf
                                                .csrfTokenRepository(
                                                                org.springframework.security.web.csrf.CookieCsrfTokenRepository
                                                                                .withHttpOnlyFalse()))
                                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                                .authenticationProvider(authenticationProvider(passwordEncoder));

                return http.build();
        }
}
