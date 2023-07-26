package com.sigma.cinematicketpro.filter;

import com.sigma.cinematicketpro.TestUtils;
import com.sigma.cinematicketpro.entity.CtpUser;
import com.sigma.cinematicketpro.service.CtpUserService;
import com.sigma.cinematicketpro.util.JwtTokenManager;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {
    @Mock
    private JwtTokenManager jwtTokenManager;
    @Mock
    private CtpUserService ctpUserService;
    @Mock
    MockHttpServletRequest mockRequest;
    @Mock
    MockHttpServletResponse mockResponse;
    @Mock
    MockFilterChain mockFilterChain;
    @InjectMocks
    private JwtAuthenticationFilter sut;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {"Basic YWxleF9ib246cGFzcw==", "NOT_A_BEARER_TOKEN_AUTH NOT_A_TOKEN"})
    void shouldNotSetAuthenticationWhenBearerTokenIsMissing(String invalidAuthHeader) throws ServletException, IOException {
        when(mockRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(invalidAuthHeader);

        sut.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        assertThat(isAuthorized()).isFalse();
        verify(mockFilterChain, times(1)).doFilter(mockRequest, mockResponse);
    }

    @Test
    void shouldNotSetAuthenticationWhenTokenHasNullUsername() throws ServletException, IOException {
        String authHeaderWithInvalidToken = getAuthHeaderWithToken("invalid_token");

        when(mockRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(authHeaderWithInvalidToken);
        when(jwtTokenManager.getUsername(any())).thenReturn(null);

        sut.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        assertThat(isAuthorized()).isFalse();
        verify(mockFilterChain, times(1)).doFilter(mockRequest, mockResponse);
    }

    @Test
    void shouldNotChangeAuthenticationWhenAuthenticationIsAlreadySet() throws ServletException, IOException {
        String authHeader = getAuthHeaderWithToken("token");
        var initialAuth = mock(UsernamePasswordAuthenticationToken.class);

        SecurityContextHolder.getContext().setAuthentication(initialAuth);
        when(mockRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(authHeader);
        when(jwtTokenManager.getUsername(any())).thenReturn("username");

        sut.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        assertThat(isAuthorized()).isTrue();
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(initialAuth);

        verify(mockFilterChain, times(1)).doFilter(mockRequest, mockResponse);
    }

    @Test
    void shouldNotSetAuthenticationWhenTokenIsInvalid() throws ServletException, IOException {
        String authHeader = getAuthHeaderWithToken("token");
        UserDetails userDetails = TestUtils.getUserDetails();

        when(mockRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(authHeader);
        when(jwtTokenManager.getUsername(any())).thenReturn(userDetails.getUsername());
        when(jwtTokenManager.isTokenValid(any(), any())).thenReturn(false);
        when(ctpUserService.loadUserByUsername(any())).thenReturn(userDetails);

        sut.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        assertThat(isAuthorized()).isFalse();

        verify(mockFilterChain, times(1)).doFilter(mockRequest, mockResponse);
    }

    @Test
    void shouldSetSetAuthenticationWhenTokenIsValid() throws ServletException, IOException {
        String authHeader = getAuthHeaderWithToken("token");
        UserDetails userDetails = TestUtils.getUserDetails();

        when(mockRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(authHeader);
        when(jwtTokenManager.getUsername(any())).thenReturn(userDetails.getUsername());
        when(jwtTokenManager.isTokenValid(any(), any())).thenReturn(true);
        when(ctpUserService.loadUserByUsername(any())).thenReturn(userDetails);

        sut.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        UserDetails actualAuthUser = (CtpUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        assertThat(isAuthorized()).isTrue();
        assertThat(actualAuthUser).isEqualTo(userDetails);

        verify(mockFilterChain, times(1)).doFilter(mockRequest, mockResponse);
    }

    private boolean isAuthorized() {
        return SecurityContextHolder.getContext().getAuthentication() != null;
    }

    private static String getAuthHeaderWithToken(String token) {
        return "Bearer %s".formatted(token);
    }
}