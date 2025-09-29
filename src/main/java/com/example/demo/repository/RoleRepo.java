package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Roles;



@Repository
public interface RoleRepo extends JpaRepository<Roles, Long> {
   Roles findByName(String name);
}
