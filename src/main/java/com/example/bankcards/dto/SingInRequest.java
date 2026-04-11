package com.example.bankcards.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "request for sign in")
public class SingInRequest {

    @Schema(description = "username",example = "mike")
    @NotBlank(message = "username can't be empty")
    private String username;

    @Schema(description = "password",example = "aboba123")
    @NotBlank(message = "password can't be empty")
    private String password;
}
