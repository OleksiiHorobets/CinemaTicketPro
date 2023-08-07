package com.sigma.cinematicketpro.service;

import com.sigma.cinematicketpro.dto.auth.AuthenticationRequest;
import com.sigma.cinematicketpro.dto.auth.RegistrationRequest;

public interface AuthService {
    String register(RegistrationRequest registrationRequest);

    String authenticate(AuthenticationRequest authenticationRequest);
}
