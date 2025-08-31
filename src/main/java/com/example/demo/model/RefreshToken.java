package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;

    @NotNull
    @Column(nullable = false)
    private int expiresIn;

    @OneToOne()
    @JoinColumn(name="user_id" , referencedColumnName = "id")
    private User user;
}
