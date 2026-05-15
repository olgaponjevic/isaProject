package com.example.isaBack.controller;

import com.example.isaBack.dto.AuthResponse;
import com.example.isaBack.dto.LoginRequest;
import com.example.isaBack.dto.RegisterRequest;
import com.example.isaBack.service.AuthService;
import com.example.isaBack.service.LoginAttemptService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final LoginAttemptService loginAttemptService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("Registracija uspešna. Proveri email za aktivaciju naloga.");
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activate(@RequestParam String token) {
        authService.activate(token);
        return ResponseEntity.ok("Nalog uspešno aktiviran!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String ip = getClientIp(httpRequest);

        if (loginAttemptService.isBlocked(ip)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Previše neuspešnih pokušaja. Pokušaj ponovo za minut.");
        }

        try {
            AuthResponse response = authService.login(request);
            loginAttemptService.resetAttempts(ip);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            loginAttemptService.recordFailedAttempt(ip);
            throw e;
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isEmpty()) {
            return xfHeader.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}