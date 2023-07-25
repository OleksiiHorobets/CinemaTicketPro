package com.sigma.cinematicketpro.service.impl;

import com.sigma.cinematicketpro.TestUtils;
import com.sigma.cinematicketpro.dto.AuthenticationRequest;
import com.sigma.cinematicketpro.dto.RegistrationRequest;
import com.sigma.cinematicketpro.entity.CtpUser;
import com.sigma.cinematicketpro.entity.Role;
import com.sigma.cinematicketpro.exception.UserAlreadyExistsException;
import com.sigma.cinematicketpro.repository.RoleRepository;
import com.sigma.cinematicketpro.repository.UserRepository;
import com.sigma.cinematicketpro.util.JwtTokenUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private JwtTokenUtils jwtTokenUtils;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl sut;

    @Test
    void shouldSetDefaultUserRoleToNewlyRegisteredUserWhenRegisterInvoked() {
        RegistrationRequest regRequest = TestUtils.getValidRegistrationRequest();
        CtpUser expectedCtpUser = buildDefaultUserFromRequest(regRequest);
        String expectedToken = "generated-token";
        String encodedPassword = "encoded-password";

        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(expectedCtpUser);
        when(roleRepository.findByName(any())).thenReturn(new Role(1L, "ROLE_USER"));
        when(jwtTokenUtils.generateToken(expectedCtpUser)).thenReturn(expectedToken);
        when(passwordEncoder.encode(regRequest.getPassword())).thenReturn(encodedPassword);

        String actualToken = sut.register(regRequest);

        assertThat(actualToken).isEqualTo(expectedToken);

        verify(userRepository, times(1)).save(expectedCtpUser);
    }

    @Test
    void shouldThrowUserAlreadyExistsExceptionWithErrorListIfUserAlreadyExistsWhenRegisterInvoked() {
        RegistrationRequest regRequest = TestUtils.getValidRegistrationRequest();

        List<String> expectedConstraintVailotaionList = List.of(
                "User with such email already exists: " + regRequest.getEmail(),
                "User with such username already exists: " + regRequest.getUsername()
        );
        when(userRepository.existsByEmail(any())).thenReturn(true);
        when(userRepository.existsByUsername(any())).thenReturn(true);

        assertThatThrownBy(() -> sut.register(regRequest))
                .isExactlyInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("User already exists")
                .hasFieldOrPropertyWithValue("errorsList", expectedConstraintVailotaionList);

        verify(userRepository, times(1)).existsByEmail(regRequest.getEmail());
        verify(userRepository, times(1)).existsByUsername(regRequest.getUsername());
        verify(userRepository, never()).save(any());
    }


    @Test
    void shouldThrowBadCredentialsExceptionWhenAuthorizeWithNoExistingUsername() {
        AuthenticationRequest authRequest = new AuthenticationRequest(
                "non_existing_username",
                "password"
        );
        when(authenticationManager.authenticate(any())).thenThrow(BadCredentialsException.class);

        assertThatThrownBy(() -> sut.authenticate(authRequest))
                .isExactlyInstanceOf(BadCredentialsException.class);

        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    void shouldReturnTokenWhenAuthorizeWithCorrectCredentials() {
        AuthenticationRequest authRequest = new AuthenticationRequest(
                "username",
                "wrong_password"
        );
        CtpUser expectedUser = mock(CtpUser.class);
        Authentication authentication = mock(Authentication.class);
        String expectedToken = "token";

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(expectedUser);
        when(jwtTokenUtils.generateToken(expectedUser)).thenReturn(expectedToken);

        String actualToken = sut.authenticate(authRequest);

        assertThat(actualToken).isEqualTo(expectedToken);
        verify(authenticationManager, times(1)).authenticate(any());
    }

    private CtpUser buildDefaultUserFromRequest(RegistrationRequest regRequest) {
        return CtpUser.builder()
                .username(regRequest.getUsername())
                .firstName(regRequest.getFirstName())
                .lastName(regRequest.getLastName())
                .email(regRequest.getEmail())
                .password("encoded-password")
                .roles(Set.of(new Role(1L, "ROLE_USER")))
                .build();
    }
}