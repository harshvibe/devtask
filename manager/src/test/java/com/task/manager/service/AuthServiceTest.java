package com.task.manager.service;

import com.task.manager.dto.AuthResponse;
import com.task.manager.dto.LoginRequestDto;
import com.task.manager.dto.RegisterRequestDto;
import com.task.manager.dto.UserResponse;
import com.task.manager.entity.User;
import com.task.manager.handler.InvalidCredentialException;
import com.task.manager.repository.UserRepository;
import com.task.manager.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_hashesPassword_andNormalizesEmail() {
        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("HASHED");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResponse response = authService.register(
                new RegisterRequestDto("  alice ", " Alice@Example.COM ", "password123"));

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(saved.capture());
        assertThat(saved.getValue().getPassword()).isEqualTo("HASHED");
        assertThat(response.username()).isEqualTo("alice");
        assertThat(response.email()).isEqualTo("alice@example.com");
    }

    @Test
    void register_duplicateUsername_throws() {
        when(userRepository.existsByUsername("alice")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(
                new RegisterRequestDto("alice", "a@example.com", "password123")))
                .isInstanceOf(com.task.manager.handler.DuplicateUserException.class)
                .hasMessageContaining("Username");
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_duplicateEmail_throws() {
        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("a@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(
                new RegisterRequestDto("alice", "a@example.com", "password123")))
                .isInstanceOf(com.task.manager.handler.DuplicateUserException.class)
                .hasMessageContaining("Email");
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_withCorrectPassword_returnsToken() {
        User user = new User("alice", "a@example.com", "HASHED");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "HASHED")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("the.jwt.token");
        when(jwtService.getExpiresInSeconds()).thenReturn(3600L);

        AuthResponse response = authService.login(new LoginRequestDto("alice", "password123"));

        assertThat(response.accessToken()).isEqualTo("the.jwt.token");
        assertThat(response.tokentype()).isEqualTo("Bearer");
        assertThat(response.expireInSeconds()).isEqualTo(3600L);
    }

    @Test
    void login_wrongPassword_throws() {
        User user = new User("alice", "a@example.com", "HASHED");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "HASHED")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequestDto("alice", "wrong")))
                .isInstanceOf(InvalidCredentialException.class)
                .hasMessage("Invalid username or password");
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void login_unknownUser_throwsSameMessage() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginRequestDto("ghost", "whatever")))
                .isInstanceOf(InvalidCredentialException.class)
                .hasMessage("invalid username or password");
    }
}

