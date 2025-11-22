package br.com.fiap.Aprenda.service;

import br.com.fiap.Aprenda.dto.*;
import br.com.fiap.Aprenda.exception.RecursoNaoEncontradoException;
import br.com.fiap.Aprenda.model.Usuario;
import br.com.fiap.Aprenda.model.PreferenciasUsuario;
import br.com.fiap.Aprenda.repository.UsuarioRepository;
import br.com.fiap.Aprenda.repository.PreferenciasUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço para autenticação e registro de usuários
 */
@Service
@RequiredArgsConstructor
public class AutenticacaoService {

        private final UsuarioRepository usuarioRepository;
        private final PreferenciasUsuarioRepository preferenciasRepository;
        private final PasswordEncoder passwordEncoder;
        private final AuthenticationManager authenticationManager;
        private final EmailService emailService;

        @Transactional
        public RespostaAutenticacao registrar(RequisicaoRegistro requisicao) {
                // Validar se senhas coincidem
                if (!requisicao.getSenha().equals(requisicao.getConfirmarSenha())) {
                        throw new IllegalArgumentException("Senhas não coincidem");
                }

                // Validar se email já existe
                if (usuarioRepository.existsByEmail(requisicao.getEmail())) {
                        throw new IllegalArgumentException("Email já cadastrado");
                }

                // Criar usuário
                Usuario usuario = Usuario.builder()
                                .nome(requisicao.getNome())
                                .email(requisicao.getEmail())
                                .senha(passwordEncoder.encode(requisicao.getSenha()))
                                .pontosTotais(0)
                                .onboardingConcluido(false)
                                .build();

                usuario = usuarioRepository.save(usuario);

                // Criar preferências vazias
                PreferenciasUsuario preferencias = PreferenciasUsuario.builder()
                                .usuario(usuario)
                                .onboardingConcluido(false)
                                .build();
                preferenciasRepository.save(preferencias);

                // Enviar email de boas-vindas de forma assíncrona (não bloqueia a resposta)
                emailService.enviarEmailBoasVindas(usuario.getEmail(), usuario.getNome());

                return RespostaAutenticacao.builder()
                                .sucesso(true)
                                .mensagem("Cadastro realizado com sucesso!")
                                .usuario(mapearParaDTO(usuario))
                                .build();
        }

        public RespostaAutenticacao login(RequisicaoLogin requisicao) {
                // Autenticar usuário
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                requisicao.getEmail(),
                                                requisicao.getSenha()));

                // Buscar usuário
                Usuario usuario = usuarioRepository.findByEmail(requisicao.getEmail())
                                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

                return RespostaAutenticacao.builder()
                                .sucesso(true)
                                .mensagem("Login realizado com sucesso!")
                                .usuario(mapearParaDTO(usuario))
                                .build();
        }

        public RespostaApi<String> esqueciSenha(String email) {
                // Verificar se email existe
                usuarioRepository.findByEmail(email)
                                .orElseThrow(() -> new RecursoNaoEncontradoException("Email não encontrado"));

                // Gerar código de recuperação simples (pode ser melhorado no futuro)
                String codigoRecuperacao = gerarCodigoRecuperacao();

                // Enviar email de forma assíncrona (não bloqueia a resposta)
                emailService.enviarEmailEsqueciSenha(email, codigoRecuperacao);

                return RespostaApi.sucesso("Email de recuperação enviado com sucesso!");
        }

        private String gerarCodigoRecuperacao() {
                // Gera um código simples de 6 dígitos
                return String.valueOf((int) (Math.random() * 900000) + 100000);
        }

        private UsuarioDTO mapearParaDTO(Usuario usuario) {
                return UsuarioDTO.builder()
                                .id(usuario.getId())
                                .nome(usuario.getNome())
                                .email(usuario.getEmail())
                                .pontosTotais(usuario.getPontosTotais())
                                .onboardingConcluido(usuario.getOnboardingConcluido())
                                .criadoEm(usuario.getCriadoEm())
                                .build();
        }
}
