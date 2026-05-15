package com.example.isaBack.service;

import com.example.isaBack.dto.AuthResponse;
import com.example.isaBack.dto.LoginRequest;
import com.example.isaBack.dto.RegisterRequest;
import com.example.isaBack.model.ActivationToken;
import com.example.isaBack.model.Role;
import com.example.isaBack.model.User;
import com.example.isaBack.repository.ActivationTokenRepository;
import com.example.isaBack.repository.UserRepository;
import com.example.isaBack.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ActivationTokenRepository activationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Value("${app.activation.token-expiration-hours}")
    private long tokenExpirationHours;

    @Transactional
    public void register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Lozinke se ne poklapaju");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email već postoji");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Korisničko ime već postoji");
        }

        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .address(request.getAddress())
                .enabled(false)
                .role(Role.USER)
                .build();

        userRepository.save(user);

        ActivationToken activationToken = ActivationToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiresAt(LocalDateTime.now().plusHours(tokenExpirationHours))
                .used(false)
                .build();

        activationTokenRepository.save(activationToken);

        emailService.sendActivationEmail(user.getEmail(), user.getUsername(), activationToken.getToken());
    }

    @Transactional
    public void activate(String token) {
        ActivationToken activationToken = activationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Neispravan aktivacioni token"));

        if (activationToken.isUsed()) {
            throw new RuntimeException("Token je već iskorišćen");
        }
        if (activationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token je istekao");
        }

        User user = activationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        activationToken.setUsed(true);
        activationTokenRepository.save(activationToken);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen"));

        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }
}