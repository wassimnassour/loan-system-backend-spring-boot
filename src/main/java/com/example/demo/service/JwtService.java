package com.example.demo.service;

import com.example.demo.exception.JwtAuthenticationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.refreshSecretKey}")
    private String refreshSecretKey;

    @Value("${jwt.expiration}")
    private Long tokenExpiration;


    @Value("${jwt.refreshTokenExpiration}")
    private Long refreshTokenExpiration;


    private Key tokenKey;
    private Key refreshTokenKey;

    // Initialize the Key after injection
    @PostConstruct
    public void init() {

        this.tokenKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.refreshTokenKey = Keys.hmacShaKeyFor(refreshSecretKey.getBytes(StandardCharsets.UTF_8));
    }





    public Claims extractClaims(String token , Key key) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (UnsupportedJwtException e) {
            throw new JwtAuthenticationException("Invalid JWT: Unsupported algorithm or incorrect key");
        } catch (MalformedJwtException e) {
            throw new JwtAuthenticationException("Invalid JWT: Malformed token");
        } catch (ExpiredJwtException e) {
            throw new JwtAuthenticationException("Invalid JWT: Token has expired");
        } catch (IllegalArgumentException e) {
            throw new JwtAuthenticationException("Invalid JWT: Token cannot be null or empty");
        }
    }


    public Key getTokenSecretKey(){
        return tokenKey;
    }

    public Key getRefreshTokenKey(){
        return tokenKey;
    }

    public String extractUsernameToken(String token) {
        return extractClaims(token,getTokenSecretKey()).getSubject();
    }

    public String extractUsernameRefreshToken(String token) {
        return extractClaims(token,getRefreshTokenKey()).getSubject();
    }


    public Boolean isTokenExpired(String token) {
        return extractClaims(token , getTokenSecretKey()).getExpiration().before(new Date());
    }


    public Boolean isRefreshTokenExpired(String token) {
        return extractClaims(token , getRefreshTokenKey()).getExpiration().before(new Date());
    }

    public Boolean verifyAccessToken (String token , UserDetails user) {
        return !isTokenExpired(token) && user.getUsername().equals(extractUsernameToken(token));
    }

    public Boolean verifyRefreshToken (String token , UserDetails user) {
        return !isTokenExpired(token) && user.getUsername().equals(extractUsernameRefreshToken(token));
    }

    public String generateRefreshToken (UserDetails user) {

        return generateToken(user ,refreshTokenKey ,  refreshTokenExpiration );

    }

    public String generateAccessToken(UserDetails user) {
        return generateToken(user ,getTokenSecretKey() , tokenExpiration);
    }

    public String generateToken(UserDetails user , Key singingkey  , Long expiration ) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList());

        Date now = new Date();

        Instant expiryDate = Instant.from(Instant.now()).plusMillis(expiration);


        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername()) // this sets "sub"
                .setIssuedAt(now)
                .setExpiration(Date.from(expiryDate))
                .signWith(singingkey, SignatureAlgorithm.HS256) // specify algorithm explicitly
                .compact();
    }

//    @SuppressWarnings("unchecked")
//    public List<String> extractRoles(String token) {
//        Claims claims = extractClaims(token);
//        return (List<String>) claims.get("roles");
//    }
}
