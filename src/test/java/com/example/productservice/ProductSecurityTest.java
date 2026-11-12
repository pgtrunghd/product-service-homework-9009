package com.example.productservice;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductSecurityTest {
    private static final String SECRET = "vS6K5vH8N2zB4xR9mQ3pL1wT7yC5vH8N2zB4xR9mQ3pL1wT7yC5vH8N2z";
    @Autowired MockMvc mvc;

    private String token(String secret, String role, long expiresAt) {
        return Jwts.builder().setSubject("alice").claim("roles", List.of(role))
            .setIssuedAt(new Date()).setExpiration(new Date(expiresAt))
            .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
            .compact();
    }

    @Test void userMayReadButCannotDelete() throws Exception {
        String jwt = token(SECRET, "USER", System.currentTimeMillis() + 900000);
        mvc.perform(get("/api/products").header("Authorization", "Bearer " + jwt))
            .andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(1));
        mvc.perform(delete("/api/products/1").header("Authorization", "Bearer " + jwt))
            .andExpect(status().isForbidden());
    }

    @Test void adminMayDelete() throws Exception {
        String jwt = token(SECRET, "ADMIN", System.currentTimeMillis() + 900000);
        mvc.perform(delete("/api/products/1").header("Authorization", "Bearer " + jwt))
            .andExpect(status().isOk());
    }

    @Test void missingInvalidAndExpiredTokensAreUnauthorized() throws Exception {
        mvc.perform(get("/api/products")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/products").header("Authorization", "Bearer " +
            token("wrong-signing-key-at-least-32-bytes-long", "ADMIN", System.currentTimeMillis() + 900000)))
            .andExpect(status().isUnauthorized());
        mvc.perform(get("/api/products").header("Authorization", "Bearer " +
            token(SECRET, "ADMIN", System.currentTimeMillis() - 1000)))
            .andExpect(status().isUnauthorized());
    }
}
