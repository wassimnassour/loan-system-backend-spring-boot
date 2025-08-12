package com.example.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginUserRequestDTO {
    @NotNull(message = "Email is Required")
    private String email;

    @NotNull(message = "password is Required")
    private String password;
}
