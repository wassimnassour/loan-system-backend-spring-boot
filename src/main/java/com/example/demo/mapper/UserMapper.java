package com.example.demo.mapper;


import com.example.demo.dto.response.CreateUserResponseDTO;
import com.example.demo.dto.response.LoginUserResponseDTO;
import com.example.demo.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    LoginUserResponseDTO userToLoginUserDto(User user);

    default LoginUserResponseDTO userToLoginUserDto(User user , String token) {
        LoginUserResponseDTO createUserResponseDTO = new LoginUserResponseDTO();
        createUserResponseDTO.setEmail(user.getEmail());
        createUserResponseDTO.setToken(token);
        return createUserResponseDTO;
    }

    default  CreateUserResponseDTO UserToCreateUserDto(User user , String token){
        CreateUserResponseDTO createUserResponseDTO = new CreateUserResponseDTO();
        createUserResponseDTO.setEmail(user.getEmail());
        createUserResponseDTO.setToken(token);
        return createUserResponseDTO;
    }
}
