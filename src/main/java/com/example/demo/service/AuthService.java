package com.example.demo.service;

import com.example.demo.dto.request.LoginUserRequestDTO;
import com.example.demo.dto.request.RefreshTokenRequestDto;
import com.example.demo.dto.request.RegisterUserRequestDTO;
import com.example.demo.dto.response.CreateUserResponseDTO;
import com.example.demo.dto.response.JwtResponse;
import com.example.demo.dto.response.LoginUserResponseDTO;
import com.example.demo.enums.EnumRole;
import com.example.demo.exception.UnauthorizedException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.RefreshToken;
import com.example.demo.model.Roles;
import com.example.demo.model.User;
import com.example.demo.repository.RefreshTokenRepo;
import com.example.demo.repository.RoleRepo;
import com.example.demo.repository.UserRepo;
import lombok.AllArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepo userRepo;
    private  final RoleRepo roleRepo;
    private final RefreshTokenRepo refreshTokenRepo;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserMapper userMapper;



    public LoginUserResponseDTO login(LoginUserRequestDTO loginUserRequestDTO) {
        logger.info("Login User: {}", loginUserRequestDTO.getEmail());
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUserRequestDTO.getEmail(), loginUserRequestDTO.getPassword())
            );
            
            if (authentication.isAuthenticated()) {
                User user = (User) authentication.getPrincipal();
                String token = jwtService.generateAccessToken(user);
                String refreshToken = jwtService.generateRefreshToken(user);
                return userMapper.userToLoginUserDto(user, token , refreshToken);
            } else {
                throw new UnauthorizedException("Invalid Email or Password");
            }
        } catch (BadCredentialsException e) {
            logger.error("Authentication failed for user: {}", loginUserRequestDTO.getEmail(), e);
            throw new UnauthorizedException("Invalid email or password");
        } catch (InternalAuthenticationServiceException e) {
            // This often wraps UsernameNotFoundException
            if (e.getCause() instanceof UsernameNotFoundException) {
                logger.error("User not found: {}", loginUserRequestDTO.getEmail(), e);
                throw new UnauthorizedException("User not found");
            } else {
                logger.error("Internal authentication error for user: {}", loginUserRequestDTO.getEmail(), e);
                throw new UnauthorizedException("Authentication failed");
            }
        } catch (Exception e) {
            logger.error("Unexpected authentication error for user: {}", loginUserRequestDTO.getEmail(), e);
            throw new UnauthorizedException("Authentication failed");
        }
    }
    public CreateUserResponseDTO register(RegisterUserRequestDTO requestPayload) {
        try {
            logger.info("Registering new user: {}", requestPayload.getEmail());

            String password = requestPayload.getPassword();
            String email = requestPayload.getEmail();
            String name  = requestPayload.getName();

            // Check if user already exists
            User existingUser = userRepo.findByEmail(email);
            if (existingUser != null) {
                throw new IllegalArgumentException("User with email " + email + " already exists");
            }

            // Create new user
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));

            // Get or create USER role dynamically
            Roles roleUser = roleRepo.findByName(EnumRole.USER.name());
            if (roleUser == null) {
                roleUser = new Roles();
                roleUser.setName(EnumRole.USER.name());
                roleRepo.save(roleUser);
            }

            // Assign role using Set to avoid duplicates
            Set<Roles> roles = new HashSet<>();
            roles.add(roleUser);
            user.setRoles(roles);
            userRepo.save(user);

            return userMapper.UserToCreateUserDto(user, jwtService.generateAccessToken(user) , jwtService.generateRefreshToken(user) );

        } catch (Exception e) {
            logger.error("Unexpected authentication error for user: {}", requestPayload.getEmail(), e);
            throw new UnauthorizedException("Authentication failed");
        }
    }

    public JwtResponse refreshToken(RefreshTokenRequestDto refreshTokenRequestDto) {
        RefreshToken token = refreshTokenRepo.findByToken(refreshTokenRequestDto.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (!jwtService.verifyRefreshToken(token.getToken(), token.getUser())) {
            throw new RuntimeException("Invalid or expired refresh token");
        }

        User user = token.getUser();
        String newRefreshToken = jwtService.generateRefreshToken(user);
        String newAccessToken = jwtService.generateAccessToken(user);

        return new JwtResponse(newAccessToken, newRefreshToken);
    }

}
