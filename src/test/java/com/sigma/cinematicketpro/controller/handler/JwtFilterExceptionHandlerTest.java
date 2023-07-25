package com.sigma.cinematicketpro.controller.handler;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.ServletException;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static com.sigma.cinematicketpro.TestUtils.getObjectMapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JwtFilterExceptionHandlerTest {
    @Mock
    private MockFilterChain filterChain;

    @Spy
    private MockHttpServletResponse response;

    @Mock
    private MockHttpServletRequest request;

    private JwtFilterExceptionHandler sut;

    @BeforeEach
    void setUp() {
        sut = new JwtFilterExceptionHandler(getObjectMapper());
    }

    @Test
    void shouldCallDoFilter() throws ServletException, IOException {
        sut.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void shouldCatchJwtExceptionAndWriteApiErrorResponseInResponseWhenFilterChainThrowsJwtException() throws ServletException, IOException, JSONException {
        doThrow(new JwtException("The token is invalid!"))
                .when(filterChain).doFilter(any(), any());

        String expectedResponseContent = """
                {
                    "code" : 403,
                    "message" : "The token is invalid!"
                 }
                """;

        sut.doFilterInternal(request, response, filterChain);

        int actualStatus = response.getStatus();
        String actualContentType = response.getContentType();
        String actualResponseContent = response.getContentAsString();

        assertThat(actualStatus).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(actualContentType).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        JSONAssert.assertEquals(actualResponseContent, expectedResponseContent, true);

        verify(filterChain, times(1)).doFilter(request, response);
    }
}