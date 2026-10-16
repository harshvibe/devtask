package com.task.manager.service;

import com.task.manager.dto.AuthResponse;
import com.task.manager.dto.LoginRequestDto;
import com.task.manager.dto.RegisterRequestDto;
import com.task.manager.dto.UserResponse;
import com.task.manager.entity.User;
import com.task.manager.handler.InvalidCredentialException;
import com.task.manager.repository.UserRepository;
import com.task.manager.security.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,PasswordEncoder passwordEncoder,JwtService jwtService){
        this.jwtService=jwtService;
        this.passwordEncoder=passwordEncoder;
        this.userRepository=userRepository;
    }
    @Transactional
    public UserResponse register(RegisterRequestDto requestDto) {
        String username = requestDto.username().trim();
        String email = requestDto.email().trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByUsername(username)) {
            throw new DuplicateUserException("Username is already taken");
        }
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateUserException("Email is already registered");
        }

        User user = new User(username, email, passwordEncoder.encode(requestDto.password()));
        return UserResponse.from(userRepository.save(user));
    }
    @Transactional
    public AuthResponse login(LoginRequestDto requestDto){
        User user=userRepository.findByUsername(requestDto.username().trim())
                .orElseThrow(()->new InvalidCredentialException("invalid username or password"));
        if (!passwordEncoder.matches(requestDto.password(), user.getPassword())) {
            throw new InvalidCredentialException("Invalid username or password");
        }
        return new AuthResponse(jwtService.generateToken(user), "Bearer", jwtService.getExpiresInSeconds());
    }
}
