package com.ayd2.library.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    @Value("${security.jwt.secret.key}")
    private String SECRET_KEY;

    @Value("${security.jwt.ttlMillis}")
    private Long TTL_MILLIS;

    @Value("${security.jwt.issuer}")
    private String ISSUER;

    public String generateToken(UserDetails userDetails) {
        var authorities = userDetails.getAuthorities();

        return Jwts
                .builder()
                .claims(Map.of("authorities", authorities))
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + TTL_MILLIS))
                .issuer(ISSUER)
                .signWith(getSecretKey())
                .compact();
    }

    public String getUsername(String token) throws IOException {
        var claims = extractClaims(token);
        return claims.getSubject();
    }

    public boolean isValid(String token) throws IOException {
        try {
            var claims = extractClaims(token);
            var expiration = claims.getExpiration();
            return new Date().before(expiration);
        } catch (ExpiredJwtException expiredJwtException) {
            return false;
        }
    }

    private Claims extractClaims(String token) throws IOException {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
