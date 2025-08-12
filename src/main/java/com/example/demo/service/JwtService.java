package com.example.demo.service;

import com.example.demo.exception.JwtAuthenticationException;
import com.example.demo.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expiration;

    private Key key;

    // Initialize the Key after injection
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", userDetails.getUsername());
        claims.put("email", userDetails.getUsername()); // Since username is email in our case
        
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public String generateToken(User user) {
        return generateToken((UserDetails) user);
    }


    public Claims extractClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (UnsupportedJwtException e) {
            throw new JwtAuthenticationException("Invalid JWT: Unsupported algorithm or incorrect key");
        } catch (MalformedJwtException e) {
            throw new JwtAuthenticationException("Invalid JWT: Malformed token");
        } catch (ExpiredJwtException e) {
            throw new JwtAuthenticationException("Invalid JWT: Token has expired");
        } catch (SignatureException e) {
            throw new JwtAuthenticationException("Invalid JWT: Signature verification failed");
        } catch (IllegalArgumentException e) {
            throw new JwtAuthenticationException("Invalid JWT: Token cannot be null or empty");
        }
    }


    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }


    public Boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public Boolean verifyToken (String token , UserDetails user) {
        return !isTokenExpired(token) && user.getUsername().equals(extractUsername(token));
    }
}
