package br.com.fiap.Aprenda.service;

import br.com.fiap.Aprenda.dto.RequisicaoCompletarDesafio;
import br.com.fiap.Aprenda.exception.RecursoNaoEncontradoException;
import br.com.fiap.Aprenda.model.Desafio;
import br.com.fiap.Aprenda.model.PerguntaDesafio;
import br.com.fiap.Aprenda.model.Usuario;
import br.com.fiap.Aprenda.model.UsuarioDesafio;
import br.com.fiap.Aprenda.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Serviço para gerenciamento de desafios
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DesafioService {

    private final DesafioRepository desafioRepository;
    private final PerguntaDesafioRepository perguntaDesafioRepository;
    private final UsuarioDesafioRepository usuarioDesafioRepository;
    private final UsuarioRepository usuarioRepository;
    private final GamificacaoService servicoGamificacao;
    private final TrofeuRepository trofeuRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "challenges")
    public Page<Desafio> listarTodos(String area, String nivel, String tipo, Pageable pageable) {
        Page<Desafio> desafios;
        if (area != null && nivel != null) {
            desafios = desafioRepository.findByAreaAndNivel(area, nivel, pageable);
        } else if (area != null) {
            desafios = desafioRepository.findByArea(area, pageable);
        } else if (nivel != null) {
            desafios = desafioRepository.findByNivel(nivel, pageable);
        } else if (tipo != null) {
            desafios = desafioRepository.findByTipo(tipo, pageable);
        } else {
            desafios = desafioRepository.findAll(pageable);
        }
        // Forçar inicialização das coleções lazy antes de fechar a sessão
        desafios.getContent().forEach(desafio -> {
            if (desafio.getPerguntas() != null) {
                desafio.getPerguntas().size();
            }
            if (desafio.getUsuariosDesafios() != null) {
                desafio.getUsuariosDesafios().size();
            }
        });
        return desafios;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "challenges", key = "#id")
    public Desafio obterPorId(Long id) {
        Desafio desafio = desafioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Desafio não encontrado"));
        // Forçar inicialização das coleções lazy antes de fechar a sessão
        if (desafio.getPerguntas() != null) {
            desafio.getPerguntas().size();
        }
        if (desafio.getUsuariosDesafios() != null) {
            desafio.getUsuariosDesafios().size();
        }
        return desafio;
    }

    @Transactional(readOnly = true)
    public List<PerguntaDesafio> obterPerguntas(Long desafioId) {
        Desafio desafio = obterPorId(desafioId);
        List<PerguntaDesafio> perguntas = perguntaDesafioRepository.findByDesafio_Id(desafioId);

        if (perguntas.isEmpty()) {
            log.warn("Desafio {} não possui perguntas. Gerando perguntas padrão.", desafioId);
            perguntas = gerarPerguntasFallback(desafio);
        }
        // Forçar inicialização das coleções opcoes dentro da transação
        perguntas.forEach(p -> {
            if (p.getOpcoes() != null) {
                p.getOpcoes().size(); // Força o carregamento
            }
        });
        return perguntas;
    }

    @Transactional
    public UsuarioDesafio completar(Long desafioId, RequisicaoCompletarDesafio requisicao) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        Desafio desafio = obterPorId(desafioId);

        if (usuarioDesafioRepository.existsByUsuario_IdAndDesafio_Id(usuario.getId(), desafioId)) {
            throw new IllegalArgumentException("Desafio já foi concluído");
        }

        // Calcular pontos baseado na performance
        double percentual = (double) requisicao.getPontuacao() / requisicao.getTotalPerguntas() * 100;
        int pontosGanhos;
        if (percentual >= 80) {
            pontosGanhos = desafio.getPontos(); // 100%
        } else if (percentual >= 60) {
            pontosGanhos = (int) (desafio.getPontos() * 0.7); // 70%
        } else if (percentual >= 40) {
            pontosGanhos = (int) (desafio.getPontos() * 0.5); // 50%
        } else {
            pontosGanhos = (int) (desafio.getPontos() * 0.3); // 30%
        }

        // Adicionar pontos ao usuário
        servicoGamificacao.adicionarPontos(usuario.getId(), pontosGanhos,
                "Desafio concluído: " + desafio.getTitulo());

        // Verificar se 100% de acerto para troféu "Perfeito"
        if (percentual >= 100) {
            try {
                trofeuRepository.findByNome("Perfeito")
                        .ifPresent(trofeu -> {
                            try {
                                servicoGamificacao.concederTrofeu(usuario.getId(), trofeu.getId());
                            } catch (IllegalArgumentException e) {
                                // Usuário já possui o troféu, ignora
                            }
                        });
            } catch (Exception e) {
                // Troféu não encontrado ou erro ao conceder, continua normalmente
            }
        }

        UsuarioDesafio usuarioDesafio = UsuarioDesafio.builder()
                .usuario(usuario)
                .desafio(desafio)
                .pontuacao(requisicao.getPontuacao())
                .totalPerguntas(requisicao.getTotalPerguntas())
                .tempoGasto(requisicao.getTempoGasto())
                .pontosGanhos(pontosGanhos)
                .build();

        return usuarioDesafioRepository.save(usuarioDesafio);
    }

    public List<UsuarioDesafio> obterCompletos() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        return usuarioDesafioRepository.findByUsuario_Id(usuario.getId());
    }

    public Optional<UsuarioDesafio> obterStatus(Long desafioId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        return usuarioDesafioRepository.findByUsuario_IdAndDesafio_Id(usuario.getId(), desafioId);
    }

    private List<PerguntaDesafio> gerarPerguntasFallback(Desafio desafio) {
        List<PerguntaDesafio> novasPerguntas = new ArrayList<>();
        List<String> opcoesPadrao = Arrays.asList("Opção A", "Opção B", "Opção C", "Opção D");

        for (int i = 1; i <= 5; i++) {
            PerguntaDesafio pergunta = PerguntaDesafio.builder()
                    .desafio(desafio)
                    .pergunta(String.format(
                            "Pergunta %d sobre %s (%s)?",
                            i,
                            desafio.getArea(),
                            desafio.getNivel()))
                    .opcoes(new ArrayList<>(opcoesPadrao))
                    .indiceCorreto(0)
                    .explicacao("Resposta correta: Opção A.")
                    .build();
            novasPerguntas.add(pergunta);
        }

        return perguntaDesafioRepository.saveAll(novasPerguntas);
    }

    @Transactional
    public Desafio criar(Desafio desafio) {
        return desafioRepository.save(desafio);
    }

    @Transactional
    public Desafio atualizar(Long id, Desafio desafioAtualizado) {
        Desafio desafioExistente = desafioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Desafio não encontrado"));

        desafioExistente.setTipo(desafioAtualizado.getTipo());
        desafioExistente.setArea(desafioAtualizado.getArea());
        desafioExistente.setNivel(desafioAtualizado.getNivel());
        desafioExistente.setTitulo(desafioAtualizado.getTitulo());
        desafioExistente.setDescricao(desafioAtualizado.getDescricao());
        desafioExistente.setPontos(desafioAtualizado.getPontos());
        desafioExistente.setIcone(desafioAtualizado.getIcone());
        desafioExistente.setDificuldade(desafioAtualizado.getDificuldade());

        Desafio desafioSalvo = desafioRepository.save(desafioExistente);
        // Forçar inicialização das coleções lazy antes de fechar a sessão
        if (desafioSalvo.getPerguntas() != null) {
            desafioSalvo.getPerguntas().size();
        }
        if (desafioSalvo.getUsuariosDesafios() != null) {
            desafioSalvo.getUsuariosDesafios().size();
        }
        return desafioSalvo;
    }

    @Transactional
    public void deletar(Long id) {
        Desafio desafio = obterPorId(id);
        desafioRepository.delete(desafio);
    }
}
