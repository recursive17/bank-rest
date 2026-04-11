package com.example.bankcards.controller;

import com.example.bankcards.dto.JwtAuthenticationResponse;
import com.example.bankcards.dto.SingInRequest;
import com.example.bankcards.dto.SingUpRequest;
import com.example.bankcards.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация")
public class AuthController {

    private final AuthenticationService authenticationService;

    @Operation(summary = "registration")
    @PostMapping("/sign-up")
    public JwtAuthenticationResponse singUp(@RequestBody @Valid SingUpRequest request){
        return authenticationService.signUp(request);
    }

    @Operation(summary = "authorization")
    @PostMapping("/sign-in")
    public JwtAuthenticationResponse singIn(@RequestBody @Valid SingInRequest request){
        return authenticationService.signIn(request);
    }

}
