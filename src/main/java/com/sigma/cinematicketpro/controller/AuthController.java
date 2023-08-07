package com.sigma.cinematicketpro.controller;

import com.sigma.cinematicketpro.dto.auth.AuthenticationRequest;
import com.sigma.cinematicketpro.dto.auth.AuthenticationResponse;
import com.sigma.cinematicketpro.dto.auth.RegistrationRequest;
import com.sigma.cinematicketpro.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    @ResponseBody
    public AuthenticationResponse login(@Valid @RequestBody AuthenticationRequest authenticationRequest) {
        String token = authService.authenticate(authenticationRequest);

        return AuthenticationResponse.builder()
                .token(token)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @PostMapping("/registration")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public AuthenticationResponse register(@Valid @RequestBody RegistrationRequest registrationRequest) {
        String token = authService.register(registrationRequest);

        return AuthenticationResponse.builder()
                .token(token)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
