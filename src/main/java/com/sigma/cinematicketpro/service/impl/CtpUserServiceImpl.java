package com.sigma.cinematicketpro.service.impl;

import com.sigma.cinematicketpro.repository.UserRepository;
import com.sigma.cinematicketpro.service.CtpUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CtpUserServiceImpl implements CtpUserService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found: %s".formatted(username)));
    }
}
