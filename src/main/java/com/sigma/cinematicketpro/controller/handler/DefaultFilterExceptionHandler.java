package com.sigma.cinematicketpro.controller.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigma.cinematicketpro.exception.ApiErrorResponse;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultFilterExceptionHandler extends OncePerRequestFilter {
    private final ObjectMapper mapper;

    @Override
    public void doFilterInternal(@NotNull HttpServletRequest request,
                                 @NotNull HttpServletResponse response,
                                 @NotNull FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            setErrorResponse(HttpStatus.FORBIDDEN, response, e);
            log.warn(e.getMessage(), e.getCause());
        }
    }

    private void setErrorResponse(HttpStatus status, HttpServletResponse response, Throwable ex) throws IOException {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                status.value(),
                ex.getMessage()
        );

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        response.getWriter().write(
                mapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(apiErrorResponse)
        );
    }
}