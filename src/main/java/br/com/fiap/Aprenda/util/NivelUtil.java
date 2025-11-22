package br.com.fiap.Aprenda.util;

import lombok.Getter;

/**
 * Utilitário para calcular níveis e rankings baseados em pontos
 * Sistema gamificado: Bronze, Prata, Ouro, Platina, Diamante, Mestre, Lendário
 */
public class NivelUtil {

    /**
     * Enum representando os níveis do sistema
     */
    @Getter
    public enum Nivel {
        BRONZE("Bronze", "🥉", "#CD7F32", 0, 100),
        PRATA("Prata", "🥈", "#C0C0C0", 100, 500),
        OURO("Ouro", "🥇", "#FFD700", 500, 1500),
        PLATINA("Platina", "💎", "#E5E4E2", 1500, 3000),
        DIAMANTE("Diamante", "💠", "#B9F2FF", 3000, 5000),
        MESTRE("Mestre", "👑", "#FF6B35", 5000, 10000),
        LENDARIO("Lendário", "⭐", "#9D4EDD", 10000, Integer.MAX_VALUE);

        private final String nome;
        private final String icone;
        private final String cor;
        private final int pontosMinimos;
        private final int pontosMaximos;

        Nivel(String nome, String icone, String cor, int pontosMinimos, int pontosMaximos) {
            this.nome = nome;
            this.icone = icone;
            this.cor = cor;
            this.pontosMinimos = pontosMinimos;
            this.pontosMaximos = pontosMaximos;
        }
    }

    /**
     * Calcula o nível atual baseado nos pontos
     */
    public static Nivel calcularNivel(int pontos) {
        for (Nivel nivel : Nivel.values()) {
            if (pontos >= nivel.getPontosMinimos() && pontos < nivel.getPontosMaximos()) {
                return nivel;
            }
        }
        return Nivel.LENDARIO; // Se ultrapassar todos os níveis
    }

    /**
     * Calcula o progresso para o próximo nível (0-100)
     */
    public static int calcularProgressoProximoNivel(int pontos) {
        Nivel nivelAtual = calcularNivel(pontos);

        if (nivelAtual == Nivel.LENDARIO) {
            return 100; // Já está no nível máximo
        }

        int pontosNoNivelAtual = pontos - nivelAtual.getPontosMinimos();
        int pontosNecessariosParaProximo = nivelAtual.getPontosMaximos() - nivelAtual.getPontosMinimos();

        return (int) ((pontosNoNivelAtual * 100.0) / pontosNecessariosParaProximo);
    }

    /**
     * Retorna o próximo nível
     */
    public static Nivel obterProximoNivel(int pontos) {
        Nivel nivelAtual = calcularNivel(pontos);

        if (nivelAtual == Nivel.LENDARIO) {
            return null; // Não há próximo nível
        }

        Nivel[] niveis = Nivel.values();
        for (int i = 0; i < niveis.length - 1; i++) {
            if (niveis[i] == nivelAtual) {
                return niveis[i + 1];
            }
        }
        return null;
    }

    /**
     * Calcula quantos pontos faltam para o próximo nível
     */
    public static int pontosParaProximoNivel(int pontos) {
        Nivel proximoNivel = obterProximoNivel(pontos);
        if (proximoNivel == null) {
            return 0; // Já está no nível máximo
        }
        return proximoNivel.getPontosMinimos() - pontos;
    }

    /**
     * Retorna informações completas do nível em formato de DTO
     */
    public static NivelInfo obterNivelInfo(int pontos) {
        Nivel nivelAtual = calcularNivel(pontos);
        Nivel proximoNivel = obterProximoNivel(pontos);

        return NivelInfo.builder()
                .nivelAtual(nivelAtual)
                .proximoNivel(proximoNivel)
                .pontos(pontos)
                .progresso(calcularProgressoProximoNivel(pontos))
                .pontosParaProximo(pontosParaProximoNivel(pontos))
                .pontosNoNivelAtual(pontos - nivelAtual.getPontosMinimos())
                .pontosNecessariosParaProximo(
                        proximoNivel != null ? proximoNivel.getPontosMinimos() - nivelAtual.getPontosMinimos() : 0)
                .build();
    }

    /**
     * Classe DTO para informações de nível
     */
    @lombok.Getter
    @lombok.Setter
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class NivelInfo {
        private Nivel nivelAtual;
        private Nivel proximoNivel;
        private int pontos;
        private int progresso; // 0-100
        private int pontosParaProximo;
        private int pontosNoNivelAtual;
        private int pontosNecessariosParaProximo;
    }
}
