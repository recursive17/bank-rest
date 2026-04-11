package com.example.bankcards.controller;
import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.entity.Role;
import com.example.bankcards.security.JwtAuthenticationFilter;
import com.example.bankcards.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void getAllUsers_shouldReturnUsers() throws Exception {
        UserResponse user = new UserResponse();
        user.setId(2L);
        user.setUsername("mike01");
        user.setEmail("mike01@test.com");
        user.setRole(Role.ROLE_USER);

        when(userService.getAllUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].username").value("mike01"))
                .andExpect(jsonPath("$[0].email").value("mike01@test.com"))
                .andExpect(jsonPath("$[0].role").value("ROLE_USER"));
    }

    @Test
    void getUserById_shouldReturnUser() throws Exception {
        UserResponse user = new UserResponse();
        user.setId(2L);
        user.setUsername("mike01");
        user.setEmail("mike01@test.com");
        user.setRole(Role.ROLE_USER);

        when(userService.getUserById(2L)).thenReturn(user);

        mockMvc.perform(get("/users/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.username").value("mike01"));
    }

    @Test
    void createUser_shouldReturnCreatedUser() throws Exception {
        UserResponse user = new UserResponse();
        user.setId(2L);
        user.setUsername("alex01");
        user.setEmail("alex01@test.com");
        user.setRole(Role.ROLE_USER);

        when(userService.createUser(any())).thenReturn(user);

        String requestBody = """
                  {
                    "username": "alex01",
                    "email": "alex01@test.com",
                    "password": "aboba123",
                    "role": "ROLE_USER"
                  }
                  """;

        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.username").value("alex01"));
    }

    @Test
    void deleteUser_shouldReturnOk() throws Exception {
        doNothing().when(userService).deleteUser(2L);

        mockMvc.perform(delete("/users/2"))
                .andExpect(status().isOk());
    }
}
