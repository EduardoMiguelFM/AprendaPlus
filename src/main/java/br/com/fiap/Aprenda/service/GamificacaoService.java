package br.com.fiap.Aprenda.service;

import br.com.fiap.Aprenda.exception.RecursoNaoEncontradoException;
import br.com.fiap.Aprenda.model.*;
import br.com.fiap.Aprenda.repository.*;
import br.com.fiap.Aprenda.util.NivelUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço para sistema de gamificação (pontos, troféus, estatísticas)
 */
@Service
@RequiredArgsConstructor
public class GamificacaoService {

        private final UsuarioRepository usuarioRepository;
        private final UsuarioCursoRepository usuarioCursoRepository;
        private final UsuarioDesafioRepository usuarioDesafioRepository;
        private final TrofeuRepository trofeuRepository;
        private final UsuarioTrofeuRepository usuarioTrofeuRepository;
        private final TransacaoPontosRepository transacaoPontosRepository;
        private final EmailService emailService;

        @Transactional
        public void adicionarPontos(Long usuarioId, Integer pontos, String motivo) {
                Usuario usuario = usuarioRepository.findById(usuarioId)
                                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

                usuario.setPontosTotais(usuario.getPontosTotais() + pontos);
                usuarioRepository.save(usuario);

                TransacaoPontos transacao = TransacaoPontos.builder()
                                .usuario(usuario)
                                .pontos(pontos)
                                .motivo(motivo)
                                .tipoTransacao("ganho")
                                .build();
                transacaoPontosRepository.save(transacao);
        }

        public Integer obterPontosTotais(Long usuarioId) {
                Usuario usuario = usuarioRepository.findById(usuarioId)
                                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
                return usuario.getPontosTotais();
        }

        public List<TransacaoPontos> obterHistoricoPontos(Long usuarioId, Pageable pageable) {
                return transacaoPontosRepository.findByUsuario_IdOrderByCriadoEmDesc(usuarioId, pageable).getContent();
        }

        @Transactional
        public UsuarioTrofeu concederTrofeu(Long usuarioId, Long trofeuId) {
                Usuario usuario = usuarioRepository.findById(usuarioId)
                                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

                Trofeu trofeu = trofeuRepository.findById(trofeuId)
                                .orElseThrow(() -> new RecursoNaoEncontradoException("Troféu não encontrado"));

                if (usuarioTrofeuRepository.existsByUsuario_IdAndTrofeu_Id(usuarioId, trofeuId)) {
                        throw new IllegalArgumentException("Usuário já possui este troféu");
                }

                UsuarioTrofeu usuarioTrofeu = UsuarioTrofeu.builder()
                                .usuario(usuario)
                                .trofeu(trofeu)
                                .build();

                usuarioTrofeu = usuarioTrofeuRepository.save(usuarioTrofeu);

                // Enviar notificação de conquista de forma assíncrona
                emailService.enviarNotificacaoConquista(
                                usuario.getEmail(),
                                trofeu.getNome(),
                                trofeu.getDescricao());

                return usuarioTrofeu;
        }

        public List<UsuarioTrofeu> obterTrofeusUsuario(Long usuarioId) {
                return usuarioTrofeuRepository.findByUsuario_Id(usuarioId);
        }

        public EstatisticasDTO obterEstatisticas(Long usuarioId) {
                Usuario usuario = usuarioRepository.findById(usuarioId)
                                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

                List<UsuarioCurso> todosCursos = usuarioCursoRepository.findByUsuario_Id(usuarioId);
                long cursosEmAndamento = todosCursos.stream()
                                .filter(c -> "em_andamento".equals(c.getStatus()))
                                .count();
                long cursosConcluidos = todosCursos.stream()
                                .filter(c -> "concluido".equals(c.getStatus()))
                                .count();

                int desafiosCompletos = usuarioDesafioRepository.findByUsuario_Id(usuarioId).size();

                // Calcular progresso geral (média de progresso dos cursos)
                double progressoGeral = todosCursos.stream()
                                .mapToInt(UsuarioCurso::getProgresso)
                                .average()
                                .orElse(0.0);

                List<UsuarioTrofeu> trofeus = obterTrofeusUsuario(usuarioId);

                // TODO: Calcular ranking
                EstatisticasDTO.RankingInfo ranking = EstatisticasDTO.RankingInfo.builder()
                                .posicao(1)
                                .total(1000L)
                                .build();

                // Calcular informações de nível
                NivelUtil.NivelInfo nivelInfo = NivelUtil.obterNivelInfo(usuario.getPontosTotais());

                return EstatisticasDTO.builder()
                                .pontos(usuario.getPontosTotais())
                                .cursosEmAndamento((int) cursosEmAndamento)
                                .cursosConcluidos((int) cursosConcluidos)
                                .desafiosCompletos(desafiosCompletos)
                                .progressoGeral((int) progressoGeral)
                                .trofes(trofeus.stream().map(this::mapearParaDTO).collect(Collectors.toList()))
                                .ranking(ranking)
                                .nivelInfo(nivelInfo)
                                .build();
        }

        private EstatisticasDTO.TrofeuDTO mapearParaDTO(UsuarioTrofeu usuarioTrofeu) {
                Trofeu trofeu = usuarioTrofeu.getTrofeu();
                return EstatisticasDTO.TrofeuDTO.builder()
                                .id(trofeu.getId())
                                .nome(trofeu.getNome())
                                .descricao(trofeu.getDescricao())
                                .icone(trofeu.getIcone())
                                .conquistadoEm(usuarioTrofeu.getConquistadoEm())
                                .build();
        }

        // Inner class para DTO de estatísticas
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class EstatisticasDTO {
                private Integer pontos;
                private Integer cursosEmAndamento;
                private Integer cursosConcluidos;
                private Integer desafiosCompletos;
                private Integer progressoGeral;
                private List<TrofeuDTO> trofes;
                private RankingInfo ranking;
                private NivelUtil.NivelInfo nivelInfo;

                @Data
                @Builder
                @NoArgsConstructor
                @AllArgsConstructor
                public static class TrofeuDTO {
                        private Long id;
                        private String nome;
                        private String descricao;
                        private String icone;
                        private java.time.LocalDateTime conquistadoEm;
                }

                @Data
                @Builder
                @NoArgsConstructor
                @AllArgsConstructor
                public static class RankingInfo {
                        private Integer posicao;
                        private Long total;
                }
        }
}
