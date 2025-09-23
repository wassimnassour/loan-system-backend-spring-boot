package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;

    @Column(nullable = false)
    private int expiresIn;

    @OneToOne()
    @JoinColumn(name="user_id" , referencedColumnName = "id")
    private User user;
}
