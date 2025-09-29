package com.example.demo.service;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
                RefreshToken refreshTokenEntity =new  RefreshToken();
                refreshTokenEntity.setToken(refreshToken);
                refreshTokenEntity.setUser(user);
                refreshTokenRepo.save(refreshTokenEntity);

                return userMapper.userToLoginUserDto(user, token , refreshToken);
            } else {
                throw new UnauthorizedException("Invalid Email or Password");
            }
        } catch (BadCredentialsException e) {
            logger.error("Authentication failed for user: {}", loginUserRequestDTO.getEmail(), e);
            throw new UnauthorizedException("Invalid email or password");
        } catch (InternalAuthenticationServiceException e) {
                logger.error("Internal authentication error for user: {}", loginUserRequestDTO.getEmail(), e);
                throw new UnauthorizedException("Authentication failed");

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
        System.out.println("Refresh Token: " + refreshTokenRequestDto.getRefreshToken());
        RefreshToken token = refreshTokenRepo.findByToken(refreshTokenRequestDto.getRefreshToken())
                .orElseThrow(() -> new BadCredentialsException("Refresh token not found"));

        // Check if token is expired using the safe method
        Boolean isExpired = jwtService.isRefreshTokenExpiredSafe(token.getToken());
        
        if (isExpired == null) {
            // Token is malformed/invalid
            refreshTokenRepo.delete(token);
            throw new RuntimeException("Invalid refresh token format");
        } else if (isExpired) {
            // Token is expired - delete it from database
            refreshTokenRepo.delete(token);
            throw new RuntimeException("Refresh token has expired");
        }
        
        // Additional verification (username match)
        if (!token.getUser().getUsername().equals(jwtService.extractUsernameRefreshToken(token.getToken()))) {
            refreshTokenRepo.delete(token);
            throw new RuntimeException("Invalid refresh token");
        }

        User user = token.getUser();
        String newRefreshToken = jwtService.generateRefreshToken(user);
        String newAccessToken = jwtService.generateAccessToken(user);

        return new JwtResponse(newAccessToken, newRefreshToken);
    }

}
