package com.example.demo.controller;



import com.example.demo.dto.request.ClientAssignNewRoleRequestDto;
import com.example.demo.dto.request.ClientDeleteRequestDto;
import com.example.demo.model.User;

import com.example.demo.service.ClientService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/clients")
@RestController
public class ClientController {
    ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteClientAutherzationController(@RequestBody ClientDeleteRequestDto clientDeleteRequestDto) {
        return ResponseEntity.ok(clientService.deleteUser(clientDeleteRequestDto));
    }

    @GetMapping("/assign-new-role/{clientId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> assignNewRoleAuthorizationController(@PathVariable Long clientId, @RequestBody ClientAssignNewRoleRequestDto clientAssignNewRoleRequest) {
        return ResponseEntity.ok(clientService.assignNewRole(clientId, clientAssignNewRoleRequest));
    }

    @GetMapping("/all")
    // @PreAuthorize("hasRole('ADMIN')") // Temporarily disabled for testing
    public ResponseEntity<List<User>> listAllClientsAuthorizationController() {
        return ResponseEntity.ok(clientService.listingAllClients());
    }


}
