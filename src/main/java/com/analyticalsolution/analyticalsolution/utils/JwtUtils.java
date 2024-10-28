/**
 * File: JwtUtils.java
 * Author: Atharv Mirgal
 * Description: Utility class for managing JSON Web Tokens (JWT) used in secure authentication processes.
 *              Provides methods for generating various tokens (standard, password reset, and email verification),
 *              extracting claims like username and expiration, and validating token integrity and expiration status.
 *              Utilizes HMAC signing for secure token management.
 * Created on: 11/10/2024
 * Last Modified: 28/10/2024
 */


package com.analyticalsolution.analyticalsolution.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {

    // Secret key
    @Value("${app.secret-key}")
    private String SECRET_KEY;

    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // Generate token
    public String generateToken(String username){
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    // Generate token
    public String generatePasswordToken(String username){
        Map<String, Object> claims = new HashMap<>();
        return createPasswordToken(claims, username);
    }

    // Generate verification token
    public String generateVerificationToken(String email){
        Map<String, Object> claims = new HashMap<>();
        return createVerificationToken(claims, email);
    }

    // Creating a token
    private String createToken(Map<String, Object> claims, String subject){
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .header().empty().add("typ", "JWT")
                .and()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(getSigningKey())
                .compact();
    }

    // Creating a password token
    private String createPasswordToken(Map<String, Object> claims, String subject){
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .header().empty().add("typ", "JWT")
                .and()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 5))
                .signWith(getSigningKey())
                .compact();
    }

    // Creating a token
    private String createVerificationToken(Map<String, Object> claims, String subject){
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .header().empty().add("typ", "JWT")
                .and()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {
        try{
            Claims claims = extractAllClaims(token);
            return claims.getSubject();
        }catch (Exception e){
            return null;
        }
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }
}
