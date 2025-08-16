package com.example.demo.service;

import com.example.demo.dto.request.LoginUserRequestDTO;
import com.example.demo.dto.request.RegisterUserRequestDTO;
import com.example.demo.dto.response.CreateUserResponseDTO;
import com.example.demo.dto.response.LoginUserResponseDTO;
import com.example.demo.enums.EnumRole;
import com.example.demo.exception.UnauthorizedException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepo;
import lombok.AllArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepo userRepo;
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
                String token = jwtService.generateToken(user);
                return userMapper.userToLoginUserDto(user, token);
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
          EnumRole role = requestPayload.getRole();
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
          user.setRole(role != null ? requestPayload.getRole() : EnumRole.USER);
          userRepo.save(user);
          return userMapper.UserToCreateUserDto( user, jwtService.generateToken(user));
      }catch (Exception e) {
          logger.error("Unexpected authentication error for user: {}", requestPayload.getEmail(), e);
          throw new UnauthorizedException("Authentication failed");
      }
    }
}
