package com.example.demo.mapper;



import org.springframework.stereotype.Component;

import com.example.demo.dto.response.CreateUserResponseDTO;
import com.example.demo.dto.response.LoginUserResponseDTO;
import com.example.demo.model.User;

@Component
public class UserMapper {

    public LoginUserResponseDTO userToLoginUserDto(User user , String token , String refreshToken) {
        LoginUserResponseDTO createUserResponseDTO = new LoginUserResponseDTO();
        createUserResponseDTO.setEmail(user.getEmail());
        createUserResponseDTO.setToken(token);
        createUserResponseDTO.setRefreshToken(refreshToken);
        return createUserResponseDTO;
    }

    public  CreateUserResponseDTO UserToCreateUserDto(User user , String token, String refreshToken){
        CreateUserResponseDTO createUserResponseDTO = new CreateUserResponseDTO();
        createUserResponseDTO.setEmail(user.getEmail());
        createUserResponseDTO.setToken(token);
        createUserResponseDTO.setRefreshToken(refreshToken);
        return createUserResponseDTO;
    }
}
