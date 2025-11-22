package br.com.fiap.Aprenda.service;

import br.com.fiap.Aprenda.exception.RecursoNaoEncontradoException;
import br.com.fiap.Aprenda.model.Trilha;
import br.com.fiap.Aprenda.model.Usuario;
import br.com.fiap.Aprenda.model.UsuarioTrilha;
import br.com.fiap.Aprenda.repository.TrilhaRepository;
import br.com.fiap.Aprenda.repository.UsuarioRepository;
import br.com.fiap.Aprenda.repository.UsuarioTrilhaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Serviço para gerenciamento de trilhas de aprendizado
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TrilhaService {

    private final TrilhaRepository trilhaRepository;
    private final UsuarioTrilhaRepository usuarioTrilhaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CursoService cursoService;

    @Cacheable(value = "trails")
    public Page<Trilha> listarTodos(String area, Pageable pageable) {
        if (area != null && !area.trim().isEmpty()) {
            return trilhaRepository.findByArea(area, pageable);
        }
        return trilhaRepository.findAll(pageable);
    }

    public Trilha obterPorId(Long id) {
        return trilhaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Trilha não encontrada"));
    }

    public List<UsuarioTrilha> obterTrilhasUsuario() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        return usuarioTrilhaRepository.findByUsuario_Id(usuario.getId());
    }

    @Transactional
    public UsuarioTrilha inscrever(Long trilhaId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        Trilha trilha = obterPorId(trilhaId);

        if (usuarioTrilhaRepository.existsByUsuario_IdAndTrilha_Id(usuario.getId(), trilhaId)) {
            throw new IllegalArgumentException("Usuário já está inscrito nesta trilha");
        }

        UsuarioTrilha usuarioTrilha = UsuarioTrilha.builder()
                .usuario(usuario)
                .trilha(trilha)
                .build();

        UsuarioTrilha salvo = usuarioTrilhaRepository.save(usuarioTrilha);

        trilha.getCursos().forEach(curso -> {
            try {
                cursoService.inscrever(curso.getId());
            } catch (IllegalArgumentException e) {
                // já inscrito, ignora
            } catch (Exception e) {
                log.warn("Falha ao auto-inscrever no curso {} da trilha {}: {}", curso.getId(), trilhaId,
                        e.getMessage());
            }
        });

        return salvo;
    }

    public Map<String, Object> obterProgresso(Long trilhaId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        // Verificar se usuário existe
        usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        Trilha trilha = obterPorId(trilhaId);

        // TODO: Calcular progresso baseado em cursos concluídos na trilha
        long cursosCompletos = 0; // Implementar lógica
        int totalCursos = trilha.getCursos().size();

        double percentualCompleto = totalCursos > 0
                ? (double) cursosCompletos / totalCursos * 100
                : 0;

        Map<String, Object> progresso = new HashMap<>();
        progresso.put("trilhaId", trilhaId);
        progresso.put("cursosCompletos", cursosCompletos);
        progresso.put("totalCursos", totalCursos);
        progresso.put("percentualCompleto", percentualCompleto);

        return progresso;
    }
}
