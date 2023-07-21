package com.sigma.cinematicketpro.service;

import com.sigma.cinematicketpro.dto.AuthenticationRequest;
import com.sigma.cinematicketpro.dto.RegistrationRequest;

public interface AuthService {
    String register(RegistrationRequest registrationRequest);

    String authenticate(AuthenticationRequest authenticationRequest);
}
