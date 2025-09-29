package io.github.ferrazsergio.clt2pj.service;

import io.github.ferrazsergio.clt2pj.domain.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenService {

    private final String secretKey = "7dd2c499e70ba909ad18db766a8308abe602b0aaef259ceae2724ca960e4df35"; // Troque para variável de ambiente!
    private final long expirationMillis = 1000 * 60 * 60 * 6; // 6 horas

    public String generateToken(Usuario usuario) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .setSubject(usuario.getEmail())
                .claim("id", usuario.getId())
                .claim("papeis", usuario.getPapeis())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
}