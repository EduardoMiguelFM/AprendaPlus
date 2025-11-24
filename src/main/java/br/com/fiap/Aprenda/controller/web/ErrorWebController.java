package br.com.fiap.Aprenda.controller.web;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller para tratamento de erros
 */
@Controller
public class ErrorWebController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            model.addAttribute("statusCode", statusCode);
            model.addAttribute("statusText", HttpStatus.valueOf(statusCode).getReasonPhrase());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("mensagem", "Página não encontrada");
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                model.addAttribute("mensagem", "Acesso negado. Você não tem permissão para acessar este recurso.");
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addAttribute("mensagem", "Erro interno do servidor");
            } else {
                model.addAttribute("mensagem", message != null ? message.toString() : "Ocorreu um erro");
            }
        } else {
            model.addAttribute("statusCode", 500);
            model.addAttribute("statusText", "Erro");
            model.addAttribute("mensagem", "Ocorreu um erro desconhecido");
        }

        return "error";
    }
}








