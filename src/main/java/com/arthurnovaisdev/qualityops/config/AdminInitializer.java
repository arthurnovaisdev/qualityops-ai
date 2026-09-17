package com.arthurnovaisdev.qualityops.config;

import com.arthurnovaisdev.qualityops.entity.User;
import com.arthurnovaisdev.qualityops.enums.Role;
import com.arthurnovaisdev.qualityops.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AdminInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${ADMIN_NAME}")
    private String adminName;

    @Value("${ADMIN_EMAIL}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    @Bean
    ApplicationRunner createInitialAdmin() {
        return args -> {
            if (!userRepository.existsByEmail(adminEmail)) {

                User admin = User.builder()
                        .name(adminName)
                        .email(adminEmail)
                        .passwordHash(passwordEncoder.encode(adminPassword))
                        .role(Role.ADMIN)
                        .build();

                userRepository.save(admin);
            }
        };
    }
}
