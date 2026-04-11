package com.example.bankcards.dto;

import com.example.bankcards.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {

    @NotBlank(message = "username can't be empty")
    @Size(min = 5, max = 50, message = "username length must be between 5 and 50")
    private String username;

    @NotBlank(message = "email can't be empty")
    @Email(message = "email should be asd@example.com")
    @Size(max = 255, message = "email can't be more than 255")
    private String email;

    @NotBlank(message = "password can't be empty")
    @Size(min = 8, max = 255, message = "password length must be between 8 and 255")
    private String password;

    @NotNull(message = "role can't be null")
    private Role role;
}