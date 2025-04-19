//package com.sklassics.cars.services;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.ExpiredJwtException;
//import io.jsonwebtoken.JwtException;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.stereotype.Service;
//
//import javax.crypto.SecretKey;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//
//@Service
//public class JwtService {
//
//    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
//    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour
//
//    // ✅ Generate JWT token with userId & mobileNumber
//    public String generateToken(String mobileNumber, String role) {
//        Map<String, Object> claims = new HashMap<>();
//        
//        claims.put("mobileNumber", mobileNumber);
//        claims.put("role", role);
//
//        String token = Jwts.builder()
//        	    .setClaims(claims)
//        	    .setSubject(mobileNumber)
//        	    .setIssuedAt(new Date())
//        	    .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
//        	    .signWith(SECRET_KEY) 
//        	    .compact();
//
//
//        // Log the token in a production environment
//        System.out.println("Generated Token: " + token);
//        return token;
//    }
//
//    // ✅ Validate if the token is expired
//    public boolean isTokenExpired(String token) {
//        try {
//            Date expirationDate = getExpirationDateFromToken(token);
//            System.out.println("Token expiration date: " + expirationDate);
//            System.out.println("Current date: " + new Date());
//            boolean isExpired = expirationDate != null && expirationDate.before(new Date());
//            System.out.println("Is token expired: " + isExpired);
//            return isExpired;
//        } catch (Exception e) {
//            System.out.println("Error while checking token expiration: " + e.getMessage());
//            return true; // Treat any exception as expired or invalid
//        }
//    }
//
//    // ✅ Get expiration date from token
//    private Date getExpirationDateFromToken(String token) {
//        Claims claims = getClaimsFromToken(token);
//        return (claims != null) ? claims.getExpiration() : null;
//    }
//
//    // ✅ Extract claims from token safely
//    private Claims getClaimsFromToken(String token) {
//        try {
//            Claims claims = Jwts.parserBuilder()
//                    .setSigningKey(SECRET_KEY)
//                    .build()
//                    .parseClaimsJws(token)
//                    .getBody();
//            System.out.println("Token claims extracted successfully: " + claims);
//            return claims;
//        } catch (ExpiredJwtException e) {
//            System.out.println("Token has expired: " + e.getMessage());
//            return null;
//        } catch (JwtException e) {
//            System.out.println("Invalid token: " + e.getMessage());
//            return null;
//        } catch (Exception e) {
//            System.out.println("Unexpected error while parsing token: " + e.getMessage());
//            return null;
//        }
//    }
//
//    // ✅ Extract role from token
//    public String extractRole(String token) {
//        Claims claims = getClaimsFromToken(token);
//        String role = (claims != null) ? claims.get("role", String.class) : null;
//        System.out.println("Extracted role from token: " + role);
//        return role;
//    }
//
//    // ✅ Extract mobileNumber
//    public String extractMobileNumber(String token) {
//        Claims claims = getClaimsFromToken(token);
//        String mobileNumber = (claims != null) ? claims.get("mobileNumber", String.class) : null;
//        System.out.println("Extracted mobile number from token: " + mobileNumber);
//        return mobileNumber;
//    }
//}
