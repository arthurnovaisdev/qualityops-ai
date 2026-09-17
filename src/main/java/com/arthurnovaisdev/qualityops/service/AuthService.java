package com.arthurnovaisdev.qualityops.service;


import com.arthurnovaisdev.qualityops.dto.request.LoginRequestDTO;
import com.arthurnovaisdev.qualityops.dto.response.LoginResponseDTO;
import com.arthurnovaisdev.qualityops.entity.User;
import com.arthurnovaisdev.qualityops.repository.UserRepository;
import com.arthurnovaisdev.qualityops.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public LoginResult login (LoginRequestDTO dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.email(),
                        dto.password()
                )
        );

        User user = userRepository.findByEmail(dto.email())
                .orElseThrow();

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPasswordHash())
                        .roles(user.getRole().name())
                        .disabled(!user.isActive())
                        .build();

        String token = jwtService.generateToken(userDetails);

        LoginResponseDTO response = new LoginResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );

        return new LoginResult(token, response);
    }

    public record LoginResult(
            String token,
            LoginResponseDTO response
    ) {}
}
