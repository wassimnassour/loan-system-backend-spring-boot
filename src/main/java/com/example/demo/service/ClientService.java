package com.example.demo.service;

import java.util.List;
import java.util.Set;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.request.ClientAssignNewRoleRequestDto;
import com.example.demo.dto.request.ClientDeleteRequestDto;
import com.example.demo.model.Roles;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepo;
import com.example.demo.repository.UserRepo;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ClientService {

    UserRepo userRepo;
    RoleRepo roleRepo;

    public String deleteUser(ClientDeleteRequestDto clientDeleteRequestDto) {
        try {
            userRepo.deleteById(clientDeleteRequestDto.getId());
            return "success delete user";
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    @Transactional
    public String assignNewRole(Long clientId, ClientAssignNewRoleRequestDto clientAssignNewRoleRequest) {

        User user = userRepo.findById(clientId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));


        Roles role =  roleRepo.findById(clientAssignNewRoleRequest.getRoleId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));

         Set<Roles> roles = user.getRoles();
         if(roles.contains(role)){
             throw new ResponseStatusException(HttpStatus.CONFLICT, "Role already exists");
         }

         user.setRoles(roles);
         userRepo.save(user);
        return "Success assign new role";
    }

    public List<User> listingAllClients() {
        return userRepo.findAll();
    }
}
