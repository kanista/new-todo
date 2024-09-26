package com.example.demo.util;


import com.example.demo.entity.ERole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


//methods for extracting information from the JWT and validating it.
@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    // Generate a secure key
    // Use a predefined secret key (make sure it's sufficiently secure and long)
    private final String SECRET_KEY = "your-secure-predefined-new-key-here";
    private final long JWT_EXPIRATION = 86400000; // 1 day


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractName(String token) {
        return extractClaim(token, claims -> claims.get("name", String.class));
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY.getBytes()) // Convert the key to bytes
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
            throw new JwtException("Invalid JWT token: " + e.getMessage(), e);
        }
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(String name, String email, ERole role) {
        try {
            Map<String, Object> claims = new HashMap<>();
            claims.put("name", name);
            claims.put("role", role);
            return createToken(claims, email);
        } catch (Exception e) {
            logger.error("Error generating JWT token: {}", e.getMessage());
            throw new RuntimeException("Token generation failed.");
        }
    }


    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + JWT_EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes()) // Use byte[] for signing
                .compact();
    }


    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        Boolean tokenValid = extractedUsername.equals(username) && !isTokenExpired(token);
        logger.info("Token Validation - Extracted Username: {}", extractedUsername);
        logger.info("Token Validity: {}", tokenValid);
        return tokenValid;
    }
}
