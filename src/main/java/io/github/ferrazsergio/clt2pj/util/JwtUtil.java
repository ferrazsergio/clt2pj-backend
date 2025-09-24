package io.github.ferrazsergio.clt2pj.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;

/**
 * Utilitário para geração de tokens JWT.
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpirationMs;

    /**
     * Gera um token JWT para o e-mail e os papéis informados.
     *
     * @param email e-mail do usuário
     * @param papeis papéis do usuário
     * @return token JWT gerado
     */
    public String gerarToken(String email, Set<String> papeis) {
        return Jwts.builder()
                .setSubject(email)
                .claim("papeis", papeis)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }
}