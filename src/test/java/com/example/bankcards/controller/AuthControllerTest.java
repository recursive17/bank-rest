package com.example.bankcards.controller;

import com.example.bankcards.dto.JwtAuthenticationResponse;
import com.example.bankcards.security.JwtAuthenticationFilter;
import com.example.bankcards.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void signUp_shouldReturnToken() throws Exception {
        JwtAuthenticationResponse response = JwtAuthenticationResponse.builder()
                .token("jwt-sign-up-token")
                .build();

        when(authenticationService.signUp(any())).thenReturn(response);

        String requestBody = """
                  {
                    "username": "mike01",
                    "email": "mike01@test.com",
                    "password": "aboba123"
                  }
                  """;

        mockMvc.perform(post("/auth/sign-up")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-sign-up-token"));
    }

    @Test
    void signIn_shouldReturnToken() throws Exception {
        JwtAuthenticationResponse response = JwtAuthenticationResponse.builder()
                .token("jwt-sign-in-token")
                .build();

        when(authenticationService.signIn(any())).thenReturn(response);

        String requestBody = """
                  {
                    "username": "mike01",
                    "password": "aboba123"
                  }
                  """;

        mockMvc.perform(post("/auth/sign-in")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-sign-in-token"));
    }

    @Test
    void signUp_shouldReturnBadRequest_whenValidationFails() throws Exception {
        String requestBody = """
                  {
                    "username": "",
                    "email": "not-email",
                    "password": "123"
                  }
                  """;

        mockMvc.perform(post("/auth/sign-up")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("validation failed"));
    }
}
