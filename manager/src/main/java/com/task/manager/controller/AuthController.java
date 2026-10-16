package com.task.manager.controller;

import com.task.manager.dto.AuthResponse;
import com.task.manager.dto.LoginRequestDto;
import com.task.manager.dto.RegisterRequestDto;
import com.task.manager.dto.UserResponse;
import com.task.manager.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService=authService;
    }
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequestDto requestDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(requestDto));
    }
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequestDto request) {
        return authService.login(request);
    }
}

