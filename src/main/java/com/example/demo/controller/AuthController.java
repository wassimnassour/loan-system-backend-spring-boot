package com.example.demo.controller;

import com.example.demo.dto.request.LoginUserRequestDTO;
import com.example.demo.dto.request.RefreshTokenRequestDto;
import com.example.demo.dto.request.RegisterUserRequestDTO;
import com.example.demo.dto.response.CreateUserResponseDTO;
import com.example.demo.dto.response.JwtResponse;
import com.example.demo.dto.response.LoginUserResponseDTO;
import com.example.demo.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
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

    @PostMapping("/refresh-token")
    public ResponseEntity<JwtResponse> refreshToken(@RequestBody RefreshTokenRequestDto refreshTokenRequestDto) {
        return  ResponseEntity.ok(authService.refreshToken(refreshTokenRequestDto));
    }
}
