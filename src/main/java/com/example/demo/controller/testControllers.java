package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testControllers {
    @GetMapping("/test")
    public ResponseEntity<String> testController() {
        return new ResponseEntity<>("wdqwdwe" , HttpStatus.ACCEPTED);
    }
}
