package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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
            Boolean isTokenExpired = jwtService.isTokenExpired(token);
            if (!isTokenExpired) {

               String email =  jwtService.extractUsernameToken(token);
                User user = userRepo.findByEmail(email);
                if(user != null) {

                    List<GrantedAuthority> listGrantedAutherties = user.getAuthorities().stream().map(authority ->
                            new SimpleGrantedAuthority(authority.getAuthority())).collect(Collectors.toList());
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null , listGrantedAutherties);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
