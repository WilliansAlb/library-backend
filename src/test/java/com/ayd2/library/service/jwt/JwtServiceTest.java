package com.ayd2.library.service.jwt;
import com.ayd2.library.util.enums.RoleEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ClaimsBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JwtServiceTest {

    private JwtService jwtService;
    private static final String TOKEN_VALID = "eyJhbGciOiJIUzUxMiJ9.eyJhdXRob3JpdGllcyI6W3siYXV0aG9yaXR5IjoiMCJ9XSwic3ViIjoid2lsbGlhbnNBbGIiLCJpYXQiOjE3MTYwOTYyODYsImV4cCI6MTcxNjcwMTA4NiwiaXNzIjoiTWFpbiJ9.aXkY_cXOpGfBG7ckCakOcyv-AKhqrZQHT-AYjzg8wYMEIOoi41v6K7YktU6RWgWxFFKeBoI0FIc-Bktxv4FHGg";
    private static final String USERNAME_TOKEN = "williansAlb";
    public static final String S_KEY = "ZW9KbW8kbUxwU21ybndlKmIzRHNAXm95WmoqQE5qc2R2cnVaQGhmRFZmWFhqQllmall4VVcla0xTUnFGVk5RRw==";
    public static final String ISSUER = "Main";
    public static final Long TTL_MILLIS = 3600000L;
    public static final String PASSWORD_TOKEN = "testPassword";

    @BeforeEach
    public void setUp() {
        jwtService = new JwtService();

        // Setting the private fields using reflection (or use a constructor if available)
        setValueForField(jwtService, "SECRET_KEY", S_KEY);
        setValueForField(jwtService, "TTL_MILLIS", TTL_MILLIS); // 1 hour
        setValueForField(jwtService, "ISSUER", ISSUER);
    }

    private void setValueForField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGenerateToken() throws Exception {
        User nuevo = new User(USERNAME_TOKEN, PASSWORD_TOKEN, Collections.singletonList(new SimpleGrantedAuthority(RoleEnum.LIBRARIAN.roleId)));
        String token = jwtService.generateToken(nuevo);
        assertNotNull(token);
    }

    @Test
    public void testGetUsername() throws Exception {
        String username = jwtService.getUsername(TOKEN_VALID);
        assertEquals(USERNAME_TOKEN, username);
    }

    @Test
    public void testIsValid() throws Exception {
        String tokenValid = createToken(3000L, Collections.singletonList(new SimpleGrantedAuthority(RoleEnum.LIBRARIAN.roleId)));
        boolean valid = jwtService.isValid(tokenValid);
        assertTrue(valid);
    }

    @Test
    public void testIsNotValid() throws Exception {
        String tokenInvalid = createToken(-3000L, Collections.singletonList(new SimpleGrantedAuthority(RoleEnum.LIBRARIAN.roleId)));
        boolean valid = jwtService.isValid(tokenInvalid);
        assertFalse(valid);
    }

    private String createToken(long ttlMillis, Collection<? extends GrantedAuthority> authorities) {
        SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(S_KEY));

        return Jwts.builder()
                .claims(Map.of("authorities", authorities))
                .subject(USERNAME_TOKEN)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ttlMillis))
                .issuer(ISSUER)
                .signWith(secretKey)
                .compact();
    }

}
