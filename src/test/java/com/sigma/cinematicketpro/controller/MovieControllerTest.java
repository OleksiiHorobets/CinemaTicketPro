package com.sigma.cinematicketpro.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sigma.cinematicketpro.TestUtils;
import com.sigma.cinematicketpro.dto.MovieDTO;
import com.sigma.cinematicketpro.entity.Movie;
import com.sigma.cinematicketpro.exception.ResourceNotFoundException;
import com.sigma.cinematicketpro.filter.JwtAuthenticationFilter;
import com.sigma.cinematicketpro.service.AppUserService;
import com.sigma.cinematicketpro.service.MovieService;
import com.sigma.cinematicketpro.util.JwtTokenUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.sigma.cinematicketpro.TestUtils.getObjectMapper;
import static com.sigma.cinematicketpro.TestUtils.mapMvcResultIntoObject;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MovieController.class)
@ExtendWith(MockitoExtension.class)
class MovieControllerTest {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private MovieService movieService;

    @MockBean
    private JwtTokenUtils jwtTokenUtils;

    @MockBean
    private AppUserService userService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    void shouldReturnListOfMoviesWhenGetAllMovies() throws Exception {
        List<MovieDTO> expectedMovieList = TestUtils.getValidMovieDTOs();
        when(movieService.getAllMovies()).thenReturn(expectedMovieList);

        RequestBuilder request = MockMvcRequestBuilders.get("/movies");

        MvcResult response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<MovieDTO> actualMovieList = mapMvcResultIntoObject(response, new TypeReference<>() {
        });

        assertThat(actualMovieList).isEqualTo(expectedMovieList);
        verify(movieService, times(1)).getAllMovies();
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenGetMovieByIdAndMovieIdNotExists() throws Exception {
        String movieId = "movieId";

        when(movieService.getMovieById(movieId)).thenThrow(ResourceNotFoundException.class);

        RequestBuilder request = MockMvcRequestBuilders.get("/movies/{id}", movieId);

        mockMvc.perform(request)
                .andExpect(status().isNotFound());

        verify(movieService, times(1)).getMovieById(movieId);
    }

    @Test
    void shouldReturnMovieWhenGetMovieById() throws Exception {
        MovieDTO expectedMovie = TestUtils.getValidMovieDTO();
        String movieId = expectedMovie.getId();

        when(movieService.getMovieById(movieId)).thenReturn(expectedMovie);

        RequestBuilder request = MockMvcRequestBuilders.get("/movies/{id}", movieId);

        MvcResult response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        MovieDTO actualMovie = mapMvcResultIntoObject(response, new TypeReference<>() {
        });

        assertThat(actualMovie).isEqualTo(expectedMovie);
        verify(movieService, times(1)).getMovieById(movieId);
    }

    @Test
    void shouldCallMovieServiceSaveMethodWhenSaveMovie() throws Exception {
        MovieDTO movieDTOtoSave = TestUtils.getValidMovieDtoNoId();
        MovieDTO expectedMovieDTO = TestUtils.getValidMovieDTO();

        when(movieService.saveMovie(movieDTOtoSave)).thenReturn(expectedMovieDTO);

        RequestBuilder request = MockMvcRequestBuilders.post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(movieDTOtoSave))
                .characterEncoding(StandardCharsets.UTF_8);

        MvcResult response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        MovieDTO actualMovieDTO = mapMvcResultIntoObject(response, new TypeReference<>() {
        });

        assertThat(actualMovieDTO).isEqualTo(expectedMovieDTO);
        verify(movieService, times(1)).saveMovie(movieDTOtoSave);
    }

    @Test
    void shouldThrowMethodArgumentNotValidExceptionWhenSaveInvalidMovie() throws Exception {
        MovieDTO invalidMovieDTO = TestUtils.getInvalidMovieDTO();
        String expectedResponse = """
                {
                    "code": 400,
                    "message": "Validation issues occurred",
                    "data": {
                      "voteAverage": "must be less than or equal to 10",
                      "tmdbId": "TMDB ID should not be negative",
                      "voteCount": "must be greater than or equal to 0",
                      "id": "Id must be between 16 and 32 characters",
                      "title": "Title should not be blank",
                      "posterPath": "Invalid poster URI"
                    }
                  }
                """;

        RequestBuilder request = MockMvcRequestBuilders.post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(invalidMovieDTO))
                .characterEncoding(StandardCharsets.UTF_8);

        MvcResult response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponse = response.getResponse().getContentAsString();

        JSONAssert.assertEquals(actualResponse, expectedResponse, true);
    }

    @Test
    void shouldCallDeleteOnMovieServiceWhenDeleteMovieById() throws Exception {
        Movie expectedMovie = TestUtils.getValidMovie();
        String movieId = expectedMovie.getId();

        RequestBuilder request = MockMvcRequestBuilders.delete("/movies/{id}", movieId);

        mockMvc.perform(request)
                .andExpect(status().isNoContent())
                .andDo(print())
                .andReturn();

        verify(movieService, times(1)).deleteMovieById(movieId);
    }

    @Test
    void shouldReturnUpdatedMovieWhenUpdateMovie() throws Exception {
        MovieDTO movieToUpdateDto = TestUtils.getValidMovieDtoNoId();
        MovieDTO expectedMovieDto = TestUtils.getValidMovieDTO();

        String movieId = expectedMovieDto.getId();

        when(movieService.updateMovie(movieId, movieToUpdateDto)).thenReturn(expectedMovieDto);

        RequestBuilder request = MockMvcRequestBuilders.put("/movies/{id}", movieId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(movieToUpdateDto));

        MvcResult response = mockMvc.perform(request)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        MovieDTO actualMovieDto = mapMvcResultIntoObject(response, new TypeReference<>() {
        });

        assertThat(actualMovieDto).isEqualTo(expectedMovieDto);
        verify(movieService, times(1)).updateMovie(movieId, movieToUpdateDto);
    }

    @Test
    void shouldThrowMethodArgumentNotValidExceptionWhenUpdateInvalidMovie() throws Exception {
        MovieDTO invalidMovieDTO = TestUtils.getInvalidMovieDTO();
        String expectedResponse = """
                {
                    "code": 400,
                    "message": "Validation issues occurred",
                    "data": {
                      "voteAverage": "must be less than or equal to 10",
                      "tmdbId": "TMDB ID should not be negative",
                      "voteCount": "must be greater than or equal to 0",
                      "id": "Id must be between 16 and 32 characters",
                      "title": "Title should not be blank",
                      "posterPath": "Invalid poster URI"
                    }
                  }
                """;

        RequestBuilder request = MockMvcRequestBuilders.put("/movies/{id}", invalidMovieDTO.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(invalidMovieDTO))
                .characterEncoding(StandardCharsets.UTF_8);

        MvcResult response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponse = response.getResponse().getContentAsString();

        JSONAssert.assertEquals(actualResponse, expectedResponse, true);
    }
}