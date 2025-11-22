package br.com.fiap.Aprenda.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço para integração com Spring AI
 * Fornece recomendações inteligentes usando IA generativa
 */
@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(value = "spring.ai.openai.chat.enabled", havingValue = "true", matchIfMissing = true)
public class IAService {

    private final ChatClient.Builder chatClientBuilder;

    @Value("${spring.ai.openai.chat.enabled:true}")
    private boolean aiEnabled;

    /**
     * Gera recomendações personalizadas de cursos baseado no perfil do usuário
     */
    public String gerarRecomendacoesCursos(List<String> areasInteresse, List<String> niveis, Integer pontosTotais) {
        try {
            List<String> areas = (areasInteresse == null || areasInteresse.isEmpty())
                    ? List.of("programação", "dados", "inteligência artificial")
                    : areasInteresse;
            List<String> niveisConsiderados = (niveis == null || niveis.isEmpty())
                    ? List.of("Iniciante")
                    : niveis;

            if (!aiEnabled) {
                return gerarRecomendacaoFallback(areas);
            }

            log.info("Gerando recomendação IA para áreas: {} com níveis: {}", areas, niveisConsiderados);

            String prompt = String.format(
                    "Você é um assistente educacional da plataforma Aprenda+. " +
                            "O usuário tem interesse nas seguintes áreas: %s. " +
                            "Níveis de conhecimento: %s. " +
                            "Pontos totais: %d. " +
                            "Gere uma recomendação personalizada e motivadora de cursos (máximo 3 parágrafos, em português). "
                            +
                            "Seja entusiasmado e específico sobre por que esses cursos são ideais para o perfil do usuário.",
                    areas.stream().collect(Collectors.joining(", ")),
                    niveisConsiderados.stream().collect(Collectors.joining(", ")),
                    pontosTotais);

            ChatClient chatClient = chatClientBuilder.build();
            String resposta = chatClient.prompt(prompt).call().content();
            return resposta;
        } catch (Exception e) {
            log.error("Erro ao gerar recomendação IA: {}", e.getMessage());
            List<String> areas = (areasInteresse == null || areasInteresse.isEmpty())
                    ? List.of("programação", "dados", "inteligência artificial")
                    : areasInteresse;
            return gerarRecomendacaoFallback(areas);
        }
    }

    /**
     * Responde perguntas do usuário sobre cursos, aprendizado, etc.
     */
    public String responderPergunta(String pergunta, String contextoUsuario) {
        try {
            if (!aiEnabled) {
                return gerarRespostaFallback(contextoUsuario);
            }

            log.info("Processando pergunta do usuário: {}", pergunta);

            String prompt = String.format(
                    "Você é um assistente educacional amigável e prestativo da plataforma Aprenda+. " +
                            "Contexto do usuário: %s. " +
                            "Pergunta do usuário: %s. " +
                            "Responda de forma clara, útil e motivadora (máximo 2 parágrafos, em português). " +
                            "Se a pergunta não for sobre educação ou cursos, seja educado e redirecione para tópicos educacionais.",
                    contextoUsuario != null ? contextoUsuario : "Usuário da plataforma",
                    pergunta);

            ChatClient chatClient = chatClientBuilder.build();
            String resposta = chatClient.prompt(prompt).call().content();
            return resposta;
        } catch (Exception e) {
            log.error("Erro ao processar pergunta: {}", e.getMessage());
            return gerarRespostaFallback(contextoUsuario);
        }
    }

    /**
     * Gera dicas de aprendizado personalizadas
     */
    public String gerarDicasAprendizado(String area, String nivel) {
        try {
            if (!aiEnabled) {
                return gerarDicasFallback(area, nivel);
            }

            log.info("Gerando dicas IA para área: {} nível: {}", area, nivel);

            String prompt = String.format(
                    "Você é um mentor educacional. " +
                            "Forneça 3 dicas práticas e motivadoras para aprender %s no nível %s. " +
                            "Seja específico e acionável (formato de lista, em português).",
                    area, nivel);

            ChatClient chatClient = chatClientBuilder.build();
            String resposta = chatClient.prompt(prompt).call().content();
            return resposta;
        } catch (Exception e) {
            log.error("Erro ao gerar dicas: {}", e.getMessage());
            return gerarDicasFallback(area, nivel);
        }
    }

    private String gerarRecomendacaoFallback(List<String> areasInteresse) {
        StringBuilder recomendacao = new StringBuilder();
        if (areasInteresse == null || areasInteresse.isEmpty()) {
            return "Finalize o onboarding ou atualize suas áreas de interesse para receber recomendações personalizadas.";
        }
        recomendacao.append("Com base no seu perfil, recomendamos explorar: ");
        for (int i = 0; i < areasInteresse.size(); i++) {
            String area = areasInteresse.get(i);
            recomendacao.append(area);
            if (i < areasInteresse.size() - 1) {
                recomendacao.append(", ");
            }
        }
        recomendacao.append(
                ". Esses cursos estão alinhados com suas áreas favoritas e ajudam a evoluir de forma consistente.");
        return recomendacao.toString();
    }

    private String gerarDicasFallback(String area, String nivel) {
        return String.format(
                "1. Pratique regularmente exercícios de %s no nível %s. " +
                        "2. Assista aos cursos disponíveis e faça anotações. " +
                        "3. Participe dos desafios para consolidar seu conhecimento.",
                area, nivel);
    }

    private String gerarRespostaFallback(String contextoUsuario) {
        if (contextoUsuario == null || contextoUsuario.isBlank()) {
            return "Finalize seu onboarding para que eu conheça melhor seus interesses e consiga sugerir cursos ideais.";
        }
        return "Estou ajustando minhas recomendações com base no seu perfil. Enquanto isso, explore os cursos sugeridos e continue avançando nos desafios!";
    }
}
