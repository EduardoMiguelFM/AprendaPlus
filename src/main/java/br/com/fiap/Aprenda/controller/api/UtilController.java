package br.com.fiap.Aprenda.controller.api;

import br.com.fiap.Aprenda.dto.RespostaApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller REST para endpoints utilitários
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Utilidades", description = "Endpoints utilitários da API")
public class UtilController {

    @GetMapping("/health")
    @Operation(summary = "Health check da API")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> resposta = new HashMap<>();
        resposta.put("status", "UP");
        resposta.put("servico", "Aprenda+ API");
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/areas")
    @Operation(summary = "Listar todas as áreas de interesse disponíveis")
    public ResponseEntity<RespostaApi<List<String>>> obterAreas() {
        List<String> areas = Arrays.asList(
                "ia", "dados", "programacao", "sustentabilidade", "design",
                "marketing", "gestao", "vendas", "rh", "financas", "saude", "educacao");
        return ResponseEntity.ok(RespostaApi.sucesso(areas, "Áreas recuperadas com sucesso"));
    }

    @GetMapping("/niveis")
    @Operation(summary = "Listar todos os níveis disponíveis")
    public ResponseEntity<RespostaApi<List<String>>> obterNiveis() {
        List<String> niveis = Arrays.asList("Iniciante", "Intermediário", "Avançado");
        return ResponseEntity.ok(RespostaApi.sucesso(niveis, "Níveis recuperados com sucesso"));
    }

    @GetMapping("/config")
    @Operation(summary = "Configurações gerais do app")
    public ResponseEntity<RespostaApi<Map<String, Object>>> obterConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("appName", "Aprenda+");
        config.put("versao", "1.0.0");
        config.put("maxAreasInteresse", 3);
        config.put("idiomasSuportados", Arrays.asList("pt", "en", "es"));
        return ResponseEntity.ok(RespostaApi.sucesso(config, "Configurações recuperadas com sucesso"));
    }
}






