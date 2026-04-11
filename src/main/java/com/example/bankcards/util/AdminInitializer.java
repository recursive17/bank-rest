package com.example.bankcards.util;

import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import liquibase.command.CommandOverride;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public void run(String... args) throws Exception {
        if(userRepository.findByUsername("admin").isPresent()){
            return;
        }
        User admin  = new User();
        admin.setRole(Role.ROLE_ADMIN);
        admin.setUsername("admin");
        admin.setEmail("admin@local");
        admin.setPassword(passwordEncoder.encode("admin"));

        userRepository.save(admin);
    }
}
