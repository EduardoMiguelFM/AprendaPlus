package br.com.fiap.Aprenda.controller.web;

import br.com.fiap.Aprenda.dto.*;
import br.com.fiap.Aprenda.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller Web para Onboarding (áreas de interesse, níveis, confirmação)
 */
@Controller
@RequestMapping("/onboarding")
@RequiredArgsConstructor
public class OnboardingWebController {

    private final UsuarioService servicoUsuario;

    // Áreas disponíveis
    private static final Map<String, String> AREAS = Map.ofEntries(
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

    // Níveis disponíveis
    private static final List<String> NIVEIS = Arrays.asList("Iniciante", "Intermediário", "Avançado");

    @GetMapping("/areas-interesse")
    public String areasInteresse(Model model) {
        try {
            // Verificar se usuário já completou onboarding
            var usuario = servicoUsuario.obterUsuarioAtual();
            if (usuario.getOnboardingConcluido()) {
                return "redirect:/dashboard";
            }

            RequisicaoPreferencias preferencias = servicoUsuario.obterPreferencias();
            model.addAttribute("preferencias", preferencias);
            model.addAttribute("todasAreas", AREAS);
            model.addAttribute("todosNiveis", NIVEIS);
            // Flag para carregar o script JavaScript
            model.addAttribute("loadAreasScript", true);

            return "onboarding/areas-interesse";
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

    @PostMapping("/areas-interesse")
    public String salvarAreasInteresse(HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        try {
            // Processar parâmetros do formulário
            Map<String, String[]> parameterMap = request.getParameterMap();
            List<AreaInteresseDTO> areasInteresse = new java.util.ArrayList<>();

            // Buscar todas as áreas enviadas (preferencias.areasInteresse[0].area, etc)
            Map<String, String> areaNivelMap = new HashMap<>();

            for (String paramName : parameterMap.keySet()) {
                if (paramName.startsWith("preferencias.areasInteresse[") && paramName.endsWith("].area")) {
                    String indexStr = paramName.substring("preferencias.areasInteresse[".length(),
                            paramName.length() - "].area".length());
                    String area = parameterMap.get(paramName)[0];

                    // Buscar nível correspondente
                    String nivelParam = "preferencias.areasInteresse[" + indexStr + "].nivel";
                    String nivel = "Iniciante"; // Valor padrão

                    if (parameterMap.containsKey(nivelParam)) {
                        nivel = parameterMap.get(nivelParam)[0];
                    }

                    if (area != null && !area.trim().isEmpty()) {
                        areaNivelMap.put(area, nivel);
                    }
                }
            }

            // Construir lista de áreas de interesse
            for (Map.Entry<String, String> entry : areaNivelMap.entrySet()) {
                areasInteresse.add(AreaInteresseDTO.builder()
                        .area(entry.getKey())
                        .nivel(entry.getValue() != null ? entry.getValue() : "Iniciante")
                        .build());
            }

            if (areasInteresse.isEmpty()) {
                redirectAttributes.addFlashAttribute("erro", "Selecione pelo menos uma área de interesse");
                return "redirect:/onboarding/areas-interesse";
            }

            if (areasInteresse.size() > 3) {
                redirectAttributes.addFlashAttribute("erro", "Selecione no máximo 3 áreas de interesse");
                return "redirect:/onboarding/areas-interesse";
            }

            RequisicaoPreferencias requisicao = RequisicaoPreferencias.builder()
                    .areasInteresse(areasInteresse)
                    .onboardingConcluido(false)
                    .build();

            servicoUsuario.salvarPreferencias(requisicao);
            redirectAttributes.addFlashAttribute("sucesso", "Preferências salvas com sucesso!");
            return "redirect:/onboarding/confirmacao";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/onboarding/areas-interesse";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao salvar preferências: " + e.getMessage());
            return "redirect:/onboarding/areas-interesse";
        }
    }

    @GetMapping("/confirmacao")
    public String confirmacao(Model model) {
        try {
            RequisicaoPreferencias preferencias = servicoUsuario.obterPreferencias();
            model.addAttribute("preferencias", preferencias);
            model.addAttribute("todasAreas", AREAS);

            // Criar lista de áreas com nomes formatados
            if (preferencias != null && preferencias.getAreasInteresse() != null
                    && !preferencias.getAreasInteresse().isEmpty()) {
                List<String> areasComNomes = preferencias.getAreasInteresse().stream()
                        .map(areaDto -> {
                            String nomeCompleto = AREAS.getOrDefault(areaDto.getArea(), areaDto.getArea());
                            return nomeCompleto + " (" + areaDto.getNivel() + ")";
                        })
                        .collect(Collectors.toList());
                model.addAttribute("areasComNomes", areasComNomes);
            } else {
                model.addAttribute("areasComNomes", new java.util.ArrayList<>());
            }

            return "onboarding/confirmacao";
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

    @PostMapping("/concluir")
    public String concluirOnboarding(RedirectAttributes redirectAttributes) {
        try {
            RequisicaoPreferencias preferencias = servicoUsuario.obterPreferencias();
            preferencias.setOnboardingConcluido(true);
            servicoUsuario.salvarPreferencias(preferencias);
            redirectAttributes.addFlashAttribute("sucesso", "Onboarding concluído! Bem-vindo(a)!");
            return "redirect:/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao concluir onboarding.");
            return "redirect:/onboarding/confirmacao";
        }
    }
}
