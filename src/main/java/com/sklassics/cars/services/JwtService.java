package com.sklassics.cars.services;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour

    
    public String generateToken(String email, String role, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        
        claims.put("email", email);
        claims.put("role", role);
        claims.put("userId", userId);

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();

        System.out.println("Generated Token: " + token);
        return token;
    }

    // ✅ Validate if the token is expired
    public boolean isTokenExpired(String token) {
        try {
            Date expirationDate = getExpirationDateFromToken(token);
            System.out.println("Token expiration date: " + expirationDate);
            System.out.println("Current date: " + new Date());
            boolean isExpired = expirationDate != null && expirationDate.before(new Date());
            System.out.println("Is token expired: " + isExpired);
            return isExpired;
        } catch (Exception e) {
            System.out.println("Error while checking token expiration: " + e.getMessage());
            return true; // Treat any exception as expired or invalid
        }
    }

    // ✅ Get expiration date from token
    private Date getExpirationDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return (claims != null) ? claims.getExpiration() : null;
    }

    // ✅ Extract claims from token safely
    private Claims getClaimsFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            System.out.println("Token claims extracted successfully: " + claims);
            return claims;
        } catch (ExpiredJwtException e) {
            System.out.println("Token has expired: " + e.getMessage());
            return null;
        } catch (JwtException e) {
            System.out.println("Invalid token: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.out.println("Unexpected error while parsing token: " + e.getMessage());
            return null;
        }
    }

    // ✅ Extract role from token
    public String extractRole(String token) {
        Claims claims = getClaimsFromToken(token);
        String role = (claims != null) ? claims.get("role", String.class) : null;
        System.out.println("Extracted role from token: " + role);
        return role;
    }

    // ✅ Extract mobileNumber
    public String extractEmailFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        String email = (claims != null) ? claims.get("email", String.class) : null;
        System.out.println("Extracted email from token: " + email);
        return email;
    }
    
    public long extractUserId(String token) {
        Claims claims = getClaimsFromToken(token);
        Long userId = (claims != null) ? claims.get("userId", Long.class) : null;
        System.out.println("Extracted userId from token: " + userId);
        return userId ;
    }

}
