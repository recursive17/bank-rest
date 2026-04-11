package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "request for registration")
public class SingUpRequest {

    @Schema(description = "username",example = "mike")
    @Size(min = 5,max=50,message = "username length must be between 5 and 50")
    @NotBlank(message = "username can't be empty")
    private String username;

    @Schema(description = "email",example = "jonathan@gmail.com")
    @Size(min = 5,max=255,message = "email length must be between 5 and 255")
    @NotBlank(message = "email can't be empty")
    @Email(message = "email should be asd@example.com")
    private String email;


    @Schema(description = "password",example = "aboba123")
    @Size(min = 8 , max=255,message = "password length must be between 8 and 255")
    @NotBlank(message = "password can't be empty")
    private String password;

}
