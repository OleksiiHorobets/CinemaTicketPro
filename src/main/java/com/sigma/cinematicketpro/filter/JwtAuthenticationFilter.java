package com.sigma.cinematicketpro.filter;

import com.sigma.cinematicketpro.service.CtpUserService;
import com.sigma.cinematicketpro.util.JwtTokenUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;
    private final CtpUserService ctpUserService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        getBearerToken(request).ifPresent(jwt -> {
            String username = jwtTokenUtils.getUsername(jwt);

            Optional.ofNullable(username)
                    .filter(user -> isUserNotAuthenticated())
                    .map(ctpUserService::loadUserByUsername)
                    .filter(user -> jwtTokenUtils.isTokenValid(jwt, user))
                    .ifPresent(setUpAuthToken(request));
        });
        filterChain.doFilter(request, response);
    }

    private boolean isUserNotAuthenticated() {
        return SecurityContextHolder.getContext().getAuthentication() == null;
    }

    private Optional<String> getBearerToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
                .filter(token -> token.startsWith("Bearer "))
                .map(token -> token.replaceFirst("Bearer ", ""));
    }

    private UsernamePasswordAuthenticationToken buildAuthToken(HttpServletRequest request, UserDetails userDetails) {
        var authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );
        authToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );
        return authToken;
    }

    private Consumer<UserDetails> setUpAuthToken(HttpServletRequest request) {
        return userDetails -> {
            var authToken = buildAuthToken(request, userDetails);
            SecurityContextHolder.getContext().setAuthentication(authToken);
        };
    }
}