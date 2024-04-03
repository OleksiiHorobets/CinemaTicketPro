package com.sigma.cinematicketpro.service.impl;

import com.sigma.cinematicketpro.dto.auth.AuthenticationRequest;
import com.sigma.cinematicketpro.dto.auth.RegistrationRequest;
import com.sigma.cinematicketpro.entity.CtpUser;
import com.sigma.cinematicketpro.entity.Role;
import com.sigma.cinematicketpro.exception.UserAlreadyExistsException;
import com.sigma.cinematicketpro.repository.RoleRepository;
import com.sigma.cinematicketpro.repository.UserRepository;
import com.sigma.cinematicketpro.service.AuthService;
import com.sigma.cinematicketpro.util.JwtTokenManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final JwtTokenManager jwtTokenManager;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;

  @Override
  @Transactional
  public String register(RegistrationRequest registrationRequest) {
    checkIfAlreadyExists(registrationRequest);

    Role defaultUserRole = getDefaultUserRole();
    CtpUser ctpUser = buildUser(registrationRequest, defaultUserRole);

    CtpUser registeredUser = userRepository.save(ctpUser);

    return jwtTokenManager.generateToken(registeredUser);
  }

  @Override
  @Transactional
  public String authenticate(AuthenticationRequest authRequest) {
    Authentication auth = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
    );

    CtpUser user = (CtpUser) auth.getPrincipal();

    return jwtTokenManager.generateToken(user);
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
    return roleRepository.findByName("ROLE_USER");
  }

  private CtpUser buildUser(RegistrationRequest registrationRequest, Role userRole) {
    return CtpUser.builder()
        .username(registrationRequest.getUsername())
        .firstName(registrationRequest.getFirstName())
        .lastName(registrationRequest.getLastName())
        .email(registrationRequest.getEmail())
        .password(passwordEncoder.encode(registrationRequest.getPassword()))
        .roles(Set.of(userRole))
        .build();
  }
}