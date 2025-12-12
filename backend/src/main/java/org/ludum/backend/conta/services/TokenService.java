package org.ludum.backend.conta.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.ludum.dominio.identidade.conta.entities.Conta;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
public class TokenService {

    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRATION_TIME = 86400000; // 1 dia
    
    // Blacklist de tokens invalidados (em produção, usar Redis ou banco de dados)
    private final Set<String> tokensInvalidados = new HashSet<>();

    public String gerarToken(Conta conta) {
        return Jwts.builder()
                .setSubject(conta.getId().getValue())
                .claim("nome", conta.getNome())
                .claim("tipo", conta.getTipo().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    public boolean validarToken(String token) {
        if (token == null || tokensInvalidados.contains(token)) {
            return false;
        }
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extrairUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public void invalidarToken(String token) {
        tokensInvalidados.add(token);
    }
}
