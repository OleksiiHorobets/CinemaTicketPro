package com.sigma.cinematicketpro.controller;

import com.sigma.cinematicketpro.TestUtils;
import com.sigma.cinematicketpro.dto.AuthenticationRequest;
import com.sigma.cinematicketpro.dto.RegistrationRequest;
import com.sigma.cinematicketpro.filter.JwtAuthenticationFilter;
import com.sigma.cinematicketpro.service.AppUserService;
import com.sigma.cinematicketpro.service.AuthService;
import com.sigma.cinematicketpro.util.JwtTokenUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;

import static com.sigma.cinematicketpro.TestUtils.getObjectMapper;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class AuthControllerTest {
    private static final String timestampRegex = "\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}:\\d{2}";

    @MockBean
    private AuthService authService;

    @Autowired
    public AuthController sut;

    private MockMvc mockMvc;

    @MockBean
    private JwtTokenUtils jwtTokenUtils;

    @MockBean
    private AppUserService userService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(sut)
                .build();
    }

    @Test
    void shouldReturnTokenAndTimestampInAuthenticationResponseWhenLoginIsSuccessful() throws Exception {
        AuthenticationRequest authRequest = new AuthenticationRequest(
                "username",
                "password"
        );

        when(authService.authenticate(any()))
                .thenReturn("token");

        RequestBuilder request = MockMvcRequestBuilders.post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(authRequest))
                .characterEncoding(StandardCharsets.UTF_8);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("token")))
                .andExpect(jsonPath("$.timestamp", matchesPattern(timestampRegex)));
    }

    @Test
    void shouldReturnTokenAndTimestampInAuthenticationResponseWhenRegisterIsSuccessful() throws Exception {
        RegistrationRequest registrationRequest = TestUtils.getValidRegistrationRequest();

        when(authService.register(any()))
                .thenReturn("token");

        RequestBuilder request = MockMvcRequestBuilders.post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(registrationRequest))
                .characterEncoding(StandardCharsets.UTF_8);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", is("token")))
                .andExpect(jsonPath("$.timestamp", matchesPattern(timestampRegex)));
    }
}