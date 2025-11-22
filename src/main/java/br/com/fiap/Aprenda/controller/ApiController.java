package br.com.fiap.Aprenda.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller REST de exemplo
 * Demonstra uso adequado de verbos HTTP e códigos de status
 */
@RestController
@RequestMapping("/api/public")
public class ApiController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("application", "Aprenda+");

        return ResponseEntity.ok(response);
    }
}
