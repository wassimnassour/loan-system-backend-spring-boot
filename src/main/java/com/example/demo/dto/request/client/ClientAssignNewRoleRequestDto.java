package com.example.demo.dto.request.client;


import com.example.demo.enums.EnumRole;
import lombok.Data;

@Data
public class ClientAssignNewRoleRequestDto {
    private Long clientId;
    private EnumRole role;
}
