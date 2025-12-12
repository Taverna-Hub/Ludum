package org.ludum.backend.conta.controllers;

import org.ludum.backend.conta.dto.AuthResponse;
import org.ludum.backend.conta.dto.LoginRequest;
import org.ludum.backend.conta.dto.RegistroRequest;
import org.ludum.backend.conta.services.AuthService;
import org.ludum.backend.conta.services.TokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;

    public AuthController(AuthService authService, TokenService tokenService) {
        this.authService = authService;
        this.tokenService = tokenService;
    }

    @PostMapping("/registro")
    public ResponseEntity<AuthResponse> registrar(@RequestBody RegistroRequest request) {
        AuthResponse response = authService.registrar(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + response.getToken())
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + response.getToken())
                .body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenService.invalidarToken(token);
        }
        return ResponseEntity.ok(Map.of("mensagem", "Logout realizado com sucesso"));
    }
}
