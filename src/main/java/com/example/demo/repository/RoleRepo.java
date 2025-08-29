package com.example.demo.repository;

import com.example.demo.model.Roles;
import com.example.demo.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepo extends JpaRepository<Roles, Long> {
   Roles findByName(String name);
}
