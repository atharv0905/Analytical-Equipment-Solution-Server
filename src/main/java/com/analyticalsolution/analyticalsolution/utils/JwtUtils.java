/**
 * File: JwtUtils.java
 * Author: Atharv Mirgal
 * Description: Utility class for handling JSON Web Token (JWT) operations including token generation,
 *              extraction of claims (such as username and expiration), and validation of tokens.
 *              This class provides methods to create and manage JWT tokens securely using HMAC signing.
 * Created on: 11/10/2024
 * Last Modified: 11/10/2024
 */

package com.analyticalsolution.analyticalsolution.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {

    // Secret key
    private String SECRET_KEY = "TaK+HaV^uvCHEFsEVfypW#7g9^k*Z8$V";

    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // Generate token
    public String generateToken(String username){
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    // Generate verification token
    public String generateVerificationToken(String username){
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
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
