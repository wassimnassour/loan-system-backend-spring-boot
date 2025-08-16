package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class demoAutherzationController {

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> userDemoAutherzationController() {


        return ResponseEntity.ok("Success Getting user Endpoint");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> AdminOnlyAutherzationController( ) {
        return ResponseEntity.ok("Success Getting admin Endpoint");
    }
}
