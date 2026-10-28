package com.vianadanieldev.helpflow.infrastructure.adapter.in.web;

import com.vianadanieldev.helpflow.application.port.out.AuthenticationPort;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationPort authenticationPort;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return authenticationPort.authenticate(request.corporateEmail(), request.password())
                .map(token -> ResponseEntity.ok(new TokenResponse(token)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    public record LoginRequest(@NotBlank String corporateEmail, @NotBlank String password) {}
    public record TokenResponse(String accessToken) {}
}