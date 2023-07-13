package com.sigma.cinematicketpro.controller;

import com.sigma.cinematicketpro.exception.ApiErrorResponse;
import com.sigma.cinematicketpro.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

@WebMvcTest(controllers = {GlobalExceptionHandler.class, MovieController.class})
class GlobalExceptionHandlerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private MovieController movieController;

    @Autowired
    private GlobalExceptionHandler sut;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(movieController)
                .setControllerAdvice(sut)
                .build();
    }

    @Test
    void shouldHandleResourceNotFoundExceptionWhenControllerThrowsIt() throws Exception {
        String movieId = "movieId";

        when(movieController.getMovieById(any()))
                .thenThrow(new ResourceNotFoundException("Movie not found: {%s}".formatted(movieId)));

        mockMvc.perform(get("/movies/{id}", movieId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"code\":404,\"message\":\"Movie not found: {%s}\"}".formatted(movieId)))
                .andReturn();
    }

    @ParameterizedTest
    @ValueSource(classes = {IllegalArgumentException.class, NullPointerException.class, ArrayIndexOutOfBoundsException.class})
    void shouldHandleAllOtherExceptions(Class<? extends Exception> exception) throws Exception {
        String movieId = "movieId";

        when(movieController.getMovieById(any())).thenThrow(exception);

        mockMvc.perform(get("/movies/{id}", movieId))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"code\":500}"))
                .andReturn();
    }

    @Test
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