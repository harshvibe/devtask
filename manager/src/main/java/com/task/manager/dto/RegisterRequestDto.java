package com.task.manager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequestDto(
        @NotBlank(message = "username must not be blank")
        @Size(min = 3,max = 50,message = "username must be 3 to 50 character")
        @Pattern(regexp = "^[A-Za-z0-9_.-]+$",
        message = "username may only contain letter, digits, '.', '_' and '-'\"")
        String username,

        @NotBlank(message = "email must not be blank")
        @Email(message = "email must be a valid address")
        @Size(max = 100, message = "email must be at most 100 characters")
        String email,

        @NotBlank(message = "password must not be blank")
        @Size(min = 8, max = 72, message = "password must be 8 to 72 characters")
        String password
) {
}
