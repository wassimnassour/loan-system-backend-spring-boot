package com.example.demo.service;

import com.example.demo.exception.JwtAuthenticationException;
import com.example.demo.repository.UserRepo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
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
    private String expiration;

    private Key key;

    // Initialize the Key after injection
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }



    public String generateToken(UserDetails user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList());

        Date now = new Date();

        Date expiryDate = new Date(System.currentTimeMillis()  * 1000L);


        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername()) // this sets "sub"
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256) // specify algorithm explicitly
                .compact();
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
