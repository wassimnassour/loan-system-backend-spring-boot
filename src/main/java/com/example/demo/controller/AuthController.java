package com.example.demo.controller;

import com.example.demo.dto.request.LoginUserRequestDTO;
import com.example.demo.dto.request.RegisterUserRequestDTO;
import com.example.demo.dto.response.CreateUserResponseDTO;
import com.example.demo.dto.response.LoginUserResponseDTO;
import com.example.demo.model.User;
import com.example.demo.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginUserResponseDTO> login(@Valid @RequestBody LoginUserRequestDTO requestPayload) {
        return ResponseEntity.ok(authService.login(requestPayload));
    }

    @PostMapping("/register")
    public ResponseEntity<CreateUserResponseDTO> register(@Valid @RequestBody RegisterUserRequestDTO requestPayload) {
        return ResponseEntity.ok(authService.register(requestPayload));
    }
}
