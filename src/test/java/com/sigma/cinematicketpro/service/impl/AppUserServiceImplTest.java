package com.sigma.cinematicketpro.service.impl;

import com.sigma.cinematicketpro.entity.AppUser;
import com.sigma.cinematicketpro.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppUserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AppUserServiceImpl sut;

    @Test
    void shouldLoadUserByUsernameWhenUserExists() {
        String username = "username";
        AppUser userDetailsMock = mock(AppUser.class);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(userDetailsMock));

        UserDetails result = sut.loadUserByUsername(username);

        assertThat(result).isEqualTo(userDetailsMock);
    }

    @Test
    void shouldThrowUsernameNotFoundExceptionWhenUserNotExists() {
        String username = "non-existent-user";
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut.loadUserByUsername(username))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Username not found: %s".formatted(username));
    }
}