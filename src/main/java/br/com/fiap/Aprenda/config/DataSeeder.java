package br.com.fiap.Aprenda.config;

import br.com.fiap.Aprenda.model.*;
import br.com.fiap.Aprenda.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Configuração para popular o banco de dados com dados iniciais
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {

        private final CursoRepository cursoRepository;
        private final DesafioRepository desafioRepository;
        private final PerguntaDesafioRepository perguntaDesafioRepository;
        private final TrilhaRepository trilhaRepository;
        private final TrofeuRepository trofeuRepository;

        private static final String[] AREAS_PADRAO = { "ia", "dados", "programacao", "sustentabilidade", "design",
                        "marketing", "gestao", "vendas", "rh", "financas", "saude", "educacao" };
        private static final String[] NIVEIS_PADRAO = { "Iniciante", "Intermediário", "Avançado" };
        private static final String[] ICONES_PADRAO = { "🧠", "📊", "💻", "🌱", "🎨", "📱", "📋", "💰", "👥", "💵",
                        "🥗",
                        "📚" };

        private record CursoSeed(String titulo, String descricao, String area, String duracao, String nivel,
                        String icone, String conteudo, String instrutor, double avaliacao, int totalAulas) {
        }

        @Bean
        @Transactional
        @Profile("!test")
        public CommandLineRunner seedData() {
                return args -> {
                        if (cursoRepository.count() == 0) {
                                log.info("Iniciando seed de dados...");
                                popularCursos();
                                garantirCursosPorAreaENivel();
                                popularDesafios();
                                popularTrilhas();
                                popularTrofeus();
                                log.info("Seed de dados concluído!");
                        } else {
                                log.info("Dados já existem no banco. Verificando desafios e trilhas...");

                                garantirCursosPorAreaENivel();

                                // Só criar desafios se não existirem
                                if (desafioRepository.count() == 0) {
                                        log.info("Criando desafios...");
                                        popularDesafios();
                                        log.info("Desafios criados!");
                                } else {
                                        log.info("Desafios já existem ({})", desafioRepository.count());
                                }

                                // Só criar trilhas se não existirem
                                if (trilhaRepository.count() == 0) {
                                        log.info("Criando trilhas...");
                                        popularTrilhas();
                                        log.info("Trilhas criadas!");
                                } else {
                                        log.info("Trilhas já existem ({})", trilhaRepository.count());
                                }

                                // Só criar troféus se não existirem
                                if (trofeuRepository.count() == 0) {
                                        log.info("Criando troféus...");
                                        popularTrofeus();
                                        log.info("Troféus criados!");
                                } else {
                                        log.info("Troféus já existem ({})", trofeuRepository.count());
                                }
                        }

                        garantirDesafiosAtualizados();
                };
        }

        private void popularCursos() {
                log.info("Criando cursos...");

                List<Curso> cursos = obterCursosSeed().stream()
                                .map(this::converterParaCurso)
                                .collect(Collectors.toList());

                cursoRepository.saveAll(cursos);
                log.info("{} cursos criados", cursos.size());
        }

        private Curso converterParaCurso(CursoSeed seed) {
                return criarCurso(seed.titulo(), seed.descricao(), seed.area(), seed.duracao(), seed.nivel(),
                                seed.icone(),
                                seed.conteudo(), seed.instrutor(), seed.avaliacao(), seed.totalAulas());
        }

        private List<CursoSeed> obterCursosSeed() {
                return Arrays.asList(
                                // Inteligência Artificial
                                new CursoSeed("Introdução à Inteligência Artificial", "Curso introdutório sobre IA",
                                                "ia",
                                                "40 horas", "Iniciante", "🤖",
                                                "Fundamentos de IA, Machine Learning básico", "Prof. Silva", 4.5, 12),
                                new CursoSeed("Machine Learning Avançado", "Curso avançado de ML", "ia", "60 horas",
                                                "Avançado", "🧠",
                                                "Redes neurais, deep learning", "Prof. Santos", 4.8, 18),
                                new CursoSeed("Modelos Preditivos com IA",
                                                "Construindo pipelines de machine learning do zero", "ia",
                                                "50 horas", "Intermediário", "🤖",
                                                "Pré-processamento, tuning e monitoramento de modelos", "Prof. Andrade",
                                                4.7, 16),

                                // Dados
                                new CursoSeed("Fundamentos de Ciência de Dados", "Introdução à análise de dados",
                                                "dados",
                                                "50 horas", "Iniciante", "📊", "Python, Pandas, análise exploratória",
                                                "Prof. Oliveira", 4.6, 15),
                                new CursoSeed("Big Data e Analytics", "Trabalhando com grandes volumes de dados",
                                                "dados", "70 horas", "Intermediário", "💾",
                                                "Hadoop, Spark, análise de dados massivos", "Prof. Costa", 4.7, 20),
                                new CursoSeed("Arquitetura de Data Lakehouse", "Estruture dados em escala empresarial",
                                                "dados", "80 horas", "Avançado", "🗄️",
                                                "Lakehouse, governança, processamento em tempo real", "Prof. Batista",
                                                4.9, 22),

                                // Programação
                                new CursoSeed("Programação Java do Zero", "Aprenda Java desde o início", "programacao",
                                                "80 horas", "Iniciante", "☕", "Sintaxe, OOP, Collections",
                                                "Prof. Almeida", 4.9, 25),
                                new CursoSeed("Desenvolvimento Web Full Stack", "Construa aplicações web completas",
                                                "programacao", "100 horas", "Intermediário", "🌐",
                                                "HTML, CSS, JavaScript, Spring Boot", "Prof. Lima", 4.8, 30),
                                new CursoSeed("Arquitetura de Microsserviços em Java",
                                                "Padrões avançados para sistemas resilientes", "programacao",
                                                "90 horas", "Avançado", "⚙️",
                                                "Microsserviços, mensageria, observabilidade e segurança",
                                                "Prof. Prado",
                                                4.9, 24),

                                // Sustentabilidade
                                new CursoSeed("Sustentabilidade Empresarial", "Implemente práticas sustentáveis",
                                                "sustentabilidade", "30 horas", "Iniciante", "🌱",
                                                "ESG, economia circular", "Prof. Green", 4.5, 10),
                                new CursoSeed("Energias Renováveis", "Fontes alternativas de energia",
                                                "sustentabilidade", "45 horas", "Intermediário", "⚡",
                                                "Solar, eólica, hidrelétrica", "Prof. Energy", 4.6, 12),
                                new CursoSeed("Estratégias ESG Avançadas",
                                                "Integre sustentabilidade ao core do negócio",
                                                "sustentabilidade", "60 horas", "Avançado", "🌎",
                                                "ESG reporting, créditos de carbono, cadeias circulares",
                                                "Prof. Helena", 4.8, 18),

                                // Design
                                new CursoSeed("Design UX/UI Básico", "Princípios de design de interfaces", "design",
                                                "40 horas", "Iniciante", "🎨", "Figma, wireframes, protótipos",
                                                "Prof. Design", 4.7, 14),
                                new CursoSeed("Design Thinking Avançado", "Metodologia de inovação", "design",
                                                "50 horas", "Intermediário", "💡", "Empatia, ideação, prototipação",
                                                "Prof. Innovate", 4.8, 16),
                                new CursoSeed("Design Systems e Motion", "Experiências consistentes multiplataforma",
                                                "design", "55 horas", "Avançado", "🌀",
                                                "Design systems, microinterações, acessibilidade avançada",
                                                "Prof. Martins", 4.9, 18),

                                // Marketing
                                new CursoSeed("Marketing Digital Essencial", "Fundamentos do marketing online",
                                                "marketing", "35 horas", "Iniciante", "📱",
                                                "SEO, SEM, redes sociais", "Prof. Marketing", 4.6, 11),
                                new CursoSeed("Campanhas Multicanal e Automação",
                                                "Crie jornadas personalizadas em escala", "marketing", "45 horas",
                                                "Intermediário", "📣",
                                                "CRM, automação inteligente, testes A/B", "Prof. Torres", 4.8, 15),
                                new CursoSeed("Estratégias de Growth Hacking", "Crescimento acelerado de negócios",
                                                "marketing", "55 horas", "Avançado", "🚀",
                                                "Análise de dados, automação", "Prof. Growth", 4.9, 18),

                                // Gestão
                                new CursoSeed("Fundamentos de Gestão e Planejamento",
                                                "Principais conceitos de administração moderna", "gestao", "35 horas",
                                                "Iniciante", "📘",
                                                "Planejamento, organização, indicadores básicos", "Prof. Souza", 4.5,
                                                10),
                                new CursoSeed("Gestão de Projetos Ágil", "Metodologias ágeis e Scrum", "gestao",
                                                "45 horas", "Intermediário", "📋", "Scrum, Kanban, Jira",
                                                "Prof. Manager", 4.7, 15),
                                new CursoSeed("Liderança Estratégica", "Desenvolva habilidades de liderança", "gestao",
                                                "60 horas", "Avançado", "👔", "Tomada de decisão, gestão de equipes",
                                                "Prof. Leader", 4.8, 20),

                                // Vendas
                                new CursoSeed("Técnicas de Vendas Modernas", "Venda mais e melhor", "vendas",
                                                "30 horas", "Iniciante", "💰", "Processo de vendas, objeções",
                                                "Prof. Sales", 4.6, 10),
                                new CursoSeed("Vendas Consultivas Avançadas", "Abordagem consultiva em vendas",
                                                "vendas",
                                                "50 horas", "Intermediário", "🤝",
                                                "Descoberta, proposta, fechamento", "Prof. Consultant", 4.7, 16),
                                new CursoSeed("Negociações Complexas e Enterprise",
                                                "Estratégias para vendas B2B de alto valor", "vendas", "60 horas",
                                                "Avançado", "🏆",
                                                "Account-based selling, SPIN, contratos complexos", "Prof. Mendes", 4.8,
                                                18),

                                // RH
                                new CursoSeed("Gestão de Recursos Humanos", "Fundamentos de RH", "rh", "40 horas",
                                                "Iniciante", "👥", "Recrutamento, seleção, treinamento", "Prof. HR",
                                                4.5, 12),
                                new CursoSeed("Employee Experience e Cultura",
                                                "Crie jornadas memoráveis para colaboradores", "rh", "45 horas",
                                                "Intermediário", "🤗",
                                                "Cultura, diversidade, EVP e comunicação interna", "Prof. Silveira",
                                                4.7, 14),
                                new CursoSeed("People Analytics", "Dados e análises em RH", "rh", "55 horas",
                                                "Avançado", "📈",
                                                "Métricas, KPIs, People Data", "Prof. Analytics", 4.8, 17),

                                // Finanças
                                new CursoSeed("Finanças Pessoais", "Organize suas finanças", "financas", "25 horas",
                                                "Iniciante", "💵", "Orçamento, investimentos básicos",
                                                "Prof. Finance", 4.7, 8),
                                new CursoSeed("Planejamento Financeiro Corporativo",
                                                "Estruture orçamentos e cenários", "financas", "55 horas",
                                                "Intermediário", "💼",
                                                "Forecast, WACC, valuation básico", "Prof. Ribeiro", 4.8, 16),
                                new CursoSeed("Análise Financeira Empresarial", "Análise avançada de demonstrações",
                                                "financas", "70 horas", "Avançado", "📊",
                                                "DRE, balanço, fluxo de caixa", "Prof. Analyst", 4.9, 22),

                                // Saúde
                                new CursoSeed("Nutrição e Bem-estar", "Alimentação saudável", "saude", "30 horas",
                                                "Iniciante", "🥗", "Nutrientes, dietas balanceadas", "Prof. Health",
                                                4.6, 10),
                                new CursoSeed("Medicina Preventiva", "Prevenção e promoção da saúde", "saude",
                                                "50 horas", "Intermediário", "🏥",
                                                "Vacinação, check-ups, prevenção", "Prof. Doctor", 4.7, 15),
                                new CursoSeed("Saúde Digital e HealthTech", "Tecnologias para cuidado inteligente",
                                                "saude", "60 horas", "Avançado", "🩺",
                                                "Telemedicina, prontuário eletrônico, análise de dados clínicos",
                                                "Prof. Carvalho", 4.8, 17),

                                // Educação
                                new CursoSeed("Metodologias de Ensino", "Como ensinar de forma eficaz", "educacao",
                                                "40 horas", "Iniciante", "📚", "Pedagogia, didática", "Prof. Teacher",
                                                4.8, 13),
                                new CursoSeed("Educação Online e EAD", "Ensino a distância", "educacao", "60 horas",
                                                "Intermediário", "💻", "Plataformas, ferramentas, avaliação",
                                                "Prof. Online", 4.7, 18),
                                new CursoSeed("Educação Personalizada por Dados",
                                                "Aprendizagem adaptativa e analytics educacional", "educacao",
                                                "55 horas", "Avançado", "🧠",
                                                "Learning analytics, IA aplicada, experiências imersivas",
                                                "Prof. Moreira", 4.8, 16));
        }

        private Curso criarCurso(String titulo, String descricao, String area, String duracao,
                        String nivel, String icone, String conteudo, String instrutor,
                        Double avaliacao, Integer totalAulas) {
                return Curso.builder()
                                .titulo(titulo)
                                .descricao(descricao)
                                .area(area)
                                .duracao(duracao)
                                .nivel(nivel)
                                .icone(icone)
                                .conteudo(conteudo)
                                .instrutor(instrutor)
                                .avaliacao(avaliacao)
                                .totalAulas(totalAulas)
                                .build();
        }

        private void garantirCursosPorAreaENivel() {
                String[] areas = { "ia", "dados", "programacao", "sustentabilidade", "design", "marketing", "gestao",
                                "vendas", "rh", "financas", "saude", "educacao" };
                String[] niveis = { "Iniciante", "Intermediário", "Avançado" };

                Map<String, CursoSeed> seedPorChave = obterCursosSeed().stream()
                                .collect(Collectors.toMap(seed -> seed.area() + "::" + seed.nivel(), seed -> seed,
                                                (existente, ignorado) -> existente));

                for (String area : areas) {
                        for (String nivel : niveis) {
                                if (!cursoRepository.existsByAreaAndNivel(area, nivel)) {
                                        CursoSeed seed = seedPorChave
                                                        .getOrDefault(area + "::" + nivel,
                                                                        gerarSeedPadrao(area, nivel));
                                        cursoRepository.save(converterParaCurso(seed));
                                        log.info("Curso padrão criado para área {} ({})", area, nivel);
                                }
                        }
                }
        }

        private CursoSeed gerarSeedPadrao(String area, String nivel) {
                String nomeArea = obterNomeArea(area);
                String tituloNivel = switch (nivel) {
                        case "Intermediário" -> "Práticas Essenciais de ";
                        case "Avançado" -> "Masterclass em ";
                        default -> "Fundamentos de ";
                };
                String duracaoPadrao = switch (nivel) {
                        case "Intermediário" -> "45 horas";
                        case "Avançado" -> "60 horas";
                        default -> "30 horas";
                };
                int totalAulas = switch (nivel) {
                        case "Intermediário" -> 15;
                        case "Avançado" -> 18;
                        default -> 12;
                };
                String conteudoPadrao = switch (nivel) {
                        case "Intermediário" -> "Projetos guiados, ferramentas profissionais e estudos de caso reais";
                        case "Avançado" -> "Laboratórios avançados, desafios reais e certificações recomendadas";
                        default -> "Conceitos básicos, vocabulário da área e exercícios introdutórios";
                };

                return new CursoSeed(
                                tituloNivel + nomeArea,
                                "Conteúdo curado automaticamente para garantir a disponibilidade desta área.",
                                area,
                                duracaoPadrao,
                                nivel,
                                obterIconeArea(area),
                                conteudoPadrao + " focados em " + nomeArea,
                                "Equipe Aprenda+",
                                4.7,
                                totalAulas);
        }

        private String obterIconeArea(String area) {
                return switch (area) {
                        case "ia" -> "🤖";
                        case "dados" -> "📊";
                        case "programacao" -> "💻";
                        case "sustentabilidade" -> "🌱";
                        case "design" -> "🎨";
                        case "marketing" -> "📱";
                        case "gestao" -> "📋";
                        case "vendas" -> "💰";
                        case "rh" -> "👥";
                        case "financas" -> "💵";
                        case "saude" -> "🥗";
                        case "educacao" -> "📚";
                        default -> "📘";
                };
        }

        private void popularDesafios() {
                log.info("Criando desafios...");
                String[] areas = AREAS_PADRAO;
                String[] niveis = NIVEIS_PADRAO;
                String[] icones = ICONES_PADRAO;

                for (int i = 0; i < areas.length; i++) {
                        String area = areas[i];
                        String icone = icones[i];
                        String nomeArea = obterNomeArea(area);

                        for (String nivel : niveis) {
                                criarOuAtualizarDesafio(area, nivel, icone, nomeArea);
                        }
                }

                log.info("Desafios criados para todas as áreas e níveis");
        }

        private void garantirDesafiosAtualizados() {
                log.info("Sincronizando desafios e perguntas...");
                for (int i = 0; i < AREAS_PADRAO.length; i++) {
                        String area = AREAS_PADRAO[i];
                        String icone = ICONES_PADRAO[i];
                        String nomeArea = obterNomeArea(area);

                        for (String nivel : NIVEIS_PADRAO) {
                                Desafio desafio = criarOuAtualizarDesafio(area, nivel, icone, nomeArea);
                                atualizarPerguntasSeNecessario(desafio, area, nivel);
                        }
                }
        }

        private Desafio criarOuAtualizarDesafio(String area, String nivel, String icone, String nomeArea) {
                List<Desafio> existentes = desafioRepository.findByAreaAndNivel(area, nivel);
                Desafio desafio;

                if (existentes.isEmpty()) {
                        desafio = criarQuiz(area, nivel,
                                        "Quiz: " + nomeArea + " - " + nivel,
                                        "Teste seus conhecimentos sobre " + nomeArea + " no nível " + nivel
                                                        .toLowerCase(),
                                        calcularPontosPorNivel(nivel), icone);
                } else {
                        desafio = existentes.get(0);
                        desafio.setTitulo("Quiz: " + nomeArea + " - " + nivel);
                        desafio.setDescricao("Teste seus conhecimentos sobre " + nomeArea + " no nível "
                                        + nivel.toLowerCase());
                        desafio.setPontos(calcularPontosPorNivel(nivel));
                        desafio.setIcone(icone);
                        desafio.setDificuldade(definirDificuldade(nivel));
                        desafioRepository.save(desafio);

                        // remover duplicados
                        if (existentes.size() > 1) {
                                existentes.stream().skip(1).forEach(extra -> {
                                        var perguntasExtra = perguntaDesafioRepository.findByDesafio_Id(extra.getId());
                                        perguntaDesafioRepository.deleteAll(perguntasExtra);
                                        desafioRepository.delete(extra);
                                });
                        }
                }

                return desafio;
        }

        private void atualizarPerguntasSeNecessario(Desafio desafio, String area, String nivel) {
                List<PerguntaDesafio> atuais = perguntaDesafioRepository.findByDesafio_Id(desafio.getId());
                boolean precisaAtualizar = atuais.size() != 5
                                || atuais.stream().anyMatch(p -> p.getPergunta() == null
                                                || p.getPergunta().startsWith("Pergunta "));

                if (!precisaAtualizar) {
                        return;
                }

                perguntaDesafioRepository.deleteAll(atuais);
                List<PerguntaDesafio> novasPerguntas = gerarPerguntas(area, nivel);
                adicionarPerguntas(desafio, novasPerguntas);
                log.info("Perguntas atualizadas para desafio {} ({})", area, nivel);
        }

        private int calcularPontosPorNivel(String nivel) {
                return switch (nivel) {
                        case "Iniciante" -> 25;
                        case "Intermediário" -> 40;
                        default -> 65;
                };
        }

        private String definirDificuldade(String nivel) {
                return switch (nivel) {
                        case "Iniciante" -> "Fácil";
                        case "Intermediário" -> "Médio";
                        default -> "Difícil";
                };
        }

        private String obterNomeArea(String area) {
                Map<String, String> nomes = Map.ofEntries(
                                Map.entry("ia", "Inteligência Artificial"),
                                Map.entry("dados", "Ciência de Dados"),
                                Map.entry("programacao", "Programação"),
                                Map.entry("sustentabilidade", "Sustentabilidade"),
                                Map.entry("design", "Design"),
                                Map.entry("marketing", "Marketing Digital"),
                                Map.entry("gestao", "Gestão"),
                                Map.entry("vendas", "Vendas"),
                                Map.entry("rh", "Recursos Humanos"),
                                Map.entry("financas", "Finanças"),
                                Map.entry("saude", "Saúde"),
                                Map.entry("educacao", "Educação"));
                return nomes.getOrDefault(area, area);
        }

        private List<PerguntaDesafio> gerarPerguntas(String area, String nivel) {
                List<PerguntaDesafio> perguntas = new ArrayList<>();

                // Gerar perguntas baseadas na área e nível
                // Iniciante: perguntas básicas e conceituais
                // Intermediário: perguntas práticas e aplicadas
                // Avançado: perguntas complexas e técnicas
                switch (area) {
                        case "ia":
                                if ("Iniciante".equals(nivel)) {
                                        perguntas.addAll(gerarPerguntasIAIniciante());
                                } else if ("Intermediário".equals(nivel)) {
                                        perguntas.addAll(gerarPerguntasIAIntermediario());
                                } else {
                                        perguntas.addAll(gerarPerguntasIAAvancado());
                                }
                                break;
                        case "dados":
                                if ("Iniciante".equals(nivel)) {
                                        perguntas.addAll(gerarPerguntasDadosIniciante());
                                } else if ("Intermediário".equals(nivel)) {
                                        perguntas.addAll(gerarPerguntasDadosIntermediario());
                                } else {
                                        perguntas.addAll(gerarPerguntasDadosAvancado());
                                }
                                break;
                        case "programacao":
                                if ("Iniciante".equals(nivel)) {
                                        perguntas.addAll(gerarPerguntasProgramacaoIniciante());
                                } else if ("Intermediário".equals(nivel)) {
                                        perguntas.addAll(gerarPerguntasProgramacaoIntermediario());
                                } else {
                                        perguntas.addAll(gerarPerguntasProgramacaoAvancado());
                                }
                                break;
                        case "sustentabilidade":
                                if ("Iniciante".equals(nivel)) {
                                        perguntas.addAll(gerarPerguntasSustentabilidadeIniciante());
                                } else if ("Intermediário".equals(nivel)) {
                                        perguntas.addAll(gerarPerguntasSustentabilidadeIntermediario());
                                } else {
                                        perguntas.addAll(gerarPerguntasSustentabilidadeAvancado());
                                }
                                break;
                        case "design":
                                if ("Iniciante".equals(nivel)) {
                                        perguntas.addAll(gerarPerguntasDesignIniciante());
                                } else if ("Intermediário".equals(nivel)) {
                                        perguntas.addAll(gerarPerguntasDesignIntermediario());
                                } else {
                                        perguntas.addAll(gerarPerguntasDesignAvancado());
                                }
                                break;
                        case "marketing":
                                if ("Iniciante".equals(nivel)) {
                                        perguntas.addAll(gerarPerguntasMarketingIniciante());
                                } else if ("Intermediário".equals(nivel)) {
                                        perguntas.addAll(gerarPerguntasMarketingIntermediario());
                                } else {
                                        perguntas.addAll(gerarPerguntasMarketingAvancado());
                                }
                                break;
                        case "gestao":
                                if ("Iniciante".equals(nivel)) {
                                        perguntas.addAll(gerarPerguntasGestaoIniciante());
                                } else if ("Intermediário".equals(nivel)) {
                                        perguntas.addAll(gerarPerguntasGestaoIntermediario());
                                } else {
                                        perguntas.addAll(gerarPerguntasGestaoAvancado());
                                }
                                break;
                        case "vendas":
                                if ("Iniciante".equals(nivel)) {
                                        perguntas.addAll(gerarPerguntasVendasIniciante());
                                } else if ("Intermediário".equals(nivel)) {
                                        perguntas.addAll(gerarPerguntasVendasIntermediario());
                                } else {
                                        perguntas.addAll(gerarPerguntasVendasAvancado());
                                }
                                break;
                        case "rh":
                                if ("Iniciante".equals(nivel)) {
                                        perguntas.addAll(gerarPerguntasRHIniciante());
                                } else if ("Intermediário".equals(nivel)) {
                                        perguntas.addAll(gerarPerguntasRHIntermediario());
                                } else {
                                        perguntas.addAll(gerarPerguntasRHAvancado());
                                }
                                break;
                        case "financas":
                                if ("Iniciante".equals(nivel)) {
                                        perguntas.addAll(gerarPerguntasFinancasIniciante());
                                } else if ("Intermediário".equals(nivel)) {
                                        perguntas.addAll(gerarPerguntasFinancasIntermediario());
                                } else {
                                        perguntas.addAll(gerarPerguntasFinancasAvancado());
                                }
                                break;
                        case "saude":
                                if ("Iniciante".equals(nivel)) {
                                        perguntas.addAll(gerarPerguntasSaudeIniciante());
                                } else if ("Intermediário".equals(nivel)) {
                                        perguntas.addAll(gerarPerguntasSaudeIntermediario());
                                } else {
                                        perguntas.addAll(gerarPerguntasSaudeAvancado());
                                }
                                break;
                        case "educacao":
                                if ("Iniciante".equals(nivel)) {
                                        perguntas.addAll(gerarPerguntasEducacaoIniciante());
                                } else if ("Intermediário".equals(nivel)) {
                                        perguntas.addAll(gerarPerguntasEducacaoIntermediario());
                                } else {
                                        perguntas.addAll(gerarPerguntasEducacaoAvancado());
                                }
                                break;
                        default:
                                // Perguntas genéricas para áreas não especificadas (5 perguntas)
                                for (int i = 1; i <= 5; i++) {
                                        perguntas.add(criarPergunta(
                                                        "Pergunta " + i + " sobre " + obterNomeArea(area) + " (" + nivel
                                                                        + ")?",
                                                        Arrays.asList("Opção A", "Opção B", "Opção C", "Opção D"),
                                                        0, "Explicação da resposta"));
                                }
                                break;
                }

                // Garantir que temos exatamente 5 perguntas
                while (perguntas.size() < 5) {
                        perguntas.add(criarPergunta(
                                        "Pergunta adicional sobre " + obterNomeArea(area) + " (" + nivel + ")?",
                                        Arrays.asList("Opção A", "Opção B", "Opção C", "Opção D"),
                                        0, "Explicação da resposta"));
                }

                return perguntas.subList(0, Math.min(5, perguntas.size()));
        }

        // Métodos para gerar perguntas de IA por nível (5 perguntas)
        private List<PerguntaDesafio> gerarPerguntasIAIniciante() {
                return Arrays.asList(
                                criarPergunta("O que significa IA?",
                                                Arrays.asList("Internet Artificial", "Inteligência Artificial",
                                                                "Informação Automatizada", "Indústria Automobilística"),
                                                1, "IA significa Inteligência Artificial"),
                                criarPergunta("Qual técnica de IA aprende com exemplos?",
                                                Arrays.asList("Machine Learning", "Processamento de Linguagem Natural",
                                                                "Visão Computacional", "Robótica"),
                                                0, "Machine Learning aprende com exemplos"),
                                criarPergunta("O que é uma rede neural?",
                                                Arrays.asList("Sistema inspirado no cérebro humano",
                                                                "Tipo de banco de dados", "Linguagem de programação",
                                                                "Ferramenta de design"),
                                                0, "Rede neural é inspirada no cérebro"),
                                criarPergunta("O que significa NLP?",
                                                Arrays.asList("Natural Language Processing",
                                                                "Neural Learning Process",
                                                                "Network Learning Protocol",
                                                                "Next Level Programming"),
                                                0, "NLP significa Processamento de Linguagem Natural"),
                                criarPergunta("O que é aprendizado supervisionado?",
                                                Arrays.asList("Aprender com dados rotulados",
                                                                "Aprender sem dados", "Aprender apenas teoria",
                                                                "Aprender sem supervisão"),
                                                0, "Aprendizado supervisionado usa dados rotulados"));
        }

        private List<PerguntaDesafio> gerarPerguntasIAIntermediario() {
                return Arrays.asList(
                                criarPergunta("O que é Deep Learning?",
                                                Arrays.asList("Aprendizado profundo usando redes neurais",
                                                                "Aprendizado raso", "Aprendizado manual",
                                                                "Aprendizado sem dados"),
                                                0, "Deep Learning usa redes neurais profundas"),
                                criarPergunta("O que é overfitting?",
                                                Arrays.asList("Modelo muito específico aos dados de treino",
                                                                "Modelo muito genérico", "Modelo perfeito",
                                                                "Modelo sem dados"),
                                                0, "Overfitting é quando o modelo memoriza os dados"),
                                criarPergunta("O que é reinforcement learning?",
                                                Arrays.asList("Aprendizado por reforço",
                                                                "Aprendizado sem reforço",
                                                                "Aprendizado manual",
                                                                "Aprendizado automático"),
                                                0, "Reinforcement learning aprende por tentativa e erro"),
                                criarPergunta("O que é transfer learning?",
                                                Arrays.asList("Reutilizar conhecimento de um modelo treinado",
                                                                "Transferir dados", "Transferir código",
                                                                "Transferir arquivos"),
                                                0, "Transfer learning reutiliza conhecimento"),
                                criarPergunta("O que é feature engineering?",
                                                Arrays.asList("Criar variáveis relevantes para o modelo",
                                                                "Engenharia de software",
                                                                "Design de features",
                                                                "Análise de dados"),
                                                0, "Feature engineering cria variáveis úteis"));
        }

        private List<PerguntaDesafio> gerarPerguntasIAAvancado() {
                return Arrays.asList(
                                criarPergunta("O que é attention mechanism?",
                                                Arrays.asList("Mecanismo que foca em partes relevantes",
                                                                "Mecanismo de atenção",
                                                                "Mecanismo de memória",
                                                                "Mecanismo de processamento"),
                                                0, "Attention mechanism foca em partes relevantes"),
                                criarPergunta("O que é transformer architecture?",
                                                Arrays.asList("Arquitetura baseada em attention",
                                                                "Arquitetura de rede neural",
                                                                "Arquitetura de banco de dados",
                                                                "Arquitetura de software"),
                                                0, "Transformer usa attention mechanism"),
                                criarPergunta("O que é GAN (Generative Adversarial Network)?",
                                                Arrays.asList("Rede que gera dados através de competição",
                                                                "Rede generativa", "Rede adversarial",
                                                                "Rede competitiva"),
                                                0, "GAN gera dados através de competição"),
                                criarPergunta("O que é gradient descent?",
                                                Arrays.asList("Algoritmo de otimização",
                                                                "Algoritmo de classificação",
                                                                "Algoritmo de agrupamento",
                                                                "Algoritmo de busca"),
                                                0, "Gradient descent otimiza modelos"),
                                criarPergunta("O que é backpropagation?",
                                                Arrays.asList("Algoritmo de treinamento de redes neurais",
                                                                "Algoritmo de classificação",
                                                                "Algoritmo de agrupamento",
                                                                "Algoritmo de busca"),
                                                0, "Backpropagation treina redes neurais"));
        }

        // Métodos para gerar perguntas de Dados por nível (5 perguntas)
        private List<PerguntaDesafio> gerarPerguntasDadosIniciante() {
                return Arrays.asList(
                                criarPergunta("O que é um DataFrame?",
                                                Arrays.asList("Estrutura de dados bidimensional",
                                                                "Banco de dados", "Arquivo texto",
                                                                "Linguagem de programação"),
                                                0, "DataFrame é uma estrutura bidimensional"),
                                criarPergunta("Qual biblioteca Python é mais usada para análise de dados?",
                                                Arrays.asList("Pandas", "NumPy", "Matplotlib",
                                                                "Scikit-learn"),
                                                0, "Pandas é a biblioteca mais popular"),
                                criarPergunta("O que significa ETL?",
                                                Arrays.asList("Extract, Transform, Load",
                                                                "Enter, Transfer, Leave",
                                                                "Export, Test, Launch",
                                                                "Error, Test, Log"),
                                                0, "ETL significa Extrair, Transformar e Carregar"),
                                criarPergunta("O que é um outlier?",
                                                Arrays.asList("Valor que se destaca dos demais",
                                                                "Valor médio", "Valor mínimo",
                                                                "Valor máximo"),
                                                0, "Outlier é um valor atípico"),
                                criarPergunta("O que é correlação?",
                                                Arrays.asList("Relação entre variáveis",
                                                                "Causa e efeito", "Independência",
                                                                "Aleatoriedade"),
                                                0, "Correlação mede relação entre variáveis"));
        }

        private List<PerguntaDesafio> gerarPerguntasDadosIntermediario() {
                return Arrays.asList(
                                criarPergunta("O que é feature engineering?",
                                                Arrays.asList("Criação de variáveis relevantes",
                                                                "Engenharia de software",
                                                                "Design de features",
                                                                "Análise de dados"),
                                                0, "Feature engineering cria variáveis úteis"),
                                criarPergunta("O que é cross-validation?",
                                                Arrays.asList("Validação cruzada dos dados",
                                                                "Validação única",
                                                                "Validação sem dados",
                                                                "Validação manual"),
                                                0, "Cross-validation valida o modelo"),
                                criarPergunta("O que é um modelo de regressão?",
                                                Arrays.asList("Modelo que prevê valores contínuos",
                                                                "Modelo que classifica",
                                                                "Modelo que agrupa",
                                                                "Modelo que ordena"),
                                                0, "Regressão prevê valores contínuos"),
                                criarPergunta("O que é clustering?",
                                                Arrays.asList("Agrupamento de dados similares",
                                                                "Separação de dados",
                                                                "Ordenação de dados",
                                                                "Classificação de dados"),
                                                0, "Clustering agrupa dados similares"),
                                criarPergunta("O que é data cleaning?",
                                                Arrays.asList("Limpeza e preparação de dados",
                                                                "Exclusão de dados",
                                                                "Criação de dados",
                                                                "Análise de dados"),
                                                0, "Data cleaning prepara os dados"));
        }

        private List<PerguntaDesafio> gerarPerguntasDadosAvancado() {
                return Arrays.asList(
                                criarPergunta("O que é PCA (Principal Component Analysis)?",
                                                Arrays.asList("Redução de dimensionalidade",
                                                                "Análise de componentes",
                                                                "Análise de dados",
                                                                "Análise de features"),
                                                0, "PCA reduz dimensionalidade"),
                                criarPergunta("O que é gradient boosting?",
                                                Arrays.asList("Técnica de ensemble learning",
                                                                "Técnica de classificação",
                                                                "Técnica de agrupamento",
                                                                "Técnica de busca"),
                                                0, "Gradient boosting é ensemble learning"),
                                criarPergunta("O que é random forest?",
                                                Arrays.asList("Ensemble de árvores de decisão",
                                                                "Floresta aleatória",
                                                                "Algoritmo de classificação",
                                                                "Algoritmo de agrupamento"),
                                                0, "Random forest combina árvores"),
                                criarPergunta("O que é SVM?",
                                                Arrays.asList("Support Vector Machine",
                                                                "Simple Vector Machine",
                                                                "Support Value Machine",
                                                                "Simple Value Machine"),
                                                0, "SVM é algoritmo de classificação"),
                                criarPergunta("O que é k-means?",
                                                Arrays.asList("Algoritmo de clustering",
                                                                "Algoritmo de classificação",
                                                                "Algoritmo de regressão",
                                                                "Algoritmo de busca"),
                                                0, "K-means agrupa dados"));
        }

        // Métodos para gerar perguntas de Programação por nível
        private List<PerguntaDesafio> gerarPerguntasProgramacaoIniciante() {
                return selecionarPerguntasPorNivel(gerarPerguntasProgramacao(), "Iniciante");
        }

        private List<PerguntaDesafio> gerarPerguntasProgramacaoIntermediario() {
                return selecionarPerguntasPorNivel(gerarPerguntasProgramacao(), "Intermediário");
        }

        private List<PerguntaDesafio> gerarPerguntasProgramacaoAvancado() {
                return selecionarPerguntasPorNivel(gerarPerguntasProgramacao(), "Avançado");
        }

        // Métodos para gerar perguntas de Sustentabilidade por nível
        private List<PerguntaDesafio> gerarPerguntasSustentabilidadeIniciante() {
                return selecionarPerguntasPorNivel(gerarPerguntasSustentabilidade(), "Iniciante");
        }

        private List<PerguntaDesafio> gerarPerguntasSustentabilidadeIntermediario() {
                return selecionarPerguntasPorNivel(gerarPerguntasSustentabilidade(), "Intermediário");
        }

        private List<PerguntaDesafio> gerarPerguntasSustentabilidadeAvancado() {
                return selecionarPerguntasPorNivel(gerarPerguntasSustentabilidade(), "Avançado");
        }

        // Métodos para gerar perguntas de Design por nível
        private List<PerguntaDesafio> gerarPerguntasDesignIniciante() {
                return selecionarPerguntasPorNivel(gerarPerguntasDesign(), "Iniciante");
        }

        private List<PerguntaDesafio> gerarPerguntasDesignIntermediario() {
                return selecionarPerguntasPorNivel(gerarPerguntasDesign(), "Intermediário");
        }

        private List<PerguntaDesafio> gerarPerguntasDesignAvancado() {
                return selecionarPerguntasPorNivel(gerarPerguntasDesign(), "Avançado");
        }

        // Métodos para gerar perguntas de Marketing por nível
        private List<PerguntaDesafio> gerarPerguntasMarketingIniciante() {
                return selecionarPerguntasPorNivel(gerarPerguntasMarketing(), "Iniciante");
        }

        private List<PerguntaDesafio> gerarPerguntasMarketingIntermediario() {
                return selecionarPerguntasPorNivel(gerarPerguntasMarketing(), "Intermediário");
        }

        private List<PerguntaDesafio> gerarPerguntasMarketingAvancado() {
                return selecionarPerguntasPorNivel(gerarPerguntasMarketing(), "Avançado");
        }

        // Métodos para gerar perguntas de Gestão por nível
        private List<PerguntaDesafio> gerarPerguntasGestaoIniciante() {
                return selecionarPerguntasPorNivel(gerarPerguntasGestao(), "Iniciante");
        }

        private List<PerguntaDesafio> gerarPerguntasGestaoIntermediario() {
                return selecionarPerguntasPorNivel(gerarPerguntasGestao(), "Intermediário");
        }

        private List<PerguntaDesafio> gerarPerguntasGestaoAvancado() {
                return selecionarPerguntasPorNivel(gerarPerguntasGestao(), "Avançado");
        }

        // Métodos para gerar perguntas de Vendas por nível
        private List<PerguntaDesafio> gerarPerguntasVendasIniciante() {
                return selecionarPerguntasPorNivel(gerarPerguntasVendas(), "Iniciante");
        }

        private List<PerguntaDesafio> gerarPerguntasVendasIntermediario() {
                return selecionarPerguntasPorNivel(gerarPerguntasVendas(), "Intermediário");
        }

        private List<PerguntaDesafio> gerarPerguntasVendasAvancado() {
                return selecionarPerguntasPorNivel(gerarPerguntasVendas(), "Avançado");
        }

        // Métodos para gerar perguntas de RH por nível
        private List<PerguntaDesafio> gerarPerguntasRHIniciante() {
                return selecionarPerguntasPorNivel(gerarPerguntasRH(), "Iniciante");
        }

        private List<PerguntaDesafio> gerarPerguntasRHIntermediario() {
                return selecionarPerguntasPorNivel(gerarPerguntasRH(), "Intermediário");
        }

        private List<PerguntaDesafio> gerarPerguntasRHAvancado() {
                return selecionarPerguntasPorNivel(gerarPerguntasRH(), "Avançado");
        }

        // Métodos para gerar perguntas de Finanças por nível
        private List<PerguntaDesafio> gerarPerguntasFinancasIniciante() {
                return selecionarPerguntasPorNivel(gerarPerguntasFinancas(), "Iniciante");
        }

        private List<PerguntaDesafio> gerarPerguntasFinancasIntermediario() {
                return selecionarPerguntasPorNivel(gerarPerguntasFinancas(), "Intermediário");
        }

        private List<PerguntaDesafio> gerarPerguntasFinancasAvancado() {
                return selecionarPerguntasPorNivel(gerarPerguntasFinancas(), "Avançado");
        }

        // Métodos para gerar perguntas de Saúde por nível
        private List<PerguntaDesafio> gerarPerguntasSaudeIniciante() {
                return selecionarPerguntasPorNivel(gerarPerguntasSaude(), "Iniciante");
        }

        private List<PerguntaDesafio> gerarPerguntasSaudeIntermediario() {
                return selecionarPerguntasPorNivel(gerarPerguntasSaude(), "Intermediário");
        }

        private List<PerguntaDesafio> gerarPerguntasSaudeAvancado() {
                return selecionarPerguntasPorNivel(gerarPerguntasSaude(), "Avançado");
        }

        // Métodos para gerar perguntas de Educação por nível
        private List<PerguntaDesafio> gerarPerguntasEducacaoIniciante() {
                return selecionarPerguntasPorNivel(gerarPerguntasEducacao(), "Iniciante");
        }

        private List<PerguntaDesafio> gerarPerguntasEducacaoIntermediario() {
                return selecionarPerguntasPorNivel(gerarPerguntasEducacao(), "Intermediário");
        }

        private List<PerguntaDesafio> gerarPerguntasEducacaoAvancado() {
                return selecionarPerguntasPorNivel(gerarPerguntasEducacao(), "Avançado");
        }

        private List<PerguntaDesafio> selecionarPerguntasPorNivel(List<PerguntaDesafio> banco, String nivel) {
                int inicio = 0;
                if ("Intermediário".equals(nivel)) {
                        inicio = 5;
                } else if ("Avançado".equals(nivel)) {
                        inicio = 10;
                }
                int fim = Math.min(banco.size(), inicio + 5);
                if (fim - inicio < 5) {
                        inicio = Math.max(0, banco.size() - 5);
                        fim = banco.size();
                }
                return new ArrayList<>(banco.subList(inicio, fim));
        }

        private List<PerguntaDesafio> gerarPerguntasProgramacao() {
                return Arrays.asList(
                                criarPergunta("O que é uma variável?",
                                                Arrays.asList("Um espaço na memória que armazena dados",
                                                                "Um tipo de função", "Um erro de código",
                                                                "Uma linguagem"),
                                                0, "Variável é um espaço na memória para dados"),
                                criarPergunta("O que significa OOP?",
                                                Arrays.asList("Programação Orientada a Objetos",
                                                                "Operação de Ordem Primária",
                                                                "Objeto de Ordem Pública",
                                                                "Organização de Objetos Públicos"),
                                                0, "OOP significa Programação Orientada a Objetos"),
                                criarPergunta("Qual estrutura repete código enquanto uma condição é verdadeira?",
                                                Arrays.asList("Loop", "Condicional", "Função", "Variável"),
                                                0, "Loop repete código enquanto a condição é verdadeira"),
                                criarPergunta("O que é um algoritmo?",
                                                Arrays.asList("Sequência de passos para resolver um problema",
                                                                "Tipo de dado", "Linguagem de programação",
                                                                "Ferramenta de debug"),
                                                0, "Algoritmo é uma sequência de passos lógicos"),
                                criarPergunta("O que significa API?",
                                                Arrays.asList("Application Programming Interface",
                                                                "Advanced Programming Interface",
                                                                "Automated Programming Interface",
                                                                "Applied Programming Interface"),
                                                0, "API significa Interface de Programação de Aplicações"),
                                criarPergunta("O que é Git?",
                                                Arrays.asList("Sistema de controle de versão",
                                                                "Linguagem de programação",
                                                                "Banco de dados", "Framework"),
                                                0, "Git é um sistema de controle de versão"),
                                criarPergunta("O que é um framework?",
                                                Arrays.asList("Conjunto de ferramentas e bibliotecas",
                                                                "Linguagem de programação",
                                                                "Banco de dados", "Editor de código"),
                                                0, "Framework fornece estrutura para desenvolvimento"),
                                criarPergunta("O que significa REST?",
                                                Arrays.asList("Representational State Transfer",
                                                                "Remote State Transfer",
                                                                "Resource State Transfer",
                                                                "Request State Transfer"),
                                                0, "REST é um estilo arquitetural para APIs"),
                                criarPergunta("O que é um banco de dados relacional?",
                                                Arrays.asList("Banco que organiza dados em tabelas relacionadas",
                                                                "Banco sem estrutura",
                                                                "Banco apenas em memória",
                                                                "Banco sem relacionamentos"),
                                                0, "Banco relacional usa tabelas e relacionamentos"),
                                criarPergunta("O que é debug?",
                                                Arrays.asList("Processo de encontrar e corrigir erros",
                                                                "Criar código", "Testar aplicação",
                                                                "Documentar código"),
                                                0, "Debug é o processo de depuração de código"),
                                criarPergunta("O que é polimorfismo em POO?",
                                                Arrays.asList("Capacidade de um método ter múltiplas implementações",
                                                                "Capacidade de herdar múltiplas classes",
                                                                "Capacidade de compilar automaticamente",
                                                                "Capacidade de armazenar dados"),
                                                0,
                                                "Polimorfismo permite comportamentos diferentes para a mesma interface"),
                                criarPergunta("Qual o objetivo dos testes unitários?",
                                                Arrays.asList("Validar pequenas partes do código de forma isolada",
                                                                "Testar somente a interface",
                                                                "Testar apenas o banco de dados",
                                                                "Testar o servidor"),
                                                0, "Testes unitários garantem o comportamento de unidades isoladas"),
                                criarPergunta("O que representa o princípio SOLID 'Single Responsibility'?",
                                                Arrays.asList("Cada classe deve ter apenas um motivo para mudar",
                                                                "Classes devem ser únicas no projeto",
                                                                "Cada método deve ser privado",
                                                                "Classes não podem herdar"),
                                                0, "Single Responsibility define responsabilidades únicas"),
                                criarPergunta("Qual benefício de arquiteturas de microsserviços?",
                                                Arrays.asList("Escalabilidade independente dos componentes",
                                                                "Código monolítico mais simples",
                                                                "Menos pipelines de deploy",
                                                                "Dependência única de banco de dados"),
                                                0, "Microsserviços permitem escalar partes específicas"),
                                criarPergunta("Qual prática reduz vulnerabilidades listadas no OWASP Top 10?",
                                                Arrays.asList("Validação de entrada e uso de prepared statements",
                                                                "Desabilitar logs",
                                                                "Remover autenticação",
                                                                "Compartilhar credenciais"),
                                                0, "Validação e prepared statements mitigam injeções"));
        }

        private List<PerguntaDesafio> gerarPerguntasSustentabilidade() {
                return Arrays.asList(
                                criarPergunta("O que significa ESG?",
                                                Arrays.asList("Environmental, Social, Governance",
                                                                "Energy, Solar, Green",
                                                                "Eco, Sustainable, Green",
                                                                "Earth, Save, Global"),
                                                0, "ESG significa Ambiental, Social e Governança"),
                                criarPergunta("Qual é a principal fonte de energia renovável?",
                                                Arrays.asList("Solar", "Petróleo", "Carvão", "Gás Natural"),
                                                0, "Energia solar é uma das principais renováveis"),
                                criarPergunta("O que é economia circular?",
                                                Arrays.asList("Sistema que elimina desperdício e reutiliza recursos",
                                                                "Economia baseada em dinheiro",
                                                                "Sistema linear de produção",
                                                                "Economia sem reciclagem"),
                                                0, "Economia circular reutiliza e elimina desperdício"),
                                criarPergunta("O que é pegada de carbono?",
                                                Arrays.asList("Medida de emissões de gases de efeito estufa",
                                                                "Medida de consumo de água",
                                                                "Medida de resíduos",
                                                                "Medida de energia"),
                                                0, "Pegada de carbono mede impacto ambiental"),
                                criarPergunta("O que significa sustentabilidade?",
                                                Arrays.asList("Atender necessidades sem comprometer o futuro",
                                                                "Usar todos os recursos disponíveis",
                                                                "Crescer sem limites",
                                                                "Produzir o máximo possível"),
                                                0, "Sustentabilidade preserva recursos para o futuro"),
                                criarPergunta("O que é reciclagem?",
                                                Arrays.asList("Processo de transformar resíduos em novos produtos",
                                                                "Descartar resíduos",
                                                                "Queimar resíduos",
                                                                "Enterrar resíduos"),
                                                0, "Reciclagem transforma resíduos em novos materiais"),
                                criarPergunta("O que são energias renováveis?",
                                                Arrays.asList("Energias que se regeneram naturalmente",
                                                                "Energias que se esgotam",
                                                                "Energias fósseis",
                                                                "Energias não renováveis"),
                                                0, "Energias renováveis se regeneram naturalmente"),
                                criarPergunta("O que é desenvolvimento sustentável?",
                                                Arrays.asList("Desenvolvimento que preserva o meio ambiente",
                                                                "Desenvolvimento sem preocupação ambiental",
                                                                "Desenvolvimento apenas econômico",
                                                                "Desenvolvimento sem planejamento"),
                                                0, "Desenvolvimento sustentável equilibra economia e ambiente"),
                                criarPergunta("O que significa carbono neutro?",
                                                Arrays.asList("Compensar todas as emissões de carbono",
                                                                "Emitir mais carbono",
                                                                "Ignorar emissões",
                                                                "Aumentar emissões"),
                                                0, "Carbono neutro significa compensar todas as emissões"),
                                criarPergunta("O que é biodiversidade?",
                                                Arrays.asList("Variedade de vida na Terra",
                                                                "Apenas plantas",
                                                                "Apenas animais",
                                                                "Apenas humanos"),
                                                0, "Biodiversidade é a variedade de formas de vida"),
                                criarPergunta("Qual framework é usado em relatórios de sustentabilidade?",
                                                Arrays.asList("GRI Standards", "SOX", "PMI", "ITIL"),
                                                0, "GRI é referência global para relatórios ESG"),
                                criarPergunta("O que são créditos de carbono?",
                                                Arrays.asList("Certificados que representam redução de emissões",
                                                                "Impostos sobre CO2", "Subvenções governamentais",
                                                                "Licenças de prospecção"),
                                                0, "Créditos de carbono compensam emissões excedentes"),
                                criarPergunta("O que é análise de materialidade ESG?",
                                                Arrays.asList("Identificar temas mais relevantes para o negócio e sociedade",
                                                                "Analisar apenas finanças",
                                                                "Avaliar somente marketing",
                                                                "Mensurar apenas produção"),
                                                0, "Materialidade prioriza temas ESG críticos"),
                                criarPergunta("Qual prática reforça governança climática?",
                                                Arrays.asList("Metas alinhadas ao Science Based Targets",
                                                                "Aumentar combustíveis fósseis",
                                                                "Ignorar stakeholders",
                                                                "Reduzir auditorias"),
                                                0, "Metas SBTi conectam estratégia ao clima"),
                                criarPergunta("O que é economia regenerativa?",
                                                Arrays.asList("Modelo que restaura ecossistemas e comunidades",
                                                                "Modelo linear tradicional",
                                                                "Apenas reciclagem",
                                                                "Somente lucro a curto prazo"),
                                                0, "Economia regenerativa busca impacto positivo líquido"));
        }

        private List<PerguntaDesafio> gerarPerguntasDesign() {
                return Arrays.asList(
                                criarPergunta("O que significa UX?",
                                                Arrays.asList("User Experience", "User Example", "User Export",
                                                                "User Extension"),
                                                0, "UX significa Experiência do Usuário"),
                                criarPergunta("O que é um wireframe?",
                                                Arrays.asList("Esboço estrutural de uma interface",
                                                                "Código de programação", "Imagem finalizada",
                                                                "Paleta de cores"),
                                                0, "Wireframe é um esboço estrutural"),
                                criarPergunta("Qual princípio de design foca na organização visual?",
                                                Arrays.asList("Hierarquia", "Cor", "Fonte", "Espaçamento"),
                                                0, "Hierarquia organiza elementos visualmente"),
                                criarPergunta("O que é UI?",
                                                Arrays.asList("User Interface", "User Interaction",
                                                                "User Integration", "User Information"),
                                                0, "UI significa Interface do Usuário"),
                                criarPergunta("O que é design thinking?",
                                                Arrays.asList("Metodologia centrada no usuário",
                                                                "Apenas desenhar",
                                                                "Apenas programar",
                                                                "Apenas testar"),
                                                0, "Design thinking é centrado no usuário"),
                                criarPergunta("O que é acessibilidade em design?",
                                                Arrays.asList("Design que todos podem usar",
                                                                "Design apenas para alguns",
                                                                "Design complexo",
                                                                "Design sem padrões"),
                                                0, "Acessibilidade torna o design usável para todos"),
                                criarPergunta("O que é prototipagem?",
                                                Arrays.asList("Criar versão inicial para testar",
                                                                "Criar versão final",
                                                                "Criar apenas desenhos",
                                                                "Criar sem testar"),
                                                0, "Prototipagem cria versões testáveis"),
                                criarPergunta("O que é contraste em design?",
                                                Arrays.asList("Diferença visual entre elementos",
                                                                "Similaridade entre elementos",
                                                                "Uniformidade",
                                                                "Monocromia"),
                                                0, "Contraste cria diferenciação visual"),
                                criarPergunta("O que é tipografia?",
                                                Arrays.asList("Arte de escolher e usar fontes",
                                                                "Apenas escrever",
                                                                "Apenas desenhar",
                                                                "Apenas colorir"),
                                                0, "Tipografia é a arte das fontes"),
                                criarPergunta("O que é design responsivo?",
                                                Arrays.asList("Design que se adapta a diferentes telas",
                                                                "Design fixo",
                                                                "Design apenas para desktop",
                                                                "Design apenas para mobile"),
                                                0, "Design responsivo se adapta a qualquer tela"),
                                criarPergunta("Para que serve um grid em design?",
                                                Arrays.asList("Organizar elementos visualmente de forma consistente",
                                                                "Apenas criar cores",
                                                                "Apenas definir fontes",
                                                                "Apenas exportar telas"),
                                                0, "Grids ajudam a manter ritmo visual"),
                                criarPergunta("O que são design tokens?",
                                                Arrays.asList("Variáveis que centralizam estilos do design system",
                                                                "Plugins de prototipagem",
                                                                "Templates prontos",
                                                                "Bibliotecas de ícones"),
                                                0, "Tokens conectam design e código"),
                                criarPergunta("Qual o objetivo dos testes de usabilidade remotos?",
                                                Arrays.asList("Validar protótipos com usuários em diferentes contextos",
                                                                "Substituir pesquisas qualitativas",
                                                                "Apenas validar layout responsivo",
                                                                "Somente medir performance do app"),
                                                0, "Testes remotos ampliam feedbacks"),
                                criarPergunta("O que caracteriza experiências multimodais?",
                                                Arrays.asList("Interfaces que combinam voz, toque, gesto ou AR",
                                                                "Apenas interfaces móveis",
                                                                "Somente design 2D",
                                                                "Somente texto"),
                                                0, "Experiências multimodais envolvem múltiplos canais sensoriais"),
                                criarPergunta("Qual métrica avalia satisfação do usuário?",
                                                Arrays.asList("NPS (Net Promoter Score)", "FPS", "CPC", "MTBF"),
                                                0, "NPS mede lealdade e satisfação"));
        }

        private List<PerguntaDesafio> gerarPerguntasMarketing() {
                return Arrays.asList(
                                criarPergunta("O que significa SEO?",
                                                Arrays.asList("Search Engine Optimization",
                                                                "Social Engine Optimization",
                                                                "Simple Engine Optimization",
                                                                "Smart Engine Optimization"),
                                                0, "SEO significa Otimização para Mecanismos de Busca"),
                                criarPergunta("O que é CTR?",
                                                Arrays.asList("Click-Through Rate", "Click-To-Read",
                                                                "Click-To-Rate", "Click-To-Reply"),
                                                0, "CTR é a taxa de cliques"),
                                criarPergunta("Qual rede social é melhor para B2B?",
                                                Arrays.asList("LinkedIn", "Instagram", "TikTok", "Snapchat"),
                                                0, "LinkedIn é a principal rede B2B"),
                                criarPergunta("O que é marketing de conteúdo?",
                                                Arrays.asList("Criar conteúdo relevante para atrair público",
                                                                "Apenas vender",
                                                                "Apenas anunciar",
                                                                "Apenas postar"),
                                                0, "Marketing de conteúdo cria valor para o público"),
                                criarPergunta("O que é persona?",
                                                Arrays.asList("Representação do cliente ideal",
                                                                "Apenas um cliente",
                                                                "Apenas um produto",
                                                                "Apenas uma marca"),
                                                0, "Persona representa o cliente ideal"),
                                criarPergunta("O que significa ROI em marketing?",
                                                Arrays.asList("Retorno sobre investimento",
                                                                "Rate of Interest",
                                                                "Return of Income",
                                                                "Rate of Investment"),
                                                0, "ROI mede retorno sobre investimento"),
                                criarPergunta("O que é funil de marketing?",
                                                Arrays.asList("Jornada do cliente da descoberta à compra",
                                                                "Apenas vendas",
                                                                "Apenas publicidade",
                                                                "Apenas conteúdo"),
                                                0, "Funil representa a jornada do cliente"),
                                criarPergunta("O que é remarketing?",
                                                Arrays.asList("Reconquistar clientes que visitaram o site",
                                                                "Marketing novo",
                                                                "Marketing inicial",
                                                                "Marketing sem público"),
                                                0, "Remarketing foca em quem já conhece a marca"),
                                criarPergunta("O que é inbound marketing?",
                                                Arrays.asList("Atrair clientes com conteúdo relevante",
                                                                "Interromper com publicidade",
                                                                "Vender diretamente",
                                                                "Spam"),
                                                0, "Inbound marketing atrai com conteúdo"),
                                criarPergunta("O que é análise de métricas?",
                                                Arrays.asList("Medir e analisar resultados de campanhas",
                                                                "Apenas criar campanhas",
                                                                "Apenas gastar",
                                                                "Apenas postar"),
                                                0, "Análise de métricas mede resultados"),
                                criarPergunta("O que significa LTV?",
                                                Arrays.asList("Lifetime Value", "Long Term View", "Lead Time Value",
                                                                "Limited Time Value"),
                                                0, "LTV mede o valor total que um cliente gera"),
                                criarPergunta("Para que serve a automação de marketing?",
                                                Arrays.asList("Orquestrar comunicações personalizadas em escala",
                                                                "Encerrar campanhas",
                                                                "Excluir leads",
                                                                "Aumentar custo por clique"),
                                                0, "Automação personaliza jornadas em grande volume"),
                                criarPergunta("Qual a finalidade de testes A/B?",
                                                Arrays.asList("Comparar duas variações e escolher a mais eficiente",
                                                                "Duplicar campanhas",
                                                                "Reduzir orçamento",
                                                                "Pausar anúncios"),
                                                0, "Testes A/B validam hipóteses com dados"),
                                criarPergunta("O que é modelo de atribuição?",
                                                Arrays.asList("Distribuir crédito entre pontos de contato",
                                                                "Apenas rastrear cookies",
                                                                "Somente medir cliques",
                                                                "Somente medir impressões"),
                                                0, "Modelos de atribuição indicam canais que geram conversão"),
                                criarPergunta("O que é social listening?",
                                                Arrays.asList("Monitorar e analisar conversas sobre a marca",
                                                                "Publicar conteúdo orgânico",
                                                                "Apenas impulsionar posts",
                                                                "Somente responder comentários"),
                                                0, "Social listening capta insights das redes sociais"));
        }

        private List<PerguntaDesafio> gerarPerguntasGestao() {
                return Arrays.asList(
                                criarPergunta("O que é Scrum?",
                                                Arrays.asList("Framework ágil de gestão de projetos",
                                                                "Linguagem de programação", "Tipo de reunião",
                                                                "Ferramenta de design"),
                                                0, "Scrum é um framework ágil"),
                                criarPergunta("Qual é o papel do Product Owner?",
                                                Arrays.asList("Definir prioridades do produto",
                                                                "Desenvolver código", "Testar software",
                                                                "Designer"),
                                                0, "Product Owner define prioridades"),
                                criarPergunta("O que significa MVP?",
                                                Arrays.asList("Minimum Viable Product", "Most Valuable Player",
                                                                "Maximum Value Product",
                                                                "Minimum Value Process"),
                                                0, "MVP significa Produto Mínimo Viável"),
                                criarPergunta("O que é gestão de projetos?",
                                                Arrays.asList("Aplicar conhecimento para atingir objetivos",
                                                                "Apenas planejar",
                                                                "Apenas executar",
                                                                "Apenas controlar"),
                                                0, "Gestão de projetos aplica conhecimento e habilidades"),
                                criarPergunta("O que é Kanban?",
                                                Arrays.asList("Sistema visual de gestão de trabalho",
                                                                "Linguagem de programação",
                                                                "Banco de dados",
                                                                "Framework web"),
                                                0, "Kanban é um sistema visual de gestão"),
                                criarPergunta("O que é liderança?",
                                                Arrays.asList("Influenciar pessoas para atingir objetivos",
                                                                "Apenas comandar",
                                                                "Apenas controlar",
                                                                "Apenas gerenciar"),
                                                0, "Liderança influencia e motiva pessoas"),
                                criarPergunta("O que é gestão de mudanças?",
                                                Arrays.asList("Processo de transição organizacional",
                                                                "Apenas mudar",
                                                                "Apenas manter",
                                                                "Apenas resistir"),
                                                0, "Gestão de mudanças facilita transições"),
                                criarPergunta("O que é OKR?",
                                                Arrays.asList("Objectives and Key Results",
                                                                "Only Key Results",
                                                                "Objectives Key Results",
                                                                "Only Known Results"),
                                                0, "OKR significa Objetivos e Resultados-Chave"),
                                criarPergunta("O que é gestão de equipes?",
                                                Arrays.asList("Coordenar e motivar equipes para resultados",
                                                                "Apenas controlar",
                                                                "Apenas delegar",
                                                                "Apenas supervisionar"),
                                                0, "Gestão de equipes coordena e motiva"),
                                criarPergunta("O que é gestão de riscos?",
                                                Arrays.asList("Identificar e mitigar riscos do projeto",
                                                                "Ignorar riscos",
                                                                "Apenas aceitar riscos",
                                                                "Apenas documentar"),
                                                0, "Gestão de riscos identifica e mitiga problemas"),
                                criarPergunta("Qual o propósito de um PMO?",
                                                Arrays.asList("Padronizar e apoiar a gestão de projetos",
                                                                "Executar apenas projetos de TI",
                                                                "Conceder financiamentos",
                                                                "Somente contratar fornecedores"),
                                                0, "O PMO define processos e suporte"),
                                criarPergunta("O que são KPIs?",
                                                Arrays.asList("Indicadores-chave de desempenho",
                                                                "Planos de investimento",
                                                                "Ciclos de projeto", "Custos indiretos"),
                                                0, "KPIs mensuram desempenho estratégico"),
                                criarPergunta("Como o compliance contribui para a gestão?",
                                                Arrays.asList("Garante aderência a normas e reduz riscos",
                                                                "Aumenta informalidade",
                                                                "Dispensa auditorias",
                                                                "Foca apenas em vendas"),
                                                0, "Compliance protege a organização"),
                                criarPergunta("O que é transformação digital?",
                                                Arrays.asList("Uso estratégico de tecnologia para gerar novos modelos",
                                                                "Trocar computadores antigos",
                                                                "Criar apenas um app",
                                                                "Digitalizar documentos"),
                                                0, "Transformação digital reinventa processos e produtos"),
                                criarPergunta("Qual benefício de uma cultura data-driven?",
                                                Arrays.asList("Decisões baseadas em evidências e métricas",
                                                                "Gestão puramente intuitiva",
                                                                "Menos governança",
                                                                "Menos colaboração"),
                                                0, "Cultura data-driven utiliza dados na estratégia"));
        }

        private List<PerguntaDesafio> gerarPerguntasVendas() {
                return Arrays.asList(
                                criarPergunta("O que é um funil de vendas?",
                                                Arrays.asList("Processo que guia cliente da descoberta à compra",
                                                                "Ferramenta de marketing", "Tipo de produto",
                                                                "Método de pagamento"),
                                                0, "Funil de vendas guia o cliente no processo"),
                                criarPergunta("Qual é a primeira etapa do processo de vendas?",
                                                Arrays.asList("Prospecção", "Apresentação", "Fechamento",
                                                                "Pós-venda"),
                                                0, "Prospecção é a primeira etapa"),
                                criarPergunta("O que significa BANT?",
                                                Arrays.asList("Budget, Authority, Need, Timeline",
                                                                "Buy, Ask, Negotiate, Trade",
                                                                "Best, Average, New, Total",
                                                                "Business, Action, Network, Team"),
                                                0, "BANT qualifica leads"),
                                criarPergunta("O que é objeção em vendas?",
                                                Arrays.asList("Resistência do cliente à proposta",
                                                                "Apenas aceitação",
                                                                "Apenas interesse",
                                                                "Apenas desinteresse"),
                                                0, "Objeção é resistência que precisa ser tratada"),
                                criarPergunta("O que é fechamento de venda?",
                                                Arrays.asList("Conseguir o compromisso de compra",
                                                                "Apenas apresentar",
                                                                "Apenas prospectar",
                                                                "Apenas qualificar"),
                                                0, "Fechamento é obter o compromisso"),
                                criarPergunta("O que é rapport?",
                                                Arrays.asList("Criar conexão e confiança com o cliente",
                                                                "Apenas vender",
                                                                "Apenas apresentar",
                                                                "Apenas pressionar"),
                                                0, "Rapport cria conexão e confiança"),
                                criarPergunta("O que é upselling?",
                                                Arrays.asList("Vender produto mais caro ou adicional",
                                                                "Vender produto mais barato",
                                                                "Não vender",
                                                                "Apenas informar"),
                                                0, "Upselling aumenta o valor da venda"),
                                criarPergunta("O que é cross-selling?",
                                                Arrays.asList("Vender produtos complementares",
                                                                "Vender apenas um produto",
                                                                "Não vender",
                                                                "Apenas informar"),
                                                0, "Cross-selling vende produtos relacionados"),
                                criarPergunta("O que é negociação?",
                                                Arrays.asList("Processo de chegar a um acordo",
                                                                "Apenas aceitar",
                                                                "Apenas recusar",
                                                                "Apenas discutir"),
                                                0, "Negociação busca acordo mutuamente benéfico"),
                                criarPergunta("O que é pós-venda?",
                                                Arrays.asList("Atendimento após a venda",
                                                                "Apenas vender",
                                                                "Apenas prospectar",
                                                                "Apenas fechar"),
                                                0, "Pós-venda mantém relacionamento com cliente"),
                                criarPergunta("Qual metodologia explora Situação, Problema, Implicação e Necessidade?",
                                                Arrays.asList("SPIN Selling", "SCRUM", "PMBOK", "BANT"),
                                                0, "SPIN Selling guia a descoberta consultiva"),
                                criarPergunta("O que caracteriza Account-Based Selling?",
                                                Arrays.asList("Foco em contas estratégicas com abordagens personalizadas",
                                                                "Venda massiva para consumidores",
                                                                "Apenas marketing digital",
                                                                "Somente vendas internas"),
                                                0, "ABS concentra esforços em contas chave"),
                                criarPergunta("Qual elemento fortalece uma proposta de valor?",
                                                Arrays.asList("Clareza do problema resolvido e diferenciais",
                                                                "Listar apenas preço",
                                                                "Enviar propostas genéricas",
                                                                "Evitar métricas"),
                                                0, "Proposta de valor conecta dores e benefícios"),
                                criarPergunta("O que é sales forecasting?",
                                                Arrays.asList("Previsão de vendas com base em dados e pipeline",
                                                                "Apenas analisar histórico",
                                                                "Somente projetar custos",
                                                                "Criar metas sem dados"),
                                                0, "Forecasting antecipa resultados e orienta decisões"),
                                criarPergunta("Como Customer Success apoia vendas?",
                                                Arrays.asList("Garante adoção, reduz churn e gera upsell",
                                                                "Somente responde chamados técnicos",
                                                                "Define preços",
                                                                "Trata apenas marketing"),
                                                0, "CS aumenta o valor de longo prazo dos clientes"));
        }

        private List<PerguntaDesafio> gerarPerguntasRH() {
                return Arrays.asList(
                                criarPergunta("O que significa onboarding?",
                                                Arrays.asList("Processo de integração de novos funcionários",
                                                                "Processo de demissão", "Processo de avaliação",
                                                                "Processo de treinamento"),
                                                0, "Onboarding integra novos funcionários"),
                                criarPergunta("O que é turnover?",
                                                Arrays.asList("Taxa de rotatividade de funcionários",
                                                                "Taxa de contratação", "Taxa de promoção",
                                                                "Taxa de treinamento"),
                                                0, "Turnover é a rotatividade"),
                                criarPergunta("Qual é o objetivo do feedback 360?",
                                                Arrays.asList("Avaliação de múltiplas fontes",
                                                                "Avaliação única", "Avaliação automática",
                                                                "Avaliação sem feedback"),
                                                0, "Feedback 360 avalia de múltiplas fontes"),
                                criarPergunta("O que é recrutamento?",
                                                Arrays.asList("Processo de atrair e selecionar candidatos",
                                                                "Apenas contratar",
                                                                "Apenas demitir",
                                                                "Apenas treinar"),
                                                0, "Recrutamento atrai e seleciona talentos"),
                                criarPergunta("O que é seleção de pessoal?",
                                                Arrays.asList("Escolher o melhor candidato para a vaga",
                                                                "Apenas recrutar",
                                                                "Apenas contratar",
                                                                "Apenas treinar"),
                                                0, "Seleção escolhe o candidato ideal"),
                                criarPergunta("O que é avaliação de desempenho?",
                                                Arrays.asList("Avaliar o desempenho dos funcionários",
                                                                "Apenas promover",
                                                                "Apenas demitir",
                                                                "Apenas contratar"),
                                                0, "Avaliação mede desempenho e desenvolvimento"),
                                criarPergunta("O que é plano de carreira?",
                                                Arrays.asList("Caminho de desenvolvimento profissional",
                                                                "Apenas promoção",
                                                                "Apenas salário",
                                                                "Apenas benefícios"),
                                                0, "Plano de carreira define trajetória profissional"),
                                criarPergunta("O que é clima organizacional?",
                                                Arrays.asList("Ambiente e percepção dos funcionários",
                                                                "Apenas estrutura física",
                                                                "Apenas salários",
                                                                "Apenas benefícios"),
                                                0, "Clima organizacional reflete o ambiente de trabalho"),
                                criarPergunta("O que é retenção de talentos?",
                                                Arrays.asList("Manter funcionários na organização",
                                                                "Apenas contratar",
                                                                "Apenas demitir",
                                                                "Apenas treinar"),
                                                0, "Retenção mantém talentos na empresa"),
                                criarPergunta("O que é desenvolvimento de pessoas?",
                                                Arrays.asList("Investir no crescimento dos funcionários",
                                                                "Apenas contratar",
                                                                "Apenas demitir",
                                                                "Apenas avaliar"),
                                                0, "Desenvolvimento investe no crescimento"),
                                criarPergunta("Qual o objetivo do People Analytics avançado?",
                                                Arrays.asList("Tomar decisões de RH guiadas por dados e previsões",
                                                                "Substituir líderes",
                                                                "Eliminar entrevistas",
                                                                "Aumentar burocracia"),
                                                0, "People analytics conecta dados a decisões estratégicas"),
                                criarPergunta("O que é EVP?",
                                                Arrays.asList("Employee Value Proposition", "Enterprise Value Process",
                                                                "Engagement Value Plan", "Employer Vision Program"),
                                                0, "EVP comunica o valor de trabalhar na empresa"),
                                criarPergunta("Qual prática fortalece diversidade e inclusão?",
                                                Arrays.asList("Programas com metas, educação e métricas claras",
                                                                "Ignorar recortes",
                                                                "Eliminar feedbacks",
                                                                "Centralizar decisões"),
                                                0, "D&I exige ações estruturadas e mensuráveis"),
                                criarPergunta("O que define a jornada do colaborador?",
                                                Arrays.asList("Experiência completa do profissional com a empresa",
                                                                "Apenas onboarding",
                                                                "Apenas desligamento",
                                                                "Apenas remuneração"),
                                                0, "Jornada engloba todos os pontos de contato"),
                                criarPergunta("O que é futuro do trabalho?",
                                                Arrays.asList("Novos modelos flexíveis, digitais e orientados a propósito",
                                                                "Só trabalho presencial",
                                                                "Apenas terceirização",
                                                                "Apenas remuneração variável"),
                                                0, "Futuro do trabalho integra tecnologia, flexibilidade e cultura"));
        }

        private List<PerguntaDesafio> gerarPerguntasFinancas() {
                return Arrays.asList(
                                criarPergunta("O que é juros compostos?",
                                                Arrays.asList("Juros calculados sobre juros anteriores",
                                                                "Juros simples", "Juros fixos",
                                                                "Juros variáveis"),
                                                0, "Juros compostos acumulam sobre si mesmos"),
                                criarPergunta("O que significa ROI?",
                                                Arrays.asList("Return on Investment", "Rate of Interest",
                                                                "Return of Income", "Rate of Investment"),
                                                0, "ROI significa Retorno sobre Investimento"),
                                criarPergunta("O que é um ativo?",
                                                Arrays.asList("Recurso que gera valor",
                                                                "Obrigação financeira", "Despesa", "Receita"),
                                                0, "Ativo é um recurso que gera valor"),
                                criarPergunta("O que é um passivo?",
                                                Arrays.asList("Obrigação financeira",
                                                                "Recurso que gera valor", "Receita", "Despesa"),
                                                0, "Passivo são obrigações financeiras"),
                                criarPergunta("O que é fluxo de caixa?",
                                                Arrays.asList("Movimentação de entradas e saídas de dinheiro",
                                                                "Apenas entradas",
                                                                "Apenas saídas",
                                                                "Apenas saldo"),
                                                0, "Fluxo de caixa registra movimentações financeiras"),
                                criarPergunta("O que é capital de giro?",
                                                Arrays.asList("Recursos para operação do negócio",
                                                                "Apenas lucro",
                                                                "Apenas investimento",
                                                                "Apenas dívida"),
                                                0, "Capital de giro financia operações"),
                                criarPergunta("O que é DRE?",
                                                Arrays.asList("Demonstração do Resultado do Exercício",
                                                                "Demonstração de Receitas",
                                                                "Demonstração de Despesas",
                                                                "Demonstração de Lucros"),
                                                0, "DRE mostra resultado financeiro"),
                                criarPergunta("O que é margem de lucro?",
                                                Arrays.asList("Percentual de lucro sobre receita",
                                                                "Apenas lucro",
                                                                "Apenas receita",
                                                                "Apenas custo"),
                                                0, "Margem de lucro mede rentabilidade"),
                                criarPergunta("O que é investimento?",
                                                Arrays.asList("Aplicação de recursos para gerar retorno",
                                                                "Apenas gasto",
                                                                "Apenas despesa",
                                                                "Apenas custo"),
                                                0, "Investimento busca retorno futuro"),
                                criarPergunta("O que é planejamento financeiro?",
                                                Arrays.asList("Estratégia para gerenciar recursos",
                                                                "Apenas gastar",
                                                                "Apenas economizar",
                                                                "Apenas investir"),
                                                0, "Planejamento financeiro organiza recursos"),
                                criarPergunta("O que é valuation?",
                                                Arrays.asList("Processo de determinar o valor de uma empresa",
                                                                "Apenas calcular impostos",
                                                                "Apenas controlar estoque",
                                                                "Apenas registrar despesas"),
                                                0, "Valuation utiliza métodos como fluxo de caixa descontado"),
                                criarPergunta("Qual a finalidade de um hedge financeiro?",
                                                Arrays.asList("Proteger contra oscilações de preço ou câmbio",
                                                                "Aumentar riscos",
                                                                "Eliminar controles",
                                                                "Apenas reduzir impostos"),
                                                0, "Hedge reduz exposição a variações"),
                                criarPergunta("O que significa WACC?",
                                                Arrays.asList("Custo médio ponderado de capital",
                                                                "Capital inicial de giro",
                                                                "Lucro bruto",
                                                                "Margem bruta"),
                                                0, "WACC combina custo de dívida e patrimônio"),
                                criarPergunta("O que são derivativos?",
                                                Arrays.asList("Contratos cujo valor deriva de um ativo de referência",
                                                                "Aplicações de renda fixa",
                                                                "Somente ações",
                                                                "Somente imóveis"),
                                                0, "Derivativos permitem proteção e alavancagem"),
                                criarPergunta("Qual órgão regula o mercado de capitais no Brasil?",
                                                Arrays.asList("CVM", "BACEN", "IBGE", "BID"),
                                                0, "A CVM supervisiona o mercado de capitais"));
        }

        private List<PerguntaDesafio> gerarPerguntasSaude() {
                return Arrays.asList(
                                criarPergunta("Quantos litros de água devemos beber por dia?",
                                                Arrays.asList("2-3 litros", "1 litro", "5 litros", "500ml"),
                                                0, "Recomenda-se 2-3 litros por dia"),
                                criarPergunta("O que são macronutrientes?",
                                                Arrays.asList("Carboidratos, proteínas e gorduras",
                                                                "Vitaminas", "Minerais", "Fibras"),
                                                0, "Macronutrientes são carboidratos, proteínas e gorduras"),
                                criarPergunta("Qual é a recomendação de exercícios semanais?",
                                                Arrays.asList("150 minutos de atividade moderada",
                                                                "30 minutos", "500 minutos",
                                                                "Sem necessidade"),
                                                0, "Recomenda-se 150 minutos semanais"),
                                criarPergunta("O que é IMC?",
                                                Arrays.asList("Índice de Massa Corporal",
                                                                "Índice de Massa Cardíaca",
                                                                "Índice de Massa Celular",
                                                                "Índice de Massa Corporativa"),
                                                0, "IMC mede relação peso/altura"),
                                criarPergunta("O que é sono de qualidade?",
                                                Arrays.asList("7-9 horas de sono reparador",
                                                                "4 horas", "12 horas", "Sem sono"),
                                                0, "Sono de qualidade é 7-9 horas"),
                                criarPergunta("O que é estresse?",
                                                Arrays.asList("Resposta do corpo a pressões",
                                                                "Apenas cansaço",
                                                                "Apenas fadiga",
                                                                "Apenas relaxamento"),
                                                0, "Estresse é resposta a pressões"),
                                criarPergunta("O que é meditação?",
                                                Arrays.asList("Prática de atenção e relaxamento",
                                                                "Apenas dormir",
                                                                "Apenas descansar",
                                                                "Apenas pensar"),
                                                0, "Meditação desenvolve atenção plena"),
                                criarPergunta("O que é alimentação balanceada?",
                                                Arrays.asList("Dieta com todos os nutrientes necessários",
                                                                "Apenas proteínas",
                                                                "Apenas carboidratos",
                                                                "Apenas gorduras"),
                                                0, "Alimentação balanceada inclui todos os nutrientes"),
                                criarPergunta("O que é atividade física?",
                                                Arrays.asList("Movimento corporal que gasta energia",
                                                                "Apenas caminhar",
                                                                "Apenas correr",
                                                                "Apenas descansar"),
                                                0, "Atividade física é qualquer movimento"),
                                criarPergunta("O que é bem-estar?",
                                                Arrays.asList("Estado de saúde física e mental",
                                                                "Apenas saúde física",
                                                                "Apenas saúde mental",
                                                                "Apenas ausência de doença"),
                                                0, "Bem-estar é saúde física e mental"),
                                criarPergunta("O que é telemedicina?",
                                                Arrays.asList("Prestação de serviços médicos a distância",
                                                                "Apenas consultas presenciais",
                                                                "Somente exames laboratoriais",
                                                                "Apenas automedicação"),
                                                0, "Telemedicina usa recursos digitais para atendimento"),
                                criarPergunta("Qual benefício do prontuário eletrônico?",
                                                Arrays.asList("Centralizar histórico clínico e facilitar decisões",
                                                                "Eliminar profissionais de saúde",
                                                                "Reduzir exames",
                                                                "Limitar acesso a dados"),
                                                0, "PEP integra informações do paciente"),
                                criarPergunta("O que é protocolo clínico baseado em evidências?",
                                                Arrays.asList("Guia de conduta definido por estudos científicos",
                                                                "Opinião individual",
                                                                "Marketing hospitalar",
                                                                "Documento administrativo"),
                                                0, "Protocolos baseados em evidências padronizam tratamentos"),
                                criarPergunta("Qual objetivo da saúde digital?",
                                                Arrays.asList("Integrar dados, tecnologia e jornada do paciente",
                                                                "Substituir profissionais",
                                                                "Limitar telemetria",
                                                                "Aumentar burocracia"),
                                                0, "Saúde digital melhora cuidado com dados e tecnologia"),
                                criarPergunta("O que é biohacking?",
                                                Arrays.asList("Uso de técnicas para otimizar corpo e mente de forma segura",
                                                                "Uso de softwares maliciosos",
                                                                "Apenas dietas restritivas",
                                                                "Apenas cirurgias"),
                                                0, "Biohacking combina ciência e hábitos para performance"));
        }

        private List<PerguntaDesafio> gerarPerguntasEducacao() {
                return Arrays.asList(
                                criarPergunta("O que é aprendizagem ativa?",
                                                Arrays.asList("Aluno participa ativamente do processo",
                                                                "Aprendizagem passiva", "Apenas leitura",
                                                                "Apenas escuta"),
                                                0, "Aprendizagem ativa envolve participação"),
                                criarPergunta("O que significa EAD?",
                                                Arrays.asList("Educação a Distância",
                                                                "Ensino a Distância",
                                                                "Estudo a Distância",
                                                                "Escola a Distância"),
                                                0, "EAD significa Educação a Distância"),
                                criarPergunta("Qual método foca em aprender fazendo?",
                                                Arrays.asList("Aprendizagem prática",
                                                                "Aprendizagem teórica",
                                                                "Aprendizagem passiva",
                                                                "Aprendizagem automática"),
                                                0, "Aprendizagem prática é aprender fazendo"),
                                criarPergunta("O que é pedagogia?",
                                                Arrays.asList("Ciência e arte de ensinar",
                                                                "Apenas ensinar",
                                                                "Apenas aprender",
                                                                "Apenas estudar"),
                                                0, "Pedagogia é a ciência do ensino"),
                                criarPergunta("O que é andragogia?",
                                                Arrays.asList("Educação de adultos",
                                                                "Educação de crianças",
                                                                "Educação de adolescentes",
                                                                "Educação de idosos"),
                                                0, "Andragogia foca em educação de adultos"),
                                criarPergunta("O que é metodologia ativa?",
                                                Arrays.asList("Método que coloca aluno no centro",
                                                                "Método tradicional",
                                                                "Método passivo",
                                                                "Método sem interação"),
                                                0, "Metodologia ativa coloca aluno no centro"),
                                criarPergunta("O que é avaliação formativa?",
                                                Arrays.asList("Avaliação durante o processo de aprendizagem",
                                                                "Apenas no final",
                                                                "Apenas no início",
                                                                "Apenas uma vez"),
                                                0, "Avaliação formativa acompanha o aprendizado"),
                                criarPergunta("O que é aprendizagem colaborativa?",
                                                Arrays.asList("Aprender em grupo e colaboração",
                                                                "Apenas sozinho",
                                                                "Apenas com professor",
                                                                "Apenas lendo"),
                                                0, "Aprendizagem colaborativa usa trabalho em grupo"),
                                criarPergunta("O que é gamificação na educação?",
                                                Arrays.asList("Usar elementos de jogos no ensino",
                                                                "Apenas jogar",
                                                                "Apenas estudar",
                                                                "Apenas competir"),
                                                0, "Gamificação torna aprendizado mais engajador"),
                                criarPergunta("O que é educação inclusiva?",
                                                Arrays.asList("Educação para todos, sem exclusão",
                                                                "Apenas para alguns",
                                                                "Apenas para maioria",
                                                                "Apenas para minorias"),
                                                0, "Educação inclusiva acolhe todos os alunos"),
                                criarPergunta("O que é aprendizagem adaptativa?",
                                                Arrays.asList("Tecnologia que ajusta conteúdos ao ritmo do aluno",
                                                                "Apenas aulas expositivas",
                                                                "Somente avaliações finais",
                                                                "Apenas exercícios impressos"),
                                                0, "Aprendizagem adaptativa personaliza trilhas"),
                                criarPergunta("Qual o papel do learning analytics?",
                                                Arrays.asList("Usar dados para melhorar experiências educacionais",
                                                                "Substituir professores",
                                                                "Eliminar avaliações",
                                                                "Apenas medir frequência"),
                                                0, "Learning analytics transforma dados em insights"),
                                criarPergunta("Como a realidade aumentada apoia o ensino?",
                                                Arrays.asList("Proporcionando experiências imersivas e contextualizadas",
                                                                "Eliminando laboratórios",
                                                                "Substituindo livros por completo",
                                                                "Apenas exibindo textos"),
                                                0, "Realidade aumentada amplia a vivência prática"),
                                criarPergunta("Por que desenvolver competências socioemocionais?",
                                                Arrays.asList("Elas fortalecem colaboração, empatia e liderança",
                                                                "Para reduzir conteúdos acadêmicos",
                                                                "Para eliminar avaliações",
                                                                "Para evitar projetos em grupo"),
                                                0, "Soft skills complementam o desempenho cognitivo"),
                                criarPergunta("O que é microlearning?",
                                                Arrays.asList("Conteúdo em módulos curtos e focados",
                                                                "Cursos longos sem pausa",
                                                                "Aulas apenas presenciais",
                                                                "Avaliações extensas"),
                                                0, "Microlearning facilita consumo rápido e contínuo"));
        }

        private Desafio criarQuiz(String area, String nivel, String titulo, String descricao, int pontos,
                        String icone) {
                Desafio quiz = Desafio.builder()
                                .tipo("quiz")
                                .area(area)
                                .nivel(nivel)
                                .titulo(titulo)
                                .descricao(descricao)
                                .pontos(pontos)
                                .icone(icone)
                                .dificuldade("Fácil")
                                .build();
                return desafioRepository.save(quiz);
        }

        private PerguntaDesafio criarPergunta(String pergunta, List<String> opcoes, int indiceCorreto,
                        String explicacao) {
                return PerguntaDesafio.builder()
                                .pergunta(pergunta)
                                .opcoes(opcoes)
                                .indiceCorreto(indiceCorreto)
                                .explicacao(explicacao)
                                .build();
        }

        private void adicionarPerguntas(Desafio desafio, List<PerguntaDesafio> perguntas) {
                for (PerguntaDesafio pergunta : perguntas) {
                        pergunta.setDesafio(desafio);
                        perguntaDesafioRepository.save(pergunta);
                }
        }

        private void popularTrilhas() {
                log.info("Criando trilhas...");

                // Trilha IA
                criarTrilha("ia", "Trilha Completa de Inteligência Artificial",
                                "Aprenda IA do básico ao avançado", "🤖", "#4A90E2", "Iniciante", "100 horas");

                // Trilha Dados
                criarTrilha("dados", "Trilha de Ciência de Dados",
                                "Domine análise e visualização de dados", "📊", "#2ECC71", "Iniciante", "120 horas");

                // Trilha Programação
                criarTrilha("programacao", "Trilha de Desenvolvimento Full Stack",
                                "Torne-se um desenvolvedor completo", "💻", "#E74C3C", "Iniciante", "180 horas");

                // Trilha Sustentabilidade
                criarTrilha("sustentabilidade", "Trilha de Sustentabilidade Empresarial",
                                "Implemente práticas sustentáveis na sua empresa", "🌱", "#27AE60", "Iniciante",
                                "75 horas");

                // Trilha Design
                criarTrilha("design", "Trilha de Design UX/UI",
                                "Crie interfaces incríveis e funcionais", "🎨", "#9B59B6", "Iniciante", "90 horas");

                // Trilha Marketing
                criarTrilha("marketing", "Trilha de Marketing Digital",
                                "Domine estratégias de marketing online", "📱", "#3498DB", "Iniciante", "90 horas");

                // Trilha Gestão
                criarTrilha("gestao", "Trilha de Gestão e Liderança",
                                "Desenvolva habilidades de gestão e liderança", "📋", "#F39C12", "Iniciante",
                                "105 horas");

                // Trilha Vendas
                criarTrilha("vendas", "Trilha de Vendas e Negociação",
                                "Aprenda técnicas avançadas de vendas", "💰", "#E67E22", "Iniciante", "80 horas");

                // Trilha RH
                criarTrilha("rh", "Trilha de Recursos Humanos",
                                "Gestão estratégica de pessoas", "👥", "#1ABC9C", "Iniciante", "95 horas");

                // Trilha Finanças
                criarTrilha("financas", "Trilha de Finanças e Investimentos",
                                "Domine finanças pessoais e empresariais", "💵", "#16A085", "Iniciante", "95 horas");

                // Trilha Saúde
                criarTrilha("saude", "Trilha de Saúde e Bem-estar",
                                "Vida saudável e equilibrada", "🥗", "#2ECC71", "Iniciante", "80 horas");

                // Trilha Educação
                criarTrilha("educacao", "Trilha de Educação e Pedagogia",
                                "Metodologias modernas de ensino", "📚", "#34495E", "Iniciante", "100 horas");

                log.info("Trilhas criadas");
        }

        private void criarTrilha(String area, String titulo, String descricao, String icone,
                        String cor, String nivelMinimo, String duracaoTotal) {
                List<Curso> cursos = cursoRepository
                                .findByArea(area, org.springframework.data.domain.Pageable.unpaged())
                                .getContent();

                List<Desafio> desafios = desafioRepository.findByArea(area);

                if (!cursos.isEmpty()) {
                        Trilha trilha = Trilha.builder()
                                        .titulo(titulo)
                                        .descricao(descricao)
                                        .cursos(cursos.stream().limit(2).toList())
                                        .desafios(new java.util.HashSet<>(desafios.stream().limit(2).toList()))
                                        .icone(icone)
                                        .cor(cor)
                                        .area(area)
                                        .nivelMinimo(nivelMinimo)
                                        .duracaoTotal(duracaoTotal)
                                        .build();
                        trilhaRepository.save(trilha);
                }
        }

        private void popularTrofeus() {
                log.info("Criando troféus...");

                List<Trofeu> trofeus = Arrays.asList(
                                Trofeu.builder()
                                                .nome("Perfeito")
                                                .descricao("Acertou 100% das perguntas em um desafio")
                                                .icone("🏆")
                                                .build(),
                                Trofeu.builder()
                                                .nome("Primeiro Passo")
                                                .descricao("Completou seu primeiro curso")
                                                .icone("🎓")
                                                .build(),
                                Trofeu.builder()
                                                .nome("Mestre")
                                                .descricao("Completou 10 cursos")
                                                .icone("👑")
                                                .build(),
                                Trofeu.builder()
                                                .nome("Desafio Máximo")
                                                .descricao("Completou 50 desafios")
                                                .icone("⚡")
                                                .build());

                trofeuRepository.saveAll(trofeus);
                log.info("{} troféus criados", trofeus.size());
        }
}
