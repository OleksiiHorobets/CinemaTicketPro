package com.sigma.cinematicketpro.controller;

import com.sigma.cinematicketpro.config.WebSecurityConfiguration;
import com.sigma.cinematicketpro.controller.handler.GlobalExceptionHandler;
import com.sigma.cinematicketpro.dto.AuthenticationRequest;
import com.sigma.cinematicketpro.exception.ApiErrorResponse;
import com.sigma.cinematicketpro.exception.ResourceNotFoundException;
import com.sigma.cinematicketpro.service.CtpUserService;
import com.sigma.cinematicketpro.util.JwtTokenUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sigma.cinematicketpro.TestUtils.getObjectMapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {GlobalExceptionHandler.class,
        MovieController.class,
        AuthController.class,
        WebSecurityConfiguration.class})
class GlobalExceptionHandlerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuthController authController;
    @MockBean
    private MovieController movieController;

    @MockBean
    private JwtTokenUtils jwtTokenUtils;

    @MockBean
    private CtpUserService ctpUserService;

    @Autowired
    private GlobalExceptionHandler sut;

    @Test
    @WithMockUser("admin")
    void shouldHandleResourceNotFoundExceptionWhenControllerThrowsIt() throws Exception {
        String movieId = "movieId";

        when(movieController.getMovieById(any()))
                .thenThrow(new ResourceNotFoundException("Movie not found: {%s}".formatted(movieId)));

        mockMvc.perform(get("/movies/{id}", movieId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"code\":404,\"message\":\"Movie not found: {%s}\"}".formatted(movieId)));
    }

    @Test
    void shouldHandleBadCredentialsExceptionWhenControllerThrowsIt() throws Exception {
        AuthenticationRequest authRequest = new AuthenticationRequest(
                "username",
                "password"
        );

        when(authController.login(any()))
                .thenThrow(new BadCredentialsException("Invalid Credentials"));

        RequestBuilder loginRequest = MockMvcRequestBuilders.post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(authRequest))
                .characterEncoding(StandardCharsets.UTF_8);

        mockMvc.perform(loginRequest)
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"code\":401,\"message\":\"Invalid Credentials\"}"));
    }

    @ParameterizedTest
    @WithMockUser("admin")
    @ValueSource(classes = {IllegalArgumentException.class, NullPointerException.class, ArrayIndexOutOfBoundsException.class})
    void shouldHandleAllOtherExceptions(Class<? extends Exception> exception) throws Exception {
        String movieId = "movieId";

        when(movieController.getMovieById(any())).thenThrow(exception);

        mockMvc.perform(get("/movies/{id}", movieId))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"code\":500}"));
    }

    @Test
    @WithMockUser("admin")
    void shouldFilterErrorsWithNullDefaultMessages() {
        Map<String, String> expectedErrorsMap = new HashMap<>();
        expectedErrorsMap.put("tmdbId", "TMDB ID should not be negative");
        expectedErrorsMap.put("title", "Title should not be blank");

        MethodArgumentNotValidException exceptionMock = mock(MethodArgumentNotValidException.class);

        BindingResult bindingResultMock = mock(BindingResult.class);

        FieldError tmdbFieldError = new FieldError("movieDto", "tmdbId", "TMDB ID should not be negative");
        FieldError titleFieldError = new FieldError("movieDto", "title", "Title should not be blank");
        FieldError mockFieldErrorWithNullMessage = mock(FieldError.class);

        when(mockFieldErrorWithNullMessage.getDefaultMessage()).thenReturn(null);
        when(exceptionMock.getBindingResult()).thenReturn(bindingResultMock);
        when(bindingResultMock.getAllErrors()).thenReturn(List.of(tmdbFieldError, titleFieldError, mockFieldErrorWithNullMessage));

        ApiErrorResponse response = sut.handleValidationExceptions(exceptionMock);

        HashMap<String, String> actualErrorsMap = (HashMap<String, String>) response.getData();

        assertThat(actualErrorsMap).isEqualTo(expectedErrorsMap);
        verify(mockFieldErrorWithNullMessage, times(1)).getDefaultMessage();
    }
}