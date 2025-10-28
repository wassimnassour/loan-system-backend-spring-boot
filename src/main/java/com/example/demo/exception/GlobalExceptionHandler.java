package com.example.demo.exception;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorizedException(UnauthorizedException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach((error) -> errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<String> handleJwtAuthenticationException(JwtAuthenticationException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

   @ExceptionHandler(NoHandlerFoundException.class)
   public ResponseEntity<Map<String, Object>> handleNoHandlerFoundException(NoHandlerFoundException e) {
       Map<String, Object> errorResponse = new HashMap<>();
       errorResponse.put("timestamp", LocalDateTime.now().toString());
       errorResponse.put("status", 404);
       errorResponse.put("error", "Not Found");
       errorResponse.put("message", "The requested endpoint does not exist");
       errorResponse.put("path", e.getRequestURL());

       return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
   }
   @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequestException(BadRequestException e) {
       Map<String, Object> errorResponse = new HashMap<>();
       errorResponse.put("timestamp", LocalDateTime.now().toString());
       errorResponse.put("status", 400);
       errorResponse.put("error", "Not Found");
       errorResponse.put("message", e.getMessage());


        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
   }
}
