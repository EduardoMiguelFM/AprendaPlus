package br.com.fiap.Aprenda.service;

import br.com.fiap.Aprenda.dto.AulaDTO;
import br.com.fiap.Aprenda.exception.RecursoNaoEncontradoException;
import br.com.fiap.Aprenda.model.Curso;
import br.com.fiap.Aprenda.model.PreferenciasUsuario;
import br.com.fiap.Aprenda.model.Usuario;
import br.com.fiap.Aprenda.model.UsuarioCurso;
import br.com.fiap.Aprenda.repository.CursoRepository;
import br.com.fiap.Aprenda.repository.PreferenciasUsuarioRepository;
import br.com.fiap.Aprenda.repository.UsuarioCursoRepository;
import br.com.fiap.Aprenda.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Serviço para gerenciamento de cursos
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CursoService {

    private static final Map<String, List<String[]>> AULAS_BASE_POR_AREA = Map.ofEntries(
            Map.entry("ia", List.of(
                    new String[] { "Panorama da Inteligência Artificial",
                            "Evolução, conceitos fundamentais e casos de uso.", "15 min" },
                    new String[] { "Machine Learning vs Deep Learning",
                            "Diferenças práticas entre os principais paradigmas.", "18 min" },
                    new String[] { "Dados: combustível da IA",
                            "Coleta, limpeza e preparação de dados para modelos.", "20 min" },
                    new String[] { "Modelos supervisionados", "Aplicações em classificação e regressão.", "22 min" },
                    new String[] { "Modelos não supervisionados",
                            "Clusterização e redução de dimensionalidade em ação.", "18 min" },
                    new String[] { "Tendências e ética em IA", "Responsabilidade, viés e governança.", "16 min" })),
            Map.entry("dados", List.of(
                    new String[] { "Fundamentos de Ciência de Dados", "Ciclo analítico do problema ao insight.",
                            "15 min" },
                    new String[] { "Ambiente Python", "Tour por Jupyter, pandas e bibliotecas essenciais.", "17 min" },
                    new String[] { "Exploração e visualização", "Dashboards, gráficos e métricas-chave.", "20 min" },
                    new String[] { "Modelagem estatística", "Regressão, correlação e testes de hipóteses.", "22 min" },
                    new String[] { "Big Data na prática", "Hadoop, Spark e arquiteturas distribuídas.", "24 min" },
                    new String[] { "Storytelling com dados", "Como transformar números em decisões.", "16 min" })),
            Map.entry("programacao", List.of(
                    new String[] { "Configuração do ambiente", "Ferramentas, IDEs e boas práticas iniciais.",
                            "12 min" },
                    new String[] { "Lógica e estruturas básicas", "Variáveis, condicionais e loops.", "20 min" },
                    new String[] { "Funções e modularização", "Organizando código reutilizável.", "18 min" },
                    new String[] { "Coleções e estruturas de dados", "Listas, mapas e conjuntos na prática.",
                            "22 min" },
                    new String[] { "Orientação a Objetos", "Classes, herança e encapsulamento.", "24 min" },
                    new String[] { "Projeto prático guiado", "Construindo uma aplicação completa.", "26 min" })),
            Map.entry("sustentabilidade", List.of(
                    new String[] { "ESG e Agenda 2030", "Pilares ambientais, sociais e de governança.", "15 min" },
                    new String[] { "Economia circular", "Modelos de reaproveitamento e redução de resíduos.",
                            "18 min" },
                    new String[] { "Energia renovável aplicada", "Solar, eólica e híbridos.", "20 min" },
                    new String[] { "Indicadores de sustentabilidade", "KPIs, frameworks e relatórios.", "17 min" },
                    new String[] { "Engajamento interno", "Cultura, squads verdes e comunicação.", "16 min" },
                    new String[] { "Cases de impacto", "Negócios que escalaram com sustentabilidade.", "18 min" })),
            Map.entry("design", List.of(
                    new String[] { "Princípios de UX/UI", "Heurísticas, contraste e hierarquia visual.", "14 min" },
                    new String[] { "Pesquisa com usuários", "Métodos qualitativos e quantitativos.", "18 min" },
                    new String[] { "Wireframes e prototipação", "Do rascunho ao protótipo navegável.", "20 min" },
                    new String[] { "Sistemas de design", "Componentização, grids e tokens.", "22 min" },
                    new String[] { "Acessibilidade digital", "WCAG, testes e ferramentas.", "16 min" },
                    new String[] { "Entrega e handoff", "Colaboração com squads de desenvolvimento.", "15 min" })),
            Map.entry("marketing", List.of(
                    new String[] { "Funil de Marketing Digital", "Jornada do usuário e métricas chave.", "15 min" },
                    new String[] { "SEO e Conteúdo", "On-page, off-page e calendário editorial.", "18 min" },
                    new String[] { "Mídia paga inteligente", "Segmentação, lances e criativos vencedores.", "20 min" },
                    new String[] { "Automação e CRM", "Fluxos, nutrição de leads e scoring.", "22 min" },
                    new String[] { "Growth Experiments", "Hipóteses, testes A/B e squads de crescimento.", "18 min" },
                    new String[] { "Analytics e Reporting", "KPIs e storytelling para stakeholders.", "16 min" })),
            Map.entry("gestao", List.of(
                    new String[] { "Agilidade em escala", "Scrum, Kanban e frameworks híbridos.", "17 min" },
                    new String[] { "Planejamento estratégico", "OKRs, metas e priorização.", "20 min" },
                    new String[] { "Gestão de equipes", "Feedback contínuo e rituais de performance.", "18 min" },
                    new String[] { "Riscos e governança", "Mapeamento, mitigação e compliance.", "19 min" },
                    new String[] { "Finanças para gestores", "Fluxo de caixa, DRE e indicadores.", "21 min" },
                    new String[] { "Comunicação executiva", "Influência e storytelling corporativo.", "16 min" })),
            Map.entry("vendas", List.of(
                    new String[] { "Playbook comercial", "Processo, funil e papéis do time.", "15 min" },
                    new String[] { "Prospecção moderna", "Social selling, cadências e ferramentas.", "18 min" },
                    new String[] { "Discovery de alto impacto", "Perguntas, escuta ativa e mapeamento.", "20 min" },
                    new String[] { "Demonstrações que convertem", "Storytelling e provas sociais.", "18 min" },
                    new String[] { "Negociação e fechamento", "Ancoragem, objeções e urgência.", "19 min" },
                    new String[] { "Pós-venda e expansão", "Sucesso do cliente e upsell.", "17 min" })),
            Map.entry("rh", List.of(
                    new String[] { "Panorama de RH estratégico", "Transformação digital e people analytics.",
                            "15 min" },
                    new String[] { "Recrutamento data-driven", "Techniques de atração e seleção justa.", "18 min" },
                    new String[] { "Onboarding memorável", "Jornada dos primeiros 90 dias.", "17 min" },
                    new String[] { "Desenvolvimento e learning", "Trilhas personalizadas e academias internas.",
                            "20 min" },
                    new String[] { "Clima e engajamento", "Pesquisas, squads e ações de impacto.", "18 min" },
                    new String[] { "People Analytics aplicado", "KPIs, dashboards e previsões.", "19 min" })),
            Map.entry("financas", List.of(
                    new String[] { "Fundamentos financeiros", "Demonstrações contábeis e indicadores.", "16 min" },
                    new String[] { "Planejamento e orçamento", "Rolling forecast, CAPEX e OPEX.", "18 min" },
                    new String[] { "Análise de investimentos", "Payback, VPL e TIR.", "20 min" },
                    new String[] { "Gestão de riscos", "Câmbio, crédito e compliance.", "18 min" },
                    new String[] { "Finanças pessoais x corporativas", "Principais diferenças e cruzamentos.",
                            "16 min" },
                    new String[] { "Dashboard financeiro", "Construindo painéis para decisão.", "17 min" })),
            Map.entry("saude", List.of(
                    new String[] { "Prevenção e bem-estar", "Estilos de vida e check-ups.", "14 min" },
                    new String[] { "Nutrição baseada em evidências", "Macronutrientes e planejamento alimentar.",
                            "17 min" },
                    new String[] { "Atividade física segura", "Periodização, intensidade e recuperação.", "18 min" },
                    new String[] { "Saúde mental e produtividade", "Técnicas de gerenciamento do estresse.", "20 min" },
                    new String[] { "Protocolos preventivos", "Vacinas, exames e monitoramento.", "18 min" },
                    new String[] { "Tendências em healthtech", "Wearables, telemedicina e IA.", "17 min" })),
            Map.entry("educacao", List.of(
                    new String[] { "Metodologias ativas", "PBL, sala invertida e aprendizagem por projetos.",
                            "15 min" },
                    new String[] { "Design instrucional", "Objetivos, avaliações e roteiros.", "17 min" },
                    new String[] { "Tecnologias educacionais", "LMS, ferramentas síncronas e assíncronas.", "18 min" },
                    new String[] { "Avaliação formativa", "Rubricas, feedback e gamificação.", "19 min" },
                    new String[] { "Inclusão e acessibilidade", "Recursos adaptativos e desenho universal.", "17 min" },
                    new String[] { "Educação híbrida e EAD", "Modelos, engajamento e métricas.", "18 min" })));

    private final CursoRepository cursoRepository;
    private final UsuarioCursoRepository usuarioCursoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PreferenciasUsuarioRepository preferenciasRepository;
    private final GamificacaoService servicoGamificacao;

    @Transactional(readOnly = true)
    @Cacheable(value = "courses")
    public Page<Curso> listarTodos(String area, String nivel, Pageable pageable) {
        Page<Curso> cursos;
        if (area != null && nivel != null) {
            cursos = cursoRepository.findByAreaAndNivel(area, nivel, pageable);
        } else if (area != null) {
            cursos = cursoRepository.findByArea(area, pageable);
        } else if (nivel != null) {
            cursos = cursoRepository.findByNivel(nivel, pageable);
        } else {
            cursos = cursoRepository.findAll(pageable);
        }
        // Forçar inicialização das coleções lazy antes de fechar a sessão
        cursos.getContent().forEach(curso -> {
            if (curso.getUsuariosCursos() != null) {
                curso.getUsuariosCursos().size();
            }
            if (curso.getTrilhas() != null) {
                curso.getTrilhas().size();
            }
        });
        return cursos;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "courses", key = "#id")
    public Curso obterPorId(Long id) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Curso não encontrado"));
        // Forçar inicialização das coleções lazy antes de fechar a sessão
        if (curso.getUsuariosCursos() != null) {
            curso.getUsuariosCursos().size();
        }
        if (curso.getTrilhas() != null) {
            curso.getTrilhas().size();
        }
        return curso;
    }

    @Transactional(readOnly = true)
    public List<Curso> obterSugeridos() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

            PreferenciasUsuario preferencias = preferenciasRepository.findByUsuario_Id(usuario.getId())
                    .orElse(null);

            if (preferencias == null || preferencias.getAreasInteresse().isEmpty()) {
                return cursoRepository.findAll().stream().limit(6).collect(Collectors.toList());
            }

            List<String> listaAreas = preferencias.getAreasInteresse();
            List<String> listaNiveis = preferencias.getNiveisInteresse() != null
                    ? preferencias.getNiveisInteresse()
                    : List.of();

            var cursosPorAreaENivel = java.util.stream.IntStream.range(0, listaAreas.size())
                    .boxed()
                    .flatMap(i -> {
                        String area = listaAreas.get(i);
                        String nivel = listaNiveis.size() > i ? listaNiveis.get(i) : null;
                        if (nivel != null && !nivel.isBlank()) {
                            return cursoRepository.findByAreaAndNivel(area, nivel, Pageable.ofSize(2)).stream();
                        }
                        return cursoRepository.findByArea(area, Pageable.ofSize(2)).stream();
                    })
                    .collect(java.util.LinkedHashMap<Long, Curso>::new,
                            (map, curso) -> map.putIfAbsent(curso.getId(), curso),
                            java.util.LinkedHashMap::putAll)
                    .values()
                    .stream()
                    .limit(6)
                    .collect(Collectors.toList());

            if (cursosPorAreaENivel.isEmpty()) {
                return cursoRepository.findAll().stream().limit(6).collect(Collectors.toList());
            }

            return cursosPorAreaENivel;
        } catch (Exception e) {
            log.warn("Falha ao gerar cursos sugeridos personalizados, retornando fallback.", e);
            // Se não autenticado, retorna cursos populares
            return cursoRepository.findAll().stream().limit(6).collect(Collectors.toList());
        }
    }

    public List<Curso> obterPorArea(String area, String nivel, Pageable pageable) {
        if (nivel != null) {
            return cursoRepository.findByAreaAndNivel(area, nivel, pageable).getContent();
        }
        return cursoRepository.findByArea(area, pageable).getContent();
    }

    @Transactional
    public UsuarioCurso inscrever(Long cursoId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        Curso curso = obterPorId(cursoId);

        if (usuarioCursoRepository.existsByUsuario_IdAndCurso_Id(usuario.getId(), cursoId)) {
            throw new IllegalArgumentException("Usuário já está inscrito neste curso");
        }

        UsuarioCurso usuarioCurso = UsuarioCurso.builder()
                .usuario(usuario)
                .curso(curso)
                .progresso(0)
                .status("em_andamento")
                .build();

        return usuarioCursoRepository.save(usuarioCurso);
    }

    public List<UsuarioCurso> obterCursosUsuario(String status) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        if (status != null) {
            return usuarioCursoRepository.findByUsuario_IdAndStatus(usuario.getId(), status);
        }
        return usuarioCursoRepository.findByUsuario_Id(usuario.getId());
    }

    @Transactional
    public UsuarioCurso atualizarProgresso(Long cursoId, Integer progresso) {
        return atualizarProgressoDetalhado(cursoId, progresso).usuarioCurso();
    }

    private AtualizacaoCursoResultado atualizarProgressoDetalhado(Long cursoId, Integer progresso) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        UsuarioCurso usuarioCurso = usuarioCursoRepository.findByUsuario_IdAndCurso_Id(usuario.getId(), cursoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Inscrição não encontrada"));

        int progressoAnterior = usuarioCurso.getProgresso();
        usuarioCurso.setProgresso(Math.min(100, Math.max(0, progresso)));

        // Sistema de pontos por milestones e conclusão
        int progressoAtual = usuarioCurso.getProgresso();
        Curso curso = usuarioCurso.getCurso();

        // Calcular pontos base baseado na duração e nível do curso
        int pontosBase = calcularPontosBaseCurso(curso);
        int pontosGanhos = 0;

        // Verificar milestones (25%, 50%, 75%, 100%)
        int[] milestones = { 25, 50, 75, 100 };
        for (int milestone : milestones) {
            if (progressoAnterior < milestone && progressoAtual >= milestone) {
                int pontosMilestone = (int) (pontosBase * (milestone / 100.0) * 0.25); // 25% dos pontos base por
                                                                                       // milestone
                servicoGamificacao.adicionarPontos(
                        usuario.getId(),
                        pontosMilestone,
                        "Milestone " + milestone + "% - " + curso.getTitulo());
                pontosGanhos += pontosMilestone;
            }
        }

        // Bônus por conclusão (100%)
        boolean cursoConcluidoAgora = false;
        if (progressoAtual >= 100 && !"concluido".equals(usuarioCurso.getStatus())) {
            cursoConcluidoAgora = true;
            usuarioCurso.setStatus("concluido");

            // Bônus de conclusão: 50% dos pontos base
            int bonusConclusao = (int) (pontosBase * 0.5);
            servicoGamificacao.adicionarPontos(
                    usuario.getId(),
                    bonusConclusao,
                    "🎉 Curso Concluído! - " + curso.getTitulo());
            pontosGanhos += bonusConclusao;

            // Bônus extra por nível do curso
            int bonusNivel = calcularBonusNivel(curso.getNivel());
            if (bonusNivel > 0) {
                servicoGamificacao.adicionarPontos(
                        usuario.getId(),
                        bonusNivel,
                        "⭐ Bônus de Dificuldade - " + curso.getTitulo());
                pontosGanhos += bonusNivel;
            }
        }

        UsuarioCurso salvo = usuarioCursoRepository.save(usuarioCurso);
        return new AtualizacaoCursoResultado(salvo, pontosGanhos, cursoConcluidoAgora);
    }

    public UsuarioCurso obterProgresso(Long cursoId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        return usuarioCursoRepository.findByUsuario_IdAndCurso_Id(usuario.getId(), cursoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Inscrição não encontrada"));
    }

    public int estimarPontosTotais(Curso curso) {
        int pontosBase = calcularPontosBaseCurso(curso);
        int pontosPorMilestone = (int) Math.round(pontosBase * 1.125); // 62,5% milestones + 50% conclusão
        return pontosPorMilestone + calcularBonusNivel(curso.getNivel());
    }

    public List<AulaDTO> gerarPlanoAulas(Curso curso) {
        List<String[]> base = obterConteudoBase(curso.getArea());
        int totalPlanejado = curso.getTotalAulas() != null ? curso.getTotalAulas() : base.size();
        List<AulaDTO> aulas = new ArrayList<>();

        for (int i = 0; i < Math.min(totalPlanejado, base.size()); i++) {
            String[] dados = base.get(i);
            aulas.add(AulaDTO.builder()
                    .numero(i + 1)
                    .titulo(dados[0])
                    .resumo(dados[1])
                    .duracao(dados[2])
                    .concluida(false)
                    .build());
        }

        // Se o curso tem mais aulas do que o conteúdo base, gera conteúdos extras
        for (int i = base.size(); i < totalPlanejado; i++) {
            aulas.add(AulaDTO.builder()
                    .numero(i + 1)
                    .titulo("Projeto guiado " + (i + 1))
                    .resumo("Aplicação prática dos aprendizados desta etapa.")
                    .duracao("20 min")
                    .concluida(false)
                    .build());
        }

        return aulas;
    }

    @Transactional
    public CursoConclusaoResultado concluirAula(Long cursoId, int numeroAula) {
        if (numeroAula < 1) {
            throw new IllegalArgumentException("Aula inválida");
        }

        UsuarioCurso usuarioCurso = obterProgresso(cursoId);
        int totalAulas = usuarioCurso.getCurso().getTotalAulas() != null ? usuarioCurso.getCurso().getTotalAulas() : 1;
        int numeroNormalizado = Math.min(numeroAula, totalAulas);

        int novoProgresso = (int) Math.round((numeroNormalizado / (double) totalAulas) * 100);
        AtualizacaoCursoResultado resultado = null;
        if (novoProgresso > usuarioCurso.getProgresso()) {
            resultado = atualizarProgressoDetalhado(cursoId, novoProgresso);
        }

        if (resultado == null) {
            resultado = new AtualizacaoCursoResultado(usuarioCurso, 0, false);
        }

        return new CursoConclusaoResultado(resultado.cursoConcluidoAgora(), resultado.pontosGanhos());
    }

    /**
     * Calcula pontos base do curso baseado em duração e nível
     * Cursos valem mais que quizzes porque duram mais tempo
     */
    private int calcularPontosBaseCurso(Curso curso) {
        int pontosBase = 100; // Base mínima

        // Multiplicador por nível
        switch (curso.getNivel().toLowerCase()) {
            case "iniciante":
                pontosBase = 120;
                break;
            case "intermediário":
            case "intermediario":
                pontosBase = 180;
                break;
            case "avançado":
            case "avancado":
                pontosBase = 250;
                break;
        }

        // Bônus por duração (cursos mais longos valem mais)
        if (curso.getDuracao() != null && !curso.getDuracao().isEmpty()) {
            try {
                // Tentar extrair horas da string (ex: "20 horas", "10h", etc)
                String duracaoStr = curso.getDuracao().replaceAll("[^0-9]", "");
                if (!duracaoStr.isEmpty()) {
                    int horas = Integer.parseInt(duracaoStr);
                    if (horas >= 20) {
                        pontosBase = (int) (pontosBase * 1.5); // +50% para cursos longos
                    } else if (horas >= 10) {
                        pontosBase = (int) (pontosBase * 1.25); // +25% para cursos médios
                    }
                }
            } catch (NumberFormatException e) {
                // Se não conseguir parsear, ignora o bônus
            }
        }

        return pontosBase;
    }

    /**
     * Calcula bônus extra baseado no nível de dificuldade
     */
    private int calcularBonusNivel(String nivel) {
        switch (nivel.toLowerCase()) {
            case "iniciante":
                return 10;
            case "intermediário":
            case "intermediario":
                return 25;
            case "avançado":
            case "avancado":
                return 50;
            default:
                return 0;
        }
    }

    private List<String[]> obterConteudoBase(String area) {
        return AULAS_BASE_POR_AREA.getOrDefault(area, List.of(
                new String[] { "Fundamentos essenciais", "Conceitos necessários para começar com segurança.",
                        "12 min" },
                new String[] { "Ferramentas e setup", "Ambiente de trabalho, atalhos e produtividade.", "15 min" },
                new String[] { "Aplicando na prática", "Demonstração guiada com exercícios.", "18 min" },
                new String[] { "Boas práticas", "Padrões, organização e colaboração em equipe.", "16 min" },
                new String[] { "Projeto guiado", "Construindo um mini-projeto passo a passo.", "22 min" },
                new String[] { "Próximos passos", "Referências, comunidade e desafios avançados.", "14 min" }));
    }

    private record AtualizacaoCursoResultado(UsuarioCurso usuarioCurso, int pontosGanhos, boolean cursoConcluidoAgora) {
    }

    public record CursoConclusaoResultado(boolean cursoConcluido, int pontosGanhos) {
    }

    @Transactional
    public Curso criar(Curso curso) {
        return cursoRepository.save(curso);
    }

    @Transactional
    public Curso atualizar(Long id, Curso cursoAtualizado) {
        Curso cursoExistente = cursoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Curso não encontrado"));

        cursoExistente.setTitulo(cursoAtualizado.getTitulo());
        cursoExistente.setDescricao(cursoAtualizado.getDescricao());
        cursoExistente.setArea(cursoAtualizado.getArea());
        cursoExistente.setDuracao(cursoAtualizado.getDuracao());
        cursoExistente.setNivel(cursoAtualizado.getNivel());
        cursoExistente.setIcone(cursoAtualizado.getIcone());
        cursoExistente.setConteudo(cursoAtualizado.getConteudo());
        cursoExistente.setInstrutor(cursoAtualizado.getInstrutor());
        cursoExistente.setAvaliacao(cursoAtualizado.getAvaliacao());
        cursoExistente.setTotalAulas(cursoAtualizado.getTotalAulas());

        Curso cursoSalvo = cursoRepository.save(cursoExistente);
        // Forçar inicialização das coleções lazy antes de fechar a sessão
        if (cursoSalvo.getUsuariosCursos() != null) {
            cursoSalvo.getUsuariosCursos().size();
        }
        if (cursoSalvo.getTrilhas() != null) {
            cursoSalvo.getTrilhas().size();
        }
        return cursoSalvo;
    }

    @Transactional
    public void deletar(Long id) {
        Curso curso = obterPorId(id);
        cursoRepository.delete(curso);
    }
}
