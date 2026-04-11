package com.example.bankcards.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "response with token")
public class JwtAuthenticationResponse {
    @Schema(description = "token",example = "abcde9")
    private String token;

}
