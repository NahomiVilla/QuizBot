package com.discordbot.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import net.dv8tion.jda.api.entities.User;

import java.util.Base64;
import java.security.Key;

public class AuthService {
    private static final String SECRET_KEY = "GVoVEujj1RfRMyU3KBcb0rFyw19yydI1OuupGdJ1dsw=";

    public String generateToken(String userId) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSigningKey() {
    byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
    return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    public boolean isAuthenticated(User user) {
        String token = getTokenFromUser(user);
        if (token == null) {
            return false;
        }
        try {
            Claims claims = Jwts.parserBuilder()
                                .setSigningKey(getSigningKey())
                                .build()
                                .parseClaimsJws(token)
                                .getBody();
            return claims.getSubject().equals(user.getId());
        } catch (Exception e) {
            return false;
        }
    }

    private String getTokenFromUser(User user) {
        String token =generateToken(user.getId());
        
        // Aquí deberías obtener el token JWT del usuario, puede ser desde una base de datos o un almacenamiento temporal
        return token;
    }
}
