package com.example.demo.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.exception.JwtAuthenticationException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepo;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class JwtFilterChain extends OncePerRequestFilter {
    private  final JwtService jwtService;
    private  final UserRepo userRepo;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            
            try {
                Boolean isTokenExpired = jwtService.isTokenExpired(token);
                if (!isTokenExpired) {
                    String email = jwtService.extractUsernameToken(token);
                    User user = userRepo.findByEmail(email);
                    if(user != null) {
                        List<GrantedAuthority> listGrantedAutherties = user.getAuthorities().stream().map(authority ->
                                new SimpleGrantedAuthority(authority.getAuthority())).collect(Collectors.toList());
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null , listGrantedAutherties);
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            } catch (JwtAuthenticationException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonResponse = objectMapper.writeValueAsString(new ErrorResponse(
                    "Unauthorized", 
                    e.getMessage(), 
                    401
                ));
                
                response.getWriter().write(jsonResponse);
                return; // Don't continue the filter chain
            }
        }

        filterChain.doFilter(request, response);
    }
    
    // Inner class for error response
    private static class ErrorResponse {
        public String error;
        public String message;
        public int status;
        
        public ErrorResponse(String error, String message, int status) {
            this.error = error;
            this.message = message;
            this.status = status;
        }
    }
}
