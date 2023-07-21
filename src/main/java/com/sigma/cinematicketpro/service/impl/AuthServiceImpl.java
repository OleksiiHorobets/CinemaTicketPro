package com.sigma.cinematicketpro.service.impl;

import com.sigma.cinematicketpro.dto.AuthenticationRequest;
import com.sigma.cinematicketpro.dto.RegistrationRequest;
import com.sigma.cinematicketpro.entity.AppUser;
import com.sigma.cinematicketpro.entity.Role;
import com.sigma.cinematicketpro.exception.NoSuchRoleException;
import com.sigma.cinematicketpro.exception.UserAlreadyExistsException;
import com.sigma.cinematicketpro.repository.UserRepository;
import com.sigma.cinematicketpro.repository.RoleRepository;
import com.sigma.cinematicketpro.service.AuthService;
import com.sigma.cinematicketpro.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String register(RegistrationRequest registrationRequest) {
        checkIfAlreadyExists(registrationRequest);

        Role defaultUserRole = getDefaultUserRole();
        AppUser appUser = buildUser(registrationRequest, defaultUserRole);

        AppUser registeredUser = userRepository.save(appUser);

        return jwtTokenUtils.generateToken(registeredUser);
    }

    @Override
    public String authenticate(AuthenticationRequest authRequest) {
        final String username = authRequest.getUsername();

        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid Credentials"));

        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid Credentials");
        }

        return jwtTokenUtils.generateToken(user);
    }

    private void checkIfAlreadyExists(RegistrationRequest registrationRequest) {
        List<String> constraintViolationList = new ArrayList<>();

        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            constraintViolationList.add("User with such email already exists: " + registrationRequest.getEmail());
        }
        if (userRepository.existsByUsername(registrationRequest.getUsername())) {
            constraintViolationList.add("User with such username already exists: " + registrationRequest.getUsername());
        }

        if (!constraintViolationList.isEmpty()) {
            throw new UserAlreadyExistsException("User already exists", constraintViolationList);
        }
    }

    private Role getDefaultUserRole() {
        return roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new NoSuchRoleException("No such user role found: ROLE_USER"));
    }

    private AppUser buildUser(RegistrationRequest registrationRequest, Role userRole) {
        return AppUser.builder()
                .username(registrationRequest.getUsername())
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .roles(Set.of(userRole))
                .build();
    }
}
