package br.com.fiap.Aprenda.controller.api;

import br.com.fiap.Aprenda.dto.*;
import br.com.fiap.Aprenda.model.Usuario;
import br.com.fiap.Aprenda.service.AutenticacaoService;
import br.com.fiap.Aprenda.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller REST para autenticação
 * Segue padrão do MotoVision: HTTP Basic Authentication
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints para autenticação mobile")
public class AutenticacaoController {

    private final AutenticacaoService servicoAutenticacao;
    private final UsuarioService servicoUsuario;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    @Operation(summary = "Registrar novo usuário")
    public ResponseEntity<RespostaAutenticacao> registrar(@Valid @RequestBody RequisicaoRegistro requisicao) {
        RespostaAutenticacao resposta = servicoAutenticacao.registrar(requisicao);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PostMapping("/login")
    @Operation(summary = "Login mobile", description = "Autentica usuário para aplicação mobile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody RequisicaoLogin requisicao) {
        try {
            Usuario usuario = servicoUsuario.buscarPorEmail(requisicao.getEmail());

            if (usuario != null && passwordEncoder.matches(requisicao.getSenha(), usuario.getSenha())) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Login realizado com sucesso");
                response.put("user", Map.of(
                        "id", usuario.getId(),
                        "nome", usuario.getNome(),
                        "email", usuario.getEmail(),
                        "pontosTotais", usuario.getPontosTotais() != null ? usuario.getPontosTotais() : 0,
                        "onboardingConcluido",
                        usuario.getOnboardingConcluido() != null ? usuario.getOnboardingConcluido() : false));
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Email ou senha incorretos");
                return ResponseEntity.status(401).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erro interno do servidor");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/validate")
    @Operation(summary = "Validar autenticação", description = "Valida se o usuário está autenticado")
    public ResponseEntity<Map<String, Object>> validate() {
        Map<String, Object> response = new HashMap<>();
        response.put("valid", true);
        response.put("message", "Autenticação válida");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Recuperar senha")
    public ResponseEntity<RespostaApi<String>> esqueciSenha(@RequestBody RequisicaoEsqueciSenha requisicao) {
        RespostaApi<String> resposta = servicoAutenticacao.esqueciSenha(requisicao.getEmail());
        return ResponseEntity.ok(resposta);
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class RequisicaoEsqueciSenha {
        private String email;
    }
}
