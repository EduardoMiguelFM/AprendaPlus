package br.com.fiap.Aprenda.service;

import br.com.fiap.Aprenda.dto.*;
import br.com.fiap.Aprenda.exception.RecursoNaoEncontradoException;
import br.com.fiap.Aprenda.model.Usuario;
import br.com.fiap.Aprenda.model.PreferenciasUsuario;
import br.com.fiap.Aprenda.repository.UsuarioRepository;
import br.com.fiap.Aprenda.repository.PreferenciasUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço para gerenciamento de usuários
 */
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PreferenciasUsuarioRepository preferenciasRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioDTO obterUsuarioAtual() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
        return mapearParaDTO(usuario);
    }

    @Transactional
    public UsuarioDTO atualizarPerfil(String nome, String email, String senha) {
        Usuario usuario = obterUsuarioEntidade();

        if (nome != null && !nome.isBlank()) {
            usuario.setNome(nome);
        }

        if (email != null && !email.isBlank()) {
            if (!email.equals(usuario.getEmail()) && usuarioRepository.existsByEmail(email)) {
                throw new IllegalArgumentException("Email já está em uso");
            }
            usuario.setEmail(email);
        }

        if (senha != null && !senha.isBlank()) {
            if (senha.length() < 6) {
                throw new IllegalArgumentException("Senha deve ter no mínimo 6 caracteres");
            }
            usuario.setSenha(passwordEncoder.encode(senha));
        }

        usuario = usuarioRepository.save(usuario);
        return mapearParaDTO(usuario);
    }

    @Transactional
    public void deletarPerfil() {
        Usuario usuario = obterUsuarioEntidade();
        usuarioRepository.delete(usuario);
    }

    @Transactional
    public RequisicaoPreferencias obterPreferencias() {
        Usuario usuario = obterUsuarioEntidade();
        PreferenciasUsuario preferencias = preferenciasRepository.findByUsuario_Id(usuario.getId())
                .orElseGet(() -> preferenciasRepository.save(PreferenciasUsuario.builder()
                        .usuario(usuario)
                        .build()));

        List<AreaInteresseDTO> areasInteresse = preferencias.getAreasInteresse().stream()
                .map(area -> {
                    int index = preferencias.getAreasInteresse().indexOf(area);
                    String nivel = index < preferencias.getNiveisInteresse().size()
                            ? preferencias.getNiveisInteresse().get(index)
                            : "Iniciante";
                    return AreaInteresseDTO.builder()
                            .area(area)
                            .nivel(nivel)
                            .build();
                })
                .collect(Collectors.toList());

        RequisicaoPreferencias requisicao = new RequisicaoPreferencias();
        requisicao.setAreasInteresse(areasInteresse);
        requisicao.setOnboardingConcluido(preferencias.getOnboardingConcluido());

        return requisicao;
    }

    @Transactional
    public RequisicaoPreferencias salvarPreferencias(RequisicaoPreferencias requisicao) {
        Usuario usuario = obterUsuarioEntidade();

        if (requisicao.getAreasInteresse().size() > 3) {
            throw new IllegalArgumentException("Máximo de 3 áreas de interesse permitidas");
        }

        PreferenciasUsuario preferencias = preferenciasRepository.findByUsuario_Id(usuario.getId())
                .orElse(PreferenciasUsuario.builder().usuario(usuario).build());

        List<String> areas = requisicao.getAreasInteresse().stream()
                .map(AreaInteresseDTO::getArea)
                .collect(Collectors.toList());

        List<String> niveis = requisicao.getAreasInteresse().stream()
                .map(AreaInteresseDTO::getNivel)
                .collect(Collectors.toList());

        preferencias.getAreasInteresse().clear();
        preferencias.getAreasInteresse().addAll(areas);

        preferencias.getNiveisInteresse().clear();
        preferencias.getNiveisInteresse().addAll(niveis);
        preferencias.setOnboardingConcluido(requisicao.getOnboardingConcluido() != null
                ? requisicao.getOnboardingConcluido()
                : false);

        if (preferencias.getOnboardingConcluido()) {
            usuario.setOnboardingConcluido(true);
            usuarioRepository.save(usuario);
        }

        preferenciasRepository.save(preferencias);

        return obterPreferencias();
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    private Usuario obterUsuarioEntidade() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
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
